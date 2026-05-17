# Contexto para chat do Backend — Lina (conversa Mobile + API)

Documento para colar em `@docs/contexto-backend-chat.md` ou anexar no chat do repositório **LINAPROJETO-BACKEND**. Resume decisões, contratos de API e correções discutidas com o time do app Android.

**Repositório mobile:** `LINAPROJETO-MOBILE`  
**Backend:** `LINAPROJETO-BACKEND/lina` (Spring Boot, Railway)

---

## 1. Visão geral do produto

- App Android monta **cardápio semanal** (7 dias × 4 refeições/dia).
- Hoje o cardápio é salvo **no aparelho** (`CardapioLocalStore`); **não há POST** para sincronizar o `PlanoSemanal` no servidor.
- **Lista de compras** consolida ingredientes do plano semanal do usuário.
- Autenticação: JWT em `Authorization: Bearer <token>`; rotas protegidas com `AuthUtils.verificarProprietario(usuarioId)` onde aplicável.

---

## 2. Alterações feitas no backend (lista de compras)

### Problema original

O banco já tinha `ingrediente.categoria` e `refeicao_ingrediente.unidade`, mas o JSON exposto ao app estava incompleto:

| Endpoint | Faltava no JSON |
|----------|-----------------|
| `GET /lista-compras/{usuarioId}` | `unidade` em cada item |
| `GET /refeicoes/{id}` | `categoria` em cada ingrediente |

O app chegou a usar um mapa estático (`IngredienteCatalogoLocal`); **foi removido** após o backend passar os campos.

### Implementação atual

#### `ItemListaDTO`

```java
private String nomeIngrediente;
private Double quantidade;
private String unidade;  // ex.: unidades, g, xícara, pitada
```

#### `ListaComprasService`

- Percorre `PlanoSemanal` → `Cardapio` → `ItemCardapio` → `Refeicao` → `RefeicaoIngrediente`.
- Consolida quantidades com chave **`(Ingrediente, unidade)`** — mesma lógica que o app usa ao mesclar (`nome|unidade`).
- Agrupa por `ingrediente.getCategoria()`.
- Retorna via `ListaComprasMapper.toDTO(Map<CategoriaIngrediente, List<ItemListaDTO>>)`.

```java
private record ChaveConsolidacao(Ingrediente ingrediente, String unidade) {}
```

#### `IngredienteItemDTO` + `RefeicaoMapper`

- Campo `categoria` (String, nome do enum: `PROTEINAS`, `FRUTAS_E_VEGETAIS`, etc.).
- Preenchido em `toIngredienteItemDTO` a partir de `ri.getIngrediente().getCategoria().name()`.

### Contrato JSON — `GET /lista-compras/{usuarioId}`

```json
[
  {
    "categoria": "PROTEINAS",
    "itens": [
      {
        "nomeIngrediente": "Ovo",
        "quantidade": 4.0,
        "unidade": "unidades"
      }
    ]
  }
]
```

- **404** se não existir plano para o usuário (`EntidadeNaoEncontradaException`).
- **[]** se o plano existir mas não tiver itens de cardápio (comum enquanto o app só grava localmente).

### Contrato JSON — ingrediente na receita

```json
"ingredientesDetalhados": [
  {
    "nome": "Ovo",
    "quantidade": 2,
    "unidade": "unidades",
    "categoria": "PROTEINAS",
    "texto": "2 unidades de ovo"
  }
]
```

### Enum `CategoriaIngrediente`

| Valor API | Label no app |
|-----------|--------------|
| `FRUTAS_E_VEGETAIS` | Frutas e Vegetais |
| `LATICINIOS` | Laticínios |
| `GRAOS_E_CEREAIS` | Grãos e Cereais |
| `PROTEINAS` | Proteínas |
| `OUTROS` | Outros |

---

## 3. Bug HTTP 500 — “Criar cardápio” (CRÍTICO)

> **Diagnóstico:** O erro 500 provavelmente vem de `GET /refeicoes` ao abrir **Criar cardápio** — carregamento **lazy fora de transação**. A correção é no **backend** (não no layout do app).

### Sintoma

Ao abrir **Criar cardápio** no app, toast: **“Erro ao carregar refeições (500)”** ou **“Erro ao carregar Café da manhã (HTTP 500)”**.

O **500 ao abrir Criar cardápio vinha do backend**, não da UI Android.

### O que acontecia

1. Ao abrir a tela, o app chama **4 vezes**:
   - `GET /refeicoes?periodo=CAFE_DA_MANHA&usuarioId=...`
   - `GET /refeicoes?periodo=ALMOCO&usuarioId=...`
   - `GET /refeicoes?periodo=LANCHE_DA_TARDE&usuarioId=...`
   - `GET /refeicoes?periodo=JANTAR&usuarioId=...`

2. Em `RefeicaoService.listarPorPeriodoEUsuario`, cada `Refeicao` era convertida com `RefeicaoMapper.toDTO`, que monta `ingredientesDetalhados` acessando:
   - `refeicao.getIngredientes()` → relação lazy `OneToMany`
   - `ri.getIngrediente()` → relação lazy `ManyToOne`

3. O método **`listarPorPeriodoEUsuario` não tinha `@Transactional`**. A sessão JPA já estava fechada no momento do mapeamento → **`LazyInitializationException`** no servidor → resposta **HTTP 500**.

4. Inconsistência: `buscarPorId` (**detalhe** da receita) **já tinha** `@Transactional(readOnly = true)`; a **listagem** por período, não.

### Correção no backend

#### 1. `RefeicaoService.java` — transação na listagem

```java
@Transactional(readOnly = true)
public List<RefeicaoResponseDTO> listarPorPeriodoEUsuario(
        PeriodoDia periodo,
        Long usuarioId) {
    // ...
}
```

#### 2. `RefeicaoRepository.java` — `JOIN FETCH` (antes vs depois)

**Antes** (sem fetch; lazy quebrava fora da sessão):

```java
@Query("SELECT r FROM Refeicao r WHERE :periodo MEMBER OF r.periodosPermitidos")
List<Refeicao> findByPeriodo(PeriodoDia periodo);
```

**Depois** (ingredientes e ingrediente carregados na mesma query):

```java
@Query("""
SELECT DISTINCT r FROM Refeicao r
LEFT JOIN FETCH r.ingredientes ri
LEFT JOIN FETCH ri.ingrediente
WHERE :periodo MEMBER OF r.periodosPermitidos
""")
List<Refeicao> findByPeriodo(@Param("periodo") PeriodoDia periodo);
```

O mesmo padrão (`DISTINCT` + `LEFT JOIN FETCH`) foi aplicado em **`buscarValidas`**, usado quando o usuário tem restrições alimentares.

**Benefícios:** evita `LazyInitializationException`, reduz problema N+1 ao mapear várias refeições.

#### Deploy

**Deploy obrigatório no Railway** (ou ambiente em uso). Só atualizar o APK **não** resolve o 500 — a falha é no servidor.

### No app (somente mensagem de erro)

Arquivo: `CriarCardapioMainActivity.java` — método `carregarOpcoesDoBackend()`.

**Antes:**

```java
Toast.makeText(CriarCardapioMainActivity.this,
        "Erro ao carregar refeições (" + code + ")",
        Toast.LENGTH_LONG).show();
```

**Depois** (indica qual período falhou):

```java
List<CriarCardapioRefeicao> merged = new ArrayList<>();
for (String[] periodo : PERIODOS_API) {
    String query = periodo[0];
    String label = periodo[1];
    Response<List<RefeicaoResponse>> resp =
            api.listar(auth, query, userId).execute();
    if (!resp.isSuccessful()) {
        final int code = resp.code();
        final String periodoLabel = label;
        runOnUiThread(() -> {
            Toast.makeText(CriarCardapioMainActivity.this,
                    "Erro ao carregar " + periodoLabel + " (HTTP " + code + "). "
                            + "Tente novamente ou verifique o servidor.",
                    Toast.LENGTH_LONG).show();
            finish();
        });
        return;
    }
    // ...
}
```

Exemplo de toast: *“Erro ao carregar Café da manhã (HTTP 500). Tente novamente ou verifique o servidor.”*

### Checklist pós-correção

- [ ] Backend com `@Transactional` em `listarPorPeriodoEUsuario` publicado
- [ ] `findByPeriodo` e `buscarValidas` com `JOIN FETCH` publicados
- [ ] `GET /refeicoes?periodo=CAFE_DA_MANHA&usuarioId=1` retorna **200** + JSON (não 500)
- [ ] App abre **Criar cardápio** e lista refeições dos 4 períodos

---

## 4. O que o app Android consome (referência)

### Lista de compras

| Classe | Uso |
|--------|-----|
| `ListaComprasApiService` | `GET lista-compras/{usuarioId}` |
| `ComprasListaCarregador` | API primeiro; se vazia → cardápio local + `GET refeicoes/{id}` |
| `CategoriaComprasMapeador` | Enum → label PT |
| `ComprasQuantidadeFormatter` | Ex.: `(2 unidades)`, `(100 g)` |

### Criar cardápio

| Classe | Uso |
|--------|-----|
| `CriarCardapioMainActivity` | 4× `GET /refeicoes?periodo=&usuarioId=` |
| `RefeicaoApiService` | Retrofit |
| Salva semana em `CardapioLocalStore` (local apenas) |

### Cardápio / trocar refeição

- `MudarCardapioActivity` — mesmas chamadas `GET /refeicoes`.
- `GET /refeicoes/{id}` — detalhe para receita e lista de compras local.

---

## 5. Endpoints relevantes (resumo)

| Método | Rota | Auth | Observação |
|--------|------|------|------------|
| GET | `/lista-compras/{usuarioId}` | Sim | Plano no DB; pode vir `[]` |
| GET | `/refeicoes?periodo=&usuarioId=` | Sim | Catálogo por período; **exige fix 500** |
| GET | `/refeicoes/{id}?usuarioId=&periodo=` | Sim | Detalhe + ingredientes + categoria |
| GET | `/cardapio/{usuarioId}` | Sim | 7 dias; plano no servidor |
| POST | *(cardápio/plano)* | — | **Não existe** — gap conhecido |

---

## 6. Pendências / melhorias futuras (backend)

1. **POST ou PUT do plano semanal / itens de cardápio**  
   Para `/lista-compras` refletir o que o usuário montou no app sem fallback local.

2. **Tratamento global de exceções**  
   Retornar 500 com corpo útil em dev; logar `LazyInitializationException` claramente.

3. **`ListaComprasMapper`**  
   Ordenar categorias por ordem fixa (`Enum.values()`) em vez de ordem do `HashMap` (opcional, UX).

4. **Arquivo DTO**  
   Classe `IngredienteItemDTO` em `ingredienteItemDTO.java` (minúsculo) — compila no Windows; em Linux CI pode exigir nome de arquivo = nome da classe.

5. **Migration `informacao_nutricional`**  
   Usada em `toDetalheDTO`; listagem não carrega nutrição (OK).

---

## 7. Arquivos backend tocados nesta conversa

```
lina/src/main/java/com/projeto/lina/
├── controller/ListaComprasController.java
├── service/ListaComprasService.java          # ChaveConsolidacao + unidade
├── service/RefeicaoService.java              # @Transactional na listagem
├── repository/RefeicaoRepository.java        # JOIN FETCH
├── dto/ItemListaDTO.java                     # + unidade
├── dto/IngredienteItemDTO.java               # + categoria (arquivo ingredienteItemDTO.java)
├── mapper/RefeicaoMapper.java                  # categoria no builder
└── mapper/ListaComprasMapper.java              # Map<Categoria, List<ItemListaDTO>>
```

---

## 8. Como validar no backend

```bash
# Listar café (substituir token e userId)
curl -H "Authorization: Bearer <TOKEN>" \
  "https://linaprojeto-backend-production.up.railway.app/refeicoes?periodo=CAFE_DA_MANHA&usuarioId=1"

# Lista de compras
curl -H "Authorization: Bearer <TOKEN>" \
  "https://linaprojeto-backend-production.up.railway.app/lista-compras/1"
```

- Resposta `200` com JSON (não HTML de erro 500).
- `ingredientesDetalhados[].categoria` presente.
- `lista-compras[].itens[].unidade` presente.

---

## 9. Linha do tempo da conversa

1. Integrar lista de compras no app (substituir dados fake).
2. Categorias/unidades — primeiro workaround com catálogo local no app.
3. Esclarecimento: dados existem no banco, faltavam nos DTOs.
4. Backend atualizado: `unidade` em `ItemListaDTO`, `categoria` em `IngredienteItemDTO`.
5. App atualizado: remove catálogo local; consome API.
6. Erro **500 em Criar cardápio** → `GET /refeicoes` + lazy fora de transação → `@Transactional` em `listarPorPeriodoEUsuario` + `JOIN FETCH` no repository + toast no app com período que falhou (ver seção 3).

---

## 10. Documento relacionado no repo mobile

- `docs/contexto-lista-de-compras.md` — foco na feature Compras e fluxo do app.

---

*Gerado para contexto do chat do backend. Atualizar após novos endpoints (ex.: sincronização de cardápio).*

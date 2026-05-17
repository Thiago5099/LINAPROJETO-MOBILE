# Contexto: Lista de Compras (Lina — Mobile + Backend)

Documento gerado a partir da conversa de implementação da feature **Lista de Compras**. Use como contexto para continuidade do trabalho.

---

## Objetivo

Integrar a aba **Compras** do app Android com o backend, exibindo ingredientes consolidados da semana, agrupados por **categoria**, com **quantidade** e **unidade** no padrão do backend (ex.: `2 unidades`, `100 g`).

---

## Estado do backend (dados vs API)

### O banco **tem** categoria e unidade

| Dado | Onde está no backend |
|------|----------------------|
| Categoria | Tabela `ingrediente` → enum `CategoriaIngrediente` (`FRUTAS_E_VEGETAIS`, `LATICINIOS`, `GRAOS_E_CEREAIS`, `PROTEINAS`, `OUTROS`) |
| Unidade | Tabela `refeicao_ingrediente` → campo `unidade` (`unidades`, `g`, `xícara`, `colher`, etc.) |

O `ListaComprasService` **já agrupa** por `ingrediente.getCategoria()` ao montar a lista.

### O que cada endpoint **envia hoje** no JSON

#### `GET /lista-compras/{usuarioId}`

- **Autenticado** (JWT + `AuthUtils.verificarProprietario`).
- Resposta: lista de `ListaComprasDTO`:
  - `categoria` — enum (ex.: `PROTEINAS`)
  - `itens` — lista de `ItemListaDTO`:
    - `nomeIngrediente`
    - `quantidade`
    - **Não envia `unidade`** (campo não existe no DTO atual)

Exemplo:

```json
[
  {
    "categoria": "PROTEINAS",
    "itens": [
      { "nomeIngrediente": "Ovo", "quantidade": 4.0 }
    ]
  }
]
```

**Observação:** O plano semanal é criado no cadastro do usuário, mas o cardápio do app é salvo **só localmente** (`CardapioLocalStore`). Se o plano no servidor estiver vazio, `/lista-compras` retorna `[]`.

#### `GET /refeicoes/{id}?usuarioId=&periodo=`

- `ingredientesDetalhados[]` em `IngredienteItemDTO`:
  - `nome`, `quantidade`, `unidade`, `texto`
  - **Não envia `categoria`** do ingrediente

---

## Estado do app Android (implementado)

### Fluxo de carregamento (`ComprasListaCarregador`)

1. Chama `GET /lista-compras/{usuarioId}` com Bearer token.
2. Se a resposta tiver itens → monta a lista (categoria vem do **grupo** da API).
3. Se vazia ou 404 → monta a partir do **cardápio local** (`CardapioLocalStore`):
   - Para cada refeição com `refeicaoId`, chama `GET /refeicoes/{id}`.
   - Soma ingredientes duplicados na semana (cache por `refeicaoId|periodo`).
4. Se não houver cardápio salvo → lista vazia + toast orientando a montar o cardápio.

### Arquivos principais (pacote `Feature/Compras`)

| Arquivo | Função |
|---------|--------|
| `ListaComprasApiService.java` | Retrofit: `GET lista-compras/{usuarioId}` |
| `ListaComprasResponse.java` / `ItemListaResponse.java` | DTOs Gson |
| `ComprasListaCarregador.java` | Orquestra API + fallback local |
| `ComprasFragment.java` | UI: RecyclerView, progresso, login |
| `ComprasIngrediente.java` | Modelo: nome, quantidade (double), unidade, categoria |
| `ComprasAdapter.java` | Exibe itens; quantidade via `ComprasQuantidadeFormatter` |
| `CategoriaComprasMapeador.java` | `FRUTAS_E_VEGETAIS` → "Frutas e Vegetais", etc. |
| `ComprasQuantidadeFormatter.java` | Formato igual ao backend (`2 unidades`, `1/2 xícara`) |
| `IngredienteCatalogoLocal.java` | **Workaround** — mapa estático nome → categoria + unidade padrão |
| `ComprasIngredienteTextoParser.java` | Parse de texto `• ingrediente` do cardápio local |

### Outras correções no app

- `PeriodoMapeador.uiParaQuery` — matching flexível (`Café da manhã`, `Café da Manhã`, etc.).
- `MudarCardapioActivity` / `CardapioFragment` — passam e persistem `refeicaoId` ao trocar refeição.

### Cardápio local

- `CardapioLocalStore` — comentário: *"O backend ainda não expõe POST para atualizar o plano"*.
- Lista de compras do servidor depende de itens no `PlanoSemanal` no DB; na prática muitos usuários só têm cardápio no aparelho → fallback via `/refeicoes/{id}`.

---

## `IngredienteCatalogoLocal` — por que existe

Foi criado quando a restrição era **não alterar o backend**, para preencher lacunas da API:

| Fluxo | Categoria | Unidade |
|-------|-----------|---------|
| `/lista-compras` | Da API (grupo) | Não vem no JSON → catálogo local como fallback |
| Cardápio local + `/refeicoes/{id}` | Não vem no JSON → catálogo local | Da API (`ing.unidade`) |

O bloco `static { registrar(...) }` espelha os **26 ingredientes** do seed (`V3__seed_refeicoes.sql`).

Métodos:

- `categoriaLabel(nome)` → rótulo UI via `CategoriaComprasMapeador`
- `unidadePadrao(nome)` → unidade quando a API não envia

`ComprasListaCarregador.resolverUnidade(nome, unidadeApi)` — prioriza unidade da API; senão usa o catálogo.

---

## Problemas da abordagem com catálogo local

1. **Dados duplicados** — seed no servidor + lista fixa no app; desatualiza se o back mudar.
2. **Só 26 ingredientes** — nomes fora do mapa → categoria "Outros", unidade vazia.
3. **Unidade “padrão”** — uma unidade por ingrediente; pode divergir da soma real em `/lista-compras`.
4. **Manutenção** — cada ingrediente novo exige update do APK.
5. **Não é fonte da verdade** — o ideal é o backend expor o que já está no banco.

---

## Solução recomendada (alinhada ao backend)

Ajuste **mínimo** no backend (sem mudar regra de negócio):

1. **`ItemListaDTO`** — adicionar campo `unidade`.
2. **`ListaComprasService` / `ListaComprasMapper`** — preencher `unidade` a partir de `RefeicaoIngrediente` ao consolidar (cuidado ao fazer merge de quantidades com mesma unidade).
3. **`IngredienteItemDTO` + `RefeicaoMapper`** — adicionar `categoria` (`ingrediente.getCategoria().name()`).

No app:

- Ler `unidade` e `categoria` dos JSON.
- **Remover** `IngredienteCatalogoLocal.java`.
- Manter `CategoriaComprasMapeador` e `ComprasQuantidadeFormatter`.

Opcional futuro: **POST/PUT cardápio** para sincronizar plano semanal → `/lista-compras` passa a ser a fonte principal.

---

## Mapeamento de categorias (UI)

| API (`CategoriaIngrediente`) | Label na tela |
|------------------------------|---------------|
| `FRUTAS_E_VEGETAIS` | Frutas e Vegetais |
| `LATICINIOS` | Laticínios |
| `GRAOS_E_CEREAIS` | Grãos e Cereais |
| `PROTEINAS` | Proteínas |
| `OUTROS` | Outros |

Implementado em `CategoriaComprasMapeador.java`.

---

## Autenticação no app

- Token e `userId` em `SharedPreferences` (`auth`).
- Header: `ApiAuthHeaders.bearerOrNull(context)`.
- Base URL Retrofit: `RetrofitClient` → Railway production.

---

## Decisões da conversa

| Decisão | Detalhe |
|---------|---------|
| Integrar lista de compras | Substituir dados fake em `ComprasFragment` |
| Fallback local | Cardápio em `CardapioLocalStore` + detalhe de refeição |
| Restrição temporária | "Só front-end" → criado `IngredienteCatalogoLocal` |
| Esclarecimento | Backend **tem** dados; API **não expõe tudo** nos DTOs |
| Próximo passo ideal | Pequeno ajuste nos DTOs do backend + remover catálogo local |

---

## Referências no repositório

### Backend (`LINAPROJETO-BACKEND/lina`)

- `controller/ListaComprasController.java`
- `service/ListaComprasService.java`
- `dto/ListaComprasDTO.java`, `dto/ItemListaDTO.java`
- `dto/IngredienteItemDTO.java` (arquivo: `ingredienteItemDTO.java`)
- `mapper/RefeicaoMapper.java`, `mapper/ListaComprasMapper.java`
- `model/CategoriaIngrediente.java`, `model/RefeicaoIngrediente.java`
- Seed: `resources/db/migration/V3__seed_refeicoes.sql`

### Mobile (`app/.../Feature/Compras`)

- Ver tabela de arquivos acima.
- Cardápio: `Feature/Cardapio/CardapioLocalStore.java`

---

## Como testar

1. Login no app.
2. **Criar cardápio** → selecionar refeições → salvar.
3. Aba **Compras** → lista deve carregar (via API vazia + fallback local, ou via API se plano no servidor tiver itens).
4. Logcat: tag `ComprasLista` (quantidade de itens API vs local).

---

*Última atualização: conversa sobre lista de compras, categorias, unidades e catálogo local vs API.*

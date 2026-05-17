# Resumo da conversa — Integração Lina (Mobile + Backend)

Documento gerado a partir do chat sobre erros HTTP 500, lista de compras, criar cardápio e correções no backend.

**Repositórios:**
- Mobile: `LINAPROJETO-MOBILE`
- Backend (cópia local): `LINAPROJETO-BACKEND/lina`
- Backend em produção: `https://linaprojeto-backend-production.up.railway.app/`

---

## 1. Contexto do produto

- App Android monta **cardápio semanal** (7 dias × 4 refeições/dia).
- O cardápio é salvo **no aparelho** (`CardapioLocalStore`); **não há POST** para sincronizar o plano no servidor.
- **Lista de compras** no backend consolida ingredientes do `PlanoSemanal` do usuário.
- Autenticação: JWT em `Authorization: Bearer <token>`.

---

## 2. Problema reportado — HTTP 500 em “Criar cardápio”

### Sintoma no app

Toast: *"Erro ao carregar Café da manhã (HTTP 500)"* ao abrir **Criar cardápio**.

Chamadas feitas pelo app (4 períodos):

```
GET /refeicoes?periodo=CAFE_DA_MANHA&usuarioId=...
GET /refeicoes?periodo=ALMOCO&usuarioId=...
GET /refeicoes?periodo=LANCHE_DA_TARDE&usuarioId=...
GET /refeicoes?periodo=JANTAR&usuarioId=...
```

### Causa 1 — LazyInitializationException (corrigida no código)

`RefeicaoService.listarPorPeriodoEUsuario` mapeava `ingredientesDetalhados` fora de transação.

**Correção:** `@Transactional(readOnly = true)` no service + `JOIN FETCH` de ingredientes no `RefeicaoRepository`.

### Causa 2 — Tabela inexistente (confirmada pelo stack trace)

```
SQLGrammarException: relation "refeicao_informacao_nutricional" does not exist
```

- Entidade `InformacaoNutricional` mapeada para `refeicao_informacao_nutricional`.
- Hibernate tenta carregar nutrição ao listar/buscar refeições (`@OneToOne`).
- **Migration `V4__informacao_nutricional.sql` não existia** no repositório de produção (só V1–V3).

**Correção:** criar migration Flyway V4 e fazer deploy no Railway.

---

## 3. Correção principal — Migration V4

**Arquivo:** `src/main/resources/db/migration/V4__informacao_nutricional.sql`

Cria a tabela com nomes de coluna compatíveis com o Hibernate (`proteinag`, `acucaresg`, `porcao_label`, etc.) e insere seed para **Omelete com Legumes** (`refeicao_id = 1`).

Após deploy, Flyway executa automaticamente (`spring.flyway.enabled=true` em prod).

**Validação no banco:**

```sql
SELECT * FROM refeicao_informacao_nutricional WHERE refeicao_id = 1;
```

---

## 4. Alterações no backend (lista de compras + receita)

### Lista de compras

| Item | Detalhe |
|------|---------|
| `ItemListaDTO` | Campo `unidade` |
| `ListaComprasService` | Consolida por `(Ingrediente, unidade)` |
| Agrupamento | Por `CategoriaIngrediente` |

### Receita / refeições

| Item | Detalhe |
|------|---------|
| `IngredienteItemDTO` | Campo `categoria` (enum em string) |
| `GET /refeicoes/{id}` | Detalhe com nutrição, ingredientes, preparo |
| `RefeicaoRepository.findDetalheById` | `JOIN FETCH` ingredientes + `informacaoNutricional` |
| `RefeicaoService` | `@Transactional` em listagem e detalhe |

### Contrato JSON — lista de compras

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

- **404** se não existir plano para o usuário.
- **[]** se o plano existir mas estiver vazio (comum enquanto o app só grava localmente).

---

## 5. Estado do app Android (front)

**Conclusão: o front está ok** para os fluxos integrados. O HTTP 500 era problema de backend/banco.

| Fluxo | Implementação |
|-------|----------------|
| Criar cardápio | `GET /refeicoes` × 4 períodos, JWT, salva `refeicaoId` |
| Ver receita | `GET /refeicoes/{id}` com nutrição e tags `adequadoPara` |
| Lista de compras | `GET /lista-compras/{usuarioId}` → fallback cardápio local + `GET /refeicoes/{id}` |
| Base URL | `RetrofitClient` → Railway production |

### Limitações conhecidas (não são bug de integração)

1. Cardápio só no celular → `/lista-compras` quase sempre vazio; lista vem do fallback local.
2. Cardápio antigo sem `refeicaoId` → compras usam texto local (categoria “Outros”).
3. `imagemUrl` do Google Drive não é carregada na UI.
4. Nutrição detalhada só no seed da refeição id=1 (omelete); demais mostram kcal do resumo.

### Removido do app

- `IngredienteCatalogoLocal` (workaround estático) — removido após backend passar `categoria` e `unidade`.

---

## 6. Arquivos backend para colar no repositório

### Obrigatório (corrige 500)

- `src/main/resources/db/migration/V4__informacao_nutricional.sql`

### Se ainda não existirem / precisarem atualizar

| Arquivo | Função |
|---------|--------|
| `model/InformacaoNutricional.java` | Entity |
| `model/Refeicao.java` | Campo `informacaoNutricional` |
| `dto/InformacaoNutricionalDTO.java` | JSON nutrição |
| `dto/RefeicaoResponseDTO.java` | Campo `informacoesNutricionais` |
| `dto/IngredienteItemDTO.java` | `categoria`, `unidade`, `texto` |
| `dto/ItemListaDTO.java` | `unidade` |
| `repository/RefeicaoRepository.java` | JOIN FETCH + `findDetalheById` |
| `service/RefeicaoService.java` | `@Transactional` |
| `controller/RefeicaoController.java` | `GET /{id}` |
| `mapper/RefeicaoMapper.java` | Categoria + nutrição |
| `service/ListaComprasService.java` | Chave `(Ingrediente, unidade)` |

---

## 7. Ordem de deploy recomendada

1. Colar `V4__informacao_nutricional.sql` em `db/migration/`
2. Conferir entity `InformacaoNutricional` e relacionamento em `Refeicao`
3. Garantir `RefeicaoService` com `@Transactional` e repository com JOIN FETCH
4. Deploy no Railway
5. Testar com JWT: `GET /refeicoes?periodo=CAFE_DA_MANHA&usuarioId=<id>`
6. Testar no app: **Criar cardápio** e **Ver receita**

---

## 8. Outros pontos técnicos

- Sem `@ControllerAdvice`, `EntidadeNaoEncontradaException` pode retornar **500** em vez de 404.
- Sem token válido: API retorna **403**.
- `findByPeriodo` / `buscarValidas` **não** fazem fetch de nutrição na listagem; o Hibernate ainda pode disparar select na associação `@OneToOne` — por isso a tabela V4 é necessária mesmo para listar.

---

## 9. Pendências futuras (fora do escopo imediato)

- [ ] POST/PUT para sincronizar cardápio semanal no servidor
- [ ] `@ControllerAdvice` para 404 em exceções de negócio
- [ ] Seeds de nutrição para mais refeições além do omelete
- [ ] Carregar imagens (`imagemUrl`) no app
- [ ] Mais dados em `/lista-compras` quando o plano existir no banco

---

## 10. Documentos relacionados no repo

| Arquivo | Conteúdo |
|---------|----------|
| `docs/contexto-backend-chat.md` | Contexto detalhado para chat do backend |
| `docs/contexto-lista-de-compras.md` | Foco lista de compras no app |
| `docs/conversa-ver-receita-backend.md` | Spec da tela Ver receita + migration V4 original |

---

## 11. Linha do tempo da conversa

1. Integração lista de compras (backend passou `categoria` e `unidade`; app removeu catálogo local).
2. Erro HTTP 500 ao abrir Criar cardápio.
3. Diagnóstico: LazyInitialization + tabela `refeicao_informacao_nutricional` ausente.
4. Criação da migration V4 no projeto local.
5. Confirmação: front ok; depende do deploy da V4.
6. Entrega dos códigos backend para colar no repositório remoto.
7. Este resumo em markdown.

---

*Gerado em maio/2026 — conversa Cursor sobre LINAPROJETO-MOBILE + LINAPROJETO-BACKEND.*

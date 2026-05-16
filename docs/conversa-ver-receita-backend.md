# Lina — Conversa: Ver Receita e Backend

**Projeto:** LINAPROJETO-MOBILE / LINAPROJETO-BACKEND  
**Data:** 16 de maio de 2026  
**Assunto:** Análise da tela "Ver receita", adequação do backend e código completo para implementação.

---

## 1. Contexto da UI (mockup)

A tela de detalhe da receita (**Omelete com Legumes**) exibe:

| Bloco | Conteúdo esperado |
|-------|-------------------|
| Cabeçalho | Categoria ("Café da Manhã"), título, imagem |
| Chips | Tempo (15 min), calorias resumo (280 kcal) |
| Adequado para | Tags "Sem Glúten", "Sem Lactose" |
| Ingredientes | Lista com quantidade + unidade + nome |
| Modo de preparo | Texto corrido |
| Informações nutricionais | Porção, macros, subitens (fibras, açúcares, gorduras, minerais) |

**Exemplo de ingredientes na UI:**
- 2 unidades de ovos
- 1/2 xícara de tomate picado
- 1/4 xícara de cebola picada
- 1/4 xícara de pimentão picado
- 1 colher de sopa de azeite
- a gosto de sal

**Exemplo de nutrição detalhada (UI):**
- Para 1 porção
- 198 kcal, 4,9 g proteína, 21,5 g carboidratos (4,2 g fibras, 3,9 g açúcares)
- 10,1 g gordura total (2,3 g saturada, 2,0 g monoinsaturada, 4,1 g poliinsaturada)
- Colesterol < 1 mg, sal < 0,1 g, sódio 28,6 mg, potássio 173,3 mg

---

## 2. Análise do backend ANTES das mudanças

### Endpoint existente

```
GET /refeicoes?periodo={CAFE_DA_MANHA|ALMOCO|...}&usuarioId={id}
```

- Apenas **listagem** — sem `GET /refeicoes/{id}`.
- O app Android buscava na lista e comparava pelo **nome** da receita.

### DTO antigo (`RefeicaoResponseDTO`)

```java
private Long id;
private String nome;
private String imagemUrl;
private Double calorias;
private Integer tempoPreparo;
private List<String> ingredientes;      // só nomes
private List<String> restricoes;
private List<PeriodoDia> periodosPermitidos;
```

**Faltava:** `modoPreparo`, ingredientes formatados, `adequadoPara`, nutrição detalhada, `periodoLabel`.

### O que já existia no banco

| Campo | No banco | Na API |
|-------|----------|--------|
| modo_preparo | Sim | Não |
| imagem_url | Sim | Sim (app não carregava) |
| refeicao_ingrediente (qtd + unidade) | Sim | Não (só nome) |
| Período | Sim | Sim |
| Nutrição detalhada | Não | Não |

### Seed do Omelete (id=1)

- Tempo: **10 min** (UI: 15 min)
- Calorias: **220** (UI: 280 no chip / 198 na nutrição)
- Restrições em `refeicao_restricoes`: apenas **`VEGANO`** (porque contém ovo)

---

## 3. "Adequado para" — existe no backend?

**Resposta: NÃO** como campo dedicado.

### O que existe

Tabela `refeicao_restricoes` = restrições que a refeição **NÃO atende** (filtro de sugestão).

Comentário no seed:
```sql
-- RESTRIÇÕES QUE CADA REFEIÇÃO NÃO ATENDE
-- Se o usuário possui a restrição listada, a refeição NÃO é sugerida.
```

Para **Omelete com Legumes**: só `VEGANO` (tem ovo). **Não** bloqueia celíaco nem lactose.

### No app Android

- Layout tem o rótulo **"Adequado para:"**.
- Tags **"Sem Glúten"** e **"Sem Lactose"** eram **hardcoded** no `RefeicaoAdapter`.
- Ao buscar do backend, `ReceitaActivity` procurava "gluten"/"lactose" em `restricoes` — mas a API devolvia `VEGANO`, então as tags viravam **"—"**.

### Solução implementada

Campo calculado **`adequadoPara`**: se `CELIACO` **não** está nos bloqueios → "Sem Glúten"; se `LACTOSE` **não** está → "Sem Lactose"; etc.

Para o Omelete: `adequadoPara: ["Sem Glúten", "Sem Lactose"]` (sem "Vegano", pois `VEGANO` está nos bloqueios).

---

## 4. Mudanças implementadas no backend

### Arquivos novos

| Arquivo | Descrição |
|---------|-----------|
| `dto/IngredienteItemDTO.java` | quantidade, unidade, nome, texto |
| `dto/InformacaoNutricionalDTO.java` | macros e minerais |
| `model/InformacaoNutricional.java` | entidade JPA 1:1 com Refeicao |
| `db/migration/V4__informacao_nutricional.sql` | tabela + seed do Omelete |

### Arquivos alterados

| Arquivo | Mudança |
|---------|---------|
| `dto/RefeicaoResponseDTO.java` | campos novos |
| `model/Refeicao.java` | relação com InformacaoNutricional |
| `mapper/RefeicaoMapper.java` | formatação ingredientes, adequadoPara, nutrição |
| `repository/RefeicaoRepository.java` | `findDetalheById` com JOIN FETCH |
| `service/RefeicaoService.java` | `buscarPorId` |
| `controller/RefeicaoController.java` | `GET /refeicoes/{id}` |

### Novo endpoint

```
GET /refeicoes/{id}?usuarioId=1&periodo=CAFE_DA_MANHA
```

### Exemplo de resposta JSON

```json
{
  "id": 1,
  "nome": "Omelete com Legumes",
  "imagemUrl": "https://...",
  "calorias": 220.0,
  "tempoPreparo": 10,
  "modoPreparo": "1. Quebre os ovos em uma tigela...",
  "periodo": "CAFE_DA_MANHA",
  "periodoLabel": "Café da Manhã",
  "ingredientes": [
    "2 unidades de ovo",
    "1/2 xícara de tomate",
    "1/4 xícara de cebola",
    "1/4 xícara de pimentão",
    "1 colher de azeite",
    "pitada de sal"
  ],
  "ingredientesDetalhados": [
    {
      "quantidade": 2.0,
      "unidade": "unidades",
      "nome": "Ovo",
      "texto": "2 unidades de ovo"
    }
  ],
  "restricoes": ["VEGANO"],
  "adequadoPara": ["Sem Glúten", "Sem Lactose"],
  "periodosPermitidos": ["CAFE_DA_MANHA"],
  "informacoesNutricionais": {
    "porcoes": 1,
    "porcaoLabel": "Para 1 porção",
    "calorias": 198.0,
    "proteinaG": 4.9,
    "carboidratosG": 21.5,
    "fibrasG": 4.2,
    "acucaresG": 3.9,
    "gorduraTotalG": 10.1,
    "gorduraSaturadaG": 2.3,
    "gorduraMonoinsaturadaG": 2.0,
    "gorduraPoliinsaturadaG": 4.1,
    "colesterolMg": 1.0,
    "salG": 0.1,
    "sodioMg": 28.6,
    "potassioMg": 173.3
  }
}
```

---

## 5. O que fazer no banco de dados

### Opção A — Flyway automático (produção)

1. Colocar `V4__informacao_nutricional.sql` em `lina/src/main/resources/db/migration/`
2. Reiniciar o backend (`spring.flyway.enabled=true`)
3. Flyway cria a tabela e insere o exemplo do Omelete (id=1)

### Opção B — SQL manual (Neon, pgAdmin, DBeaver)

Executar o conteúdo completo do arquivo de migration.

### Conferir

```sql
SELECT * FROM refeicao_informacao_nutricional WHERE refeicao_id = 1;
```

### Inserir outras receitas

Repetir o INSERT alterando `refeicao_id` e os valores nutricionais.

### Após colar o código

```bash
cd lina
mvn clean compile
```

---

## 6. Código completo — IngredienteItemDTO.java

```java
package com.projeto.lina.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteItemDTO {
    private Double quantidade;
    private String unidade;
    private String nome;
    private String texto;
}
```

---

## 7. Código completo — InformacaoNutricionalDTO.java

```java
package com.projeto.lina.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformacaoNutricionalDTO {
    private Integer porcoes;
    private String porcaoLabel;
    private Double calorias;
    private Double proteinaG;
    private Double carboidratosG;
    private Double fibrasG;
    private Double acucaresG;
    private Double gorduraTotalG;
    private Double gorduraSaturadaG;
    private Double gorduraMonoinsaturadaG;
    private Double gorduraPoliinsaturadaG;
    private Double colesterolMg;
    private Double salG;
    private Double sodioMg;
    private Double potassioMg;
}
```

---

## 8. Código completo — InformacaoNutricional.java

```java
package com.projeto.lina.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refeicao_informacao_nutricional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InformacaoNutricional {

    @Id
    @Column(name = "refeicao_id")
    private Long refeicaoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "refeicao_id")
    private Refeicao refeicao;

    private Integer porcoes;
    private String porcaoLabel;
    private Double calorias;
    private Double proteinaG;
    private Double carboidratosG;
    private Double fibrasG;
    private Double acucaresG;
    private Double gorduraTotalG;
    private Double gorduraSaturadaG;
    private Double gorduraMonoinsaturadaG;
    private Double gorduraPoliinsaturadaG;
    private Double colesterolMg;
    private Double salG;
    private Double sodioMg;
    private Double potassioMg;
}
```

---

## 9. Código completo — RefeicaoResponseDTO.java

```java
package com.projeto.lina.dto;

import com.projeto.lina.model.PeriodoDia;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefeicaoResponseDTO {
    private Long id;
    private String nome;
    private String imagemUrl;
    private Double calorias;
    private Integer tempoPreparo;
    private String modoPreparo;
    private PeriodoDia periodo;
    private String periodoLabel;
    private List<String> ingredientes;
    private List<IngredienteItemDTO> ingredientesDetalhados;
    private List<String> restricoes;
    private List<String> adequadoPara;
    private List<PeriodoDia> periodosPermitidos;
    private InformacaoNutricionalDTO informacoesNutricionais;
}
```

---

## 10. Código completo — Refeicao.java

```java
package com.projeto.lina.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Refeicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Integer tempoPreparo;
    private double calorias;
    @Column(length = 2000)
    private String modoPreparo;
    private String imagemUrl;

    @OneToMany(mappedBy = "refeicao", cascade = CascadeType.ALL)
    private Set<RefeicaoIngrediente> ingredientes = new LinkedHashSet<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Restricao> restricoes;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<PeriodoDia> periodosPermitidos;

    @OneToMany(mappedBy = "refeicao")
    private Set<ItemCardapio> itensCardapio = new LinkedHashSet<>();

    @OneToOne(mappedBy = "refeicao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private InformacaoNutricional informacaoNutricional;
}
```

---

## 11. Código completo — RefeicaoMapper.java

Ver arquivo no repositório em:
`LINAPROJETO-BACKEND/lina/src/main/java/com/projeto/lina/mapper/RefeicaoMapper.java`

Funções principais:
- `toDTO` / `toDetalheDTO`
- `formatarIngrediente` — ex.: "2 unidades de ovo", "1/2 xícara de tomate"
- `calcularAdequadoPara` — tags positivas para a UI
- `rotuloPeriodo` — CAFE_DA_MANHA → "Café da Manhã"
- `toNutricaoDTO`

---

## 12. Código completo — RefeicaoRepository.java

```java
package com.projeto.lina.repository;

import com.projeto.lina.model.PeriodoDia;
import com.projeto.lina.model.Refeicao;
import com.projeto.lina.model.Restricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefeicaoRepository extends JpaRepository<Refeicao, Long> {

    @Query("""
    SELECT DISTINCT r FROM Refeicao r
    LEFT JOIN FETCH r.ingredientes ri
    LEFT JOIN FETCH ri.ingrediente
    LEFT JOIN FETCH r.informacaoNutricional
    WHERE r.id = :id
    """)
    Optional<Refeicao> findDetalheById(@Param("id") Long id);

    @Query("""
    SELECT r FROM Refeicao r
    WHERE :periodo MEMBER OF r.periodosPermitidos
    AND NOT EXISTS (
        SELECT res FROM r.restricoes res
        WHERE res IN :restricoesUsuario
    )
    """)
    List<Refeicao> buscarValidas(
            @Param("periodo") PeriodoDia periodo,
            @Param("restricoesUsuario") List<Restricao> restricoesUsuario
    );

    @Query("SELECT r FROM Refeicao r WHERE :periodo MEMBER OF r.periodosPermitidos")
    List<Refeicao> findByPeriodo(PeriodoDia periodo);
}
```

---

## 13. Código completo — RefeicaoService.java

```java
package com.projeto.lina.service;

import com.projeto.lina.exception.EntidadeNaoEncontradaException;
import com.projeto.lina.model.*;
import com.projeto.lina.repository.*;
import com.projeto.lina.dto.RefeicaoResponseDTO;
import com.projeto.lina.mapper.RefeicaoMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RefeicaoService {

    private final RefeicaoRepository refeicaoRepository;
    private final UsuarioRepository usuarioRepository;

    public RefeicaoService(RefeicaoRepository refeicaoRepository,
                           UsuarioRepository usuarioRepository) {
        this.refeicaoRepository = refeicaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<RefeicaoResponseDTO> listarPorPeriodoEUsuario(
            PeriodoDia periodo,
            Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));

        List<Refeicao> refeicoes;

        if (usuario.getRestricoes() == null || usuario.getRestricoes().isEmpty()) {
            refeicoes = refeicaoRepository.findByPeriodo(periodo);
        } else {
            refeicoes = refeicaoRepository.buscarValidas(
                    periodo,
                    usuario.getRestricoes()
            );
        }

        return refeicoes.stream()
                .map(RefeicaoMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public RefeicaoResponseDTO buscarPorId(Long id, Long usuarioId, PeriodoDia periodo) {
        if (usuarioId != null) {
            usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        }

        Refeicao refeicao = refeicaoRepository.findDetalheById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Refeição não encontrada"));

        PeriodoDia periodoResolvido = resolverPeriodo(refeicao, periodo);
        return RefeicaoMapper.toDetalheDTO(refeicao, periodoResolvido);
    }

    private static PeriodoDia resolverPeriodo(Refeicao refeicao, PeriodoDia periodoInformado) {
        if (periodoInformado != null) {
            return periodoInformado;
        }
        List<PeriodoDia> permitidos = refeicao.getPeriodosPermitidos();
        if (permitidos != null && !permitidos.isEmpty()) {
            return permitidos.get(0);
        }
        return null;
    }
}
```

---

## 14. Código completo — RefeicaoController.java

```java
package com.projeto.lina.controller;

import com.projeto.lina.dto.RefeicaoResponseDTO;
import com.projeto.lina.model.PeriodoDia;
import com.projeto.lina.service.RefeicaoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refeicoes")
public class RefeicaoController {

    private final RefeicaoService service;

    public RefeicaoController(RefeicaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<RefeicaoResponseDTO> listar(
            @RequestParam PeriodoDia periodo,
            @RequestParam Long usuarioId) {

        return service.listarPorPeriodoEUsuario(periodo, usuarioId);
    }

    @GetMapping("/{id}")
    public RefeicaoResponseDTO buscar(
            @PathVariable Long id,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) PeriodoDia periodo) {

        return service.buscarPorId(id, usuarioId, periodo);
    }
}
```

---

## 15. Migration SQL — V4__informacao_nutricional.sql

```sql
CREATE TABLE refeicao_informacao_nutricional (
    refeicao_id                  BIGINT PRIMARY KEY REFERENCES refeicao(id) ON DELETE CASCADE,
    porcoes                      INTEGER NOT NULL DEFAULT 1,
    porcao_label                 VARCHAR(100),
    calorias                     DOUBLE PRECISION,
    proteina_g                   DOUBLE PRECISION,
    carboidratos_g               DOUBLE PRECISION,
    fibras_g                     DOUBLE PRECISION,
    acucares_g                   DOUBLE PRECISION,
    gordura_total_g              DOUBLE PRECISION,
    gordura_saturada_g           DOUBLE PRECISION,
    gordura_monoinsaturada_g     DOUBLE PRECISION,
    gordura_poliinsaturada_g     DOUBLE PRECISION,
    colesterol_mg                DOUBLE PRECISION,
    sal_g                        DOUBLE PRECISION,
    sodio_mg                     DOUBLE PRECISION,
    potassio_mg                  DOUBLE PRECISION
);

INSERT INTO refeicao_informacao_nutricional (
    refeicao_id, porcoes, porcao_label, calorias,
    proteina_g, carboidratos_g, fibras_g, acucares_g,
    gordura_total_g, gordura_saturada_g, gordura_monoinsaturada_g, gordura_poliinsaturada_g,
    colesterol_mg, sal_g, sodio_mg, potassio_mg
) VALUES (
    1, 1, 'Para 1 porção', 198,
    4.9, 21.5, 4.2, 3.9,
    10.1, 2.3, 2.0, 4.1,
    1, 0.1, 28.6, 173.3
);
```

---

## 16. Lacunas que ainda podem existir no app Android

- `ReceitaActivity` ainda usa lista por nome em vez de `GET /refeicoes/{id}`
- Imagem (`imagemUrl`) não é carregada na tela
- Bloco nutricional hierárquico da UI não é montado a partir de `informacoesNutricionais`
- Tags "Sem Glúten" / "Sem Lactose" hardcoded em vários adapters

**Próximo passo sugerido:** atualizar o app para chamar o novo endpoint de detalhe.

---

## 17. Resumo executivo

| Item | Status |
|------|--------|
| Análise UI vs backend | Concluída |
| Campo "Adequado para" | Calculado como `adequadoPara` |
| Modo de preparo na API | Incluído |
| Ingredientes formatados | Incluído |
| Nutrição detalhada | Tabela + DTO + endpoint detalhe |
| GET /refeicoes/{id} | Novo |
| Migration V4 | Criar no Flyway ou SQL manual |

---

*Documento gerado a partir da conversa no Cursor — projeto Lina.*

package com.example.projeto.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.projeto.Feature.Cardapio.CardapioItemPersistido;
import com.example.projeto.Feature.Cardapio.Prato;
import com.example.projeto.Feature.Compras.models.ComprasIngrediente;
import com.example.projeto.Feature.CriarCardapio.CriarCardapioRefeicao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BancoHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "lina_cardapio.db";
    private static final int VERSAO = 1;
    private static final String[] DIAS = {
            "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"
    };

    public BancoHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE receitas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "periodo TEXT NOT NULL," +
                "nome TEXT NOT NULL," +
                "tempo INTEGER NOT NULL," +
                "calorias INTEGER NOT NULL," +
                "restricoes TEXT NOT NULL," +
                "ingredientes_texto TEXT NOT NULL," +
                "preparo TEXT NOT NULL," +
                "nutricional TEXT NOT NULL)");

        db.execSQL("CREATE TABLE ingredientes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "receita_id INTEGER NOT NULL," +
                "nome TEXT NOT NULL," +
                "quantidade REAL NOT NULL," +
                "unidade TEXT NOT NULL," +
                "categoria TEXT NOT NULL)");

        db.execSQL("CREATE TABLE cardapio (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "dia INTEGER NOT NULL," +
                "receita_id INTEGER NOT NULL)");

        inserirReceitasIniciais(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cardapio");
        db.execSQL("DROP TABLE IF EXISTS ingredientes");
        db.execSQL("DROP TABLE IF EXISTS receitas");
        onCreate(db);
    }

    public List<CriarCardapioRefeicao> listarOpcoesCriarCardapio() {
        List<CriarCardapioRefeicao> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, periodo, nome, tempo, calorias, ingredientes_texto, preparo " +
                "FROM receitas ORDER BY CASE periodo " +
                "WHEN 'Café da Manhã' THEN 0 WHEN 'Almoço' THEN 1 " +
                "WHEN 'Lanche da tarde' THEN 2 WHEN 'Jantar' THEN 3 ELSE 4 END, id", null);
        try {
            while (c.moveToNext()) {
                lista.add(new CriarCardapioRefeicao(
                        c.getLong(0),
                        c.getString(1),
                        c.getString(2),
                        c.getInt(3) + " min",
                        c.getInt(4) + " kcal",
                        c.getString(5),
                        c.getString(6)));
            }
        } finally {
            c.close();
        }
        return lista;
    }

    public List<Prato> listarPratosPorPeriodo(String periodo, String nomeIgnorado) {
        List<Prato> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, nome, tempo, calorias, ingredientes_texto, preparo " +
                        "FROM receitas WHERE periodo = ? ORDER BY id",
                new String[]{periodo});
        try {
            while (c.moveToNext()) {
                String nome = c.getString(1);
                if (nomeIgnorado != null && nomeIgnorado.equalsIgnoreCase(nome)) {
                    continue;
                }
                lista.add(new Prato(nome, c.getString(4), c.getString(5),
                        c.getInt(3), c.getInt(2), c.getLong(0)));
            }
        } finally {
            c.close();
        }
        return lista;
    }

    public ReceitaDetalhe buscarReceita(long id, String periodo, String nome) {
        SQLiteDatabase db = getReadableDatabase();
        List<String> args = new ArrayList<>();
        String where;
        if (id > 0) {
            where = "id = ?";
            args.add(String.valueOf(id));
        } else {
            where = "nome = ?";
            args.add(nome != null ? nome : "");
            if (periodo != null && !periodo.trim().isEmpty()) {
                where += " AND periodo = ?";
                args.add(periodo);
            }
        }

        Cursor c = db.rawQuery("SELECT id, periodo, nome, tempo, calorias, restricoes, " +
                        "ingredientes_texto, preparo, nutricional FROM receitas WHERE " + where + " LIMIT 1",
                args.toArray(new String[0]));
        try {
            if (!c.moveToFirst()) {
                return null;
            }
            return new ReceitaDetalhe(
                    c.getLong(0), c.getString(1), c.getString(2), c.getInt(3),
                    c.getInt(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8));
        } finally {
            c.close();
        }
    }

    public void salvarSemana(List<List<CardapioItemPersistido>> semana) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("cardapio", null, null);
            for (int dia = 0; dia < semana.size(); dia++) {
                List<CardapioItemPersistido> itens = semana.get(dia);
                if (itens == null) continue;
                for (CardapioItemPersistido item : itens) {
                    long receitaId = item.refeicaoId != null ? item.refeicaoId : buscarIdReceita(db, item.tipo, item.nome);
                    if (receitaId <= 0) continue;
                    ContentValues cv = new ContentValues();
                    cv.put("dia", dia);
                    cv.put("receita_id", receitaId);
                    db.insert("cardapio", null, cv);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<List<CardapioItemPersistido>> carregarSemana() {
        List<List<CardapioItemPersistido>> semana = semanaVazia();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT c.dia, r.id, r.periodo, r.nome, r.tempo, r.calorias, " +
                "r.ingredientes_texto, r.preparo FROM cardapio c " +
                "JOIN receitas r ON r.id = c.receita_id ORDER BY c.dia, " +
                "CASE r.periodo WHEN 'Café da Manhã' THEN 0 WHEN 'Almoço' THEN 1 " +
                "WHEN 'Lanche da tarde' THEN 2 WHEN 'Jantar' THEN 3 ELSE 4 END", null);
        try {
            while (c.moveToNext()) {
                int dia = c.getInt(0);
                if (dia < 0 || dia >= semana.size()) continue;
                semana.get(dia).add(new CardapioItemPersistido(
                        c.getLong(1),
                        c.getString(2),
                        c.getString(3),
                        c.getInt(4) + " min",
                        c.getInt(5) + " kcal",
                        c.getString(6),
                        c.getString(7)));
            }
        } finally {
            c.close();
        }
        return semana;
    }

    public boolean temCardapioSalvo() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM cardapio", null);
        try {
            return c.moveToFirst() && c.getInt(0) > 0;
        } finally {
            c.close();
        }
    }

    public List<ComprasIngrediente> gerarListaDeCompras() {
        Map<String, ComprasIngrediente> mapa = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT i.nome, i.quantidade, i.unidade, i.categoria " +
                "FROM cardapio c JOIN ingredientes i ON i.receita_id = c.receita_id", null);
        try {
            while (c.moveToNext()) {
                mesclar(mapa, c.getString(0), c.getDouble(1), c.getString(2), c.getString(3));
            }
        } finally {
            c.close();
        }
        return new ArrayList<>(mapa.values());
    }

    private static List<List<CardapioItemPersistido>> semanaVazia() {
        List<List<CardapioItemPersistido>> semana = new ArrayList<>();
        for (int i = 0; i < DIAS.length; i++) {
            semana.add(new ArrayList<>());
        }
        return semana;
    }

    private long buscarIdReceita(SQLiteDatabase db, String periodo, String nome) {
        Cursor c = db.rawQuery("SELECT id FROM receitas WHERE periodo = ? AND nome = ? LIMIT 1",
                new String[]{periodo != null ? periodo : "", nome != null ? nome : ""});
        try {
            return c.moveToFirst() ? c.getLong(0) : 0L;
        } finally {
            c.close();
        }
    }

    private static void mesclar(Map<String, ComprasIngrediente> mapa, String nome,
                                double quantidade, String unidade, String categoria) {
        String un = unidade != null ? unidade : "";
        String chave = nome.trim().toLowerCase() + "|" + un.toLowerCase();
        ComprasIngrediente atual = mapa.get(chave);
        if (atual == null) {
            mapa.put(chave, new ComprasIngrediente(nome, quantidade, un, categoria));
        } else {
            atual.setQuantidade(atual.getQuantidade() + quantidade);
        }
    }

    private void inserirReceitasIniciais(SQLiteDatabase db) {
        long omelete = inserirReceita(db, "Café da Manhã", "Omelete com Vegetais", 15, 280,
                "Sem Glúten\nSem Lactose",
                "• 2 unidades de ovos\n• 1/2 xícara de tomate picado\n• 1/4 xícara de cebola picada\n• 1/4 xícara de pimentão picado\n• 1 colher de sopa de azeite\n• Sal a gosto",
                "Bata os ovos em uma tigela. Aqueça o azeite em uma frigideira. Adicione os legumes e refogue. Despeje os ovos e cozinhe até firmar.",
                "Para 1 porção\nCalorias: 280 kcal\nProteína: 18 g\nCarboidratos: 12 g\nFibra alimentar: 4 g\nGorduras totais: 19 g\nSódio: 240 mg");
        inserirIngrediente(db, omelete, "Ovos", 2, "unidades", "Proteínas");
        inserirIngrediente(db, omelete, "Tomate", 0.5, "xícara", "Frutas e Vegetais");
        inserirIngrediente(db, omelete, "Cebola", 0.25, "xícara", "Frutas e Vegetais");
        inserirIngrediente(db, omelete, "Pimentão", 0.25, "xícara", "Frutas e Vegetais");

        long pao = inserirReceita(db, "Café da Manhã", "Pão Integral com Iogurte", 10, 310,
                "Sem Lactose",
                "• 2 fatias de pão integral\n• 1 pote de iogurte grego\n• 1 colher de sopa de chia",
                "Monte o pão integral e sirva com o iogurte e a chia.",
                "Para 1 porção\nCalorias: 310 kcal\nProteína: 17 g\nCarboidratos: 42 g\nGorduras totais: 8 g");
        inserirIngrediente(db, pao, "Pão Integral", 2, "fatias", "Grãos e Cereais");
        inserirIngrediente(db, pao, "Iogurte grego", 1, "pote", "Laticínios");

        long peixeAssado = inserirReceita(db, "Almoço", "Peixe Assado com Batata Doce", 25, 430,
                "Sem Glúten\nSem Lactose",
                "• 1 filé de peixe\n• 1 batata doce média\n• 1 xícara de brócolis\n• 1 colher de sopa de azeite",
                "Tempere o peixe, asse com a batata doce e finalize com brócolis cozido no vapor.",
                "Para 1 porção\nCalorias: 430 kcal\nProteína: 34 g\nCarboidratos: 38 g\nGorduras totais: 14 g");
        inserirIngrediente(db, peixeAssado, "Peixe", 1, "filé", "Proteínas");
        inserirIngrediente(db, peixeAssado, "Batata doce", 1, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, peixeAssado, "Brócolis", 1, "xícara", "Frutas e Vegetais");

        long quinoa = inserirReceita(db, "Almoço", "Quinoa com Legumes", 20, 390,
                "Sem Glúten\nSem Lactose",
                "• 1 xícara de quinoa cozida\n• 1 xícara de legumes\n• 1 colher de sopa de azeite",
                "Cozinhe a quinoa, refogue os legumes e misture tudo ainda quente.",
                "Para 1 porção\nCalorias: 390 kcal\nProteína: 13 g\nCarboidratos: 55 g\nGorduras totais: 12 g");
        inserirIngrediente(db, quinoa, "Quinoa", 1, "xícara", "Grãos e Cereais");
        inserirIngrediente(db, quinoa, "Legumes", 1, "xícara", "Frutas e Vegetais");

        long vitamina = inserirReceita(db, "Lanche da tarde", "Vitamina de Abacate", 10, 280,
                "Sem Glúten",
                "• 1/2 abacate\n• 1 copo de leite de amêndoa\n• 1 colher de sopa de aveia",
                "Bata todos os ingredientes no liquidificador até ficar cremoso.",
                "Para 1 porção\nCalorias: 280 kcal\nProteína: 6 g\nCarboidratos: 24 g\nGorduras totais: 18 g");
        inserirIngrediente(db, vitamina, "Abacate", 0.5, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, vitamina, "Leite de Amêndoa", 1, "copo", "Laticínios");

        long atum = inserirReceita(db, "Lanche da tarde", "Sanduíche Natural de Atum", 12, 320,
                "Sem Lactose",
                "• 2 fatias de pão integral\n• 1 lata de atum\n• 2 folhas de alface\n• 2 rodelas de tomate",
                "Misture o atum, monte o sanduíche com alface e tomate e sirva.",
                "Para 1 porção\nCalorias: 320 kcal\nProteína: 26 g\nCarboidratos: 34 g\nGorduras totais: 9 g");
        inserirIngrediente(db, atum, "Atum em Lata", 1, "lata", "Proteínas");
        inserirIngrediente(db, atum, "Pão Integral", 2, "fatias", "Grãos e Cereais");
        inserirIngrediente(db, atum, "Alface", 2, "folhas", "Frutas e Vegetais");

        long peixeForno = inserirReceita(db, "Jantar", "Peixe ao Forno", 25, 360,
                "Sem Glúten\nSem Lactose",
                "• 1 filé de peixe\n• 1/2 xícara de quinoa\n• 1 xícara de legumes assados",
                "Asse o peixe temperado e sirva com quinoa e legumes.",
                "Para 1 porção\nCalorias: 360 kcal\nProteína: 32 g\nCarboidratos: 29 g\nGorduras totais: 12 g");
        inserirIngrediente(db, peixeForno, "Peixe", 1, "filé", "Proteínas");
        inserirIngrediente(db, peixeForno, "Quinoa", 0.5, "xícara", "Grãos e Cereais");
        inserirIngrediente(db, peixeForno, "Legumes", 1, "xícara", "Frutas e Vegetais");

        long frango = inserirReceita(db, "Jantar", "Patinho com Legumes", 20, 410,
                "Sem Glúten\nSem Lactose",
                "• 1 porção de patinho moído\n• 1 xícara de legumes\n• 1/2 xícara de arroz integral",
                "Refogue o patinho, acrescente legumes e sirva com arroz integral.",
                "Para 1 porção\nCalorias: 410 kcal\nProteína: 33 g\nCarboidratos: 36 g\nGorduras totais: 14 g");
        inserirIngrediente(db, frango, "Patinho", 1, "porção", "Proteínas");
        inserirIngrediente(db, frango, "Legumes", 1, "xícara", "Frutas e Vegetais");
    }

    private long inserirReceita(SQLiteDatabase db, String periodo, String nome, int tempo, int calorias,
                                String restricoes, String ingredientes, String preparo, String nutricional) {
        ContentValues cv = new ContentValues();
        cv.put("periodo", periodo);
        cv.put("nome", nome);
        cv.put("tempo", tempo);
        cv.put("calorias", calorias);
        cv.put("restricoes", restricoes);
        cv.put("ingredientes_texto", ingredientes);
        cv.put("preparo", preparo);
        cv.put("nutricional", nutricional);
        return db.insert("receitas", null, cv);
    }

    private void inserirIngrediente(SQLiteDatabase db, long receitaId, String nome,
                                    double quantidade, String unidade, String categoria) {
        ContentValues cv = new ContentValues();
        cv.put("receita_id", receitaId);
        cv.put("nome", nome);
        cv.put("quantidade", quantidade);
        cv.put("unidade", unidade);
        cv.put("categoria", categoria);
        db.insert("ingredientes", null, cv);
    }

    public static class ReceitaDetalhe {
        public final long id;
        public final String periodo;
        public final String nome;
        public final int tempo;
        public final int calorias;
        public final String restricoes;
        public final String ingredientes;
        public final String preparo;
        public final String nutricional;

        ReceitaDetalhe(long id, String periodo, String nome, int tempo, int calorias,
                       String restricoes, String ingredientes, String preparo, String nutricional) {
            this.id = id;
            this.periodo = periodo;
            this.nome = nome;
            this.tempo = tempo;
            this.calorias = calorias;
            this.restricoes = restricoes;
            this.ingredientes = ingredientes;
            this.preparo = preparo;
            this.nutricional = nutricional;
        }
    }
}

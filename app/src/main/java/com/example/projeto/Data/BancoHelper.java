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
    private static final int VERSAO = 4;
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
                "WHEN 'Lanche da Tarde' THEN 2 WHEN 'Jantar' THEN 3 ELSE 4 END, id", null);
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
                "WHEN 'Lanche da Tarde' THEN 2 WHEN 'Jantar' THEN 3 ELSE 4 END", null);
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

    // Inserir todas as Refeições Aqui
    private void inserirReceitasIniciais(SQLiteDatabase db) {

        //======================================== CAFÉ DA MANHÃ ========================================

        long omelete = inserirReceita(db,
                "Café da Manhã",
                "Omelete com Legumes",
                10, 220,
                "Sem Glúten\nSem Lactose",

                "- 2 ovos\n" +
                        "- 1/2 xícara de tomate\n" +
                        "- 1/4 xícara de cebola\n" +
                        "- 1/4 xícara de pimentão\n" +
                        "- 1 colher de azeite\n" +
                        "- Sal a gosto",

                "1. Quebre os ovos em uma tigela.\n" +
                        "2. Bata até ficar homogêneo.\n" +
                        "3. Aqueça o azeite.\n" +
                        "4. Refogue os legumes.\n" +
                        "5. Adicione os ovos.\n" +
                        "6. Cozinhe até firmar.\n" +
                        "7. Sirva.",

                "- Calorias: 220 kcal;\n" +
                        "- Proteína: 14 g;\n" +
                        "- Carboidratos: 6 g;\n" +
                        "- Fibra: 2 g;\n" +
                        "- Açúcares: 3 g;\n" +
                        "- Gorduras: 15 g;\n" +
                        "- Sat: 3 g;\n" +
                        "- Mono: 6 g;\n" +
                        "- Poli: 2 g;\n" +
                        "- Colesterol: 370 mg;\n" +
                        "- Sal: 0,4 g;\n" +
                        "- Sódio: 160 mg;\n" +
                        "- Potássio: 300 mg");

        inserirIngrediente(db, omelete, "Ovos", 2, "unidades", "Proteínas");
        inserirIngrediente(db, omelete, "Tomate", 1, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, omelete, "Cebola", 1, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, omelete, "Pimentão", 1, "unidade", "Outros");
        inserirIngrediente(db, omelete, "Azeite", 1, "unidade", "Outros");

        long iogurte = inserirReceita(db,
                "Café da Manhã",
                "Iogurte com Banana e Chia",
                5, 250,
                "Sem Glúten",

                "- 1 pote de iogurte\n" +
                        "- 1 banana\n" +
                        "- 1 colher de chia",

                "1. Coloque o iogurte na tigela.\n" +
                        "2. Corte a banana.\n" +
                        "3. Adicione ao iogurte.\n" +
                        "4. Acrescente a chia.\n" +
                        "5. Misture e sirva.",

                "- Calorias: 180 kcal;\n" +
                        "- Proteína: 8 g;\n" +
                        "- Carboidratos: 20 g;\n" +
                        "- Fibra: 4 g;\n" +
                        "- Açúcares: 12 g;\n" +
                        "- Gorduras: 6 g;\n" +
                        "- Sat: 2 g;\n" +
                        "- Mono: 1,5 g;\n" +
                        "- Poli: 2,5 g;\n" +
                        "- Colesterol: 10 mg;\n" +
                        "- Sal: 0,1 g;\n" +
                        "- Sódio: 60 mg;\n" +
                        "- Potássio: 350 mg");

        inserirIngrediente(db, iogurte, "Iogurte", 2, "unidades", "Proteínas");
        inserirIngrediente(db, iogurte, "Banana", 1, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, iogurte, "Chia", 1, "unidade", "Grãos e Cereais");

        long tapiocaCafe = inserirReceita(db,
                "Café da Manhã",
                "Tapioca com Queijo",
                8, 250,
                "Sem Glúten",

                "- 2 colheres de tapioca\n" +
                        "- 1 fatia de queijo",

                "1. Aqueça a frigideira.\n" +
                        "2. Espalhe a tapioca.\n" +
                        "3. Aguarde firmar.\n" +
                        "4. Adicione o queijo.\n" +
                        "5. Dobre.\n" +
                        "6. Sirva.",

                "- Calorias: 250 kcal;\n" +
                        "- Proteína: 10 g;\n" +
                        "- Carboidratos: 30 g;\n" +
                        "- Fibra: 1 g;\n" +
                        "- Açúcares: 2 g;\n" +
                        "- Gorduras: 10 g;\n" +
                        "- Sat: 5 g;\n" +
                        "- Mono: 3 g;\n" +
                        "- Poli: 1 g;\n" +
                        "- Colesterol: 25 mg;\n" +
                        "- Sal: 0,5 g;\n" +
                        "- Sódio: 200 mg;\n" +
                        "- Potássio: 120 mg");

        inserirIngrediente(db, tapiocaCafe, "Tapioca", 1, "unidade", "Outros");
        inserirIngrediente(db, tapiocaCafe, "Queijo", 1, "unidade", "Proteínas");

        long vitamina = inserirReceita(db,
                "Café da Manhã",
                "Vitamina de Banana",
                5, 210,
                "Sem Glúten",

                "- 1 banana\n" +
                        "- 200 ml de leite\n" +
                        "- 2 colheres de aveia",

                "1. Corte a banana.\n" +
                        "2. Coloque no liquidificador.\n" +
                        "3. Adicione leite e aveia.\n" +
                        "4. Bata por 1 minuto.\n" +
                        "5. Sirva.",

                "- Calorias: 210 kcal;\n" +
                        "- Proteína: 6 g;\n" +
                        "- Carboidratos: 32 g;\n" +
                        "- Fibra: 5 g;\n" +
                        "- Açúcares: 14 g;\n" +
                        "- Gorduras: 5 g;\n" +
                        "- Sat: 2 g;\n" +
                        "- Mono: 1,5 g;\n" +
                        "- Poli: 1,5 g;\n" +
                        "- Colesterol: 10 mg;\n" +
                        "- Sal: 0,1 g;\n" +
                        "- Sódio: 80 mg;\n" +
                        "- Potássio: 400 mg");

        inserirIngrediente(db, vitamina, "Banana", 1, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, vitamina, "Leite", 200, "unidade", "Outros");
        inserirIngrediente(db, vitamina, "Aveia", 1, "unidade", "Grãos e Cereais");

        //======================================== ALMOÇO ========================================

        long frango = inserirReceita(db,
                "Almoço",
                "Frango e Arroz",
                25, 400,
                "Sem Glúten\nSem Lactose",

                "- 100 g de frango\n" +
                        "- 1/2 xícara de arroz\n" +
                        "- 1 xícara de salada\n" +
                        "- 1 colher de azeite",

                "1. Tempere o frango.\n" +
                        "2. Grelhe por 5–7 min cada lado.\n" +
                        "3. Cozinhe o arroz.\n" +
                        "4. Prepare a salada.\n" +
                        "5. Monte o prato.",

                "- Calorias: 400 kcal;\n" +
                        "- Proteína: 35 g;\n" +
                        "- Carboidratos: 40 g;\n" +
                        "- Fibra: 3 g;\n" +
                        "- Açúcares: 4 g;\n" +
                        "- Gorduras: 10 g;\n" +
                        "- Sat: 2 g;\n" +
                        "- Mono: 5 g;\n" +
                        "- Poli: 2 g;\n" +
                        "- Colesterol: 80 mg;\n" +
                        "- Sal: 0,6 g;\n" +
                        "- Sódio: 240 mg;\n" +
                        "- Potássio: 450 mg");

        inserirIngrediente(db, frango, "Frango", 100, "gramas", "Proteínas");
        inserirIngrediente(db, frango, "Arroz", 1, "unidade", "Grãos e Cereais");
        inserirIngrediente(db, frango, "Salada", 1, "unidade", "Frutas e Vegetais");

        long carne = inserirReceita(db,
                "Almoço",
                "Carne com Batata Doce",
                30, 420,
                "Sem Glúten\nSem Lactose",

                "- 100 g de carne\n" +
                        "- 1 batata doce",

                "1. Corte a batata.\n" +
                        "2. Cozinhe por 15 min.\n" +
                        "3. Tempere a carne.\n" +
                        "4. Grelhe por 4–6 min.\n" +
                        "5. Sirva.",

                "- Calorias: 420 kcal;\n" +
                        "- Proteína: 30 g;\n" +
                        "- Carboidratos: 35 g;\n" +
                        "- Fibra: 4 g;\n" +
                        "- Açúcares: 6 g;\n" +
                        "- Gorduras: 15 g;\n" +
                        "- Sat: 5 g;\n" +
                        "- Mono: 6 g;\n" +
                        "- Poli: 1 g;\n" +
                        "- Colesterol: 90 mg;\n" +
                        "- Sal: 0,5 g;\n" +
                        "- Sódio: 220 mg;\n" +
                        "- Potássio: 600 mg");

        inserirIngrediente(db, carne, "Carne", 100, "gramas", "Proteínas");
        inserirIngrediente(db, carne, "Batata Doce", 1, "unidade", "Frutas e Vegetais");

        long peixe = inserirReceita(db,
                "Almoço",
                "Peixe com Legumes",
                25, 350,
                "Sem Glúten\nSem Lactose",

                "- 1 filé de peixe\n" +
                        "- 1 xícara de legumes\n" +
                        "- 1 colher de azeite",

                "1. Tempere o peixe.\n" +
                        "2. Corte os legumes.\n" +
                        "3. Coloque na assadeira.\n" +
                        "4. Asse por 20 min.\n" +
                        "5. Sirva.",

                "- Calorias: 350 kcal;\n" +
                        "- Proteína: 28 g;\n" +
                        "- Carboidratos: 15 g;\n" +
                        "- Fibra: 3 g;\n" +
                        "- Açúcares: 5 g;\n" +
                        "- Gorduras: 12 g;\n" +
                        "- Sat: 2 g;\n" +
                        "- Mono: 6 g;\n" +
                        "- Poli: 2 g;\n" +
                        "- Colesterol: 70 mg;\n" +
                        "- Sal: 0,4 g;\n" +
                        "- Sódio: 180 mg;\n" +
                        "- Potássio: 500 mg");

        inserirIngrediente(db, peixe, "Filé de Peixe", 100, "gramas", "Proteínas");
        inserirIngrediente(db, peixe, "Legumes", 1, "unidade", "Frutas e Vegetais");

        long omeletCompleto = inserirReceita(db,
                "Almoço",
                "Omelete Completo",
                15, 300,
                "Sem Glúten",

                "- 3 ovos\n" +
                        "- 1/2 xícara de legumes\n" +
                        "- 1 colher de azeite",

                "1. Bata os ovos.\n" +
                        "2. Refogue os legumes.\n" +
                        "3. Adicione os ovos.\n" +
                        "4. Cozinhe até firmar.\n" +
                        "5. Sirva.",

                "- Calorias: 300 kcal;\n" +
                        "- Proteína: 20 g;\n" +
                        "- Carboidratos: 8 g;\n" +
                        "- Fibra: 2 g;\n" +
                        "- Açúcares: 3 g;\n" +
                        "- Gorduras: 22 g;\n" +
                        "- Sat: 5 g;\n" +
                        "- Mono: 10 g;\n" +
                        "- Poli: 3 g;\n" +
                        "- Colesterol: 550 mg;\n" +
                        "- Sal: 0,5 g;\n" +
                        "- Sódio: 210 mg;\n" +
                        "- Potássio: 320 mg");

        inserirIngrediente(db, omeletCompleto, "Ovos", 3, "unidades", "Proteínas");
        inserirIngrediente(db, omeletCompleto, "Legumes", 1, "unidade", "Frutas e Vegetais");

        //======================================== LANCHE DA TARDE ========================================

        long mixCastanhas = inserirReceita(db,
                "Lanche da Tarde",
                "Mix de Castanhas",
                2, 180,
                "Sem Glúten\nSem Lactose",

                "- 30 g de castanhas",

                "1. Separe a porção.\n" +
                        "2. Consuma diretamente.",

                "- Calorias: 180 kcal;\n" +
                        "- Proteína: 5 g;\n" +
                        "- Carboidratos: 6 g;\n" +
                        "- Fibra: 2 g;\n" +
                        "- Açúcares: 1 g;\n" +
                        "- Gorduras: 15 g;\n" +
                        "- Sódio: 5 mg");

        inserirIngrediente(db, mixCastanhas, "Castanhas", 30, "gramas", "Outros");

        long macaAmendoim = inserirReceita(db,
                "Lanche da Tarde",
                "Maçã com Pasta de Amendoim",
                5, 200,
                "Sem Glúten\nSem Lactose",

                "- 1 maçã\n" +
                        "- 1 colher de pasta de amendoim",

                "1. Lave a maçã.\n" +
                        "2. Corte em fatias.\n" +
                        "3. Retire o miolo.\n" +
                        "4. Adicione a pasta de amendoim.\n" +
                        "5. Sirva.",

                "- Calorias: 200 kcal;\n" +
                        "- Proteína: 4 g;\n" +
                        "- Carboidratos: 22 g;\n" +
                        "- Fibra: 4 g;\n" +
                        "- Açúcares: 16 g;\n" +
                        "- Gorduras: 10 g;\n" +
                        "- Sódio: 50 mg");

        inserirIngrediente(db, macaAmendoim, "Maçã", 1, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, macaAmendoim, "Pasta de Amendoim", 1, "unidade", "Outros");

        long iogurteGranola = inserirReceita(db,
                "Lanche da Tarde",
                "Iogurte com Granola",
                3, 190,
                "Sem Glúten",

                "- 1 iogurte\n" +
                        "- 2 colheres de granola",

                "1. Coloque o iogurte.\n" +
                        "2. Adicione a granola.\n" +
                        "3. Misture.\n" +
                        "4. Sirva.",

                "- Calorias: 190 kcal;\n" +
                        "- Proteína: 7 g;\n" +
                        "- Carboidratos: 25 g;\n" +
                        "- Fibra: 3 g;\n" +
                        "- Açúcares: 12 g;\n" +
                        "- Gorduras: 6 g;\n" +
                        "- Sódio: 70 mg");

        inserirIngrediente(db, iogurteGranola, "Iogurte", 1, "unidade", "Proteínas");
        inserirIngrediente(db, iogurteGranola, "Granola", 1, "unidade", "Grãos e Cereais");

        long smoothie = inserirReceita(db,
                "Lanche da Tarde",
                "Smoothie",
                5, 160,
                "Sem Glúten\nSem Lactose",

                "- 1 xícara de frutas\n" +
                        "- 200 ml de líquido",

                "1. Corte as frutas.\n" +
                        "2. Coloque no liquidificador.\n" +
                        "3. Adicione o líquido.\n" +
                        "4. Bata.\n" +
                        "5. Sirva.",

                "- Calorias: 160 kcal;\n" +
                        "- Proteína: 3 g;\n" +
                        "- Carboidratos: 30 g;\n" +
                        "- Fibra: 3 g;\n" +
                        "- Açúcares: 18 g;\n" +
                        "- Gorduras: 2 g;\n" +
                        "- Sódio: 40 mg");

        inserirIngrediente(db, smoothie, "Frutas", 1, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, smoothie, "Líquido", 200, "ml", "Outros");

        //======================================== JANTAR ========================================

        long sopaFrango = inserirReceita(db,
                "Jantar",
                "Sopa de Legumes com Frango",
                30, 250,
                "Sem Glúten\nSem Lactose",

                "- 100 g de frango\n" +
                        "- 1 xícara de legumes\n" +
                        "- Água\n" +
                        "- Sal",

                "1. Corte o frango.\n" +
                        "2. Corte os legumes.\n" +
                        "3. Coloque tudo na panela.\n" +
                        "4. Cozinhe por 25 minutos.\n" +
                        "5. Ajuste o sal.",

                "- Calorias: 250 kcal;\n" +
                        "- Proteína: 20 g;\n" +
                        "- Carboidratos: 15 g;\n" +
                        "- Fibra: 3 g;\n" +
                        "- Açúcares: 4 g;\n" +
                        "- Gorduras: 8 g;\n" +
                        "- Sódio: 180 mg");

        inserirIngrediente(db, sopaFrango, "Frango", 100, "gramas", "Proteínas");
        inserirIngrediente(db, sopaFrango, "Legumes", 1, "unidade", "Frutas e Vegetais");

        long saladaProteica = inserirReceita(db,
                "Jantar",
                "Salada Proteica",
                15, 280,
                "Sem Glúten\nSem Lactose",

                "- Folhas verdes\n" +
                        "- 100 g de frango ou 2 ovos\n" +
                        "- 1 colher de azeite",

                "1. Lave as folhas.\n" +
                        "2. Prepare a proteína.\n" +
                        "3. Misture tudo.\n" +
                        "4. Tempere.\n" +
                        "5. Sirva.",

                "- Calorias: 280 kcal;\n" +
                        "- Proteína: 18 g;\n" +
                        "- Carboidratos: 10 g;\n" +
                        "- Fibra: 3 g;\n" +
                        "- Açúcares: 3 g;\n" +
                        "- Gorduras: 18 g;\n" +
                        "- Sódio: 150 mg");

        inserirIngrediente(db, saladaProteica, "Folhas Verdes", 1, "unidade", "Frutas e Vegetais");
        inserirIngrediente(db, saladaProteica, "Frango", 100, "gramas", "Proteínas");

        long omeleteLeve = inserirReceita(db,
                "Jantar",
                "Omelete Leve",
                10, 220,
                "Sem Glúten\nSem Lactose",

                "- 2 ovos\n" +
                        "- 1 pitada de sal",

                "1. Quebre os ovos.\n" +
                        "2. Bata bem.\n" +
                        "3. Aqueça a frigideira.\n" +
                        "4. Despeje os ovos.\n" +
                        "5. Cozinhe até firmar.\n" +
                        "6. Sirva.",

                "- Calorias: 220 kcal;\n" +
                        "- Proteína: 14 g;\n" +
                        "- Carboidratos: 2 g;\n" +
                        "- Fibra: 0 g;\n" +
                        "- Açúcares: 1 g;\n" +
                        "- Gorduras: 16 g;\n" +
                        "- Sódio: 140 mg");

        inserirIngrediente(db, omeleteLeve, "Ovos", 2, "unidades", "Proteínas");

        long tapiocaJantar = inserirReceita(db,
                "Jantar",
                "Tapioca",
                10, 240,
                "Sem Glúten",

                "- 2 colheres de tapioca\n" +
                        "- Recheio a gosto",

                "1. Aqueça a frigideira.\n" +
                        "2. Espalhe a tapioca.\n" +
                        "3. Aguarde firmar.\n" +
                        "4. Adicione o recheio.\n" +
                        "5. Dobre.\n" +
                        "6. Sirva.",

                "- Calorias: 240 kcal;\n" +
                        "- Proteína: 12 g;\n" +
                        "- Carboidratos: 35 g;\n" +
                        "- Fibra: 1 g;\n" +
                        "- Açúcares: 2 g;\n" +
                        "- Gorduras: 8 g;\n" +
                        "- Sódio: 180 mg");

        inserirIngrediente(db, tapiocaJantar, "Tapioca", 2, "colheres", "Outros");
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
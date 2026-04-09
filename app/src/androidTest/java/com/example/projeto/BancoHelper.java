package com.example.projeto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BancoHelper extends SQLiteOpenHelper {

    public BancoHelper(Context context) {
        super(context, "nutricionistas.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ✅ Criar tabela
        db.execSQL("CREATE TABLE nutricionistas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT," +
                "especialidade TEXT," +
                "cidade TEXT," +
                "telefone TEXT)");

        // ✅ Inserir dados UMA VEZ só
        db.execSQL("INSERT INTO nutricionistas VALUES (null,'Dra. Mariana Costa','Intolerâncias Alimentares','Belo Horizonte, MG','(31) 99876-5432')");
        db.execSQL("INSERT INTO nutricionistas VALUES (null,'Dr. João Silva','Nutrição Esportiva','São Paulo, SP','(11) 91234-5678')");
        db.execSQL("INSERT INTO nutricionistas VALUES (null,'Dra. Ana Souza','Reeducação Alimentar','Sorocaba, SP','(15) 99888-7777')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS nutricionistas");
        onCreate(db);
    }

    // ✅ MÉTODO LISTAR (SEM ERRO)
    public ArrayList<Nutricionista> listar() {

        ArrayList<Nutricionista> lista = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM nutricionistas", null);

        while (cursor.moveToNext()) {
            lista.add(new Nutricionista(
                    cursor.getString(1), // nome
                    cursor.getString(2), // especialidade
                    cursor.getString(3), // cidade
                    cursor.getString(4)  // telefone
            ));
        }

        cursor.close();
        return lista;
    }
}
package com.example.projeto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BancoHelper extends SQLiteOpenHelper {

    public BancoHelper(Context context) {
        super(context, "nutricionistas.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE nutricionistas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT," +
                "especialidade TEXT," +
                "cidade TEXT," +
                "telefone TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void inserir(Nutricionista n) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("nome", n.nome);
        cv.put("especialidade", n.especialidade);
        cv.put("cidade", n.cidade);
        cv.put("telefone", n.telefone);

        db.insert("nutricionistas", null, cv);
    }

    public ArrayList<Nutricionista> listar() {
        ArrayList<Nutricionista> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM nutricionistas", null);

        if (c != null) {
            while (c.moveToNext()) {
                lista.add(new Nutricionista(
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4)
                ));
            }
            c.close();
        }

        return lista;
    }
}
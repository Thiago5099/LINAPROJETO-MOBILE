package com.example.projeto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.ArrayList;

public class BancoHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "nutricionistas.db";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NUTRICIONISTAS = "nutricionistas";

    public BancoHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NUTRICIONISTAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT," +
                "especialidade TEXT," +
                "cidade TEXT," +
                "telefone TEXT," +
                "email TEXT," +
                "pacientes TEXT," +
                "avaliacao REAL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUTRICIONISTAS);
        onCreate(db);
    }

    // 🔹 INSERIR DADOS
    public void inserir(String nome, String especialidade, String cidade, String telefone,
                        String email, String pacientes,float avaliacao ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("especialidade", especialidade);
        values.put("cidade", cidade);
        values.put("telefone", telefone);
        values.put("email", email);
        values.put("pacientes", pacientes);
        values.put("avaliacao", avaliacao);


        db.insert(TABLE_NUTRICIONISTAS, null, values);
        db.close();
    }

    // 🔹 LISTAR TODOS
    public ArrayList<Nutricionista> listarTodos() {
        ArrayList<Nutricionista> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NUTRICIONISTAS, null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(new Nutricionista(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getFloat(7)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }
}
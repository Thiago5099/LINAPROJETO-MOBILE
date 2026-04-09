package com.example.projeto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

public class BancoHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "nutricionistas.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "nutricionistas";

    public BancoHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT," +
                "especialidade TEXT," +
                "cidade TEXT," +
                "telefone TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long inserir(String nome, String esp, String cidade, String tel) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("nome", nome);
        cv.put("especialidade", esp);
        cv.put("cidade", cidade);
        cv.put("telefone", tel);

        return db.insert(TABLE, null, cv);
    }

    public Cursor listar() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE, null);
    }

    public Cursor buscarPorId(int id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE + " WHERE id=?", new String[]{String.valueOf(id)});
    }

    public int atualizar(int id, String nome, String esp, String cidade, String tel) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("nome", nome);
        cv.put("especialidade", esp);
        cv.put("cidade", cidade);
        cv.put("telefone", tel);

        return db.update(TABLE, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public int excluir(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, "id=?", new String[]{String.valueOf(id)});
    }
}
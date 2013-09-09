/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @author George
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLA_IMAGENES = "imagenes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMAGEN_RUTA = "imagenPath";
    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_FECHA_CREADA = "fechaCreada";
    private static final String DATABASE_NAME = "imagenes.db";
    private static final int DATABASE_VERSION = 1;
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLA_IMAGENES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_IMAGEN_RUTA
            + " text not null, " + COLUMN_TITULO
            + " text, "+ COLUMN_FECHA_CREADA
            + " text);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_IMAGENES);
        onCreate(db);
    }
}

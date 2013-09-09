/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.android.Constantes;
import com.example.android.Imagen;
import com.example.android.util.SQLiteHelper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author George
 */
public class ImagenesDao {
    
    // Database fields
  private SQLiteDatabase database;
  private SQLiteHelper dbHelper;
  private String[] allColumns = { SQLiteHelper.COLUMN_ID,
      SQLiteHelper.COLUMN_IMAGEN_RUTA, SQLiteHelper.COLUMN_TITULO, SQLiteHelper.COLUMN_FECHA_CREADA };

  public ImagenesDao(Context context) {
    dbHelper = new SQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public void createImagen(String ruta, String titulo, String fechaCreada) {
    ContentValues values = new ContentValues();
    values.put(SQLiteHelper.COLUMN_IMAGEN_RUTA, ruta);
    values.put(SQLiteHelper.COLUMN_TITULO, titulo);
    values.put(SQLiteHelper.COLUMN_FECHA_CREADA, fechaCreada.toString());
    long insertId = database.insert(SQLiteHelper.TABLA_IMAGENES, null,
        values);
  }

  public List<Imagen> getAllImagenes() {
    List<Imagen> imagenes = new ArrayList<Imagen>();

    Cursor cursor = database.query(SQLiteHelper.TABLA_IMAGENES,
        allColumns, null, null, null, null, SQLiteHelper.COLUMN_FECHA_CREADA+" DESC");
        
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Imagen imagen = cursorToImagen(cursor);
      imagenes.add(imagen);
      cursor.moveToNext();
      Log.d(Constantes.CUSTOM_LOG_TAG, imagen.getTitulo());
    }
    // Siempre hay que cerrar el cursor
    cursor.close();
    return imagenes;
  }

  private Imagen cursorToImagen(Cursor cursor) {
    
    Imagen imagen = new Imagen();
    imagen.setId(cursor.getLong(0));
    
    String ruta = cursor.getString(1);
    imagen.setImagenPath(ruta);
    
    Log.d(Constantes.CUSTOM_LOG_TAG, ruta);
    
    String titulo = cursor.getString(2);
    
    imagen.setTitulo(titulo);
        
    imagen.setFechaCreada(cursor.getString(3));
        
    return imagen;
  }
}

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import com.example.android.Imagen;
import com.example.android.util.SQLiteHelper;
import java.util.ArrayList;
import java.util.Date;
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
        allColumns, null, null, null, null, null);
    
    Log.d("GEORGE CURSOR", cursor.toString());
    
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Imagen imagen = cursorToImagen(cursor);
      imagenes.add(imagen);
      cursor.moveToNext();
      Log.d("GEORGE FOUND IMAGEN", imagen.getTitulo());
    }
    // Make sure to close the cursor
    cursor.close();
    return imagenes;
  }

  private Imagen cursorToImagen(Cursor cursor) {
    
    Imagen imagen = new Imagen();
    imagen.setId(cursor.getLong(0));
    
    String ruta = cursor.getString(1);
    
    Log.d("GEORGE", ruta);
    
    String titulo = cursor.getString(2);
    
    imagen.setTitulo(titulo);
    
    if(ruta != null){
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Log.d("GEORGE ENV", Environment.getExternalStorageDirectory().toString());
        Bitmap bitmap = BitmapFactory.decodeFile(ruta, options);
        Log.d("GEORGE", bitmap.toString());
        imagen.setImagen(bitmap);        
    }
    
    imagen.setFechaCreada(cursor.getString(3));
    
    Log.d("GEORGE IN CURSOR METHOD", imagen.toString());
    
    return imagen;
  }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.example.android.dao.ImagenesDao;
import com.example.android.util.Parser;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author George
 */
public class CargarImagenes extends Activity {

    private int usuarioId;
    private ImagenesDao imgDao;
    private List<Imagen> imgList;
    private ListView imagenListView;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle bundle = getIntent().getExtras();
        usuarioId = bundle.getInt("usuarioId");
        setContentView(R.layout.misimagenes);
        
        imgList = new ArrayList<Imagen>();
        
        imgDao = new ImagenesDao(this);
        imgDao.open();
        
        //Sacar imagenes de la tarjeta SD
        List<Imagen> imagenes = imgDao.getAllImagenes();
        imgList.addAll(imagenes);
        Log.d("GEORGE Imagenes: ", ""+imagenes.toString());
        
        imgDao.close();
        
        setImagenListView((ListView) findViewById(R.id.imagenesListview));

        final ArrayAdapter<Imagen> adapter = new ArrayAdapter<Imagen>(this, R.layout.misimagenestabla, R.id.titulo, imgList);

        getImagenListView().setAdapter(adapter);
        
        imagenListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                Imagen imagenSeleccionado = adapter.getItem(position);
                if(imagenSeleccionado.getImagen() != null){
                    
                    //TODO
                }
                
            }
        });
    }

    public void onClickBtn(View v) {
        
        Log.d("GEORGE USER ID: ", ""+usuarioId);
        new RequestTask(v).execute("http://10.0.2.2:8080/CasosDeUso5/webresources/casosdeusowsport/getimagenes?usuarioId="+usuarioId);
    }

    /**
     * @return the usuarioId
     */
    public int getUsuarioId() {
        return usuarioId;
    }

    /**
     * @param usuarioId the usuarioId to set
     */
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * @return the imgDao
     */
    public ImagenesDao getImgDao() {
        return imgDao;
    }

    /**
     * @param imgDao the imgDao to set
     */
    public void setImgDao(ImagenesDao imgDao) {
        this.imgDao = imgDao;
    }

    /**
     * @return the imagenListView
     */
    public ListView getImagenListView() {
        return imagenListView;
    }

    /**
     * @param imagenListView the imagenListView to set
     */
    public void setImagenListView(ListView imagenListView) {
        this.imagenListView = imagenListView;
    }

    class RequestTask extends AsyncTask<String, String, String> {

        private View rootView;

        public RequestTask(View rootView) {
            this.rootView = rootView;
        }

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                Log.d("info", statusLine.toString());
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.d("OH CRIKEY", e.toString());
            } catch (IOException e) {
                Log.d("OH CRIKEY", e.toString());
            }
            Log.d("info", responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            //super.onPostExecute(result);
            
            Log.d("GEORGE RESULT: ", result);
            
            try {
                
                List<Imagen> imgs = Parser.parseByDOM(result);
                
                if(!imgs.isEmpty()){
                    this.procesarImagenes(imgs);
                    ((BaseAdapter) imagenListView.getAdapter()).notifyDataSetChanged(); 
                }
                
            } catch (Exception e) {
                Log.d("ERROR!!", "" + e.getMessage());
            }
        }
        
        void procesarImagenes(List<Imagen> imgs){
            
            for(Imagen i: imgs){
                
                Log.d("GEORGE1", i.getImagen().toString());
                i.setEsNuevo(true);
                
                if(i.getImagen() != null){
                    
                    //ver si SD card es presente
                    if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                                                
                        try{
                            File sdcard = Environment.getExternalStorageDirectory();
                            String nombreArchivo = i.getTitulo() != null? i.getTitulo(): "CasoDeUso";
                            Long tsLong = System.currentTimeMillis()/1000;
                            String timestamp = tsLong.toString();
                            File f = new File (sdcard, nombreArchivo+timestamp+".png");
                            FileOutputStream out = new FileOutputStream(f);
                            i.getImagen().compress(Bitmap.CompressFormat.PNG, 90, out);
                            Log.d("FILE PATH: ", ""+f.getAbsolutePath());
                            imgDao.open();
                            imgDao.createImagen(f.getPath(), i.getTitulo(), i.getFechaCreada());
                            imgs.add(i);
                            imgDao.close();
                        }catch(FileNotFoundException e){
                            Log.e("ERROR: ", ""+e.getMessage());
                         }
                    }                                      
                }
            }
        }
        
        /**
         * @return the rootView
         */
        public View getRootView() {
            return rootView;
        }

        /**
         * @param rootView the rootView to set
         */
        public void setRootView(View rootView) {
            this.rootView = rootView;
        }
    }
}

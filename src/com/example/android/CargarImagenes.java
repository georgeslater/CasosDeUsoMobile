/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private ProgressBar progressBar;
    private Button botonSync;
    private TextView textoCargando;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle bundle = getIntent().getExtras();
        usuarioId = bundle.getInt("usuarioId");
        setContentView(R.layout.misimagenes);

        imgDao = new ImagenesDao(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imagenListView = (ListView) findViewById(R.id.imagenesListview);
        botonSync = (Button) findViewById(R.id.botonSync);
        textoCargando = (TextView) findViewById(R.id.textCargando);
        
        botonSync.setVisibility(Button.GONE);
        imagenListView.setVisibility(ListView.GONE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        textoCargando.setVisibility(TextView.VISIBLE);
        
        new CargarImagenesDeBD().execute();
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public void onClickBtn(View v) {

        Log.d("GEORGE USER ID: ", "" + usuarioId);
        new RequestTask(v).execute("http://10.0.2.2:8080/CasosDeUso5/webresources/casosdeusowsport/getimagenes?usuarioId=" + usuarioId);
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

    /**
     * @return the progressBar
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * @param progressBar the progressBar to set
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * @return the botonSync
     */
    public Button getBotonSync() {
        return botonSync;
    }

    /**
     * @param botonSync the botonSync to set
     */
    public void setBotonSync(Button botonSync) {
        this.botonSync = botonSync;
    }

    /**
     * @return the textoCargando
     */
    public TextView getTextoCargando() {
        return textoCargando;
    }

    /**
     * @param textoCargando the textoCargando to set
     */
    public void setTextoCargando(TextView textoCargando) {
        this.textoCargando = textoCargando;
    }

    class CargarImagenesDeBD extends AsyncTask<Void, Integer, Void> {

        int myProgress;
        ArrayAdapter<Imagen> adapter;
        
        @Override
        protected void onPostExecute(Void result){
            
            adapter = new ArrayAdapter<Imagen>(CargarImagenes.this, R.layout.misimagenestabla, R.id.titulo, imgList);

            getImagenListView().setAdapter(adapter);
            
            imagenListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.d("GEORGE", "CLICKITYBOBS");

                    Imagen imagenSeleccionado = adapter.getItem(position);

                    if (imagenSeleccionado.getImagen() != null) {

                        Intent intent = new Intent(CargarImagenes.this, VerImagen.class);
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        imagenSeleccionado.getImagen().compress(Bitmap.CompressFormat.PNG, 50, bs);
                        intent.putExtra("byteArray", bs.toByteArray());
                        startActivity(intent);
                    }

                }
            });
            
            progressBar.setProgress(100);
            
            adapter.notifyDataSetChanged();
            imagenListView.setVisibility(ListView.VISIBLE);
            progressBar.setVisibility(ProgressBar.GONE);
            botonSync.setVisibility(Button.VISIBLE);
            textoCargando.setVisibility(TextView.GONE);
            
        }
        
        @Override
        protected void onPreExecute() {
            myProgress = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {

            imgList = new ArrayList<Imagen>();

            imgDao.open();
            
            progressBar.setProgress(25);
            
            //Sacar imagenes de la tarjeta SD
            List<Imagen> imagenes = imgDao.getAllImagenes();
            imgList.addAll(imagenes);
            Log.d("GEORGE Imagenes: ", "" + imagenes.toString());
            
            progressBar.setProgress(75);

            imgDao.close();

            setImagenListView(imagenListView);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            progressBar.setProgress(values[0]);
        }
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

                if (!imgs.isEmpty()) {
                    this.procesarImagenes(imgs);
                }

            } catch (Exception e) {
                Log.d("ERROR!!", "" + e.getMessage());
            }
        }

        void procesarImagenes(List<Imagen> imgs) {

            List<Imagen> imagenesProcesados = new ArrayList<Imagen>();

            for (Imagen i : imgs) {

                Log.d("GEORGE1", i.getImagen().toString());
                i.setEsNuevo(true);

                if (i.getImagen() != null) {

                    //ver si SD card es presente
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

                        try {
                            File sdcard = Environment.getExternalStorageDirectory();
                            String nombreArchivo = i.getTitulo() != null ? i.getTitulo() : "CasoDeUso";
                            Long tsLong = System.currentTimeMillis() / 1000;
                            String timestamp = tsLong.toString();
                            File f = new File(sdcard, nombreArchivo + timestamp + ".png");
                            FileOutputStream out = new FileOutputStream(f);
                            i.getImagen().compress(Bitmap.CompressFormat.PNG, 90, out);
                            Log.d("FILE PATH: ", "" + f.getAbsolutePath());
                            imgDao.open();
                            imgDao.createImagen(f.getPath(), i.getTitulo(), i.getFechaCreada());
                            imagenesProcesados.add(i);
                            imgDao.close();
                        } catch (FileNotFoundException e) {
                            Log.e("ERROR: ", "" + e.getMessage());
                        }
                    }
                }
            }

            if (!imagenesProcesados.isEmpty()) {

                imgs.clear();
                imgs.addAll(imagenesProcesados);
                ((BaseAdapter) imagenListView.getAdapter()).notifyDataSetChanged();
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

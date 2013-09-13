/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android;

import android.app.Activity;
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
import android.widget.Toast;
import com.example.android.dao.ImagenesDao;
import com.example.android.util.Parser;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<String, Imagen> imgTituloMapa;
    private ListView imagenListView;
    private ProgressBar progressBar;
    private Button botonSync;
    private TextView textoCargando;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle bundle = getIntent().getExtras();
        usuarioId = bundle.getInt(Constantes.USUARIO_ID_PARAM);
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

    @Override
    public void onBackPressed() {
    }

    public void onClickBtn(View v) {

        if (usuarioId != 0) {

            Log.d("GEORGE USER ID: ", "" + usuarioId);
            botonSync.setText(getString(R.string.procesando));
            botonSync.setClickable(false);

            new RequestTask(v).execute(Constantes.SYNC_IMAGENES_URL + usuarioId);
        }
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
        protected void onPostExecute(Void result) {

            adapter = new ArrayAdapter<Imagen>(CargarImagenes.this, R.layout.misimagenestabla, R.id.titulo, imgList);

            getImagenListView().setAdapter(adapter);

            imagenListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Imagen imagenSeleccionado = adapter.getItem(position);

                    Log.d(Constantes.CUSTOM_LOG_TAG, "IMAGE PATH ES: " + imagenSeleccionado.getImagenPath());

                    if (imagenSeleccionado.getImagenPath() != null) {

                        Intent intent = new Intent(CargarImagenes.this, VerImagen.class);
                        intent.putExtra("imagenPath", imagenSeleccionado.getImagenPath());
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
            imgTituloMapa = new HashMap<String, Imagen>();

            imgDao.open();

            progressBar.setProgress(25);

            //Sacar imagenes de la tarjeta SD
            List<Imagen> imagenes = imgDao.getAllImagenes();

            for (Imagen imm : imagenes) {

                imgTituloMapa.put(imm.getTitulo(), imm);
            }

            imgList.addAll(imagenes);
            Log.d(Constantes.CUSTOM_LOG_TAG, "Imagenes: " + imagenes.toString());

            progressBar.setProgress(75);

            imgDao.close();

            setImagenListView(imagenListView);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
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
                    //Cierra la conexion.
                    response.getEntity().getContent().close();
                    //hubo un error de Web Service
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_sincronizar), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_sincronizar), Toast.LENGTH_LONG).show();
            }
            Log.d(Constantes.CUSTOM_LOG_TAG, responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d(Constantes.CUSTOM_LOG_TAG, "RESULT: " + result);

            botonSync.setText(getString(R.string.sync_texto));
            botonSync.setClickable(true);

            try {

                List<Imagen> imgs = Parser.parseByDOM(result);

                if (!imgs.isEmpty()) {
                    this.procesarImagenes(imgs);
                }

            } catch (Exception e) {
                Log.e(Constantes.CUSTOM_LOG_TAG, "ERROR!!" + e.getMessage());
                Toast.makeText(getApplicationContext(), getString(R.string.error_sincronizar), Toast.LENGTH_LONG).show();
            }
        }

        void procesarImagenes(List<Imagen> imgs) {

            List<Imagen> imagenesProcesados = new ArrayList<Imagen>();
            Boolean actualizarDataSet = false;

            for (Imagen i : imgs) {

                Log.d(Constantes.CUSTOM_LOG_TAG, i.getImagen().toString());

                Log.d(Constantes.CUSTOM_LOG_TAG, "TITULO: " + i.getTitulo());
                Log.d(Constantes.CUSTOM_LOG_TAG, "FECHA: " + i.getFechaCreada());

                if (i.getImagen() != null) {

                    Log.d(Constantes.CUSTOM_LOG_TAG, "IMAGE NOT NULL");

                    //ver si SD card es presente
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

                        Log.d(Constantes.CUSTOM_LOG_TAG, "SD CARD PRESENT");

                        try {
                            File sdcard = Environment.getExternalStorageDirectory();
                            String nombreArchivo = i.getTitulo().replaceAll(" ", "_");

                            File f = new File(sdcard, nombreArchivo + ".png");

                            if (f.exists()) {

                                Log.d("GEORGE", "exists!!");
                                Boolean b = f.delete();

                                if (b) {

                                    Log.d("GEORGE", "delete success");

                                    try {

                                        f.createNewFile();
                                        Log.d("GEORGE", "created new file");

                                    } catch (IOException e) {

                                        Log.e(Constantes.CUSTOM_LOG_TAG, "" + e.getMessage());
                                    }
                                } else {

                                    Log.e(Constantes.CUSTOM_LOG_TAG, "no se pudo borrar");
                                }
                            }

                            FileOutputStream out = new FileOutputStream(f);
                            i.getImagen().compress(Bitmap.CompressFormat.PNG, 90, out);
                            Log.d("FILE PATH: ", "" + f.getPath());
                            imgDao.open();
                            Boolean isUpdate = imgDao.createImagen(f.getPath(), i.getTitulo(), i.getFechaCreada());
                            Log.d(Constantes.CUSTOM_LOG_TAG, isUpdate ? "UPDATED " + i.toString() : "CREATED " + i.toString());
                            imgDao.close();

                            if (!isUpdate) {
                                i.setImagenPath(f.getPath());
                                imagenesProcesados.add(i);
                            } else {
                                for (Imagen immm : imgList) {

                                    if (i.getTitulo().equals(immm.getTitulo())) {

                                        immm.setImagenPath(f.getPath());
                                        actualizarDataSet = true;
                                    }
                                }
                            }
                        } catch (FileNotFoundException e) {
                            Log.e(Constantes.CUSTOM_LOG_TAG, "" + e.getMessage());
                            Toast.makeText(getApplicationContext(), getString(R.string.error_sincronizar), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            if (!imagenesProcesados.isEmpty()) {

                imgList.addAll(imagenesProcesados);
                actualizarDataSet = true;
            }

            if (actualizarDataSet) {

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

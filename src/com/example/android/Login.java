package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.android.util.Parser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Login extends Activity
{
    
    private EditText usuarioInput;
    private EditText contraseniaInput;
    private Button loginBtn;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout)
        setContentView(R.layout.login);
        usuarioInput = (EditText)findViewById(R.id.usuarioInput);
        contraseniaInput = (EditText)findViewById(R.id.contraseniaInput);
        loginBtn = (Button)findViewById(R.id.btnLogin);
    }
    
    public void login(View v){
        
        String usuarioText = usuarioInput.getText().toString();
        String contraseniaText = contraseniaInput.getText().toString();
        
        if(usuarioText != null && !usuarioText.isEmpty() && contraseniaText != null && !contraseniaText.isEmpty()){
            
            loginBtn.setText("Procesando...");
            new RequestTask(v).execute(Constantes.LOGUEARSE_URL+"?"+Constantes.USUARIO_PARAM+"="+usuarioText+"&"+Constantes.CONTRASENIA_PARAM+"="+contraseniaText);
        }
    }

    /**
     * @return the usuarioInput
     */
    public EditText getUsuarioInput() {
        return usuarioInput;
    }

    /**
     * @param usuarioInput the usuarioInput to set
     */
    public void setUsuarioInput(EditText usuarioInput) {
        this.usuarioInput = usuarioInput;
    }

    /**
     * @return the contraseniaInput
     */
    public EditText getContraseniaInput() {
        return contraseniaInput;
    }

    /**
     * @param contraseniaInput the contraseniaInput to set
     */
    public void setContraseniaInput(EditText contraseniaInput) {
        this.contraseniaInput = contraseniaInput;
    }

    /**
     * @return the loginBtn
     */
    public Button getLoginBtn() {
        return loginBtn;
    }

    /**
     * @param loginBtn the loginBtn to set
     */
    public void setLoginBtn(Button loginBtn) {
        this.loginBtn = loginBtn;
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
            Log.d("info", ""+responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                int usuarioId = Parser.parseLogin(result);
                Log.d("USUARIO ID: ", String.valueOf(usuarioId));
                
                if(usuarioId != 0){
                    
                    Intent i = new Intent(Login.this, CargarImagenes.class);
                    i.putExtra("usuarioId", usuarioId);
                    startActivity(i);
                }else{
                    loginBtn.setText("Login");
                    Toast.makeText(getApplicationContext(), "Error al loguearse", Toast.LENGTH_LONG).show();
                }
                
                //outputIv.setImageBitmap(imgs.get(0).getImagen());

            } catch (Exception e) {
                Log.d("ERROR!!", "" + e.getMessage());
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

/**
 *
 * @author George
 */
public class VerImagen extends Activity{

    private ImageView outputIv;

    @Override
    public void onCreate(Bundle icicle) {
        
        super.onCreate(icicle);
        
        setContentView(R.layout.imagen);

        if(getIntent().hasExtra("imagenPath")) {
            
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap b = BitmapFactory.decodeFile(getIntent().getStringExtra("imagenPath"), options);
            outputIv = (ImageView) findViewById(R.id.outputIv);
            outputIv.setImageBitmap(b);
            outputIv.setBackgroundColor(Color.WHITE);
        }
    }
    
    /**
     * @return the outputIv
     */
    public ImageView getOutputIv() {
        return outputIv;
    }

    /**
     * @param outputIv the outputIv to set
     */
    public void setOutputIv(ImageView outputIv) {
        this.outputIv = outputIv;
    }
}

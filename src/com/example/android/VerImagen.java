/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android;

import android.app.Activity;
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

        //outputIv = (ImageView) findViewById(R.id.imageView1);
        //outputIv.setImageBitmap(icicle.);
        outputIv.setBackgroundColor(Color.WHITE);
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

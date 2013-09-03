/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 *
 * @author George
 * Referencia: http://stackoverflow.com/questions/8166497/custom-adapter-for-list-view
 */
public class ImagenesAdapter extends ArrayAdapter<Imagen> {

    public ImagenesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        // TODO Auto-generated constructor stub
    }
    private List<Imagen> items;

    public ImagenesAdapter(Context context, int resource, List<Imagen> items) {

        super(context, resource, items);

        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.misimagenestabla, null);

        }

        Imagen p = items.get(position);

        if (p != null) {

            ImageView iv = (ImageView) v.findViewById(R.id.nuevo);
            TextView tv = (TextView) v.findViewById(R.id.titulo);

            if (iv != null && p.getEsNuevo()) {
                iv.setImageResource(R.drawable.icon_nuevo);
            }
            if (tv != null) {

                tv.setText(p.getTitulo());
            }          
        }

        return v;

    }
}

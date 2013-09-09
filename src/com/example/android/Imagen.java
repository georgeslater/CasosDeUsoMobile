/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android;

import android.graphics.Bitmap;

/**
 *
 * @author George
 */
public class Imagen {
  
    private String fechaCreada;
    private Bitmap imagen;
    private String imagenPath;
    private String titulo;
    private Long id;
    private Boolean esNuevo;
    
    public Imagen(){}
    
    public Imagen (String fechaCreada, Bitmap imagen, String titulo){
        
        this.fechaCreada = fechaCreada;
        this.imagen = imagen;
        this.titulo = titulo;
    }

    /**
     * @return the fechaCreada
     */
    public String getFechaCreada() {
        return fechaCreada;
    }

    /**
     * @param fechaCreada the fechaCreada to set
     */
    public void setFechaCreada(String fechaCreada) {
        this.fechaCreada = fechaCreada;
    }

    /**
     * @return the imagenData
     */
    public Bitmap getImagen() {
        return imagen;
    }

    /**
     * @param imagenData the imagenData to set
     */
    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the esNuevo
     */
    public Boolean getEsNuevo() {
        return esNuevo;
    }

    /**
     * @param esNuevo the esNuevo to set
     */
    public void setEsNuevo(Boolean esNuevo) {
        this.esNuevo = esNuevo;
    }
    
    @Override
    public String toString() {
        return this.getTitulo() != null? this.getTitulo(): "Caso De Uso";
    }

    /**
     * @return the imagenPath
     */
    public String getImagenPath() {
        return imagenPath;
    }

    /**
     * @param imagenPath the imagenPath to set
     */
    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }
 }

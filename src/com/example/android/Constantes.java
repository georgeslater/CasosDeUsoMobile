/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android;

/**
 *
 * @author George
 */
public class Constantes {
    
    //tag para debuggear
    public static final String CUSTOM_LOG_TAG = "GEORGE";
    
    //servicios web
    public static final String DIRECCION_IP_ESTATICA = "http://10.0.2.2";
    public static final String PUERTA = ":8080";
    public static final String SYNC_IMAGENES_URL = Constantes.DIRECCION_IP_ESTATICA+Constantes.PUERTA+"/CasosDeUso5/webresources/casosdeusowsport/"+Constantes.OBTENER_IMAGENES_REST_RESOURCE_URL+"?"+Constantes.USUARIO_ID_PARAM+"=";
    public static final String OBTENER_IMAGENES_REST_RESOURCE_URL = "getimagenes";
    public static final String USUARIO_ID_PARAM = "usuarioId";
}

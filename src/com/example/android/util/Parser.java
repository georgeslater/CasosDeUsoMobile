/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.example.android.Imagen;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author George
 */
public class Parser {

    public static int parseLogin(String response) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(response)));
        // normalize the document
        doc.getDocumentElement().normalize();
        // get the root node
        NodeList nodeList = doc.getElementsByTagName("integer");
        Log.d("miaouw", nodeList.toString());

        for (int k = 0; k < nodeList.getLength(); k++) {

            Node node = nodeList.item(0);
            Log.d("miaouw", node.getNodeName());
            Log.d("ribbit", node.getTextContent());
            if (node.getTextContent() != null) {

                try {
                    return Integer.valueOf(node.getTextContent());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;

    }

    public static List<Imagen> parseByDOM(String response) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(response)));
        // normalize the document
        doc.getDocumentElement().normalize();
        // get the root node
        NodeList nodeList = doc.getElementsByTagName("return");
        Log.d("miaouw", nodeList.toString());
        List<Imagen> imgData = new ArrayList<Imagen>();

        for (int k = 0; k < nodeList.getLength(); k++) {

            Node node = nodeList.item(0);
            Log.d("miaouw", node.getNodeName());

            Imagen img = new Imagen();

            for (int i = 0; i < node.getChildNodes().getLength(); i++) {

                Node temp = node.getChildNodes().item(i);
                Log.d("miaouw", temp.getNodeName());

                if (temp.getNodeName().equalsIgnoreCase("fechaCreada")) {

                    img.setFechaCreada(temp.getTextContent());

                } else if (temp.getNodeName().equalsIgnoreCase("imagen")) {

                    String encodedImg = temp.getTextContent();
                    Log.d("vroom", encodedImg);
                    Log.d("toom ", String.valueOf(encodedImg.length()));
                    Log.d("proom ", encodedImg.substring(encodedImg.length() - 10, encodedImg.length()));
                    byte[] decodedString = Base64.decode(encodedImg, Base64.DEFAULT);
                    Log.d("woom", decodedString.toString());
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    img.setImagen(decodedByte);
                } else if (temp.getNodeName().equalsIgnoreCase("titulo")) {

                    img.setTitulo(temp.getTextContent());
                }
            }

            imgData.add(img);
            Log.d("MIAUOW", img.toString());

            Log.d("noddy", node.toString());


        }

        return imgData;
    }
}

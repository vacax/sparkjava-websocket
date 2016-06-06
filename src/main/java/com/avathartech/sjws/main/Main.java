package com.avathartech.sjws.main;

import com.avathartech.sjws.websocket.ServidorMensajesWebSocketHandler;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import static j2html.TagCreator.*;

/**
 * Created by vacax on 05/06/16.
 */
public class Main {

    //Creando el repositorio de las sesiones recibidas.
    public static List<Session> usuariosConectados = new ArrayList<>();

    public static void main(String[] args) {

        //Habilitando el debug.
        enableDebugScreen(); //en productivo no habilitar.

        //Indicnado la ruta directa de los recursos estaticos.
        staticFiles.location("/publico");

        //Debe ir antes de abrir alguna ruta.
        webSocket("/mensajeServidor", ServidorMensajesWebSocketHandler.class);

        get("/",(request, response) ->{
            return "Ejemplo de SparkJava con WebSocket";
        });

        /**
         * http://localhost:4567/polling
         */
        get("/polling",(request, response) ->{
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return ""+format.format(new Date());
        });

        /**
         * http://localhost:4567/enviarMensaje?mensaje=Hola Mundo
         */
        get("/enviarMensaje",(request, response) ->{
            String mensaje = request.queryParams("mensaje");
            enviarMensajeAClientesConectados(mensaje);
            return "Enviando mensaje: "+mensaje;
        });




    }

    /**
     * Permite enviar un mensaje al cliente.
     * @param mensaje
     */
    public static void enviarMensajeAClientesConectados(String mensaje){
        for(Session sesionConectada : usuariosConectados){
            try {
                sesionConectada.getRemote().sendString(p(mensaje).withClass("rojo").render());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.avathartech.sjws.main;

import com.avathartech.sjws.websocket.ServidorMensajesWebSocketHandler;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static spark.Spark.*;
//import static spark.debug.DebugScreen.enableDebugScreen;

import static j2html.TagCreator.*;

/**
 * Created by vacax on 05/06/16.
 */
public class Main {

    //Creando el repositorio de las sesiones recibidas.
    public static List<Session> usuariosConectados = new ArrayList<>();

    public static void main(String[] args) {

        //Habilitando el debug.
        //enableDebugScreen(); //en productivo no habilitar.

        //Indicnado la ruta directa de los recursos estaticos.
        staticFiles.location("/publico");

        //Debe ir antes de abrir alguna ruta.
        webSocket("/mensajeServidor", ServidorMensajesWebSocketHandler.class);
        init();

        get("/",(request, response) ->{
            String tramaHtml = html(
                    j2html.TagCreator.head(title("Ejemplo de WebSocket")),
                    body(
                            h1("Ejemplo de Ajax y WebSocket"),
                            h2(a("Ejemplo Polling").withHref("/ejemploPolling.html")),
                            h2(a("Ejemplo WebSocket").withHref("/ejemploWebSocket.html"))
                    )).render();
            return tramaHtml;
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
            enviarMensajeAClientesConectados(mensaje, "rojo");
            return "Enviando mensaje: "+mensaje;
        });




    }

    /**
     * Permite enviar un mensaje al cliente.
     * Ver uso de la librería: https://j2html.com/
     * @param mensaje
     * @param color
     */
    public static void enviarMensajeAClientesConectados(String mensaje, String color){
        for(Session sesionConectada : usuariosConectados){
            try {
                sesionConectada.getRemote().sendString(p(mensaje).withClass(color).render());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

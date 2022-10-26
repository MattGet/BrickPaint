package com.example.brickpaint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.net.InetAddress;


public class BobRoss {

    private final ButtonManager manager;

    private Server server;

    private Client client;

    public BobRoss(ButtonManager manager1){
        manager = manager1;
    }

    private boolean isServer = false;
    //private final Server server = new Server();

    public boolean isServer() {
        return isServer;
    }

    private boolean isClient = false;

    public boolean isClient() {
        return isClient;
    }

    //private final Client client = new Client();

    private final Logger logger = BrickPaintController.logger;

    public void startServer(){
        if (isServer) return;
        try {
            server = new Server(56789, 1, InetAddress.getLocalHost(), manager);
            Thread t = new Thread(server);
            t.start();
            isServer = true;
            Notifications.create()
                    .title("Started Server")
                    .text("successfully started server connection!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (isServer && !isClient){
            startClient();
        }
    }

    public void stopServer(){
        if (isServer){
            server.stop();
            this.isServer = false;
            Notifications.create()
                    .title("Stopped Server")
                    .text("successfully stopped the server!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
        }
    }

    public void startClient(){
        try {
            client = new Client(56789, InetAddress.getLocalHost(), manager);
            Thread t = new Thread(client);
            t.start();
            this.isClient = true;
            Notifications.create()
                    .title("Started Client")
                    .text("successfully started client connection!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stopClient(){
        if (isClient){
            client.stop();
            isClient = false;
            Notifications.create()
                    .title("Closed Client")
                    .text("successfully closed client connection!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
        }
    }

    public void sendClientImage(Image image){
        client.sendImageToServer(SwingFXUtils.fromFXImage(image, null));
    }
}

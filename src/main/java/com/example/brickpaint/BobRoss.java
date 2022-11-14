package com.example.brickpaint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.InetAddress;


public class BobRoss {

    private final ButtonManager manager;

    private Server server;

    private Client client;
    private boolean isServer = false;
    private boolean isClient = false;
    //private final Server server = new Server();

    public BobRoss(ButtonManager manager1) {
        manager = manager1;
    }

    public boolean isServer() {
        return isServer;
    }

    public boolean isClient() {
        return isClient;
    }

    //private final Client client = new Client();


    public void startServer() {
        if (isServer) return;
        try {
            server = new Server(56789, 1, InetAddress.getLocalHost(), manager);
            Thread t = new Thread(server);
            t.start();
            isServer = true;
            BrickPaintController.logger.info("[APP] Starting Server");
            Notifications.create()
                    .title("Started Server")
                    .text("successfully started server connection!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
        } catch (Exception e) {
            Notifications.create()
                    .title("Server Error")
                    .text("failed to create server connection!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
            BrickPaintController.logger.error("[SERVER] failed to initialize server!");
            manager.toggleServerUiSilent(false);
        }

        if (isServer && !isClient) {
            startClient();
        }
    }

    public void stopServer() {
        if (isServer) {
            server.stop();
            server = null;
            this.isServer = false;
            BrickPaintController.logger.info("[APP] Stopped Server");
            Notifications.create()
                    .title("Stopped Server")
                    .text("successfully stopped the server!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
        }
        if (isClient) stopClient();
    }

    public void startClient() {
        InetAddress address;
        try {
            BrickPaintController.logger.info("[CLIENT] Starting Server Discovery");
            address = Client.discoverServer(56789);
            BrickPaintController.logger.info("[CLIENT] Found Server Address: " + address);
        } catch (IOException ex) {
            BrickPaintController.logger.error("[CLIENT] >>> " + ex);
            try {
                address = InetAddress.getLocalHost();
                BrickPaintController.logger.info("[CLIENT] Using LocalHost, address: " + address);
            } catch (Exception exc) {
                throw new RuntimeException("Failed To Find Server Address!");
            }
        }


        try {
            client = new Client(56789, address, manager);
            Thread t = new Thread(client);
            t.start();
            this.isClient = true;
            if (!isServer) BrickPaintController.logger.info("[APP] Starting Client");
            Notifications.create()
                    .title("Started Client")
                    .text("successfully started client connection!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            Notifications.create()
                    .title("Client Error")
                    .text("failed to create client connection!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
            BrickPaintController.logger.error("[CLIENT] failed to initialize client connection!");
            manager.toggleClientUiSilent(false);
        }
    }

    public void stopClient() {
        if (isClient) {
            client.stop();
            client = null;
            isClient = false;
            BrickPaintController.logger.info("[APP] Stopped Client");
            Notifications.create()
                    .title("Closed Client")
                    .text("successfully closed client connection!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(manager.colorPicker.getScene().getWindow())
                    .show();
        }
    }

    public void sendClientImage(Image image) {
        if (this.isClient) {
            client.sendImageToServer(SwingFXUtils.fromFXImage(image, null));
        }
    }
}

package com.example.brickpaint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Handles the creation and management of all server/client related
 * activities within the application
 *
 * @author matde
 */
public class BobRoss {

    private final ButtonManager manager;

    private Server server;

    private Client client;
    private boolean isServer = false;
    private boolean isClient = false;

    /**
     * Constructor that passes the applications UI controller to this class
     *
     * @param manager1 UI Controller for the application
     */
    public BobRoss(ButtonManager manager1) {
        manager = manager1;
    }

    /**
     * Getter for isServer boolean, determines if this application is acting as a server
     *
     * @return true if application is a server
     */
    public boolean isServer() {
        return isServer;
    }

    /**
     * Getter for isClient boolean, determines if this application is acting as a client
     *
     * @return true if application is a client
     */
    public boolean isClient() {
        return isClient;
    }

    //private final Client client = new Client();


    /**
     * Starts a server for the application and creates a notification
     */
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
        //creates a client for the server in order to run the server as a client p2p connection
        if (isServer && !isClient) {
            startClient();
        }
    }

    /**
     * Stops the server and creates a notification
     */
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

    /**
     * Starts a client for the application and creates a notification
     */
    public void startClient() {
        //attempts to find the coorect address to start the client connection with
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

        //attempts to create the client
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

    /**
     * Stops the client and creates a notification
     */
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

    /**
     * Helper function that has the client send an image if this application is a client
     *
     * @param image The image to send
     */
    public void sendClientImage(Image image) {
        if (this.isClient) {
            client.sendImageToServer(SwingFXUtils.fromFXImage(image, null));
        }
    }
}

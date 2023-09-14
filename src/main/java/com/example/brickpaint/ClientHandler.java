package com.example.brickpaint;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * [WORK IN PROGRESS]
 * Handles packets that are sent to Clients, with an option
 * to handle data differently if the client is also a server
 *
 * @author matde
 */
public class ClientHandler implements Runnable {
    final Socket socket;
    private final Server ManageServer;
    private final ButtonManager manager;
    private boolean running = true;


    /**
     * Constructor for the client handler that initializes the necesssary variables
     *
     * @param s        The socket assigned to this handler
     * @param manager1 The UI Controller for the application
     * @param server Boolean that determines if the handler should also handle server data
     */
    public ClientHandler(Socket s, ButtonManager manager1, Server server) {
        this.ManageServer = server;
        this.manager = manager1;
        this.socket = s;
    }

    /**
     * Helper function that stops the loop in the thread, thus stopping the thread
     */
    public void stop() {
        running = false;
    }

    /**
     * Main thread function, continuously reads output stream for images, when found it will
     * update the application canvas and send to other clients if designated as a server
     */
    @Override
    public void run() {
        while (running) {
            try {
                ImageInputStream input = ImageIO.createImageInputStream(socket.getInputStream());
                if (input == null) throw new Exception("Image Input Stream Was Null");
                BufferedImage image = ImageIO.read(input);
                if (ManageServer != null) {
                    ManageServer.updateClients(image);
                } else {
                    manager.updateCanvas(image);
                    BrickPaintController.logger.info("[CLIENT] received image and updated canvas");
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(100L);
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        }

        try {
            this.socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

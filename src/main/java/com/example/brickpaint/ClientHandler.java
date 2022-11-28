package com.example.brickpaint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
public class ClientHandler implements  Runnable{
    final InputStream dis;
    final OutputStream dos;
    final Socket s;

    private boolean running = true;

    private final boolean ManageServer;

    private final ButtonManager manager;


    /**
     * Constructor for the client handler that initializes the necesssary variables
     *
     * @param s The socket assigned to this handler
     * @param dis The Input Stream assigned to the handler
     * @param dos The Output Stream assigned to the handler
     * @param manager1 The UI Controller for the application
     * @param isServer Boolean that determines if the handler should also handle server data
     */
    public ClientHandler(Socket s, InputStream dis, OutputStream dos, ButtonManager manager1, boolean isServer)
    {
        this.ManageServer = isServer;
        this.manager = manager1;
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    /**
     * Helper function that stops the loop in the thread, thus stopping the thread
     */
    public void stop(){
        running = false;
    }

    /**
     * Main thread function, continuously reads output stream for images, when found it will
     * update the application canvas and send to other clients if designated as a server
     */
    @Override
    public void run()
    {
        while (running)
        {
            try {
                BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(s.getInputStream()));
                if (ManageServer){
                        ImageIO.write(image, "png", s.getOutputStream()); // Send image to client
                        s.getOutputStream().flush();
                        BrickPaintController.logger.info("[SERVER] pushed image to clients");
                        manager.updateCanvas(image);
                        //System.out.println("[SERVER] pushed image to clients");
                } else {
                    manager.updateCanvas(image);
                    BrickPaintController.logger.info("[CLIENT] received image and updated canvas");
                    //System.out.println("[CLIENT] received image and updated canvas");
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(100L);
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        }

        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

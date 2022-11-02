package com.example.brickpaint;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable{

    private final Socket client;

    private ClientHandler handler;

    private final ButtonManager manager;


    public Client(int port, InetAddress address, ButtonManager manager1){
        this.manager = manager1;
        try {
            client = new Socket(address, port);
            BrickPaintController.logger.info("[CLIENT] A new client is connected : " + client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        // closing resources
        try {
            handler.stop();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendImageToServer(BufferedImage image){
        try{
            ImageIO.write(image, "png", client.getOutputStream()); // Send image to client
            client.getOutputStream().flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try
        {
            // obtaining input and out streams
            InputStream dis = client.getInputStream();
            OutputStream dos = client.getOutputStream();

            handler = new ClientHandler(client, dis, dos, manager, false);
            BrickPaintController.logger.info("[CLIENT] Assigning new thread to handle incoming data");
            // create a new thread object
            Thread t = new Thread(handler);

            t.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

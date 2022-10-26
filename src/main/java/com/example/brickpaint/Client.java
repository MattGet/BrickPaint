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

    private final Socket s;

    private ClientHandler handler;

    private final ButtonManager manager;


    public Client(int port, InetAddress address, ButtonManager manager1){
        this.manager = manager1;
        try {
            s = new Socket(address, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        // closing resources
        try {
            handler.stop();
            s.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendImageToServer(BufferedImage image){
        try{
            ImageIO.write(image, "png", s.getOutputStream()); // Send image to client
            s.getOutputStream().flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try
        {
            // obtaining input and out streams
            InputStream dis = s.getInputStream();
            OutputStream dos = s.getOutputStream();

            handler = new ClientHandler(s, dis, dos, manager, false);
            // create a new thread object
            Thread t = new Thread(handler);

            // Invoking the start() method
            t.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

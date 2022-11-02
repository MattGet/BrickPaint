package com.example.brickpaint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements  Runnable{
    final InputStream dis;
    final OutputStream dos;
    final Socket s;

    private boolean running = true;

    private final boolean ManageServer;

    private final ButtonManager manager;


    // Constructor
    public ClientHandler(Socket s, InputStream dis, OutputStream dos, ButtonManager manager1, boolean isServer)
    {
        this.ManageServer = isServer;
        this.manager = manager1;
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    public void stop(){
        running = false;
    }

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

package com.example.brickpaint;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable  {
    private final ServerSocket server;

    private final List<ClientHandler> handlers = new ArrayList<>();

    private boolean running = true;

    private final ButtonManager manager;

    public Server(int port, int backlog, InetAddress address, ButtonManager manager1){
        this.manager = manager1;
        try {
            this.server = new ServerSocket(port, backlog, address);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        running = false;
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        // running infinite loop for getting
        // client request
        while (running)
        {
            Socket s = null;

            try
            {
                // socket object to receive incoming client requests
                s = server.accept();

                BrickPaintController.logger.info("[SERVER] A new client is connected : " + s);

                // obtaining input and out streams
                InputStream dis = s.getInputStream();
                OutputStream dos = s.getOutputStream();

                BrickPaintController.logger.info("[SERVER] Assigning new thread for this client");

                ClientHandler cH = new ClientHandler(s, dis, dos, manager, true);
                handlers.add(cH);

                // create a new thread object
                Thread t = new Thread(cH);

                t.start();
            }
            catch (Exception e){
                try {
                    if (s != null) s.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        try {
            for (ClientHandler client: handlers) {
                client.stop();
            }
            server.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

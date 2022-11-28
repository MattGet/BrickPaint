package com.example.brickpaint;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * [WORK IN PROGRESS]
 * Handles the creation and processes of a server thread
 *
 * @author matde
 */
public class Server implements Runnable  {
    private final ServerSocket server;

    private final List<ClientHandler> handlers = new ArrayList<>();

    private boolean running = true;

    private final ButtonManager manager;

    private ServerDiscovery discovery;

    /**
     * Base constructor for the server thread that initializes the server socket
     *
     * @param port The port to start the server on
     * @param backlog The maximum number of connections that can be queued
     * @param address The InetAddress that the server should use
     * @param manager1 The UI Controller for the application
     */
    public Server(int port, int backlog, InetAddress address, ButtonManager manager1){
        this.manager = manager1;
        try {
            this.server = new ServerSocket(port, backlog, address);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper function that will stop the server thread loop when called
     */
    public void stop(){
        running = false;
        if (discovery != null) discovery.Stop();
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Main thread the runs the server, it will look for incoming
     * connections and assign a client handler to each one until
     * the stop function is called
     */
    @Override
    public void run() {
        discovery = new ServerDiscovery();
        Thread relayTask = new Thread(discovery);
        relayTask.start();

        // running infinite loop for getting
        // client requests
        while (running)
        {
            Socket s = null;

            try
            {
                // socket that receives incoming client requests
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
            // attempt to stop all active client threads on the server, then close the server
            for (ClientHandler client: handlers) {
                client.stop();
            }
            server.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

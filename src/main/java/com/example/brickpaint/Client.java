package com.example.brickpaint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.*;

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

    public static InetAddress discoverServer(int port) throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> task = new Callable<Object>() {
            public Object call() throws IOException {
                return discovery(port);
            }
        };
        Future<Object> future = executor.submit(task);
        try {
            return (InetAddress) future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            throw new IOException("Failed to Find Server, Process Timed Out!");
        } catch (InterruptedException e) {
            throw new IOException("Failed to Find Server, Process Interrupted!");
        } catch (ExecutionException e) {
            throw new IOException("Failed to Find Server, Execution Failed!");
        } finally {
            future.cancel(true); // may or may not desire this
        }
    }

    private static java.net.InetAddress discovery(int port) throws IOException {

        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "DISCOVER_BRICKSERVER_REQUEST".getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), port);
                c.send(sendPacket);
                BrickPaintController.logger.info("[CLIENT] Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
                //e.printStackTrace();
            }

            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, port);
                        c.send(sendPacket);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }

                    BrickPaintController.logger.info("[CLIENT] Request packet sent to: " + broadcast.getHostAddress());
                }
            }

            BrickPaintController.logger.info("[CLIENT] Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            BrickPaintController.logger.info("[CLIENT] Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals("DISCOVER_BRICKSERVER_RESPONSE")) {
                //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                return (receivePacket.getAddress());
            }
            //Close the port!
            c.close();
        } catch (IOException ex) {
            BrickPaintController.logger.error("[CLIENT] Error When Finding UDP Server!");
            ex.printStackTrace();
        }
        throw new IOException("Failed to Find Server Address!");
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

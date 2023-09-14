package com.example.brickpaint;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.*;

/**
 * [WORK IN PROGRESS]
 * Handles creation of a client connection to a corresponding server
 *
 * @author matde
 */
public class Client implements Runnable {

    private final Socket client;
    private final ButtonManager manager;
    private ClientHandler handler;


    /**
     * Client constructor that initializes a socket with the specified params
     *
     * @param port     The port the client should run on
     * @param address  The Inetaddress the client should use
     * @param manager1 The UI Controller for the application
     */
    public Client(int port, InetAddress address, ButtonManager manager1) {
        this.manager = manager1;
        try {
            client = new Socket(address, port);
            BrickPaintController.logger.info("[CLIENT] A new client is connected : " + client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper function that begins the discovery process and returns the designated Inetaddress
     * of an application server if one is found, else it will throw an appropriate error
     *
     * @param port The port to look for a server on
     * @return The Inetaddress of the first server found
     * @throws IOException Error changes based on why the task failed
     */
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

    /**
     * Main discovery thread that sends a UDP packet containing a keyword
     * to all designated ports on the local network, if it receives a proper response
     * then it will return the Inetaddress of that server
     *
     * Modified from Web blog of Michiel De Mey
     * @author matde, Michiel De Mey
     * @see <a href="https://michieldemey.be/blog/network-discovery-using-udp-broadcast/">Source Code</a>
     *
     * @param port The port to look for the server on
     * @return Inetaddress of the server
     * @throws IOException Fails to find server
     */
    private static java.net.InetAddress discovery(int port) throws IOException {

        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "DISCOVER_BRICKSERVER_REQUEST".getBytes();

            //Try 255.255.255.255 first
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
                NetworkInterface networkInterface = interfaces.nextElement();

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
                return (receivePacket.getAddress());
            }
            //Close the port
            c.close();
        } catch (IOException ex) {
            BrickPaintController.logger.error("[CLIENT] Error When Finding UDP Server!");
            ex.printStackTrace();
        }
        throw new IOException("Failed to Find Server Address!");
    }

    /**
     * Helper function that closes the client socket and handler thread
     */
    public void stop() {
        // closing resources
        try {
            handler.stop();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an image to the connected server
     *
     * @param image The image that gets sent to the server
     */
    public void sendImageToServer(BufferedImage image) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(client.getOutputStream());
            ImageIO.write(image, "png", outputStream); // Send image to client
            outputStream.flush();
            client.getOutputStream().flush();
            BrickPaintController.logger.info("[CLIENT] pushed image to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread method that initializes the client handler for this client
     */
    @Override
    public void run() {
        try {
            handler = new ClientHandler(client, manager, null);
            BrickPaintController.logger.info("[CLIENT] Assigning new thread to handle incoming data");
            // create a new thread object
            Thread t = new Thread(handler);

            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

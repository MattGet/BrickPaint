package com.example.brickpaint;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * [WORK IN PROGRESS]
 * Discovery thread that runs on the server and accepts
 * UDP broadcasts from clients looking for a corresponding Server
 *
 * @author matde, Michiel De Mey
 * @see <a href="https://michieldemey.be/blog/network-discovery-using-udp-broadcast/">Source Code</a>
 */
public class ServerDiscovery implements Runnable {

    private DatagramSocket socket;
    private boolean Running = true;

    /**
     * Helper function that stops the discovery thread loop
     */
    public void Stop() {
        Running = false;
        socket.close();
    }


    /**
     * Main thread that loops continuously looking for incoming data packets containing the keyword,
     * when found it will send a return packet that it used to determine the server InetAddress
     */
    @Override
    public void run() {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(56789, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (Running) {
                BrickPaintController.logger.info("[SERVER] Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                //Packet received
                BrickPaintController.logger.info("[SERVER] Discovery packet received from: " + packet.getAddress().getHostAddress());

                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_BRICKSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_BRICKSERVER_RESPONSE".getBytes();

                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    BrickPaintController.logger.info("[SERVER] Sent packet to: " + sendPacket.getAddress().getHostAddress());
                }
            }
        } catch (Exception ex) {
            if (ex.getClass() == SocketException.class) {
                BrickPaintController.logger.error("[SERVER] Closed Discovery Thread");
                return;
            }
            BrickPaintController.logger.error("[SERVER] Error When Running Discovery Process");
            ex.printStackTrace();
        }
    }
}

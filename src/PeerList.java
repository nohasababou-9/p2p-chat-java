import java.io.*;
import java.net.*;
import java.util.*;

public class PeerList {

    // Store each peer with its socket, username, and listening port
    private static class PeerInfo {
        Socket socket;
        String username;
        int listeningPort;

        PeerInfo(Socket socket, String username, int listeningPort) {
            this.socket = socket;
            this.username = username;
            this.listeningPort = listeningPort;
        }
    }

    private final List<PeerInfo> peers = Collections.synchronizedList(new ArrayList<>());

    // Add peer with username and listening port
    public void addPeer(Socket s, String username, int listeningPort) {
        peers.add(new PeerInfo(s, username, listeningPort));
    }

    // Remove peer
    public void removePeer(Socket s) {
        synchronized (peers) {
            Iterator<PeerInfo> it = peers.iterator();
            while (it.hasNext()) {
                PeerInfo p = it.next();
                if (p.socket.equals(s)) {
                    try { s.close(); } catch (IOException e) {}
                    it.remove();
                    break;
                }
            }
        }
    }

    // Broadcast message to all peers
    public void broadcast(String message) {
        synchronized (peers) {
            Iterator<PeerInfo> it = peers.iterator();
            while (it.hasNext()) {
                PeerInfo p = it.next();
                try {
                    PrintWriter out = new PrintWriter(p.socket.getOutputStream(), true);
                    out.println(message);
                } catch (IOException e) {
                    System.out.println("[PeerList] Removing unreachable peer: " + p.username + ":" + p.listeningPort);
                    try { p.socket.close(); } catch (IOException ex) {}
                    it.remove();
                }
            }
        }
    }

    // Print friendly peer list
    public void printPeers() {
        synchronized (peers) {
            if (peers.isEmpty()) {
                System.out.println("No connected peers.");
                return;
            }
            System.out.println("Connected peers:");
            for (PeerInfo p : peers) {
                System.out.println(" - " + p.username + ":" + p.listeningPort);
            }
        }
    }

    // Close all sockets
    public void closeAll() {
        synchronized (peers) {
            for (PeerInfo p : peers) {
                try { p.socket.close(); } catch (IOException e) {}
            }
            peers.clear();
        }
    }
}

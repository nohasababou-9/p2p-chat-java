import java.io.*;
import java.net.*;
import java.util.*;

public class PeerList {
    private final List<Socket> sockets = Collections.synchronizedList(new ArrayList<>());

    public void addPeer(Socket s) {
        sockets.add(s);
    }

    public void removePeer(Socket s) {
        try {
            sockets.remove(s);
            s.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public void broadcast(String message) {
        synchronized (sockets) {
            Iterator<Socket> it = sockets.iterator();
            while (it.hasNext()) {
                Socket s = it.next();
                try {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println(message);
                } catch (IOException e) {
                    System.out.println("[PeerList] Removing unreachable peer: " + s.getRemoteSocketAddress());
                    try { s.close(); } catch (IOException ex) {}
                    it.remove();
                }
            }
        }
    }

    public void printPeers() {
        synchronized (sockets) {
            if (sockets.isEmpty()) {
                System.out.println("No connected peers.");
                return;
            }
            System.out.println("Connected peers:");
            for (Socket s : sockets) {
                System.out.println(" - " + s.getRemoteSocketAddress());
            }
        }
    }

    public void closeAll() {
        synchronized (sockets) {
            for (Socket s : sockets) {
                try { s.close(); } catch (IOException e) {}
            }
            sockets.clear();
        }
    }
}

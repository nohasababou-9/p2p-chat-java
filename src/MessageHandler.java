import java.io.*;
import java.net.*;

public class MessageHandler extends Thread {
    private Socket socket;
    private PeerList peerList;
    private String username;

    public MessageHandler(Socket socket, PeerList peerList, String username) {
        this.socket = socket;
        this.peerList = peerList;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                String display = "[" + socket.getRemoteSocketAddress() + "] " + line;
                System.out.println("\n" + display);
                System.out.print("> ");
                Utils.saveIncoming(username, display);
            }
        } catch (IOException e) {
            // connection closed
        } finally {
            try { socket.close(); } catch (IOException e) {}
            peerList.removePeer(socket);
        }
    }
}

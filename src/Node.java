import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Node {
    private String username;
    private int port;
    private PeerList peerList;

    public Node(String username, int port) {
        this.username = username;
        this.port = port;
        this.peerList = new PeerList();
    }

    public PeerList getPeerList() {
        return peerList; // Add getter to access peerList from static main
    }

    public void startServer() throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("[" + username + "] Listening on port " + port);

        // Close server socket on JVM exit to avoid resource leak
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { server.close(); } catch (IOException e) {}
        }));

        Thread acceptThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = server.accept();
                    String remote = socket.getRemoteSocketAddress().toString();
                    System.out.println("[" + username + "] Incoming connection from " + remote);
                    // create handler
                    MessageHandler handler = new MessageHandler(socket, peerList, username);
                    handler.start();
                    // register writer for sending
                    peerList.addPeer(socket);
                } catch (IOException e) {
                    System.out.println("[" + username + "] Server socket closed or error: " + e.getMessage());
                    break;
                }
            }
        });
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    public void connectToPeer(String ip, int peerPort) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, peerPort), 3000);
            System.out.println("[" + username + "] Connected to " + ip + ":" + peerPort);
            // start receiver for this socket
            MessageHandler handler = new MessageHandler(socket, peerList, username);
            handler.start();
            // register writer
            peerList.addPeer(socket);
        } catch (IOException e) {
            System.out.println("[" + username + "] Failed to connect to " + ip + ":" + peerPort + " -> " + e.getMessage());
        }
    }

    public void broadcast(String msg) {
        String formatted = username + ": " + msg;
        peerList.broadcast(formatted);
        Utils.saveOutgoing(username, formatted);
    }

    public void printHistoryFile() {
        System.out.println("History file: " + Utils.historyFileName(username));
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java Node <username> <port>");
            return;
        }
        String name = args[0];
        int port = Integer.parseInt(args[1]);

        Node node = new Node(name, port);
        node.startServer();

        Scanner sc = new Scanner(System.in);
        System.out.println("Commands: /connect <ip> <port>  |  /peers  |  /history  |  /quit");

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine();
            if (line == null) break;
            line = line.trim();
            if (line.length() == 0) continue;

            if (line.startsWith("/connect")) {
                String[] parts = line.split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Usage: /connect <ip> <port>");
                    continue;
                }
                node.connectToPeer(parts[1], Integer.parseInt(parts[2]));
            } else if (line.equals("/peers")) {
                node.getPeerList().printPeers();  // ✅ fixed
            } else if (line.equals("/history")) {
                node.printHistoryFile();
            } else if (line.equals("/quit")) {
                System.out.println("Shutting down...");
                node.getPeerList().closeAll();    // ✅ fixed
                break;
            } else {
                node.broadcast(line);
            }
        }
        sc.close();
    }
}


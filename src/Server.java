import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
    private int port;
    private static final List<OutputStream> clientOutputStreams = Collections.synchronizedList(new ArrayList<>());

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + port);

            while (true) {
                // Accept incoming client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Get client's output stream and add it to the list for broadcasting
                synchronized (clientOutputStreams) {
                    clientOutputStreams.add(clientSocket.getOutputStream());
                }

                // Start a new worker thread for handling this client
                Worker worker = new Worker(clientSocket);
                worker.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }

    // Worker class to handle each client separately
    class Worker extends Thread {
        private Socket socket;
        private BufferedReader reader;

        public Worker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received: " + message);
                    broadcastMessage(message, socket.getInetAddress().toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    synchronized (clientOutputStreams) {
                        clientOutputStreams.remove(socket.getOutputStream());
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message, String senderIp) {
            synchronized (clientOutputStreams) {
                for (OutputStream out : clientOutputStreams) {
                    try {
                        PrintWriter writer = new PrintWriter(out, true);
                        writer.println(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

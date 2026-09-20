package chess;

import java.io.EOFException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        int port = 5000;
        Schachlogik spiel = new Schachlogik();

        ExecutorService threadPool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening for objects on port " + port);
            int random = (int) (Math.random()*2);
            boolean isWhite = random == 1;
            int connectedinstanzes = 0;
            while (connectedinstanzes < 2) {
                connectedinstanzes++;
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());

                threadPool.submit(new ClientHandler(socket,isWhite));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final boolean isWhite;

        public ClientHandler(Socket socket,boolean isWhite) {
            this.socket = socket;
            this.isWhite = isWhite;
        }

        @Override
        public void run() {
            try (Socket clientSocket = this.socket;
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                in.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
                    "Move;java.lang.*;!*"
                ));
                while (true) {
                    Object object = in.readObject();
                    if (object instanceof Move) {

                    }

                }
            } catch (EOFException e) {
                System.out.println("Client disconnected cleanly: " + socket.getRemoteSocketAddress());
            } catch (Exception e) {
                System.out.println("Client handler error (" + socket.getRemoteSocketAddress() + "): " + e.getMessage());
            }
        }
    }
}
package connect4;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class connect4Server implements Runnable {
    private static final int PORT = 55555;
    private ServerSocket serverSocket;
    private volatile boolean running = false;

    public static void main(String[] args) {
        connect4Server server = new connect4Server();
        server.run();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Chess connect4Server started on port " + PORT);
            System.out.println("IpAddress: " + Inet4Address.getLocalHost().getHostAddress());
            Map<String, ClientHandler> waitingHosts = new HashMap<>();

            while (running) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleConnection(socket, waitingHosts)).start();
            }
        }catch (SocketException e){
            System.out.println("Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleConnection(Socket socket, Map<String, ClientHandler> waitingHosts) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Object request = in.readObject();
            System.out.println("new client");

            if ("HOST".equals(request)) {
                StringBuilder buffer = new StringBuilder(5);
                for (int i = 0; i < 5; i++) {
                    int randomChar = (int) (Math.random() * 26) + 97;
                    buffer.append((char) randomChar);
                }
                String roomCode = buffer.toString();

                ClientHandler hostHandler = new ClientHandler(socket, in, out);

                synchronized (waitingHosts) {
                    waitingHosts.put(roomCode, hostHandler);
                }

                System.out.println("Host created room: " + roomCode);
                out.writeObject("CODE:" + roomCode);
                out.flush();

            } else if ("JOIN".equals(request)) {
                Object code = in.readObject();
                if (code instanceof String roomCode) {

                    ClientHandler hostHandler;
                    synchronized (waitingHosts) {
                        hostHandler = waitingHosts.remove(roomCode);
                    }

                    if (hostHandler != null) {
                        ClientHandler joinerHandler = new ClientHandler(socket, in, out);
                        System.out.println("Player joined room: " + roomCode);
                        joinerHandler.sendObject("JOINED");
                        hostHandler.sendObject("FRIEND_JOINED");
                        GameRoom room = new GameRoom(hostHandler, joinerHandler);
                        room.startChessRoom();
                    } else {
                        out.writeObject("ERROR: Invalid Code");
                        out.flush();
                        socket.close();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error establishing connection: " + e.getMessage());
        }
    }
}

class GameRoom {
    private final ClientHandler player1;
    private final ClientHandler player2;
    connect4_gameLogic game = new connect4_gameLogic();

    public GameRoom(ClientHandler player1, ClientHandler player2) {
        this.player1 = player1;
        this.player2 = player2;
        if(Math.random() < 0.5) {
            this.player1.setRoom(this, true);  // White
            this.player2.setRoom(this, false); // Black
        }else{
            this.player1.setRoom(this, false);
            this.player2.setRoom(this, true);
        }
    }

    public void startChessRoom() {
        new Thread(player1).start();
        new Thread(player2).start();
    }

    public void broadcast(Object obj) {
        player1.sendObject(obj);
        player2.sendObject(obj);
    }

    public void processMove(ClientHandler sender, Integer move) {
        if (game.won != 0) {
            game.resetField();
        }
        if(sender.isPlayer1() == game.firstPlayerPlaying) {
            game.move(move);
            broadcast(move);
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private GameRoom room;
    private boolean isPlayer1;

    public ClientHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public boolean isPlayer1() {
        return isPlayer1;
    }

    public void setRoom(GameRoom room, boolean isPlayer1) {
        this.room = room;
        this.isPlayer1 = isPlayer1;
    }

    public void sendObject(Object obj) {
        try {
            if (out != null && !socket.isClosed()) {
                out.writeObject(obj);
                out.flush();
            }
        } catch (SocketException _) {
        } catch (IOException e) {
            System.err.println("Failed to send object: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        this.sendObject(isPlayer1);
        try {
            Object receivedObject;
            while ((receivedObject = in.readObject()) != null) {
                if (receivedObject instanceof Integer move) {
                    room.processMove(this, move);
                }
            }
        } catch (Exception e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                socket.close();
                room.broadcast("DISCONNECT");
            } catch (IOException ignored) {}
        }
    }
}
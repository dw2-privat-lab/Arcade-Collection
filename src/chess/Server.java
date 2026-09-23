package chess;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chess Server started on port " + PORT);
            Map<String, ClientHandler> waitingHosts = new HashMap<>();

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleConnection(socket, waitingHosts)).start();
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

            if ("HOST".equals(request)) {
                StringBuilder buffer = new StringBuilder(10);
                for (int i = 0; i < 10; i++) {
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
                        ChessRoom room = new ChessRoom(hostHandler, joinerHandler);
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

class ChessRoom {
    private final ClientHandler player1;
    private final ClientHandler player2;
    private int resetRequest = 0;
    Schachlogik game = new Schachlogik();

    public ChessRoom(ClientHandler player1, ClientHandler player2) {
        this.player1 = player1;
        this.player2 = player2;
        if(Math.random() < 0.5) {
            this.player1.setChessRoom(this, true);  // White
            this.player2.setChessRoom(this, false); // Black
        }else{
            this.player1.setChessRoom(this, false);
            this.player2.setChessRoom(this, true);
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

    public void processMove(ClientHandler sender, Move move) {
        if (game.isWhite(move.x, move.y) == sender.isWhite()) {
            if (game.canMove(move.x, move.y, move.toX, move.toY)) {
                game.move(move.x, move.y, move.toX, move.toY);
                broadcast(move);
            }
        }
    }

    public void countResetRequests(ClientHandler sender) {
        if(!sender.isSentReset()) {
            sender.setSentReset(true);
            resetRequest++;
        }
        if(resetRequest == 2){
            resetRequest = 0;
            player1.setSentReset(false);
            player2.setSentReset(false);
            game.reset();
            broadcast("RESET");
            if(Math.random() < 0.5) {
                this.player1.setChessRoom(this, true);  // White
                this.player2.setChessRoom(this, false); // Black
            }else{
                this.player1.setChessRoom(this, false);
                this.player2.setChessRoom(this, true);
            }
            player1.sendObject(player1.isWhite());
            player2.sendObject(player2.isWhite());
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private ChessRoom room;
    private boolean isWhite;
    private boolean hasSentReset = false;

    public ClientHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void setSentReset(boolean sentReset) {
        this.hasSentReset = sentReset;
    }

    public boolean isSentReset() {
        return hasSentReset;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setChessRoom(ChessRoom room, boolean isWhite) {
        this.room = room;
        this.isWhite = isWhite;
    }

    public void sendObject(Object obj) {
        try {
            if (out != null) {
                out.writeObject(obj);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // Send player color assignment to client
        this.sendObject(isWhite);
        try {
            Object receivedObject;
            while ((receivedObject = in.readObject()) != null) {
                if (receivedObject instanceof Move move) {
                    room.processMove(this, move);
                }
                if (receivedObject instanceof String resetString) {
                    if(resetString.equals("Reset"))
                        room.countResetRequests(this);
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
package chess;

import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chess Server started on port " + PORT);

            ClientHandler waitingClient = null;

            while (true) {//server laufen lassen und jede neue connection zulassen
                Socket socket = serverSocket.accept();


                ClientHandler currentClient = new ClientHandler(socket);
                //check, ob das der erste client ist der verbunden wurde
                if (waitingClient == null) {
                    waitingClient = currentClient;
                } else {
                    ChessRoom room = new ChessRoom(waitingClient, currentClient);
                    room.startChessRoom();
                    waitingClient = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



class ChessRoom {
    private final ClientHandler player1;
    private final ClientHandler player2;
    Schachlogik game = new Schachlogik();

    public ChessRoom(ClientHandler player1, ClientHandler player2) {
        this.player1 = player1;
        this.player2 = player2;
        if(Math.random()>0.5) {
            this.player1.setChessRoom(this, true);
            this.player2.setChessRoom(this, false);
        }
        else{

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
        if(game.isWhite(move.x,move.y)==sender.isWhite()) {
            if (game.canMove(move.x, move.y, move.toX, move.toY)) {
                broadcast(move);
            }
        }

    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private ChessRoom room;
    private boolean isWhite;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            Object receivedObject;
            while ((receivedObject = in.readObject()) != null) {
                if (receivedObject instanceof Move) {
                    Move move = (Move) receivedObject;

                    room.processMove(this, move);
                }
            }
        } catch (Exception _) {}
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

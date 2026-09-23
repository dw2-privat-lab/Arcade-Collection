package chess;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class MessageHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final String host;
    private final int port;
    private final String code;
    private final boolean isHost;
    private final EventListenerList listenerList = new EventListenerList();

    public MessageHandler(String host, int port, String code, boolean isHost, ActionListener actionListener) {
        this.host = host;
        this.port = port;
        this.code = code;
        this.isHost = isHost;

        if (actionListener != null) {
            listenerList.add(ActionListener.class, actionListener);
        }
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            if (isHost) {
                out.writeObject("HOST");
                out.flush();

                Object response = in.readObject();
                if (response instanceof String CodeMessage && CodeMessage.startsWith("CODE:")) {
                    String roomCode = CodeMessage.substring(5);
                    System.out.println("Hosted room with code: " + roomCode);
                    fireEvent(new ActionEvent(roomCode, ActionEvent.ACTION_PERFORMED, "RoomCodeGenerated"));
                    String friendJoined = (String)in.readObject();
                    if (friendJoined.equals("FRIEND_JOINED")) {
                        fireEvent(new ActionEvent(friendJoined, ActionEvent.ACTION_PERFORMED, "FriendJoined"));
                    }

                }
            } else {
                out.writeObject("JOIN");
                out.writeObject(code);
                out.flush();
                String response = (String) in.readObject();
                if(response.equals("JOINED")) {
                    fireEvent(new ActionEvent(new String("joined"), ActionEvent.ACTION_PERFORMED, "IncomingMessage"));
                }
            }
            System.out.println("joined");

            while (!socket.isClosed()) {
                Object readObject = in.readObject();

                if (readObject instanceof Move move) {
                    fireEvent(new ActionEvent(move, ActionEvent.ACTION_PERFORMED, "IncomingMove"));
                } else if (readObject instanceof String message) {
                    fireEvent(new ActionEvent(message, ActionEvent.ACTION_PERFORMED, "IncomingMessage"));
                } else if (readObject instanceof Boolean isWhite) {
                    fireEvent(new ActionEvent(isWhite, ActionEvent.ACTION_PERFORMED, "IncomingColor"));
                } else if (readObject instanceof Integer piece) {
                    fireEvent(new ActionEvent(piece, ActionEvent.ACTION_PERFORMED, "IncomingPiece"));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Connection closed or lost: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void fireEvent(ActionEvent event) {
        SwingUtilities.invokeLater(() -> {
            for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
                listener.actionPerformed(event);
            }
        });
    }
    public void sendChosenPiece(int piece) {
        Integer Piece = piece;
        try {
            if (out != null) {
                out.writeObject(Piece);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(Move move) {
        try {
            if (out != null) {
                out.writeObject(move);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendResetRequest() {
        try {
            if (out != null) {
                out.writeObject("Reset");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}
    }
}
package connect4;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class onlineHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final String host;
    private final int port;
    private final String Code;
    private final boolean isHost;
    private final EventListenerList listenerList = new EventListenerList();

    public onlineHandler(String host, int port, boolean isHost,String Code, ActionListener actionListener) {
        this.host = host;
        this.port = port;
        this.isHost = isHost;
        this.Code = Code;

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
                    fireEvent(new ActionEvent(roomCode, ActionEvent.ACTION_PERFORMED, "RoomHosted"));

                    String friendJoined = (String)in.readObject();
                    if (friendJoined.equals("FRIEND_JOINED"))
                        fireEvent(new ActionEvent(friendJoined, ActionEvent.ACTION_PERFORMED, "FRIEND_JOINED"));
                }
            }
            else {
                out.writeObject("JOIN");
                out.flush();
                out.writeObject(Code);
                out.flush();
                Object response = in.readObject();
                if ("JOINED".equals(response)) {
                    fireEvent(new ActionEvent("JOINED", ActionEvent.ACTION_PERFORMED, "JOINED"));
                } else if (response instanceof String strResp && strResp.startsWith("ERROR:")) {
                    fireEvent(new ActionEvent(strResp.substring(7), ActionEvent.ACTION_PERFORMED, "ERROR"));
                    return;
                }
            }
            while (!socket.isClosed()) {
                Object readObject = in.readObject();
                if (readObject instanceof Integer move) {
                    fireEvent(new ActionEvent(move, ActionEvent.ACTION_PERFORMED, "Incoming Move"));
                }
                else if (readObject instanceof Boolean isYellow) {
                    fireEvent(new ActionEvent(isYellow, ActionEvent.ACTION_PERFORMED, "Incoming Color"));
                } else if (readObject instanceof String) {
                    if (readObject.equals("DISCONNECT"))
                        fireEvent(new ActionEvent("DISCONNECTED", ActionEvent.ACTION_PERFORMED, "DISCONNECTED"));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            String msg = e.getMessage();
            if (!"Socket closed".equals(msg)) {
                switch (msg) {
                    case "Network is unreachable: connect" ->
                            fireEvent(new ActionEvent("Connection closed or lost: \nChessServer network unreachable", ActionEvent.ACTION_PERFORMED, "ERROR"));
                    case "Connection timed out: connect" ->
                            fireEvent(new ActionEvent("Connection closed or lost: \nConnection timed out", ActionEvent.ACTION_PERFORMED, "ERROR"));
                    case "Connection refused: connect" ->
                            fireEvent(new ActionEvent("Connection closed or lost: \nChessServer not running on port", ActionEvent.ACTION_PERFORMED, "ERROR"));
                    default ->
                            fireEvent(new ActionEvent("Connection closed or lost: \n" + msg, ActionEvent.ACTION_PERFORMED, "ERROR"));
                }
            }
            System.err.println("Connection closed or lost: " + msg);
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

    public void sendMove(Integer move) {
        System.out.println("Sending move: " + move);
        try {
            if (out != null) {
                out.writeObject(move);
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
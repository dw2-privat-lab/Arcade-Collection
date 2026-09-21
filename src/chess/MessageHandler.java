package chess;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class MessageHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    EventListenerList listenerList = new EventListenerList();

    public MessageHandler(Socket socket, ActionListener actionListener) {
        this.socket = socket;

        if (actionListener != null) {
            listenerList.add(ActionListener.class, actionListener);
        }

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object readObject = in.readObject();
                if (readObject != null && readObject instanceof Move) {
                    Move move = (Move) readObject;
                    ActionEvent moveEvent = new ActionEvent(move, ActionEvent.ACTION_PERFORMED, "IncomingMove");

                    for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
                        listener.actionPerformed(moveEvent);
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
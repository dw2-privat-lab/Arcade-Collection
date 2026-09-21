package chess;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientPanel extends Chess_Singleplayer_panel implements ActionListener {
    String host="localhost";
    int port=8888;
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    MessageHandler messageHandler;
    public ClientPanel(ActionListener actionListener){
        super(actionListener);
        try {
            socket = new Socket(host, port);

            messageHandler = new MessageHandler(socket,this);
            Thread messageThread = new Thread(messageHandler);
            messageThread.start();
        }catch (Exception _){}
    }
    public void setHost(String host) {
        this.host = host;
    }
    public void setPort(int port) {
        this.port = port;
    }
    @Override
    protected void sendMove(int x,int y, int toX, int toY) {
        try {
            out.writeObject(new Move(x,y,toX,toY));
        }catch (Exception e){}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("IncomingMove".equals(e.getActionCommand())) {

            Move receivedMove = (Move) e.getSource();

            spiel.move(receivedMove.x, receivedMove.y, receivedMove.toX, receivedMove.toY);

            repaint();
        }
    }
}

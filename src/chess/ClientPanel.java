package chess;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientPanel extends Chess_Singleplayer_panel {
    String host="localhost";
    int port=8888;
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    public ClientPanel(ActionListener actionListener){
        super(actionListener);

        try {
            socket = new Socket(host,port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            while(true){
                Object readObject = in.readObject();
                if(readObject!=null){
                    Move move = (Move) readObject;
                    spiel.move(move.x,move.y,move.toX,move.toY);
                }





            }




        }catch (Exception e){}
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
}
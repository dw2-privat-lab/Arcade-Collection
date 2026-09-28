package connect4;

import java.awt.*;
import java.util.ArrayList;

public class RenderMessage implements Runnable {
    private final ArrayList <String> Message = new ArrayList<>();
    private final ArrayList<Integer> FadeTimer = new ArrayList<>();

    connect4MainPanel panel;
    public RenderMessage(connect4MainPanel panel){
        this.panel = panel;
    }
    public void addMessage(String message, int fadeTimer){
        Message.add(message);
        FadeTimer.add(fadeTimer);
        panel.repaint();
    }

    public void reduceFadeTimer(){
        for(int i=0;i<3;i++){
            if(i<FadeTimer.size()) {
                FadeTimer.set(i, FadeTimer.get(i) - 1);
                if (FadeTimer.get(i) <= 0) {
                    FadeTimer.remove(i);
                    Message.remove(i);
                    panel.repaint();
                }
            }
        }
    }
    public ArrayList<String> getMessages(){
        return Message;
    }
    public String getMessage(int index){
        return Message.get(index);
    }

    @Override
    public void run() {
        while(true){
            try {
                reduceFadeTimer();
                Thread.sleep(100);
            }catch (InterruptedException _){}
        }
    }
}

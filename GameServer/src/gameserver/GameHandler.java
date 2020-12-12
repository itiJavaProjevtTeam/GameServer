/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver;

/**
 *
 * @author abdelrahmanelnagdy
 */
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abdelrahmanelnagdy
 */
  public class GameHandler extends Thread{
    DataInputStream dis;
    PrintStream ps;
    static Vector<GameHandler> clientsVector = new Vector<GameHandler>();
    public GameHandler(Socket cs) {
        try {
            dis = new DataInputStream(cs.getInputStream());
            ps  = new PrintStream(cs.getOutputStream());
            clientsVector.add(this);
            start();
        } catch (IOException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void run(){
        while (true){
        
            try {
                String str = dis.readLine();
                sendMessageToAll(str);
            } catch (IOException ex) {
                stop();
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        
        
        }
            
     }
    
    public void sendMessageToAll(String msg){
        for(GameHandler sh : clientsVector){
            
                sh.ps.println(msg);
        }
    }
    // when signed in online player +1
    // when signed out offLine Players + 1 , online -1
}
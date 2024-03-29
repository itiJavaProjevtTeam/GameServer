/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver;

import static gameserver.GameHandler.clientsVector;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abdelrahmanelnagdy
 */
public class MainServer extends Thread {

    static Vector<GameHandler> SocketVector;
    ServerSocket myServerSocket;
    Socket s;
    static int onlinePlayers = 0;
    static int offlinePlayers = 0;
    static MainServer mainServer;
    private DbConnection.DbConnectionHandler dbconnection;


    private MainServer() {
        
        try {
            myServerSocket = new ServerSocket(5007);
            SocketVector = new Vector<GameHandler>();
            dbconnection = DbConnection.DbConnectionHandler.CreateConnection();
            onlinePlayers = dbconnection.getOnlinePlayers();
            offlinePlayers = dbconnection.getOFFlinePlayers();
            
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public static MainServer getInstance(){
        if (mainServer == null){
             return mainServer = new MainServer();
        } else {
            return mainServer;
        }
        
    }

    void closeClients() {
        for (GameHandler sh : SocketVector) {
            sh.stop();
        }

    }

    public void run() {
        while (true) {
            try {
                s = myServerSocket.accept();
                GameHandler handle = new GameHandler(s);
                SocketVector.add(handle);
            } catch (IOException ex) {
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

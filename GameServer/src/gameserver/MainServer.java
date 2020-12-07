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

    public MainServer() {
        try {
            myServerSocket = new ServerSocket(5011);
            SocketVector = new Vector<GameHandler>();
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
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

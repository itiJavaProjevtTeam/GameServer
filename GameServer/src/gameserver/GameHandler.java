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
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abdelrahmanelnagdy
 */
public class GameHandler extends Thread {

    DataInputStream dis;
    PrintStream ps;
    String[] parsedMsg;
    private DbConnection.DbConnectionHandler dbconnection;

    static Vector<GameHandler> clientsVector = new Vector<GameHandler>();

    public GameHandler(Socket cs) {
        try {
            dbconnection = DbConnection.DbConnectionHandler.CreateConnection();
            dis = new DataInputStream(cs.getInputStream());
            ps = new PrintStream(cs.getOutputStream());
            clientsVector.add(this);
            start();
        } catch (IOException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        initializeDB(); // connection opened
        while (true) {

            try {
                String message = dis.readLine();
//                sendMessageToAll(str);
                if (message == null) ;
                else if (parseMessage(message) == 1){
                    if(!checkUserExistence(parsedMsg[1])){
                        addUser(parsedMsg[1], parsedMsg[2]);
                        ++MainServer.offlinePlayers;
                        updatePlayeStatus(parsedMsg[1]);
                        ps.println("register done");
                    } else {
                        System.out.println("User name is alreasdy in use");
                        ps.println("Cannot register player");
                    }
                } else if (parseMessage(message) == 2){
                    if(checkUserExistence(parsedMsg[1])){
                        updatePlayeStatus(parsedMsg[1]);
                        ++MainServer.onlinePlayers;
                        ps.println("sign in Succeeded#" + getPlayerScore(parsedMsg[1]));
                    } else {
                        ps.println("Cannot sign in");
                    }   
                } else if (parseMessage(message) == 5) {
                    ps.println(dbconnection.getOnlinePlayersList());
                }


            } catch (IOException ex) {
                stop();
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);

            }

        }

    }

    public void sendMessageToAll(String msg) {
        for (GameHandler sh : clientsVector) {

            sh.ps.println(msg);
        }
        // when signed in online player +1
        // when signed out offLine Players + 1 , online -1

    }

    public void initializeDB() {
        dbconnection.openConnection();
    }

    public void addUser(String playeName, String password) {
        dbconnection.Signup(playeName, password);
    }
    

    public int parseMessage(String requestMessage) {
        if (requestMessage == null) {
            return -1;
        }
        parsedMsg = requestMessage.split("\\#");
        if (parsedMsg[0].equals("REG")) { // register
            return 1;
        }
        if (parsedMsg[0].equals("SIGN")) { // sign in
            return 2;
        }
        if (parsedMsg[0].equals("PLAY")) { // Playing
            return 3;
        }
        if (parsedMsg[0].equals("FINISH")) { // finished playing
            return 4;
        }
        if (parsedMsg[0].equals("PLAYERLIST")) { // request PlayerList
            return 5;
        } else {
            return 6; // signOut
        }

    }

    public void updatePlayeStatus(String playeName) {
        dbconnection.UpdateScore(playeName, MIN_PRIORITY);

    }

    public long getPlayerScore(String playerName) {
        return dbconnection.GetScore(playerName);

    }
    
    public boolean checkUserExistence(String username){
        if (dbconnection.checkUserExistence(username)){
        return true;
        } else {
        return false;
        }
    }
    

}

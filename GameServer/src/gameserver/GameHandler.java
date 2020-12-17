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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String[] parsedMsg;
    private DbConnection.DbConnectionHandler dbconnection;

    static Vector<GameHandler> clientsVector = new Vector<GameHandler>();

    public GameHandler(Socket cs) {
        try {
            dbconnection = DbConnection.DbConnectionHandler.CreateConnection();
            dataOutputStream = new DataOutputStream(cs.getOutputStream());
            dataInputStream = new DataInputStream(cs.getInputStream());

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
                String message = dataInputStream.readUTF();
                System.out.println("The message sent from the socket was: " + message);
//                sendMessageToAll(str);
                if (message == null) ; else if (parseMessage(message) == 1) {
                    if (!checkUserExistence(parsedMsg[0])) {
                        addUser(parsedMsg[0], parsedMsg[1]);
                        ++MainServer.offlinePlayers;
                        updatePlayeStatus(parsedMsg[0]);
                        System.out.print("Registered");
                        dataOutputStream.writeUTF("register done");
                    } else {
                        System.out.println("User name is alreasdy in use");
                        dataOutputStream.writeUTF("Cannot register player");
                    }
                } else if (parseMessage(message) == 2) {
                    if (checkUserExistence(parsedMsg[0])) {
                        updatePlayeStatus(parsedMsg[0]);
                        signIn(parsedMsg[0], parsedMsg[1]);
                        System.out.print("signed in");
                        ++MainServer.onlinePlayers;
                        --MainServer.offlinePlayers;
                        dataOutputStream.writeUTF("sign in Succeeded#" + getPlayerScore(parsedMsg[0]));
                    } else {
                        dataOutputStream.writeUTF("Cannot sign in");
                    }
                } else if (parseMessage(message) == 5) {
                    dataOutputStream.writeUTF(dbconnection.getOnlinePlayersList());
                } else if (parseMessage(message) == 6) {

//                    dataOutputStream.writeUTF(dbconnection.GetScore(""));
                }else if (parseMessage(message) == 7) {
                    goOffline(parsedMsg[0]);
                    dataOutputStream.writeUTF("Player went offline succefully");
                    --MainServer.onlinePlayers;
                    ++MainServer.offlinePlayers;
                }

                /*
                dataOutputStream.flush();    // send the message
                dataOutputStream.close();  */  // close the stream
            } catch (IOException ex) {
                stop();
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);

            }

        }

    }

    public void sendMessageToAll(String msg) throws IOException {
        for (GameHandler sh : clientsVector) {

            sh.dataOutputStream.writeUTF(msg);
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
        parsedMsg = requestMessage.split("\\.");
        if (parsedMsg[2].equals("UP")) { // register
            return 1;
        }
        if (parsedMsg[2].equals("IN")) { // sign in
            return 2;
        }
        if (parsedMsg[2].equals("PLAY")) { // Playing
            return 3;
        }
        if (parsedMsg[2].equals("FINISH")) { // finished playing
            return 4;
        }
        if (parsedMsg[2].equals("PLAYERLIST")) { // request PlayerList
            return 5;
        }
        if (parsedMsg[2].equals("SCORELIST")) { // request SCORELIST
            return 6;
        }
        if(parsedMsg[2].equals("LOGOUT"))  { // logout request
            return 7;
        } else {
            return 100; // signOut
        }

    }

    public void updatePlayeStatus(String playeName) {
        dbconnection.updateStatus(playeName); // score needed

    }

    public long getPlayerScore(String playerName) {
        return dbconnection.GetScore(playerName);

    }

    public boolean signIn(String userName, String password) {
        return dbconnection.Signin(userName, password);
    }

    public boolean checkUserExistence(String username) {
        if (dbconnection.checkUserExistence(username)) {
            return true;
        } else {
            return false;
        }
    }
    
    public void goOffline(String playeName) {
        dbconnection.updateStatus(playeName); // score needed

    }

}

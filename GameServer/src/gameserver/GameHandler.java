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
import java.sql.ResultSet;
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
    String Pname;
    String Score;
    String GID;
    String P1;
    String P2;
    String P1Score;
    String P2Score;
    String Winner;

    boolean checkUserExistence;
    boolean checkValidPassword;

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
                    //sign up
                    if (!parsedMsg[0].isEmpty() && !parsedMsg[1].isEmpty()) {
                        checkUserExistence = checkUserExistence(parsedMsg[0]);
                        if (checkUserExistence == true) {
                            dataOutputStream.writeUTF("ALREADY EXISTS");

                        } else {
                            addUser(parsedMsg[0], parsedMsg[1]);
                            ++MainServer.offlinePlayers;
                            updatePlayeStatus(parsedMsg[0]);
                            System.out.print("Registered");
                            dataOutputStream.writeUTF("register done");
                        }

                    } else {
                        dataOutputStream.writeUTF("NO ENTRY");
                    }
                    
                    
                 //sign in
                } else if (parseMessage(message) == 2) {
                    if (!parsedMsg[0].isEmpty() && !parsedMsg[1].isEmpty()) {
                        checkUserExistence = checkUserExistence(parsedMsg[0]);
                        checkValidPassword = checkValidPassword(parsedMsg[0],parsedMsg[1]);
                        if (checkUserExistence == true && checkValidPassword == true ) {
                            updatePlayeStatus(parsedMsg[0]);
                            ++MainServer.onlinePlayers;
                            --MainServer.offlinePlayers;
                            dataOutputStream.writeUTF("sign in Succeeded#" + getPlayerScore(parsedMsg[0]));
                        } else if(checkValidPassword == false) {
                            dataOutputStream.writeUTF("NOT Valid Pass");
                        }
                        else if(checkUserExistence == false)
                        {
                           dataOutputStream.writeUTF("NOT Valid Name");
                        }                       
                        else
                        {
                            dataOutputStream.writeUTF("NOT FOUND");
                        }
                    } else {
                        dataOutputStream.writeUTF("NO ENTRY");
                    }


                } else if (parseMessage(message) == 5) {
                    System.out.print("Player + score " + Pname + Score);
                    dataOutputStream.writeUTF(Pname + Score);

                    System.out.print("playerList send successfully");
                    dataOutputStream.flush();

                } else if (parseMessage(message) == 6) {

//                    dataOutputStream.writeUTF(dbconnection.GetScore(""));
                } else if (parseMessage(message) == 7) {
                    goOffline(parsedMsg[0]);
                    dataOutputStream.writeUTF("Player went offline succefully");
                    --MainServer.onlinePlayers;
                    ++MainServer.offlinePlayers;

                }
                else if(parseMessage(message) == 8)
                {
                    System.out.print("GID+P1+P2+Winner " + GID+P1+P2+Winner);
                    dataOutputStream.writeUTF(GID+P1+P2+Winner);
                    System.out.print("History send successfully");
                    dataOutputStream.flush();
                
                }
                else if(parseMessage(message) == 9)
                {
                    System.out.println(message);
                    sendMessageToAll(message);
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
            getOnLinePlayers();
            return 5;
        }
        if (parsedMsg[2].equals("SCORELIST")) { // request SCORELIST
            return 6;
        }
        if (parsedMsg[2].equals("LOGOUT")) { // logout request
            return 7;
        } 
        if(parsedMsg[2].equals("History"))
        {
            getPlayedGames();
            return 8;
        }
        if(parsedMsg[2].equals("DUWTP"))
        {
            getPlayedGames();
            return 9;
        }
        else {
            return 100; // signOut
        }

    }

    public void updatePlayeStatus(String playeName) {
        dbconnection.updateStatus(playeName); // score needed

    }

    public long getPlayerScore(String playerName) {
        return dbconnection.GetScore(playerName);

    }
/*
    public boolean signIn(String userName, String password) {
        return dbconnection.Signin(userName, password);
    }
*/

    public boolean checkUserExistence(String username) {
        if (dbconnection.checkUserExistence(username)) {
            return true;
        } else {
            return false;
        }
    }    

    public boolean checkValidPassword(String username,String Password) {
        if (dbconnection.checkValidPassword(username,Password)) {
            return true;
        } else {
            return false;
        }

    }
    

    public void getOnLinePlayers() {

        ResultSet s = dbconnection.getOnlinePlayersList();
        Pname = "";
        Score = "";
        if (s == null) {
            System.out.println("no data in table");

        } else {
            try {
                while (s.next()) {
                    Pname += s.getString(1) + ".";
                    Score += String.valueOf(s.getInt(2)) + ".";

                }
            } catch (SQLException ex) {
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
    
     public void getPlayedGames() {

        ResultSet s = dbconnection.getOnlinePlayersList();
        GID = "";
        P1 = "";
        P2 = "";
        Winner = "";
        P1Score = "";
        P2Score = "";
        if (s == null) {
            System.out.println("no data in table");

        } else {
            try {
                while (s.next()) {
                    GID += String.valueOf(s.getInt(1)) + ".";
                    P1 += s.getString(2) + ".";
                    P1Score += String.valueOf(dbconnection.GetScoreToHistoryTable(s.getString(2))) + ".";
                    P2 += s.getString(3) + ".";
                    P2Score += String.valueOf(dbconnection.GetScoreToHistoryTable(s.getString(3))) + ".";
                    Winner +=s.getString(4) + ".";

                }
            } catch (SQLException ex) {
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public void goOffline(String playeName) {
        dbconnection.updateStatus(playeName); // score needed

    }

}


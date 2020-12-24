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
    String symbol;
    String Positions;
    String MovesPlayerName;
    boolean checkUserExistence;
    boolean checkValidPassword;
    boolean flagTurn;
    String moves;
    private DbConnection.DbConnectionHandler dbconnection;

    static Vector<GameHandler> clientsVector = new Vector<GameHandler>();

    public GameHandler(Socket cs) {
        try {
            dbconnection = DbConnection.DbConnectionHandler.CreateConnection();
            dataOutputStream = new DataOutputStream(cs.getOutputStream());
            dataInputStream = new DataInputStream(cs.getInputStream());

            clientsVector.add(this);
            start();
            flagTurn = true;
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
                    if (!parsedMsg[1].isEmpty() && !parsedMsg[2].isEmpty()) {
                        checkUserExistence = checkUserExistence(parsedMsg[0]);
                        if (checkUserExistence == true) {
                            dataOutputStream.writeUTF("ALREADY EXISTS");

                        } else {
                            addUser(parsedMsg[1], parsedMsg[2]);
                            ++MainServer.offlinePlayers;
                            updatePlayeStatus(parsedMsg[1]);
                            System.out.println("Registered");
                            dataOutputStream.writeUTF("register done");
                        }

                    } else {
                        dataOutputStream.writeUTF("NO ENTRY");
                    }

                    //sign in
                } else if (parseMessage(message) == 2) {
                    if (!parsedMsg[1].isEmpty() && !parsedMsg[2].isEmpty()) {
                        checkUserExistence = checkUserExistence(parsedMsg[1]);
                        checkValidPassword = checkValidPassword(parsedMsg[1], parsedMsg[2]);
                        if (checkUserExistence == true && checkValidPassword == true) {
                            updatePlayeStatus(parsedMsg[1]);
                            ++MainServer.onlinePlayers;
                            --MainServer.offlinePlayers;
                            dataOutputStream.writeUTF("sign in Succeeded#" + getPlayerScore(parsedMsg[1]));
                        } else if (checkValidPassword == false) {
                            dataOutputStream.writeUTF("NOT Valid Pass");
                        } else if (checkUserExistence == false) {
                            dataOutputStream.writeUTF("NOT Valid Name");
                        } else {
                            dataOutputStream.writeUTF("NOT FOUND");
                        }
                    } else {
                        dataOutputStream.writeUTF("NO ENTRY");
                    }

                } else if (parseMessage(message) == 5) {
                    getOnLinePlayers(parsedMsg[1]);

                    dataOutputStream.writeUTF("PLAYERLIST." + parsedMsg[1] + "." + Pname + Score);

                    System.out.println("playerList send successfully");
                    dataOutputStream.flush();

                } else if (parseMessage(message) == 6) {

//                    dataOutputStream.writeUTF(dbconnection.GetScore(""));
                } else if (parseMessage(message) == 7) {
                    goOffline(parsedMsg[1]);
                    dataOutputStream.writeUTF("Player went offline succefully");
                    --MainServer.onlinePlayers;
                    ++MainServer.offlinePlayers;

                } else if (parseMessage(message) == 8) {
                    System.out.println("GID+P1+P2+Winner " + GID + P1 + P1Score + P2 + P2Score + Winner);
                    dataOutputStream.writeUTF(GID + "_" + P1 + "_" + P1Score + "_" + P2 + "_" + P2Score + "_" + Winner);
                    System.out.println("History send successfully");
                    dataOutputStream.flush();

                } else if (parseMessage(message) == 9) {
                    System.out.println(message);
                    sendMessageToAll(message);
                } else if (parseMessage(message) == 10) {
                    System.out.println("Message is " + message);
                    getRecordedGames(parsedMsg[1],Integer.parseInt(parsedMsg[2]));
                    System.out.println("moves"+moves);
                    dataOutputStream.writeUTF(moves);
                    System.out.println("Moves send successfully");
                    dataOutputStream.flush();
                } else if (parseMessage(message) == 11) {
                    updatePlayeStatus(parsedMsg[1]);
                    updatePlayeStatus(parsedMsg[2]);
                    System.out.println("Message is " + message);
                    sendMessageToAll(message);
                    System.out.println("Message is sent ooooooooooo ");
                } else if (parseMessage(message) == 12) {
                    System.out.println("Message is " + message);
                    sendMessageToAll(message);
                    System.out.println("Message is sent ooooooooooo ");
                } else if (parseMessage(message) == 13) {
                    System.out.println("Message is " + message);
                    sendMessageToAll(message);
                    System.out.println("Message is sent ooooooooooo ");

                } 
                else if (parseMessage(message) == 14) {
                    System.out.println("Message is " + message);
                    sendMessageToAll(message);
                    System.out.println("Message is sent $$$$$$$$$$ ");
                } else if (parseMessage(message) == 15) {
                    System.out.println("Message is " + message);
                    sendMessageToAll("GameOnline.lose." + parsedMsg[2] + "." + parsedMsg[3]);
		    //addGame(parsedMsg[3],parsedMsg[2]);
                    //setWinner(getID(), parsedMsg[3]);
                    dbconnection.UpdateScore(parsedMsg[3],Integer.parseInt(parsedMsg[4]));
                    System.out.println("WinnerPlayer + Score"+parsedMsg[3]+parsedMsg[4]);

                } else if (parseMessage(message) == 16) {
                    System.out.println("Message is " + message);
                    sendMessageToAll(message);
                    //addGame(parsedMsg[2],parsedMsg[1]);
                    //setWinner(getID(), null);
                } 
                else if(parseMessage(message) == 17)
                {
                  System.out.println("Message is " + message);                
                   dbconnection.AddGame(parsedMsg[1],parsedMsg[2],parsedMsg[3],parsedMsg[4],parsedMsg[5]);
                                  
                }
                else if (parseMessage(message) == 18) {
                    System.out.println("Message is " + message);
                    sendMessageToAll("StartGame." + parsedMsg[1] + "." + "false" + "."+String.valueOf(getScore(parsedMsg[1]))+".X.O." + parsedMsg[2] + "." + "true" +"."+String.valueOf(getScore(parsedMsg[2]))+".O.X");
                    // setTurn();
                    // sendMessageToAll("StartGame."+parsedMsg[2]+"."+parsedMsg[1]+"."+"true"+".15.O.X");
                    System.out.println("StartGame." + parsedMsg[1] + "." + "false" + "."+String.valueOf(getScore(parsedMsg[1]))+".X.O." + parsedMsg[2] + "." + "true" +"."+String.valueOf(getScore(parsedMsg[2]))+".O.X");
                    // System.out.print("StartGame."+parsedMsg[2]+"."+parsedMsg[1]+"."+"true"+".15.O.X");
                }  
            } catch (IOException ex) {
                stop();
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);

            }

        }

    }

    public void sendMessageToAll(String msg) throws IOException {
        for (GameHandler sh : clientsVector) {

            sh.dataOutputStream.writeUTF(msg);
            System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        }

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
        if (parsedMsg[0].equals("UP")) { // register
            return 1;
        }
        if (parsedMsg[0].equals("IN")) { // sign in
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
        }
        if (parsedMsg[0].equals("SCORELIST")) { // request SCORELIST
            return 6;
        }
        if (parsedMsg[0].equals("LOGOUT")) { // logout request
            return 7;
        }
        if (parsedMsg[0].equals("History")) {
            getPlayedGames();
            return 8;
        }

       
        if(parsedMsg[0].equals("RecordedGames"))
                 return 10;
        
        if (parsedMsg[0].equals("Accept")) {
            return 11;
        }
        if (parsedMsg[0].equals("Reject")) {
            return 12;
        }
        if (parsedMsg[0].equals("DUWTP")) {
            return 13;
        }
        if (parsedMsg[0].equals("GameOnline")) {
            if (parsedMsg[1].equals("play")) {
                return 14;
            }
        }
        if (parsedMsg[0].equals("GameOnline")) {
            if (parsedMsg[1].equals("win")) {
                return 15;
            }
        }
        if (parsedMsg[0].equals("GameOnline")) {
            if (parsedMsg[1].equals("tied")) {
                return 16;
            }
        }
        
        
        if(parsedMsg[0].equals("GameOnline"))
        {
            if(parsedMsg[1].equals("EndGame")) {
                return 17;
            }
        }
        if (parsedMsg[0].equals("StartGame")) {
            return 18;
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


    public boolean checkUserExistence(String username) {
        if (dbconnection.checkUserExistence(username)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkValidPassword(String username, String Password) {
        if (dbconnection.checkValidPassword(username, Password)) {
            return true;
        } else {
            return false;
        }

    }

    public void getOnLinePlayers(String PlayerName) {

        ResultSet s = dbconnection.getOnlinePlayersList();
        Pname = "";
        Score = "";
        if (s == null) {
            System.out.println("no data in table");

        } else {
            try {
                while (s.next()) {
                    if (s.getString(1) != PlayerName) {
                        Pname += s.getString(1) + ".";
                        Score += String.valueOf(s.getInt(2)) + ".";
                    }

                }
            } catch (SQLException ex) {
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public void getPlayedGames() {

        ResultSet s = dbconnection.GetPlayedGames();
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
                    Winner += s.getString(4) + ".";

                }
            } catch (SQLException ex) {
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public void goOffline(String playeName) {
        dbconnection.updateStatus(playeName); // score needed

    }

    private void getRecordedGames(String Rname,int gid) {
        moves = "";
        try {
            ResultSet s = dbconnection.GetMoves(Rname,gid);
            
            if (s == null) {
                System.out.println("no data in table");

            } else {
                try {
                    while (s.next()) {
                        moves=s.getString(1);

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    public boolean checkIsPlaying(String playerName) {
        return dbconnection.Playing(playerName);
    }
    
    
    public long getID(){
        return dbconnection.GetGID();
    }
    
    public void setWinner (long id , String winner){
        dbconnection.SetWinner(id, winner);
    }

    private int getScore(String Pname) {
        int score = 0;
        score = dbconnection.GetScoreofPlayer(Pname);
        return score;
    }
    
    
    
}

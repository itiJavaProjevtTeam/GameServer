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
    boolean flagTurnp1 = true;
    boolean flagTurnp2 = false;
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
                    if (!parsedMsg[1].isEmpty() && !parsedMsg[2].isEmpty()) {
                        checkUserExistence = checkUserExistence(parsedMsg[0]);
                        if (checkUserExistence == true) {
                            dataOutputStream.writeUTF("ALREADY EXISTS");

                        } else {
                            addUser(parsedMsg[1], parsedMsg[2]);
                            ++MainServer.offlinePlayers;
                            updatePlayeStatus(parsedMsg[1]);
                            System.out.print("Registered");
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
                    System.out.print("Player + score " + Pname + Score);
                    dataOutputStream.writeUTF("PLAYERLIST."+parsedMsg[1]+"."+Pname + Score);

                    System.out.print("playerList send successfully");
                    dataOutputStream.flush();

                } else if (parseMessage(message) == 6) {

//                    dataOutputStream.writeUTF(dbconnection.GetScore(""));
                } else if (parseMessage(message) == 7) {
                    goOffline(parsedMsg[1]);
                    dataOutputStream.writeUTF("Player went offline succefully");
                    --MainServer.onlinePlayers;
                    ++MainServer.offlinePlayers;

                } else if (parseMessage(message) == 8) {
                    System.out.print("GID+P1+P2+Winner " + GID + P1 + P1Score + P2 + P2Score + Winner);
                    dataOutputStream.writeUTF(GID + "_" + P1 + "_" + P1Score + "_" + P2 + "_" + P2Score + "_" + Winner);
                    System.out.print("History send successfully");
                    dataOutputStream.flush();

                } else if (parseMessage(message) == 9) {
                    System.out.println(message);
                    sendMessageToAll(message);
                }
                 else if (parseMessage(message) == 10) {
                     System.out.print("Message is "+message);
                     getRecordedGames(Integer.parseInt(parsedMsg[1]));
                    System.out.print("Positions + Moves + PlayersName "+ symbol +  Positions + MovesPlayerName);
                    dataOutputStream.writeUTF(symbol + "_" + Positions + "_" + MovesPlayerName);
                    System.out.print("Moves send successfully");
                    dataOutputStream.flush();
                        }
                else if (parseMessage(message) == 11){
                    
                      dbconnection.updatePlaying(parsedMsg[1]);
                      dbconnection.updatePlaying(parsedMsg[2]);
                      System.out.print("Message is "+message);
                      sendMessageToAll(message);
                      while(!parsedMsg[0].equalsIgnoreCase("win") && !message.equalsIgnoreCase("tied") )
                      {
                       sendMessageToAll("canPlay."+parsedMsg[1]+"."+String.valueOf(flagTurnp1));
                       sendMessageToAll("canPlay."+parsedMsg[2]+"."+String.valueOf(flagTurnp2));
                       setTurn();

                      }
                                      }
                 else if (parseMessage(message) == 12){
                      System.out.print("Message is "+message);
                      sendMessageToAll(message);
                }
                 else if (parseMessage(message) == 13){
                      System.out.print("Message is "+message);
                      sendMessageToAll(message);
                      if(dbconnection.Playing(parsedMsg[1]))
                      {
                       dataOutputStream.writeUTF("Playing."  + parsedMsg[2] + "." + parsedMsg[1]);
 
                      }
                      else
                      {
                        sendMessageToAll(message);
                      }
                }
                 //add step
                 else if (parseMessage(message) == 14){
                      System.out.print("Message is "+message);
                      sendMessageToAll(message);
                }
                 else if (parseMessage(message) == 15){
                      System.out.print("Message is "+message);
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

        if(parsedMsg[0].equals("CanPlay"))
        {
            return 9;
        }
       
        if (parsedMsg[0].equals("RecordedGames")) {
            return 10;
        }
        if (parsedMsg[0].equals("Accept")) {
            return 11;
        }if (parsedMsg[0].equals("Reject")) {
            return 12;
        }if(parsedMsg[0].equals("DUWTP"))
        {
            return 13;
        }
            if(parsedMsg[0].equals("play"))
        {
            return 14;
        }
              if(parsedMsg[0].equals("win"))
        {
            return 15;
        }
              if(parsedMsg[0].equals("loss"))
        {
            return 16;
        }
          if(parsedMsg[0].equals("tied"))
        {
            return 17;
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
                    if(s.getString(1) != PlayerName)
                    {
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

    private void getRecordedGames(int gid) {
        try {
            ResultSet s = dbconnection.GetMoves(gid);
            Positions = "";
            symbol = "";
            MovesPlayerName = "";
            if (s == null) {
                System.out.println("no data in table");

            } else {
                try {
                    while (s.next()) {
                        symbol += s.getString(5) + ".";
                        Positions += String.valueOf(s.getInt(3)) + ".";
                        MovesPlayerName += s.getString(4) + ".";

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setTurn()
    {
        
        if(flagTurnp1 == true)
            flagTurnp1 = false;
        else
            flagTurnp1 = true;
        
        if(flagTurnp2 == false)
            flagTurnp2 = true;
        else
            flagTurnp2 = false;
    }

}

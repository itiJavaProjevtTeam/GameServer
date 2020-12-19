 package DbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author Fayza&Nermeen
 */
public class DbConnectionHandler {

    private static DbConnectionHandler dbch;
    private Connection con;
    public static Vector<String> playerList;

    private DbConnectionHandler() throws SQLException {

        playerList = new Vector<>();
    }

    public void openConnection() {
        try {
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/TicTacToeDb", "javaTeam", "javaTeam");
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DbConnectionHandler CreateConnection() {
        if (dbch == null) {
            try {
                dbch = new DbConnectionHandler();
            } catch (SQLException ex) {
                Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dbch;
    }

    /* *****************************Handling Players Table********************** */
    //adding Name & Pass of Player to (Players) TABLE
    public void Signup(String Pname, String psswd) {

        try {
            String querySignup = new String("insert into Players (Pname,password) values(?,?)");
            PreparedStatement stmt = con.prepareStatement(querySignup);  
            stmt.setString(1, Pname);
            stmt.setString(2, psswd);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }

    public boolean checkUserExistence(String Pname) {
        
        try {
            Statement stmt = con.createStatement();
            String queryString = new String("Select * FROM Players WHERE Pname='" + Pname + "'");
            ResultSet rs = stmt.executeQuery(queryString);
            if (rs.next()) {
                System.out.println("User Exist");
                stmt.close();
                return true;
            }
            else
            {
                stmt.close();
                return false;

            }      
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;        }
    }
    
    public boolean checkValidPassword(String Pname,String Password) {
        if (Pname != "" && Password != "") {
            try {
                String queryString = new String("Select * FROM Players WHERE Pname=?");
                
                PreparedStatement stmt = con.prepareStatement(queryString);
                stmt.setString(1, Pname);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    if (Password.equalsIgnoreCase(rs.getString("password"))) {
                        System.out.println("Valid Password");
                        stmt.close();
                        return true;
                    }
                } else {
                    System.out.println("NOT Valid Password");
                    stmt.close();
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    
    

    //check if Pname&Pass exist in db
    public boolean Signin(String Pname, String psswd)  {
        if (Pname != "" && psswd != "") {
            try {
                String queryString = new String("Select * FROM Players WHERE Pname=?");
                
                PreparedStatement stmt = con.prepareStatement(queryString);
                stmt.setString(1, Pname);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    if (psswd.equalsIgnoreCase(rs.getString("password"))) {
                        updateStatus(Pname);
                        System.out.println("OK Found");
                        stmt.close();
                        return true;
                    }
                } else {
                    System.out.println("NOT Found");
                    stmt.close();
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return false;
    }

    public void UpdateScore(String Pname, int Score) {

        try {
            Statement stmt = con.createStatement();
            String queryString = new String("UPDATE Players SET Score =" + Score + " WHERE Pname='" + Pname + "'");
            int rs = stmt.executeUpdate(queryString);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // return Score of player  
    public long GetScore(String Pname) {
        long scores = 0;
        try {
            ResultSet rs = null;
            String queryString = new String("Select Score FROM Players where status = true");
            PreparedStatement stmt = con.prepareStatement(queryString,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();         
            rs.beforeFirst();
            while (rs.next()) {
                rs.getLong(1);
            }
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scores;
    }
    
    

    /* *****************************Handling Games Table********************** */
    //add name of two players to (Games) TABLE
    public void AddGame(String P1, String P2) throws SQLException {

        Statement stmt = con.createStatement();
        String queryString = new String("insert into Games (player1,player2) values('" + P1 + "','" + P2 + "')");
        int rs = stmt.executeUpdate(queryString);
        stmt.close();
    }

    //insert name of winner player in (Games) TABLE
    public void SetWinner(long GameID, String Winner) throws SQLException {

        Statement stmt = con.createStatement();
        String queryString = new String("UPDATE Games SET winner ='" + Winner + "' WHERE GID='" + GameID + "'");
        int rs = stmt.executeUpdate(queryString);
        stmt.close();
    }

    //return game id 
    public long GetGID() throws SQLException {
        long GID = 0;
        Statement stmt = con.createStatement();
        String queryString = new String("SELECT GID FROM Games ORDER BY GID Desc Limit 1");
        ResultSet rs = stmt.executeQuery(queryString);
        rs.next();
        GID = rs.getLong(1);
        stmt.close();
        return GID;

    }

    /* *****************************Handling Moves Table********************** */
    //EX: player (x) has played his (2nd) move in position (3)
    public void AddMove(long GID, int MoveNum, int POS, String Player) throws SQLException {

        Statement stmt = con.createStatement();
        String queryString = new String("insert into Moves (GID,MoveNum,POS,Player) values(" + GID + "," + MoveNum + "," + POS + "," + "'" + Player + "')");
        int rs = stmt.executeUpdate(queryString);
        stmt.close();
    }

    //Recorded Moves
    public ResultSet GetMoves(int GID) throws SQLException {
        ResultSet rs = null;
        Statement stmt = con.createStatement();
        String queryString = new String("select * from Moves where GID=" + GID);
        rs = stmt.executeQuery(queryString);
        return rs;
    }

    //Recorded Games (gid,p1,p2,winner)
    public ResultSet GetPlayedGames(){
            ResultSet rs = null;
        try {
            Statement stmt = con.createStatement();
            String queryString = new String("Select * FROM Games ");
            rs = stmt.executeQuery(queryString);
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;

    }
    
 
    
    // return Score of All players  
    public long GetScoreToHistoryTable(String Pname) {
        
        long scores = 0;
        try {
            ResultSet rs = null;
            String queryString = new String("Select Score FROM Players where Pname ='" + Pname + "'");
            PreparedStatement stmt = con.prepareStatement(queryString,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();         
            rs.beforeFirst();
            while (rs.next()) {
                rs.getLong(1);
            }
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scores;
    }

    public int getOnlinePlayers() {
        int online = 0;
        try {
            ResultSet rs = null;
            String queryString = new String("Select * FROM Players where status = true");
            PreparedStatement stmt = con.prepareStatement(queryString,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                ++online;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return online;
    }

    public int getOFFlinePlayers() {
        int offline = 0;
        try {
            ResultSet rs = null;
            String queryString = new String("Select * FROM Players where status = false");
            PreparedStatement stmt = con.prepareStatement(queryString,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                ++offline;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return offline;
    }

    public void updateStatus(String Pname) {
        try {
            Statement stmt = con.createStatement();
            String queryString = new String("UPDATE Players SET Status = true WHERE Pname='" + Pname + "'");
            int rs = stmt.executeUpdate(queryString);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void goOffline(String Pname) {
        try {
            Statement stmt = con.createStatement();
            String queryString = new String("UPDATE Players SET Status = false WHERE Pname='" + Pname + "'");
            int rs = stmt.executeUpdate(queryString);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ResultSet getOnlinePlayersList()
    {
        ResultSet rs  = null;
        try {
            String queryString = new String("Select Pname,Score FROM Players where status = true");
            PreparedStatement stmt = con.prepareStatement(queryString,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    
    }

}

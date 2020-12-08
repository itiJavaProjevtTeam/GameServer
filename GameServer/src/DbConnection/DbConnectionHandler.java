package DbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    
    private DbConnectionHandler() throws SQLException {
        DriverManager.registerDriver(new ClientDriver());
        con = DriverManager.getConnection("jdbc:derby://localhost:1527/TicTacToeDb", "javaTeam", "javaTeam");
        }

    public static DbConnectionHandler CreateConnection() throws SQLException {
        if (dbch == null) {
            dbch = new DbConnectionHandler();
        }
        return dbch;
    }
    
     /* *****************************Handling Players Table********************** */
    
    //adding Name of player to (Players) TABLE
    public void AddPlayer(String Pname) throws SQLException {
            Statement stmt = con.createStatement();
            String queryString = new String("insert into Players (Pname) values('" + Pname + "')");
            int rs = stmt.executeUpdate(queryString);
            stmt.close();
            con.close();
    }
    
    //adding Name & Pass of Player to (Players) TABLE
    public  int Signup(String Pname, String psswd) throws SQLException {
        
            Statement stmt = con.createStatement();
            //check if name exist!
            String queryString = new String("Select * FROM Players WHERE Pname='" + Pname + "'");
            ResultSet rs = stmt.executeQuery(queryString);
            if (rs.next()) {
                System.out.println("This Username is already in use!");
                stmt.close();
                con.close();
                return 0;
            } else {
                System.out.println("Not found!");
                String querySignup = new String("insert into Players (Pname,password) values('" + Pname + "','" + psswd + "')");
                int res = stmt.executeUpdate(querySignup);
                System.out.println("Res="+res);
                stmt.close();
                con.close();
                return res;
            }

        
    }
    
    //check if Pname&Pass exist in db
    public int Signin(String Pname, String psswd) throws SQLException {
            if (Pname != "" && psswd != "") {
                Statement stmt = con.createStatement();
                String queryString = new String("Select * FROM Players WHERE Pname='" + Pname + "'");
                ResultSet rs = stmt.executeQuery(queryString);
                if (rs.next()) {
                    if (psswd.equalsIgnoreCase(rs.getString("password"))) {
                        System.out.println("OK Found");
                        stmt.close();
                        con.close();
                        return 1;
                    }
                } else {
                    System.out.println("NOT Found");
                    stmt.close();
                    con.close();
                    return 0;
                }

            }
        
        return 0;
    }
    
    // update value of score in (Players) TABLE
     public void UpdateScore(String Pname, int Score) throws SQLException {
         
            Statement stmt = con.createStatement();
            String queryString = new String("UPDATE Players SET Score =" + Score + " WHERE Pname='" + Pname + "'");
            int rs = stmt.executeUpdate(queryString);
            stmt.close();
            con.close();
        
    }
     
     // return Score of player  
     public  long GetScore(String Pname) throws SQLException {
             long Score = 0;
            Statement stmt = con.createStatement();
            String queryString = new String("Select Score FROM Players where Pname='" + Pname + "'");
            ResultSet rs = stmt.executeQuery(queryString);
            rs.next();
            Score = rs.getLong(1);
            stmt.close();
            con.close();
            return Score;
    }
     
         /* *****************************Handling Games Table********************** */
     
     //add name of two players to (Games) TABLE
    public void AddGame(String P1, String P2) throws SQLException {
        
                        Statement stmt = con.createStatement();
            String queryString = new String("insert into Games (player1,player2) values('" + P1 + "','" + P2 + "')");
            int rs = stmt.executeUpdate(queryString);
            stmt.close();
            con.close();
      
    }
    
    //insert name of winner player in (Games) TABLE
    public void SetWinner(long GameID, String Winner) throws SQLException {
       
            Statement stmt = con.createStatement();
            String queryString = new String("UPDATE Games SET winner ='" + Winner + "' WHERE GID='" + GameID + "'");
            int rs = stmt.executeUpdate(queryString);
            stmt.close();
            con.close();
          }
    
    //return game id 
    public  long GetGID() throws SQLException {
            long GID = 0;
            Statement stmt = con.createStatement();
            String queryString = new String("SELECT GID FROM Games ORDER BY GID Desc Limit 1");
            ResultSet rs = stmt.executeQuery(queryString);
            rs.next();
             GID = rs.getLong(1);
            stmt.close();
            con.close();
            return GID;

    }
    
             /* *****************************Handling Moves Table********************** */
    
    
    //EX: player (x) has played his (2nd) move in position (3)
    public void AddMove(long GID, int MoveNum, int POS, String Player) throws SQLException {
       
            Statement stmt = con.createStatement();
            String queryString = new String("insert into Moves (GID,MoveNum,POS,Player) values(" + GID + "," + MoveNum + "," + POS + "," + "'" + Player + "')");
            int rs = stmt.executeUpdate(queryString);
            stmt.close();
            con.close();
    }
    
    //Recorded Moves
    public ResultSet GetMoves(long GID) throws SQLException {
            ResultSet rs = null;
            Statement stmt = con.createStatement();
            String queryString = new String("select * from Moves where GID=" + GID);
            rs = stmt.executeQuery(queryString);
            return rs;
    }
    
    //Recorded Games (p1,p2,winner)
    public  ResultSet GetPlayedGames(String Pname) throws SQLException {
            
            ResultSet rs = null;
            Statement stmt = con.createStatement();
            String queryString = new String("Select * FROM Games where player1 = '" + Pname + "'OR player2='" + Pname + "'");
            rs = stmt.executeQuery(queryString);
            return rs;
     } 

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author abdelrahmanelnagdy
 */


public class GameServer extends Application {
    
public GameServer()
  {

  }
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new GameServerUI();
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
          
        stage.show();

    }
    
    

    /**
     * @param args the command line arguments*/
   
   
    public static void main(String[] args) {
        launch(args);
    }
    
}

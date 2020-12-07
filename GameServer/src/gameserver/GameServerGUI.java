package gameserver;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;

public class GameServerGUI extends AnchorPane {

    protected final RadioButton serverPowerBtn;
    protected final ListView onlinePlayersList;
    MainServer mainServer;
    public GameServerGUI() {
        mainServer = new MainServer();
        serverPowerBtn = new RadioButton();
        onlinePlayersList = new ListView();

        setId("AnchorPane");
        setPrefHeight(200);
        setPrefWidth(320);

        serverPowerBtn.setLayoutX(1.0);
        serverPowerBtn.setMnemonicParsing(false);
        serverPowerBtn.setText("Server OFF");

        onlinePlayersList.setLayoutX(108.0);
        onlinePlayersList.setPrefHeight(200.0);
        onlinePlayersList.setPrefWidth(212.0);

        getChildren().add(serverPowerBtn);
        getChildren().add(onlinePlayersList);
        
        serverPowerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (serverPowerBtn.getText() == "Server OFF"){ //     while server is on
                serverPowerBtn.setText("Server ON");
                mainServer.start();

                } else { //       while server is off
                    serverPowerBtn.setText("Server OFF");
                    System.out.println("Server is down");
                    mainServer.stop();
                    mainServer.closeClients();
                }       
            }
        });
    }
}

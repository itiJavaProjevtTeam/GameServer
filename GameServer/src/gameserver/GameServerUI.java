package gameserver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.PieChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class GameServerUI extends AnchorPane {

    protected final PieChart gameChart;
    protected final RadioButton serverPowerBtn;
    protected final ScrollPane onlineList;
    protected final AnchorPane anchorPane;
    protected final Text text;
    private DbConnection.DbConnectionHandler dbconnection;
    private MainServer mainServer;
    ObservableList<PieChart.Data> pieChartData;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean isFirstOpen = true;

    public GameServerUI() {

        serverPowerBtn = new RadioButton();
        onlineList = new ScrollPane();
        anchorPane = new AnchorPane();
        text = new Text();
        mainServer = new MainServer();
        pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Offline", 10), // database connection should return these actual numbers
                new PieChart.Data("Online", 10),
                new PieChart.Data("Available", 10)
        );

        gameChart = new PieChart(pieChartData);
        gameChart.setVisible(false);
        gameChart.setAnimated(false);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        refreshPieChart();

        setId("AnchorPane");
        setPrefHeight(400.0);
        setPrefWidth(600.0);

        gameChart.setLayoutY(201.0);
        gameChart.setPrefHeight(199.0);
        gameChart.setPrefWidth(179.0);
        gameChart.setTitle("Players existence chart");

        serverPowerBtn.setLayoutX(4.0);
        serverPowerBtn.setLayoutY(5.0);
        serverPowerBtn.setMnemonicParsing(false);
        serverPowerBtn.setText("Server OFF");

        onlineList.setLayoutX(386.0);
        onlineList.setLayoutY(26.0);
        onlineList.setPrefHeight(373.0);
        onlineList.setPrefWidth(214.0);

        anchorPane.setMinHeight(0.0);
        anchorPane.setMinWidth(0.0);
        anchorPane.setPrefHeight(372.0);
        anchorPane.setPrefWidth(208.0);
        onlineList.setContent(anchorPane);

        text.setLayoutX(386.0);
        text.setLayoutY(19.0);
        text.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        text.setStrokeWidth(0.0);
        text.setText("Online Players");

        getChildren().add(gameChart);
        getChildren().add(serverPowerBtn);
        getChildren().add(onlineList);
        getChildren().add(text);
        serverPowerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (serverPowerBtn.getText() == "Server OFF") {
                    gameChart.setVisible(true);
//     while server is on
                    serverPowerBtn.setText("Server ON");
                    if (isFirstOpen) {
                        mainServer.start();
                        isFirstOpen = false;
                    } else {
                        mainServer.resume();
                    }

                } else { //       while server is off
                    gameChart.setVisible(false);
                    serverPowerBtn.setText("Server OFF");
                    System.out.println("Server is down");
                    mainServer.suspend();
                    mainServer.closeClients();
                }
            }
        });

    }

    public void refreshPieChart() {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pieChartData.set(0, new PieChart.Data("Online", 10)); //mainServer.onlinePlayers
                        pieChartData.set(0, new PieChart.Data("Offline", 10)); // mainServer.offlinePlayers
                        pieChartData.set(0, new PieChart.Data("Available", 10)); //mainServer.availablePlayers                 
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}

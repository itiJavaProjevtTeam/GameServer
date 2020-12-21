package gameserver;

import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public abstract class ServerGUI extends BorderPane {

    protected final AnchorPane anchorPane;
    protected final PieChart gameChart;
    protected final RadioButton serverPowerBtn;
    protected final Label label;
    protected final AnchorPane anchorPane0;
    protected final ImageView imageView;
    protected final Label label0;
    protected final Label label1;

    public ServerGUI() {

        anchorPane = new AnchorPane();
        gameChart = new PieChart();
        serverPowerBtn = new RadioButton();
        label = new Label();
        anchorPane0 = new AnchorPane();
        imageView = new ImageView();
        label0 = new Label();
        label1 = new Label();

        setPrefHeight(400.0);
        setPrefWidth(700.0);
        setStyle("-fx-background-color: #60408b;");

        BorderPane.setAlignment(anchorPane, javafx.geometry.Pos.CENTER);
        anchorPane.setId("AnchorPane");
        anchorPane.setPrefHeight(400.0);
        anchorPane.setPrefWidth(450.0);
        anchorPane.setStyle("-fx-background-color: #ffffff;");

        gameChart.setLayoutX(88.0);
        gameChart.setLayoutY(111.0);
        gameChart.setPrefHeight(222.0);
        gameChart.setPrefWidth(253.0);
        gameChart.setStyle("-fx-background-color: #F5EEF8;");

        serverPowerBtn.setLayoutX(14.0);
        serverPowerBtn.setLayoutY(13.0);
        serverPowerBtn.setMnemonicParsing(false);
        serverPowerBtn.setText("Server ON");
        serverPowerBtn.setTextFill(javafx.scene.paint.Color.valueOf("#60408b"));
        serverPowerBtn.setFont(new Font("System Bold", 18.0));

        label.setLayoutX(110.0);
        label.setLayoutY(72.0);
        label.setText("Players existence chart");
        label.setTextFill(javafx.scene.paint.Color.valueOf("#60408b"));
        label.setFont(new Font("System Bold", 20.0));
        setRight(anchorPane);

        BorderPane.setAlignment(anchorPane0, javafx.geometry.Pos.CENTER);
        anchorPane0.setPrefHeight(400.0);
        anchorPane0.setPrefWidth(250.0);

        imageView.setFitHeight(81.0);
        imageView.setFitWidth(80.0);
        imageView.setLayoutX(29.0);
        imageView.setLayoutY(28.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        imageView.setImage(new Image(getClass().getResource("../images/logoLa.png").toExternalForm()));

        label0.setLayoutX(53.0);
        label0.setLayoutY(99.0);
        label0.setText("TicTacToe");
        label0.setTextFill(javafx.scene.paint.Color.WHITE);
        label0.setFont(new Font("System Bold", 24.0));

        label1.setLayoutX(37.0);
        label1.setLayoutY(134.0);
        label1.setText("Game Server");
        label1.setTextFill(javafx.scene.paint.Color.WHITE);
        label1.setFont(new Font("System Bold", 24.0));
        setLeft(anchorPane0);

        anchorPane.getChildren().add(gameChart);
        anchorPane.getChildren().add(serverPowerBtn);
        anchorPane.getChildren().add(label);
        anchorPane0.getChildren().add(imageView);
        anchorPane0.getChildren().add(label0);
        anchorPane0.getChildren().add(label1);

    }
}

package com.forrer.memory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Marco Forrer
 * last edit: 09.01.2020
 */

public class Main extends Application {

    public static final Map<String, Integer> players = new LinkedHashMap<>();
    public static GridPane playerList, playField;
    public static Scene scene;

    /**
     * Die main generiert das Fenster
     *
     * @param args Programmstart Argumente
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Generiert das Spielfeld
     *
     * Das Spielfeld wird in die Variable "playField" gespeichert
     *
     * Die Spielertabelle wird in die Variable playerList gespeichert
     *
     * @param primaryStage Übermittlung der Eigenschaften des Fensters
     * @throws Exception Verhinderung des Programmabsturtzes bei unerfolgtem laden der Datei
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Memory");
        scene = new Scene(root, 800, 635);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        playField = (GridPane) Main.scene.lookup("#playField");
        playField.setAlignment(Pos.CENTER);
        playerList = (GridPane) Main.scene.lookup("#list");

        primaryStage.show();

    }
}

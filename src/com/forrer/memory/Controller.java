package com.forrer.memory;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Der Kontroller ist die Schnittstelle zwischen der Benutzereingabe und der Applikation
 */
public class Controller {

    /**
     * Diese Variablen und Arrays dienen als Datenbank zum speichern von Informationen
     */
    final Map<String, Integer> images = new HashMap<String, Integer>() {{
        put("friendly", 0);
        put("loving", 0);
        put("nauseated", 0);
        put("puke", 0);
        put("scared", 0);
        put("shit", 0);
        put("smile", 0);
        put("winking", 0);
    }};
    private int playerCount = 0, uncovered = 0, correctUncovered = 0;
    private String playing;
    private ImageView lastSmiley;

    /**
     *
     * Diese Methode wird zur darstellung von Dialogen verwendet
     *
     * @param type Alerttype wird übermittelet
     * @param title Titel vom Dialog
     * @param message Nachricht im Dialog
     */
    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * In dieser Methode werden die Spieler hinzugefügt
     *
     * Dialogfenster wird angezeigt um einen Spieler hinzuzufügen
     *
     * Der neue Spieler wird in eine Linkedhashmap hinzugefügt mit der Punktzahl 0
     *
     * Spieler wird in die Tabelle hinzugefügt und angezeigt
     *
     * Bei 16 Spielern wird der Button "Add Player" automatisch entfernt
     *
     * @param event Informationen zur Benutzereingabe
     */
    @FXML
    private void addPlayer(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Player name");
        dialog.setContentText("Please enter your name:");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String username = result.get();
            Main.players.put(username, 0);
            playerCount++;

            Label label = new Label(username);
            Main.playerList.add(label, 0, playerCount);
            Main.playerList.add(new Label("0"), 1, playerCount);
            if (playerCount == 16) {
                Button button = (Button) Main.scene.lookup("#playerAdd");
                button.setVisible(false);
            }
        }
    }

    /**
     * Diese Methode wird für das anzeigen der Kartenbilder verwendet
     *
     * Sobald zwei Karten aufgedeckt werden, wird das Klicken auf dem Feld für zwei Sekunden deaktiviert
     * Falls die zwei Karten übereinstimmen erhält der spielende Spieler einen Punkt und die zwei Karten werden entfernt
     *
     * Sobald alle Karten aufgedeckt wurden ist die Runde fertig und das Feld wird zurückgesetzt
     *
     * Falls die zwei aufgedeckten Karten nicht übereinstimmen werden sie nach zwei Sekunden ausgeblendet und der Spieler wird gewechselt
     *
     * Die erste aufgedeckte Karte wird in die Variable "lastSmiley" gespeichert
     *
     * @param event Informationen zur Benutzereingabe
     * @throws IOException Verhindert einen Absturz falls die Dateien nicht geladen werden können
     */
    private void showSmiley(MouseEvent event) throws IOException {
        if (uncovered == 2) return;

        ImageView imageView = (ImageView) event.getSource();

        BufferedImage image = ImageIO.read(getClass().getResource("Resources/" + imageView.fieldSmiley + ".png"));
        imageView.setImage(SwingFXUtils.toFXImage(image, null));
        uncovered++;

        if (uncovered == 2) {
            uncovered = 0;

            Main.playField.setDisable(true);

            Timeline twoSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(2), event1 -> {
                try {
                    Main.playField.setDisable(false);
                } catch (Exception ignored) {
                }
            }));

            twoSecondsWonder.setCycleCount(1);
            twoSecondsWonder.play();

            if (lastSmiley.fieldSmiley.equals(imageView.fieldSmiley)) {
                correctUncovered++;

                Main.players.put(playing, Main.players.get(playing) + 1);

                // Spieler wird in der Tabelle gesucht und erhält einen Punkt
                boolean isNext = false;
                for (Node node : Main.playerList.getChildren()) {
                    if (node instanceof Label && ((Label) node).getText().equals(playing))
                        isNext = true;
                    else if (isNext) {
                        assert node instanceof Label;
                        ((Label) node).setText(Main.players.get(playing).toString());
                        break;
                    }

                }

                twoSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(2), event1 -> {
                    try {
                        lastSmiley.setVisible(false);
                        imageView.setVisible(false);
                    } catch (Exception ignored) {
                    }
                }));

                twoSecondsWonder.setCycleCount(1);
                twoSecondsWonder.play();

                if (correctUncovered == 8) {
                    correctUncovered = 0;

                    if (playerCount < 16) {
                        Button button = (Button) Main.scene.lookup("#playerAdd");
                        button.setVisible(true);
                    }

                    images.replaceAll((k, v) -> 0);

                    Button button = (Button) Main.scene.lookup("#gameStart");
                    button.setText("Next round");
                    button.setVisible(true);


                    Node node = Main.playField.getChildren().get(0);
                    Main.playField.getChildren().clear();
                    Main.playField.getChildren().add(0, node);
                }

            } else {
                twoSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            BufferedImage image = ImageIO.read(getClass().getResource("Resources/question.png"));
                            imageView.setImage(SwingFXUtils.toFXImage(image, null));
                            lastSmiley.setImage(SwingFXUtils.toFXImage(image, null));
                        } catch (Exception ignored) {
                        }
                    }
                }));

                twoSecondsWonder.setCycleCount(1);
                twoSecondsWonder.play();

                int i = 0;
                for (Map.Entry<String, Integer> entry : Main.players.entrySet()) {
                    if (entry.getKey().equals(playing)) {
                        try {
                            playing = Main.players.keySet().toArray()[i + 1].toString();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            playing = Main.players.keySet().stream().findFirst().get();
                        }
                        ((Label) Main.scene.lookup("#currentlyPlaying")).setText(playing);
                        break;
                    }
                    i++;
                }

            }
        } else {
            lastSmiley = imageView;
        }
    }

    /**
     * Das Spiel kann erst gestartet werden wenn mindestens zwei Spieler eingetragen sind
     *
     * Wird das Spiel gestartet, werden die Buttons "playerAdd" und "gameStart" ausgeblendet
     *
     * Bei jeder neuen Runde werden die Karten neu gemischt
     * Auf jedem Feld werden Fragezecihen gesetzt mit einem Event welches bei einem Klick die Methode "showSmiley" aufruft
     *
     * In den jeweiligen Felder werden die FeldIds abgespeichert
     *
     * Zu Beginn wird ein zufälliger Spieler ausgewählt welcher anfängt
     * Der Spieler der jeweils dran ist wird angezeigt
     *
     * @param event Informationen zur Benutzereingabe
     * @throws IOException Verhindert einen Absturz falls die Dateien nicht geladen werden können
     */
    @FXML
    private void startGame(ActionEvent event) throws IOException {

        if (playerCount < 2) {
            showAlert(Alert.AlertType.ERROR, "Player count", "There have to be at least two players.");
            return;
        }

        Main.scene.lookup("#playerAdd").setVisible(false);
        Main.scene.lookup("#gameStart").setVisible(false);
        List<String> keys = new ArrayList<>(images.keySet());

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                String firstElement;
                final int id = 4 * i + j;

                while (true) {
                    Collections.shuffle(keys);
                    firstElement = keys.stream().findFirst().get();
                    if (images.get(firstElement) != 2) {
                        images.put(firstElement, images.get(firstElement) + 1);
                        break;
                    }

                }

                BufferedImage image = ImageIO.read(getClass().getResource("Resources/question.png"));
                ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
                imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    try {
                        showSmiley(mouseEvent);
                    } catch (Exception ignored) {
                    }
                });
                imageView.fieldId = id;
                imageView.fieldSmiley = firstElement;

                Main.playField.add(imageView, j, i);

            }
        }

        keys = new ArrayList<>(Main.players.keySet());
        Collections.shuffle(keys);
        playing = keys.stream().findFirst().get();
        ((Label) Main.scene.lookup("#currentlyPlaying")).setText(playing);
    }
}

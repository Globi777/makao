package Server;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

//Okno Serwera
public class Server extends Application {
	private static Stage serverStage;
	private static Scene serverScene;
	private static GridPane serverGrid;
	private static TextArea serverArea, playerArea;
	private static Button startServerButton, stopServerButton;
	private static VBox serverVBox;
	private static boolean isLaunched = false;
	private static ArrayList<String> cards = new ArrayList<String>(Arrays.asList(
			"2Trefl", "3Trefl", "4Trefl", "5Trefl", "6Trefl", "7Trefl",
			"8Trefl", "9Trefl", "10Trefl", "waletTrefl", "damaTrefl", "krolTrefl", "asTrefl",
	        "2Pik", "3Pik", "4Pik", "5Pik", "6Pik", "7Pik", "8Pik", "9Pik", "10Pik", "waletPik",
	        "damaPik", "krolPik", "asPik",
	        "2Karo", "3Karo", "4Karo", "5Karo", "6Karo", "7Karo", "8Karo", "9Karo", "10Karo",
	        "waletKaro", "damaKaro", "krolKaro", "asKaro",
	        "2Kier", "3Kier", "4Kier", "5Kier", "6Kier", "7Kier", "8Kier", "9Kier", "10Kier",
	        "waletKier", "damaKier", "krolKier", "asKier"));

	private static void paramsOfServerGUI() throws MalformedURLException {
		serverStage = new Stage();
		serverStage.getIcons().add(new Image(Server.class.getResource("Assets/serverIcon.png").toExternalForm()));
		serverStage.setTitle("Makao - Server");
		serverStage.setResizable(false);

		serverGrid = new GridPane();
		serverGrid.setPadding(new Insets(25, 25, 25, 25));
		serverScene = new Scene(serverGrid, 560, 400);

		startServerButton = new Button("START");
		startServerButton.setPrefWidth(100);
		startServerButton.setPrefHeight(100);

		stopServerButton = new Button("STOP");
		stopServerButton.setPrefWidth(100);
		stopServerButton.setPrefHeight(100);

		serverVBox = new VBox(10);
		serverVBox.setAlignment(Pos.TOP_LEFT);
		serverVBox.getChildren().addAll(startServerButton, stopServerButton);

		serverArea = new TextArea();
		serverArea.setPrefSize(415, 200);
		serverArea.setPromptText("Console displays connections to server and disconnections from server");
		serverArea.setEditable(false);

		playerArea = new TextArea();
		playerArea.appendText("Number of players: " + Integer.toString(Listener.numberOfPlayers));
		playerArea.setEditable(false);
		playerArea.setMaxWidth(150);
		playerArea.setMaxHeight(20);

		serverGrid.add(serverVBox, 0, 0);
		serverGrid.add(serverArea, 1, 0);
		serverGrid.add(playerArea, 1, 1);

		serverScene.getStylesheets().add(Server.class.getResource("server.css").toExternalForm());
		serverStage.setScene(serverScene);
		serverStage.show();
	}

	private static void buttonsOfServerGUI() {
		startServerButton.setOnAction((ActionEvent event) -> {
			try {
				if (!isLaunched) {
					new Listener().start();
					isLaunched = !isLaunched;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		stopServerButton.setOnAction((ActionEvent event) -> {
			try {
				if (isLaunched) {
					Listener.stopServer();
					isLaunched = !isLaunched;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		serverStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				try {
					if (isLaunched) {
						Listener.stopServer();
						isLaunched = !isLaunched;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void initialize() throws MalformedURLException {
		paramsOfServerGUI();
		buttonsOfServerGUI();
	}

	public static TextArea getTextArea() {
		return serverArea;
	}

	public static TextArea getPlayers() {
		return playerArea;
	}

	public static ArrayList<String> getCards() {
		return cards;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initialize();
	}
}

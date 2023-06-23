package csc2b.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Pixes extends Application{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		GUI root = new GUI();
		Scene scene = new Scene(root,1000,600);
		//NOTE opencv docs missing for eve downloaded version
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Pixes Editor");
		primaryStage.getIcons().add(new Image("file:data/download.png"));
		primaryStage.show();
	}
}

package konan.blakime.morpion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author BLACKIME CHRISTIANNA && KONAN EVRARD HYCKAEL
 */

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TicTacToeView.fxml"));
        Scene scene = new Scene(loader.load(), 400, 350);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Morpion");
        Image image = new Image("/konan/blakime/morpion/morpion.png");
		primaryStage.getIcons().add(image);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
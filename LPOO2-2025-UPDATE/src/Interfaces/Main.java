package Interfaces;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega a tela de loading
        Parent loadingScreen = FXMLLoader.load(getClass().getResource("/Interfaces/LoadingScreen.fxml"));
        Stage loadingStage = new Stage();
        loadingStage.initStyle(StageStyle.UNDECORATED); // Remove a barra de título
        loadingStage.setScene(new Scene(loadingScreen));
        loadingStage.show();

        // A tela principal será aberta pelo LoadingScreenController após 3 segundos
    }

    public static void main(String[] args) {
        launch(args);
    }
}
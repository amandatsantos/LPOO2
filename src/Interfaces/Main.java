package Interfaces;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Interfaces/Dashboard.fxml"));
        primaryStage.setTitle("Monitoramento de Umidade e Temperatura");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false); // Impede redimensionamento da janela
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
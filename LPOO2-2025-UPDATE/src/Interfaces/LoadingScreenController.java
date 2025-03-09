package Interfaces;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoadingScreenController {

    @FXML
    private StackPane root; // Referência ao StackPane raiz

    @FXML
    private ProgressIndicator progressIndicator; // Referência ao ProgressIndicator

    public void initialize() {
        // Verifica se o progressIndicator foi injetado corretamente
        if (progressIndicator == null) {
            System.err.println("Erro: ProgressIndicator não foi injetado corretamente.");
            return;
        }

        // Aplica o estilo diretamente ao ProgressIndicator
        progressIndicator.setStyle("-fx-progress-color: green;");

        // Inicia uma tarefa em segundo plano para aguardar 3 segundos
        new Thread(() -> {
            try {
                // Aguarda 3 segundos
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Fecha a tela de loading e abre a tela principal na thread da interface gráfica
            javafx.application.Platform.runLater(() -> {
                try {
                    // Carrega a tela principal
                    Parent mainScreen = FXMLLoader.load(getClass().getResource("/Interfaces/Dashboard.fxml"));
                    Stage mainStage = new Stage();
                    mainStage.setScene(new Scene(mainScreen));
                    mainStage.setTitle("Monitoramento de Umidade e Temperatura");
                    mainStage.setResizable(false);

                    // Fecha a tela de loading
                    Stage loadingStage = (Stage) root.getScene().getWindow();
                    loadingStage.close();

                    // Exibe a tela principal
                    mainStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start(); // Inicia a thread
    }
}
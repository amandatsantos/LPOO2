package Interfaces;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class Controller {

    @FXML
    private Button btnGerenciarArduino; // Certifique-se de que o ID do botão no FXML é "btnGerenciarArduino"

    @FXML
    private AnchorPane rootPane;

    @FXML
    private BarChart<String, Number> humidityChart;

    @FXML
    private BarChart<String, Number> temperatureChart;

    @FXML
    private void initialize() {
        // Adiciona o evento de clique ao botão "Gerenciar Arduino"
        btnGerenciarArduino.setOnAction(event -> abrirModal());

        // Configurar dados iniciais nos gráficos (se necessário)
        configurarGraficos();
    }

    private void abrirModal() {
        // Criar o conteúdo da modal
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(rootPane.getScene().getWindow());
        modalStage.setTitle("Gerenciar Arduino");

        VBox modalContent = new VBox();
        modalContent.setSpacing(10);
        modalContent.setStyle("-fx-padding: 20; -fx-background-color: #FFFFFF;");

        Label mensagem = new Label("EM DESENVOLVIMENTO");
        Button btnFechar = new Button("Fechar");
        btnFechar.setOnAction(e -> modalStage.close());

        modalContent.getChildren().addAll(mensagem, btnFechar);

        Scene modalScene = new Scene(modalContent, 300, 150);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();

        // Limpar dados existentes antes de adicionar novos
        humidityChart.getData().clear();
        temperatureChart.getData().clear();

        configurarGraficos();
    }

    private void configurarGraficos() {
        // Dados da Umidade Relativa do Ar
        XYChart.Series<String, Number> humiditySeries = new XYChart.Series<>();
        humiditySeries.getData().add(new XYChart.Data<>("Seg", 27.3));
        humiditySeries.getData().add(new XYChart.Data<>("Ter", 11.7));
        humiditySeries.getData().add(new XYChart.Data<>("Qua", 19.1));
        humiditySeries.getData().add(new XYChart.Data<>("Qui", 0.3));
        humiditySeries.getData().add(new XYChart.Data<>("Sex", 23.4));
        humiditySeries.getData().add(new XYChart.Data<>("Sab", 26.4));
        humiditySeries.getData().add(new XYChart.Data<>("Dom", 16.2));
        humidityChart.getData().add(humiditySeries);

        // Dados da Medição da Temperatura
        XYChart.Series<String, Number> tempMinSeries = new XYChart.Series<>();
        tempMinSeries.setName("Mínima");
        tempMinSeries.getData().add(new XYChart.Data<>("Seg", 7));
        tempMinSeries.getData().add(new XYChart.Data<>("Ter", 5));
        tempMinSeries.getData().add(new XYChart.Data<>("Qua", 8));
        tempMinSeries.getData().add(new XYChart.Data<>("Qui", 9));
        tempMinSeries.getData().add(new XYChart.Data<>("Sex", 14));
        tempMinSeries.getData().add(new XYChart.Data<>("Sab", 6));
        tempMinSeries.getData().add(new XYChart.Data<>("Dom", 11));

        XYChart.Series<String, Number> tempMaxSeries = new XYChart.Series<>();
        tempMaxSeries.setName("Máxima");
        tempMaxSeries.getData().add(new XYChart.Data<>("Seg", 22));
        tempMaxSeries.getData().add(new XYChart.Data<>("Ter", 28));
        tempMaxSeries.getData().add(new XYChart.Data<>("Qua", 24));
        tempMaxSeries.getData().add(new XYChart.Data<>("Qui", 14));
        tempMaxSeries.getData().add(new XYChart.Data<>("Sex", 20));
        tempMaxSeries.getData().add(new XYChart.Data<>("Sab", 29));
        tempMaxSeries.getData().add(new XYChart.Data<>("Dom", 24));

        temperatureChart.getData().addAll(tempMinSeries, tempMaxSeries);
    }
}

package Interfaces;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.util.Random;

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
    private HBox temperatureLegend;  // Referência para o HBox da legenda

    private Random random = new Random();

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
        // Criando as séries de dados para umidade e temperatura
        XYChart.Series<String, Number> humiditySeries = new XYChart.Series<>();
        humiditySeries.setName("Umidade");

        XYChart.Series<String, Number> tempMinSeries = new XYChart.Series<>();
        tempMinSeries.setName("Mínima");

        XYChart.Series<String, Number> tempMaxSeries = new XYChart.Series<>();
        tempMaxSeries.setName("Máxima");

        for (String day : new String[]{"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"}) {
            humiditySeries.getData().add(new XYChart.Data<>(day, generateRandomHumidity()));
            tempMinSeries.getData().add(new XYChart.Data<>(day, generateRandomTemperatureMin()));
            tempMaxSeries.getData().add(new XYChart.Data<>(day, generateRandomTemperatureMax()));
        }

        humidityChart.getData().add(humiditySeries);
        temperatureChart.getData().addAll(tempMinSeries, tempMaxSeries);

        // Alterando as cores das barras
        humiditySeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #6398ff;"));  // Azul
        tempMinSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #4eb500;"));  // Vermelho
        tempMaxSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #b50000;"));

        // Inicialmente, o gráfico de temperaturas e a legenda estão ocultos
        temperatureChart.setVisible(false);
        temperatureLegend.setVisible(false); // Ocultando a legenda também

        // Atualizar os gráficos a cada 2 segundos
        updateChartsPeriodically();
    }

    private double generateRandomHumidity() {
        return 10 + (random.nextDouble() * 90); // Humidade entre 10 e 100%
    }

    private double generateRandomTemperatureMin() {
        return 5 + (random.nextDouble() * 10); // Temperatura mínima entre 5 e 15 graus
    }

    private double generateRandomTemperatureMax() {
        return 15 + (random.nextDouble() * 15); // Temperatura máxima entre 15 e 30 graus
    }

    private void updateChartsPeriodically() {
        // Cria um KeyFrame para atualizar os gráficos a cada 2 segundos
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), e -> updateChartData());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE); // O ciclo se repete indefinidamente
        timeline.play(); // Inicia a Timeline
    }

    private void updateChartData() {
        // Atualiza os dados da umidade
        for (XYChart.Data<String, Number> data : humidityChart.getData().get(0).getData()) {
            data.setYValue(generateRandomHumidity());
        }

        // Atualiza os dados da temperatura mínima
        for (XYChart.Data<String, Number> data : temperatureChart.getData().get(0).getData()) {
            data.setYValue(generateRandomTemperatureMin());
        }

        // Atualiza os dados da temperatura máxima
        for (XYChart.Data<String, Number> data : temperatureChart.getData().get(1).getData()) {
            data.setYValue(generateRandomTemperatureMax());
        }
    }

    @FXML
    private void showTemperatureChart() {
        // Alterna a visibilidade do gráfico de temperatura e da legenda
        boolean isVisible = temperatureChart.isVisible();
        temperatureChart.setVisible(!isVisible);
        temperatureLegend.setVisible(!isVisible); // Alterna também a visibilidade da legenda
    }

    @FXML
    private void showHumidityChart() {
        // Alterna a visibilidade do gráfico de umidade
        boolean isVisible = humidityChart.isVisible();
        humidityChart.setVisible(!isVisible);
    }
}

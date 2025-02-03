package Interfaces;
import DatabaseConnection.DatabaseConnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import Monitoramento.Monitoramento;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Controller {

    @FXML
    private void handleExit() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Deseja realmente sair?");
        alert.setContentText("Clique em OK para sair.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void ligarArduino() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Ligar Arduino");
        alert.setHeaderText(null);
        alert.setContentText("O Arduino foi ligado!");
        alert.showAndWait();
    }

    @FXML
    private void gerarPlanilha() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Gerar Planilha");
        alert.setHeaderText(null);
        alert.setContentText("A planilha foi gerada com sucesso!");
        alert.showAndWait();
    }

    @FXML
    private void openUserManual() {
        Alert manualAlert = new Alert(AlertType.INFORMATION);
        manualAlert.setTitle("Manual do Usuário");
        manualAlert.setHeaderText("Manual Básico");
        manualAlert.setContentText("Aqui você pode colocar informações do manual de uso ou redirecionar para outra janela.");
        manualAlert.showAndWait();
    }

    @FXML
    private void openSupport() {
        Alert supportAlert = new Alert(AlertType.INFORMATION);
        supportAlert.setTitle("Suporte");
        supportAlert.setHeaderText(null);
        supportAlert.setContentText("Para suporte, entre em contato com o time de desenvolvimento.");
        supportAlert.showAndWait();
    }


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
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(rootPane.getScene().getWindow());
        modalStage.setTitle("Gerenciar Arduino");

        VBox modalContent = new VBox(10);
        modalContent.setPadding(new Insets(20));
        modalContent.setStyle("-fx-background-color: #FFFFFF;");

        Label mensagem = new Label("Selecione uma ação:");
        Button btnConectar = new Button("Conectar Arduino");
        Button btnDesconectar = new Button("Desconectar Arduino");
        Button btnFechar = new Button("Fechar");

        btnConectar.setOnAction(e -> {
            // Lógica para conectar o Arduino
            System.out.println("Arduino conectado");
            exibirAlerta(AlertType.INFORMATION, "Arduino conectado com sucesso!");
        });

        btnDesconectar.setOnAction(e -> {
            // Lógica para desconectar o Arduino
            System.out.println("Arduino desconectado");
            exibirAlerta(AlertType.INFORMATION, "Arduino desconectado.");
        });

        btnFechar.setOnAction(e -> modalStage.close());

        modalContent.getChildren().addAll(mensagem, btnConectar, btnDesconectar, btnFechar);

        Scene modalScene = new Scene(modalContent, 300, 200);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();
    }

    private void exibirAlerta(AlertType alertType, String s) {
    }

    private Monitoramento monitoramento = new Monitoramento("DHT11");

    private void configurarGraficos() {
        XYChart.Series<String, Number> humiditySeries = new XYChart.Series<>();
        humiditySeries.setName("Umidade");

        XYChart.Series<String, Number> tempMinSeries = new XYChart.Series<>();
        tempMinSeries.setName("Mínima");

        XYChart.Series<String, Number> tempMaxSeries = new XYChart.Series<>();
        tempMaxSeries.setName("Máxima");

        humidityChart.getData().add(humiditySeries);
        temperatureChart.getData().addAll(tempMinSeries, tempMaxSeries);

        // Define as cores das barras
        humiditySeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #6398ff;"));  // Azul
        tempMinSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #4eb500;"));  // Verde
        tempMaxSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #b50000;"));  // Vermelho

        // Inicialmente, o gráfico de temperaturas e a legenda estão ocultos
        temperatureChart.setVisible(false);
        temperatureLegend.setVisible(false);

        // Inicia a atualização automática dos gráficos
        atualizarGraficosPeriodicamente();
    }

    private void atualizarGraficosPeriodicamente() {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), e -> atualizarDadosGraficos());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void atualizarDadosGraficos() {
        // Atualiza os dados do sensor
        monitoramento.atualizarSensor();

        // Obtém os novos valores de temperatura e umidade
        double temperatura = monitoramento.getTemperatura();
        double umidade = monitoramento.getUmidade();

        // Obtém o horário atual como identificador
        String horario = new SimpleDateFormat("HH:mm:ss").format(new Date());

        // Adiciona os novos dados ao gráfico de umidade
        humidityChart.getData().get(0).getData().add(new XYChart.Data<>(horario, umidade));

        // Adiciona os novos dados ao gráfico de temperatura (mínima e máxima)
        temperatureChart.getData().get(0).getData().add(new XYChart.Data<>(horario, temperatura - 2)); // Mínima
        temperatureChart.getData().get(1).getData().add(new XYChart.Data<>(horario, temperatura + 2)); // Máxima

        // Remove dados antigos para manter o gráfico atualizado
        if (humidityChart.getData().get(0).getData().size() > 10) {
            humidityChart.getData().get(0).getData().remove(0);
            temperatureChart.getData().get(0).getData().remove(0);
            temperatureChart.getData().get(1).getData().remove(0);
        }
    }

    @FXML
    private void showTemperatureChart() {
        boolean isVisible = temperatureChart.isVisible();
        temperatureChart.setVisible(!isVisible);
        temperatureLegend.setVisible(!isVisible);
    }

    @FXML
    private void showHumidityChart() {
        boolean isVisible = humidityChart.isVisible();
        humidityChart.setVisible(!isVisible);
    }
}
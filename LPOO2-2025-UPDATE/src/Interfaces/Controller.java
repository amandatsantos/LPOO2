package Interfaces;

import DatabaseConnection.DatabaseConnection;
import com.fazecast.jSerialComm.SerialPort;
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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import Monitoramento.Monitoramento;
import Sensor.Sensor;
import SerialCommunication.SerialCommunication;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;

public class Controller {

    // Componentes da interface
    @FXML
    private Button btnArduino;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private BarChart<String, Number> humidityChart;

    @FXML
    private BarChart<String, Number> temperatureChart;

//    @FXML
//    private HBox temperatureLegend;

    @FXML
    private HBox humidityLegend; // Referência para o HBox da legenda de umidade

    // Variáveis para leitura serial
    private SerialPort comPort;
    private LinkedList<String[]> ultimasLeituras = new LinkedList<>();

    @FXML
    private void initialize() {
        // Configura a porta serial
        configurarPortaSerial();

        // Configura o botão "Arduino"
        btnArduino.setOnAction(event -> abrirModal());

        // Configura os gráficos
        configurarGraficos();

        // Inicia a atualização automática dos gráficos
        atualizarGraficosPeriodicamente();
    }

    // Configura a porta serial
    private void configurarPortaSerial() {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals("COM8")) { // Altere para a porta correta
                comPort = port;
                break;
            }
        }

        if (comPort == null) {
            System.err.println("Porta COM8 não encontrada.");
            return;
        }

        comPort.setBaudRate(9600);
        if (!comPort.openPort()) {
            System.err.println("Falha ao abrir a porta serial.");
            return;
        }
    }

    // Processa os dados recebidos do Arduino
    private void processarDados(String data) {
        float temperatura = 0, umidadeAr = 0, umidadeSolo = 0;
        String[] linhas = data.split("\n");

        for (String linha : linhas) {
            linha = linha.trim();
            if (linha.startsWith("Umidade do ar:")) {
                umidadeAr = Float.parseFloat(linha.replace("Umidade do ar:", "").replace("%", "").trim());
            } else if (linha.startsWith("Temperatura:")) {
                temperatura = Float.parseFloat(linha.replace("Temperatura:", "").replace("°C", "").trim());
            } else if (linha.startsWith("Umidade do solo:")) {
                umidadeSolo = Float.parseFloat(linha.replace("Umidade do solo:", "").replace("%", "").trim());
            }
        }

        // Registrar no banco de dados
        DatabaseConnection.insertLeitura(temperatura, umidadeAr, umidadeSolo);

        // Criar timestamp da leitura
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Adicionar nova leitura à lista (mantendo no máximo 10 leituras)
        if (ultimasLeituras.size() >= 10) {
            ultimasLeituras.removeFirst();
        }
        ultimasLeituras.add(new String[]{timestamp, String.valueOf(temperatura), String.valueOf(umidadeAr), String.valueOf(umidadeSolo)});

        // Atualizar gráficos
        atualizarGraficos();
    }

    // Atualiza os gráficos com os dados mais recentes
    private void atualizarGraficos() {
        if (ultimasLeituras.isEmpty()) {
            return; // Não há dados para exibir
        }

        // Obtém a última leitura
        String[] ultimaLeitura = ultimasLeituras.getLast();
        double temperatura = Double.parseDouble(ultimaLeitura[1]);
        double umidadeAr = Double.parseDouble(ultimaLeitura[2]);
        double umidadeSolo = Double.parseDouble(ultimaLeitura[3]);

        // Obtém o horário atual como identificador
        String horario = new SimpleDateFormat("HH:mm:ss").format(new Date());

        // Adiciona os novos dados ao gráfico de umidade
        XYChart.Data<String, Number> umidadeArData = new XYChart.Data<>(horario, umidadeAr);
        XYChart.Data<String, Number> umidadeSoloData = new XYChart.Data<>(horario, umidadeSolo);

        humidityChart.getData().get(0).getData().add(umidadeArData); // Umidade do Ar
        humidityChart.getData().get(1).getData().add(umidadeSoloData); // Umidade do Solo

        // Aplica as cores das barras após a criação dos nós
        umidadeArData.getNode().setStyle("-fx-bar-fill: #6398ff;"); // Azul
        umidadeSoloData.getNode().setStyle("-fx-bar-fill: #8B4513;"); // Marrom

        // Adiciona os novos dados ao gráfico de temperatura
        XYChart.Data<String, Number> tempMaxData = new XYChart.Data<>(horario, temperatura + 2); // Temperatura Máxima
//        XYChart.Data<String, Number> tempMinData = new XYChart.Data<>(horario, temperatura - 2); // Temperatura Mínima

        temperatureChart.getData().get(0).getData().add(tempMaxData); // Temperatura Máxima
//        temperatureChart.getData().get(1).getData().add(tempMinData); // Temperatura Mínima

        // Aplica as cores das barras após a criação dos nós
        tempMaxData.getNode().setStyle("-fx-bar-fill: #e86f07;"); // Vermelho
//        tempMinData.getNode().setStyle("-fx-bar-fill: #4eb500;"); // Verde

        // Remove dados antigos para manter o gráfico atualizado
        if (humidityChart.getData().get(0).getData().size() > 10) {
            humidityChart.getData().get(0).getData().remove(0);
            humidityChart.getData().get(1).getData().remove(0);
        }
        if (temperatureChart.getData().get(0).getData().size() > 10) {
            temperatureChart.getData().get(0).getData().remove(0);
            temperatureChart.getData().get(1).getData().remove(0);
        }
    }

    // Configura os gráficos
    private void configurarGraficos() {
        // Configuração do gráfico de umidade
        XYChart.Series<String, Number> umidadeArSeries = new XYChart.Series<>();
        umidadeArSeries.setName("Umidade do Ar");

        XYChart.Series<String, Number> umidadeSoloSeries = new XYChart.Series<>();
        umidadeSoloSeries.setName("Umidade do Solo");

        humidityChart.getData().addAll(umidadeArSeries, umidadeSoloSeries);
        humidityChart.setLegendVisible(false);

        // Aplica as cores das barras após a criação dos nós
        umidadeArSeries.getData().forEach(data ->
                data.getNode().setStyle("-fx-bar-fill: #6398ff;")); // Azul
        umidadeSoloSeries.getData().forEach(data ->
                data.getNode().setStyle("-fx-bar-fill: #8B4513;")); // Marrom

        // Configuração do gráfico de temperatura
        XYChart.Series<String, Number> tempMaxSeries = new XYChart.Series<>();
        tempMaxSeries.setName("Temperatura Máxima");

//        XYChart.Series<String, Number> tempMinSeries = new XYChart.Series<>();
//        tempMinSeries.setName("Temperatura Mínima");

        temperatureChart.getData().addAll(tempMaxSeries);

        // Aplica as cores das barras após a criação dos nós
        tempMaxSeries.getData().forEach(data ->
                data.getNode().setStyle("-fx-bar-fill: #b50000;")); // Vermelho
//        tempMinSeries.getData().forEach(data ->
//                data.getNode().setStyle("-fx-bar-fill: #4eb500;")); // Verde

        // Torna os gráficos visíveis o tempo todo
        temperatureChart.setVisible(true);
        humidityChart.setVisible(true);
//        temperatureLegend.setVisible(true);
    }

    // Atualiza os gráficos periodicamente
    private void atualizarGraficosPeriodicamente() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            if (comPort != null && comPort.isOpen()) {
                byte[] buffer = new byte[1024];
                int bytesRead = comPort.readBytes(buffer, buffer.length);
                if (bytesRead > 0) {
                    String data = new String(buffer, 0, bytesRead);
                    processarDados(data);
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Métodos de alerta
    @FXML
    private void showHighTemperatureAlert() {
        showAlert("Alerta de alta temperatura", "A temperatura do minhocário está acima do nível ideal para a saúde das minhocas e para compostagem eficiente.\n\n" +
                "Ação recomendada: \n \n" +
                "Verifique a temperatura para garantir condições adequadas.\n ");
    }

    @FXML
    private void showLowTemperatureAlert() {
        showAlert("Alerta de baixa temperatura", "A temperatura do minhocário está abaixo do nível ideal para a saúde das minhocas e para compostagem eficiente.\n\n" +
                "Ação recomendada: \n \n" +
                "Verifique a temperatura para garantir condições adequadas.\n ");
    }

    @FXML
    private void showLowHumidityAlert() {
        showAlert("Alerta de baixa umidade", "A umidade do minhocário está abaixo do nível ideal para a saúde das minhocas e para a compostagem eficiente. \n \n" +
                "Ação recomendada: \n \n" +
                "Verifique a umidade para garantir condições adequadas.\n ");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Métodos de interação com o Arduino
    @FXML
    private void ligarArduino() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Ligar Arduino");
        alert.setHeaderText(null);
        alert.setContentText("O Arduino foi ligado!");
        alert.showAndWait();
    }

    @FXML
    private void gerarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Escolha onde salvar o arquivo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivo CSV (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.append("Data/Hora,Temperatura,Umidade do Ar,Umidade do Solo\n");
                for (String[] leitura : ultimasLeituras) {
                    writer.append(String.join(",", leitura)).append("\n");
                }

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Arquivo salvo em: " + fileToSave.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao salvar o arquivo.");
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void openSupport() {
        Alert supportAlert = new Alert(AlertType.INFORMATION);
        supportAlert.setTitle("Suporte");
        supportAlert.setHeaderText(null);
        supportAlert.setContentText("Para suporte, entre em contato com o time de desenvolvimento.\n\n Amanda Tavares - tavaresamandasantos@gmail.com\n Bruno Flores - brunoinstt@gmail.com \n Henrique Santos - henrsilvasantos@gmail.com");
        supportAlert.showAndWait();
    }

    // Método para abrir o modal do Arduino
    private void abrirModal() {
        Alert supportAlert = new Alert(AlertType.INFORMATION);
        supportAlert.setTitle("Arduino");
        supportAlert.setHeaderText(null);
        supportAlert.setContentText("Para o desenvolvimento dessa aplicação foram utilizados os seguintes materiais:\n\n Arduino: Tavares \n Porta conectada: \n Abound: \n Sensores: \n Bibliotecas: \n Método de comunicação: ");
        supportAlert.showAndWait();
    }

    private void exibirAlerta(AlertType alertType, String mensagem) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void showHumidityChart() {
        boolean isVisible = humidityChart.isVisible();
        humidityChart.setVisible(!isVisible);
    }

    @FXML
    private void showTemperatureChart() {
        boolean isVisible = temperatureChart.isVisible();
        temperatureChart.setVisible(!isVisible);
//        temperatureLegend.setVisible(!isVisible);
    }
}
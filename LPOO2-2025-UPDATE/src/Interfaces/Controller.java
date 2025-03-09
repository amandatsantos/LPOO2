package Interfaces;
import javafx.scene.control.Tooltip;
import DatabaseConnection.DatabaseConnection;
import com.fazecast.jSerialComm.SerialPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
public class Controller {

    @FXML
    private Button btnArduino;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private BarChart<String, Number> humidityBarChart;

    @FXML
    private BarChart<String, Number> temperatureBarChart;

    @FXML
    private LineChart<String, Number> humidityLineChart;

    @FXML
    private LineChart<String, Number> temperatureLineChart;

    @FXML
    private StackPane humidityStackPane;

    @FXML
    private StackPane temperatureStackPane;

    @FXML
    private HBox humidityLegend;

    @FXML
    private HBox tempAlert;

    @FXML
    private HBox umidadeArAlert;

    @FXML
    private HBox umidadeSoloAlert;

    @FXML
    private Label tempLabel;

    @FXML
    private Label umidadeArLabel;

    @FXML
    private Label umidadeSoloLabel;

    @FXML
    private VBox alertBox;

    @FXML
    private ImageView sinoImageView;

    private SerialPort comPort;
    private LinkedList<String[]> ultimasLeituras = new LinkedList<>();
    private Timeline blinkTimeline;
    private MediaPlayer mediaPlayer;

    @FXML
    private void handleMouseEnterButton(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.setScaleX(1.1);
        source.setScaleY(1.1);
    }

    @FXML
    private void handleMouseExitButton(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.setScaleX(1.0);
        source.setScaleY(1.0);
    }

    @FXML
    private void handleMouseEnter(MouseEvent event) {
        HBox alertBox = (HBox) event.getSource();
        alertBox.setScaleX(1.05);
        alertBox.setScaleY(1.05);
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        HBox alertBox = (HBox) event.getSource();
        alertBox.setScaleX(1.0);
        alertBox.setScaleY(1.0);
    }

    @FXML
    private void mostrarVersao() {
        // Cria um TextFlow para exibir texto formatado
        TextFlow textFlow = new TextFlow();

        // Adiciona "Versão 1.0" em negrito
        Text versaoText = new Text("Versão 1.0\n\n");
        versaoText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        // Adiciona o restante da mensagem
        Text atualizacoesText = new Text(
                "Updates e Melhorias:\n"
                        + "- Adição do gráfico de linha.\n"
                        + "- Melhoria nos alertas de temperatura e umidade.\n"
                        + "- Adição de som nos alertas.\n"
                        + "- Adição da tela de loading.\n"
                        + "- Correções de bugs e melhorias de desempenho."
        );

        // Adiciona os textos ao TextFlow
        textFlow.getChildren().addAll(versaoText, atualizacoesText);

        // Exibe a mensagem em um Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Versão do Projeto");
        alert.setHeaderText(null);

        // Define o conteúdo do Alert como o TextFlow
        alert.getDialogPane().setContent(textFlow);

        // Exibe o Alert
        alert.showAndWait();
    }

    @FXML
    private void toggleAlerts() {
        if (tempAlert != null && umidadeArAlert != null && umidadeSoloAlert != null) {
            boolean isVisible = tempAlert.isVisible();
            tempAlert.setVisible(!isVisible);
            umidadeArAlert.setVisible(!isVisible);
            umidadeSoloAlert.setVisible(!isVisible);
        } else {
            System.err.println("Erro: Alertas não foram injetados corretamente.");
        }
    }

    @FXML
    private void showTemperatureAlert() {
        String mensagem = "";
        if (tempLabel.getText().contains("alta")) {
            mensagem = "Alerta de alta temperatura\n\nA temperatura do minhocário está acima do nível ideal para a saúde das minhocas e para compostagem eficiente.";
        } else if (tempLabel.getText().contains("baixa")) {
            mensagem = "Alerta de baixa temperatura\n\nA temperatura do minhocário está abaixo do nível ideal para a saúde das minhocas e para compostagem eficiente.";
        } else {
            mensagem = "Temperatura ideal\n\nA temperatura do minhocário está dentro do nível estimado.";
        }
        showAlert("Alerta de Temperatura", mensagem);
    }

    @FXML
    private void showHumidityAlert() {
        String mensagem = "";
        if (umidadeArLabel.getText().contains("alta")) {
            mensagem = "Alerta de alta umidade do ar\n\nA umidade do ar está acima do nível ideal para a saúde das minhocas e para compostagem eficiente.";
        } else if (umidadeArLabel.getText().contains("baixa")) {
            mensagem = "Alerta de baixa umidade do ar\n\nA umidade do ar está abaixo do nível ideal para a saúde das minhocas e para compostagem eficiente.";
        } else {
            mensagem = "Umidade do ar ideal\n\nA umidade do ar está dentro do nível estimado.";
        }
        showAlert("Alerta de Umidade do Ar", mensagem);
    }

    @FXML
    private void showSoilHumidityAlert() {
        String mensagem = "";
        if (umidadeSoloLabel.getText().contains("alta")) {
            mensagem = "Alerta de alta umidade do solo\n\nA umidade do solo está acima do nível ideal para a saúde das minhocas e para compostagem eficiente.";
        } else if (umidadeSoloLabel.getText().contains("baixa")) {
            mensagem = "Alerta de baixa umidade do solo\n\nA umidade do solo está abaixo do nível ideal para a saúde das minhocas e para compostagem eficiente.";
        } else {
            mensagem = "Umidade do solo ideal\n\nA umidade do solo está dentro do nível ideal.";
        }
        showAlert("Alerta de Umidade do Solo", mensagem);
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
//Alerta - Toque
    private void playAlertSound() {
        try {
            String soundPath = getClass().getResource("/sounds/alerta5.mp3").toString();
            Media sound = new Media(soundPath);
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao reproduzir o som: " + e.getMessage());
        }
    }

    private void stopAlertSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void verificarAlertas(float temperatura, float umidadeAr, float umidadeSolo) {
        float tempAlta = 130;
        float tempBaixa = -2;
        float umidadeAlta = 100;
        float umidadeBaixa = -2;

        boolean hasAlert = false;

        if (temperatura >= tempAlta) {
            tempLabel.setText("Temperatura alta");
            tempAlert.setStyle("-fx-background-color: #ff4444; -fx-background-radius: 5; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
            hasAlert = true;
        } else if (temperatura <= tempBaixa) {
            tempLabel.setText("Temperatura baixa");
            tempAlert.setStyle("-fx-background-color: #ff4444; -fx-background-radius: 5; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
            hasAlert = true;
        } else {
            tempLabel.setText("Temperatura Ideal");
            tempAlert.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
        }

        if (umidadeAr >= umidadeAlta) {
            umidadeArLabel.setText("Umidade do Ar alta");
            umidadeArAlert.setStyle("-fx-background-color: #ff4444; -fx-background-radius: 5; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
            hasAlert = true;
        } else if (umidadeAr <= umidadeBaixa) {
            umidadeArLabel.setText("Umidade do Ar baixa");
            umidadeArAlert.setStyle("-fx-background-color: #ff4444; -fx-background-radius: 5; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
            hasAlert = true;
        } else {
            umidadeArLabel.setText("Umidade do Ar ideal");
            umidadeArAlert.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
        }

        if (umidadeSolo >= umidadeAlta) {
            umidadeSoloLabel.setText("Umidade do Solo alta");
            umidadeSoloAlert.setStyle("-fx-background-color: #ff4444; -fx-background-radius: 5; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
            hasAlert = true;
        } else if (umidadeSolo <= umidadeBaixa) {
            umidadeSoloLabel.setText("Umidade do Solo baixa");
            umidadeSoloAlert.setStyle("-fx-background-color: #ff4444; -fx-background-radius: 5; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
            hasAlert = true;
        } else {
            umidadeSoloLabel.setText("Umidade do Solo Ideal");
            umidadeSoloAlert.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
        }

        if (hasAlert) {
            sinoImageView.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0.5, 0, 0);");
            playAlertSound();
            startBlinking();
        } else {
            sinoImageView.setStyle("");
            stopBlinking();
        }
    }

    private void startBlinking() {
        if (blinkTimeline == null) {
            blinkTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), e -> {
                        if (sinoImageView.getStyle().contains("red")) {
                            sinoImageView.setStyle("");
                        } else {
                            sinoImageView.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0.5, 0, 0);");
                        }
                    }
                    ));
            blinkTimeline.setCycleCount(Timeline.INDEFINITE);
        }
        blinkTimeline.play();
    }

    private void stopBlinking() {
        if (blinkTimeline != null) {
            blinkTimeline.stop();
        }
        sinoImageView.setStyle("");
    }

    @FXML
    private void initialize() {
        configurarPortaSerial();
        btnArduino.setOnAction(event -> abrirModal());
        configurarGraficos();
        atualizarGraficosPeriodicamente();
    }

    private void configurarPortaSerial() {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals("COM8")) {
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

        DatabaseConnection.insertLeitura(temperatura, umidadeAr, umidadeSolo);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (ultimasLeituras.size() >= 10) {
            ultimasLeituras.removeFirst();
        }
        ultimasLeituras.add(new String[]{timestamp, String.valueOf(temperatura), String.valueOf(umidadeAr), String.valueOf(umidadeSolo)});

        atualizarGraficos();
        verificarAlertas(temperatura, umidadeAr, umidadeSolo);
    }

    private void atualizarGraficos() {
        if (ultimasLeituras.isEmpty()) {
            return;
        }

        String[] ultimaLeitura = ultimasLeituras.getLast();
        double temperatura = Double.parseDouble(ultimaLeitura[1]);
        double umidadeAr = Double.parseDouble(ultimaLeitura[2]);
        double umidadeSolo = Double.parseDouble(ultimaLeitura[3]);

        String horario = new SimpleDateFormat("HH:mm:ss").format(new Date());

        // Dados para umidade
        XYChart.Data<String, Number> umidadeArDataBar = new XYChart.Data<>(horario, umidadeAr);
        XYChart.Data<String, Number> umidadeSoloDataBar = new XYChart.Data<>(horario, umidadeSolo);

        XYChart.Data<String, Number> umidadeArDataLine = new XYChart.Data<>(horario, umidadeAr);
        XYChart.Data<String, Number> umidadeSoloDataLine = new XYChart.Data<>(horario, umidadeSolo);

        // Adiciona dados ao gráfico de barras de umidade
        humidityBarChart.getData().get(0).getData().add(umidadeArDataBar);
        humidityBarChart.getData().get(1).getData().add(umidadeSoloDataBar);

        // Adiciona dados ao gráfico de linhas de umidade
        humidityLineChart.getData().get(0).getData().add(umidadeArDataLine);
        humidityLineChart.getData().get(1).getData().add(umidadeSoloDataLine);

        // Aplica estilos às barras após adicionar os dados
        aplicarEstilosBarras(umidadeArDataBar, "#6398ff"); // Azul para umidade do ar
        aplicarEstilosBarras(umidadeSoloDataBar, "#8B4513"); // Marrom para umidade do solo

        // Aplica estilos às linhas
        aplicarEstilosLinhas(humidityLineChart.getData().get(0), "#6398ff"); // Azul para umidade do ar
        aplicarEstilosLinhas(humidityLineChart.getData().get(1), "#8B4513"); // Marrom para umidade do solo

        // Dados para temperatura
        XYChart.Data<String, Number> tempMaxDataBar = new XYChart.Data<>(horario, temperatura + 2);
        XYChart.Data<String, Number> tempMaxDataLine = new XYChart.Data<>(horario, temperatura + 2);

        // Adiciona dados ao gráfico de barras de temperatura
        temperatureBarChart.getData().get(0).getData().add(tempMaxDataBar);

        // Adiciona dados ao gráfico de linhas de temperatura
        temperatureLineChart.getData().get(0).getData().add(tempMaxDataLine);

        // Aplica estilos às barras de temperatura
        aplicarEstilosBarras(tempMaxDataBar, "#e86f07"); // Laranja para temperatura

        // Aplica estilos às linhas de temperatura
        aplicarEstilosLinhas(temperatureLineChart.getData().get(0), "#e86f07"); // Laranja para temperatura

        // Limita o número de pontos exibidos nos gráficos
        if (humidityBarChart.getData().get(0).getData().size() > 10) {
            humidityBarChart.getData().get(0).getData().remove(0);
            humidityBarChart.getData().get(1).getData().remove(0);
        }
        if (humidityLineChart.getData().get(0).getData().size() > 10) {
            humidityLineChart.getData().get(0).getData().remove(0);
            humidityLineChart.getData().get(1).getData().remove(0);
        }
        if (temperatureBarChart.getData().get(0).getData().size() > 10) {
            temperatureBarChart.getData().get(0).getData().remove(0);
        }
        if (temperatureLineChart.getData().get(0).getData().size() > 10) {
            temperatureLineChart.getData().get(0).getData().remove(0);
        }
    }

    private void aplicarEstilosBarras(XYChart.Data<String, Number> data, String cor) {
        if (data.getNode() != null) {
            data.getNode().setStyle("-fx-bar-fill: " + cor + ";");
        } else {
            // Adiciona um listener para aplicar o estilo quando o nó for criado
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + cor + ";");
                }
            });
        }
    }

    private void aplicarEstilosLinhas(XYChart.Series<String, Number> series, String cor) {
        series.getNode().setStyle("-fx-stroke: " + cor + "; -fx-stroke-width: 2px;");
    }

    private void configurarGraficos() {
        // Configuração do gráfico de umidade (barras)
        XYChart.Series<String, Number> umidadeArSeriesBar = new XYChart.Series<>();
        umidadeArSeriesBar.setName("Umidade do Ar");

        XYChart.Series<String, Number> umidadeSoloSeriesBar = new XYChart.Series<>();
        umidadeSoloSeriesBar.setName("Umidade do Solo");

        humidityBarChart.getData().addAll(umidadeArSeriesBar, umidadeSoloSeriesBar);

        // Configuração do gráfico de umidade (linhas)
        XYChart.Series<String, Number> umidadeArSeriesLine = new XYChart.Series<>();
        umidadeArSeriesLine.setName("Umidade do Ar");

        XYChart.Series<String, Number> umidadeSoloSeriesLine = new XYChart.Series<>();
        umidadeSoloSeriesLine.setName("Umidade do Solo");

        humidityLineChart.getData().addAll(umidadeArSeriesLine, umidadeSoloSeriesLine);

        // Configuração do gráfico de temperatura (barras)
        XYChart.Series<String, Number> tempMaxSeriesBar = new XYChart.Series<>();
        tempMaxSeriesBar.setName("Temperatura Máxima");

        temperatureBarChart.getData().add(tempMaxSeriesBar);

        // Configuração do gráfico de temperatura (linhas)
        XYChart.Series<String, Number> tempMaxSeriesLine = new XYChart.Series<>();
        tempMaxSeriesLine.setName("Temperatura Máxima");

        temperatureLineChart.getData().add(tempMaxSeriesLine);

        // Define a visibilidade inicial
        temperatureBarChart.setVisible(true);
        humidityBarChart.setVisible(true);
        temperatureLineChart.setVisible(false);
        humidityLineChart.setVisible(false);

    }


    @FXML
    private void toggleGraphs() {
        boolean isBarChartVisible = temperatureBarChart.isVisible();
        temperatureBarChart.setVisible(!isBarChartVisible);
        humidityBarChart.setVisible(!isBarChartVisible);
        temperatureLineChart.setVisible(isBarChartVisible);
        humidityLineChart.setVisible(isBarChartVisible);
    }
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
                // Escreve o cabeçalho do CSV
                writer.append("Data/Hora;Temperatura (°C);Umidade do Ar (%);Umidade do Solo (%)\n");

                // Escreve os dados
                for (String[] leitura : ultimasLeituras) {
                    String linha = String.join(";", leitura); // Usa ";" como delimitador
                    writer.append(linha).append("\n");
                }

                // Exibe uma mensagem de sucesso
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Arquivo salvo em: " + fileToSave.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                // Exibe uma mensagem de erro
                Alert alert = new Alert(Alert.AlertType.ERROR);
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
        Alert supportAlert = new Alert(Alert.AlertType.INFORMATION);
        supportAlert.setTitle("Suporte");
        supportAlert.setHeaderText(null);
        supportAlert.setContentText("Para suporte, entre em contato com o time de desenvolvimento.\n\n Amanda Tavares - tavaresamandasantos@gmail.com\n Bruno Flores - brunoinstt@gmail.com \n Henrique Santos - henrsilvasantos@gmail.com");
        supportAlert.showAndWait();
    }

    @FXML
    private void abrirModal() {
        Alert supportAlert = new Alert(Alert.AlertType.INFORMATION);
        supportAlert.setTitle("Arduino");
        supportAlert.setHeaderText(null);
        supportAlert.setContentText("Para o desenvolvimento dessa aplicação foram utilizados os seguintes materiais:\n\n Arduino: Uno \n Porta conectada: COM8 \n Baud Rate: 9600  \n Sensores: DHT11 e Higrômetro Pic Rasp \n Bibliotecas: DHT11 \n Método de comunicação: USB ");
        supportAlert.showAndWait();
    }
}
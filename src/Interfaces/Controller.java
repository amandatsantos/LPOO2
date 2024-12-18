package Interfaces;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class Controller  {

    @FXML
    private BarChart<String, Number> humidityChart;

    @FXML
    private BarChart<String, Number> temperatureChart;

    @FXML
    private void initialize() {
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

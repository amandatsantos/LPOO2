package Sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;



public class Controller implements Initializable {

    @FXML
    private BarChart<String, Integer> barChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        XYChart.Series series1= new XYChart.Series();
        series1.setName("2003");
        series1.getData().add(new XYChart.Data("teste", 46546));
        series1.getData().add(new XYChart.Data("teste2", 4546));
        series1.getData().add(new XYChart.Data("teste3", 546));
        series1.getData().add(new XYChart.Data("teste4", 465426));
        series1.getData().add(new XYChart.Data("teste5", 4654446));

        XYChart.Series series2= new XYChart.Series();
        series2.setName("2004");
        series2.getData().add(new XYChart.Data("teste", 13245));
        series2.getData().add(new XYChart.Data("teste2", 111));
        series2.getData().add(new XYChart.Data("teste3", 111546));
        series2.getData().add(new XYChart.Data("teste4", 111111));
        series2.getData().add(new XYChart.Data("teste5", 4132132));

        barChart.getData().addAll(series1, series2);


    }

}

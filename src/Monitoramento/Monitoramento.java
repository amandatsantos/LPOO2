/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Monitoramento;


import Sensor.Sensor;
import SerialCommunication.SerialCommunication;
import DatabaseConnection.DatabaseConnection;



public class Monitoramento {
    private Sensor sensor;
    private SerialCommunication serialCommunication;

   
    public Monitoramento(String tipoSensor) {
        serialCommunication = new SerialCommunication();
        sensor = new Sensor(tipoSensor);
    }

    
    public void atualizarSensor() {
        String dados = serialCommunication.readData();
      
        String[] partes = dados.split("\t");
        
        
        String temperaturaStr = partes[0].replace("Temperatura: ", "").replace(" °C", "").replace(",", ".");
        String umidadeStr = partes[1].replace("Umidade: ", "").replace(" %", "").replace(",", ".");
        
    
        float temperatura = Float.parseFloat(temperaturaStr);
        float umidade = Float.parseFloat(umidadeStr);
        
        sensor.setTemperatura(temperatura);
        sensor.setUmidade(umidade);
        

        DatabaseConnection.insertLeitura(temperatura, umidade);
    }

   
    public void exibirDados() {
        System.out.println("Temperatura: " + sensor.getTemperatura() + " °C");
        System.out.println("Umidade: " + sensor.getUmidade() + " %");
    }


    public String verificarAlertas() {
        if (sensor.getTemperatura() > 30.0) {
            return " Alerta! Temperatura muito alta: " + sensor.getTemperatura() + " °C";
        } else if (sensor.getUmidade() < 40.0) {
            return "️ Alerta! Umidade muito baixa: " + sensor.getUmidade() + " %";
        } else {
            return " Tudo certo: Temperatura e Umidade dentro dos limites.";
        }
    }

  
    public float getTemperatura() {
        return sensor.getTemperatura();
    }

    public float getUmidade() {
        return sensor.getUmidade();
    }
}

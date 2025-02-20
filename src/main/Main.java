/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/

package Main;

import Monitoramento.Monitoramento;
import Sensor.Sensor;
import SerialCommunication.SerialCommunication;

public class Main {
    public static void main(String[] args) {
       Sensor ArdSensor = new Sensor("DHT11"); // Agora passando o tipo esperado
        SerialCommunication minhaSerial = new SerialCommunication(); // 

        Monitoramento monitoramento = new Monitoramento(ArdSensor, minhaSerial);

        // Passando os objetos corretos para Monitoramento

        // Loop de Leitura e monitoramento
        while (true) {
            monitoramento.atualizarSensor(); // Atualiza os dados do sensor
            System.out.println(monitoramento.exibirDados()); // Exibe os dados no console
            monitoramento.verificarAlertas(); // Verifica e dispara alertas, se necessário

            try {
                Thread.sleep(5000); // Intervalo entre leituras
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

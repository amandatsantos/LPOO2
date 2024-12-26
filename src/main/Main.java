/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import Monitoramento.Monitoramento;


public class Main {
    public static void main(String[] args) {
        // Criação do objeto Monitoramento
        Monitoramento monitoramento = new Monitoramento("DHT11");

        // Loop de Leitura e monitoramento
        while (true) {
            monitoramento.atualizarSensor(); // Atualiza os dados do sensor
            monitoramento.exibirDados();    // Exibe os dados no console
            monitoramento.verificarAlertas(); // Verifica e dispara alertas, se necessário

            try {
                Thread.sleep(2000); // Intervalo de 2 segundos entre leituras
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Classe responsável por monitorar os dados recebidos de um sensor.
 * Realiza a leitura dos dados, atualiza o estado do sensor, verifica alertas
 * e exibe os dados.
 */
package Monitoramento;

import Sensor.Sensor;
import SerialCommunication.SerialCommunication;
import DatabaseConnection.DatabaseConnection;

public class Monitoramento {
    // Constantes para limites de alerta
    private static final float TEMPERATURA_ALERTA = 30.0f;
    private static final float UMIDADE_ALERTA = 40.0f;
    private static final float UMIDADE_SOLO_ALERTA = 50.0f;

    // Dependências
    private final Sensor sensor;
    private final SerialCommunication serialCommunication;

    /**
     * Construtor da classe Monitoramento.
     *
     * @param sensor               Instância do sensor.
     * @param serialCommunication  Instância da comunicação serial.
     */
    public Monitoramento(Sensor sensor, SerialCommunication serialCommunication) {
        this.sensor = sensor;
        this.serialCommunication = serialCommunication;
    }

    /**
     * Atualiza os dados do sensor a partir dos dados recebidos via comunicação serial.
     * Realiza a leitura dos dados, processa e atualiza o estado do sensor.
     */
    public void atualizarSensor() {
        try {
            // Lê os dados da comunicação serial
            String dados = serialCommunication.readData();

            // Valida se os dados estão no formato esperado
            if (dados == null || dados.isEmpty()) {
                System.err.println("Erro: Nenhum dado recebido.");
                return;
            }

            // Divide os dados em partes
            String[] partes = dados.split("\t");
            if (partes.length < 3) {
                System.err.println("Erro: Dados incompletos.");
                return;
            }

            // Processa os dados
            String temperaturaStr = partes[0].replace("Temperatura: ", "").replace(" °C", "").replace(",", ".");
            String umidadeStr = partes[1].replace("Umidade: ", "").replace(" %", "").replace(",", ".");
            String umidadeSoloStr = partes[2].replace("Umidade Solo: ", "").replace(" %", "").replace(",", ".");

            // Converte os dados para float
            float temperatura = Float.parseFloat(temperaturaStr);
            float umidade = Float.parseFloat(umidadeStr);
            float umidadeSolo = Float.parseFloat(umidadeSoloStr);

            // Atualiza o estado do sensor
            sensor.setTemperatura(temperatura);
            sensor.setUmidade(umidade);
            sensor.setUmidadeSolo(umidadeSolo);

            // Insere os dados no banco de dados
            DatabaseConnection.insertLeitura(temperatura, umidade, umidadeSolo);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Erro: Formato de dados inesperado.");
        } catch (NumberFormatException e) {
            System.err.println("Erro: Dados recebidos não são números válidos.");
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Exibe os dados atuais do sensor no console.
     *
     * @return String formatada com os dados do sensor.
     */
    public String exibirDados() {
        return "Temperatura: " + sensor.getTemperatura() + " °C\n" +
               "Umidade: " + sensor.getUmidade() + " %\n" +
               "Umidade Solo: " + sensor.getUmidadeSolo() + " %";
    }

    /**
     * Verifica se há alertas com base nos dados atuais do sensor.
     *
     * @return Objeto Alerta contendo o tipo de alerta e o valor atual, ou null se não houver alertas.
     */
   public Alerta verificarAlertas() {
    if (sensor.getTemperatura() > TEMPERATURA_ALERTA) {
        return new Alerta("Temperatura", sensor.getTemperatura());
    } else if (sensor.getUmidade() < UMIDADE_ALERTA) {
        return new Alerta("Umidade", sensor.getUmidade());
    } else if (sensor.getUmidadeSolo() < UMIDADE_SOLO_ALERTA) {
        return new Alerta("Umidade Solo", sensor.getUmidadeSolo());
    } else {
        return null; // Nenhum alerta
    }
}

    /**
     * Retorna a temperatura atual do sensor.
     *
     * @return Temperatura atual.
     */
    public float getTemperatura() {
        return sensor.getTemperatura();
    }

    /**
     * Retorna a umidade atual do sensor.
     *
     * @return Umidade atual.
     */
    public float getUmidade() {
        return sensor.getUmidade();
    }

    /**
     * Retorna a umidade do solo atual do sensor.
     *
     * @return Umidade do solo atual.
     */
    public String getUmidadeSolo() {
        return sensor.getUmidadeSolo();
    }

    /**
     * Classe interna para representar alertas.
     */
    public static class Alerta {
        private final String tipo;
        private final float valor;

        public Alerta(String tipo, float valor) {
            this.tipo = tipo;
            this.valor = valor;
        }

        public String getTipo() {
            return tipo;
        }

        public float getValor() {
            return valor;
        }

        @Override
        public String toString() {
            return "Alerta: " + tipo + " está fora do limite. Valor atual: " + valor;
        }
    }
}
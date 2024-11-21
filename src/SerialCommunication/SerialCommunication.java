/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SerialCommunication;

/**
 *
 * @author amd
 */


import java.util.Random;

public class SerialCommunication {
    private Random random;

    // Construtor
    public SerialCommunication() {
        random = new Random();
    }

    public String readData() {
        float temperatura = (float) (18 + Math.random() * 10); 
        float umidade = (float) (40 + Math.random() * 40);   
        return String.format("Temperatura: %.2f °C\tUmidade: %.2f %%", temperatura, umidade);
    }
}

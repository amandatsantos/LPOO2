package SerialCommunication;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;

public class SerialCommunication {
    private SerialPort serialPort;
    private Scanner scanner;

    // Construtor: Abre a conexão com o Arduino
    public SerialCommunication() {
        SerialPort[] ports = SerialPort.getCommPorts();
        
        if (ports.length == 0) {
            System.out.println("Nenhuma porta serial encontrada.");
            return;
        }

        //  porta correta arduino conectado 
        serialPort = SerialPort.getCommPort("COM9"); 
        serialPort.setBaudRate(9600); //  baud rate do Arduino
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(1);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            System.out.println("Conexão estabelecida com: " + serialPort.getSystemPortName());
            scanner = new Scanner(serialPort.getInputStream());
        } else {
            System.out.println("Falha ao abrir a porta serial.");
        }
    }

    // Método para ler os dados enviados pelo Arduino
    public String readData() {
        if (scanner != null && scanner.hasNextLine()) {
            return scanner.nextLine();  // Captura a linha enviada pelo Arduino
        }
        return "Nenhum dado recebido";
    }

    // Método para fechar a conexão quando não for mais necessária
    public void closeConnection() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Conexão fechada.");
        }
    }
}

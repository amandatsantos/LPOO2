import DatabaseConnection.DatabaseConnection;
import com.fazecast.jSerialComm.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class ArduinoSerialReaderGUI {

    private static JLabel statusLabel;
    private static JButton exportButton;
    private static JTable leituraTable;
    private static DefaultTableModel tableModel;
    
    private static LinkedList<String[]> ultimasLeituras = new LinkedList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Monitoramento Arduino");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // tabela com as utlimas leituras
        String[] colunas = {"Data/Hora", "Temp (°C)", "Umi Ar (%)", "Umi Solo (%)"};
        tableModel = new DefaultTableModel(colunas, 0);
        leituraTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(leituraTable);

        statusLabel = new JLabel("Status: Normal");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        exportButton = new JButton("Exportar CSV");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarCSV();
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(exportButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        // Configurando a porta serial
        SerialPort[] ports = SerialPort.getCommPorts();
        SerialPort comPort = null;

        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals("COM9")) {
                comPort = port;
                break;
            }
        }

        if (comPort == null) {
            JOptionPane.showMessageDialog(frame, "Porta COM9 não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        comPort.setBaudRate(9600);
        if (!comPort.openPort()) {
            JOptionPane.showMessageDialog(frame, "Falha ao abrir a porta serial.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        byte[] buffer = new byte[1024];
        int bytesRead;
        bytesRead = comPort.readBytes(buffer, buffer.length);


        while (true) {
            bytesRead = comPort.readBytes(buffer, buffer.length);
            if (bytesRead > 0) {
                String data = new String(buffer, 0, bytesRead);
                processarDados(data);
    System.out.println("Dados recebidos: " + data); // Depuração
    processarDados(data);
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void processarDados(String data) {
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

        atualizarTabela();
        verificarAlertas(temperatura, umidadeAr, umidadeSolo);
    }

    private static void atualizarTabela() {
        tableModel.setRowCount(0); // Limpa a tabela
        for (String[] leitura : ultimasLeituras) {
            tableModel.addRow(leitura);
        }
    }

    private static void verificarAlertas(float temperatura, float umidadeAr, float umidadeSolo) {
        String alerta = "";
        
        if (temperatura > 100) alerta += "⚠️ Temperatura alta! ";
        if (temperatura < 10) alerta += "⚠️ Temperatura baixa! ";
        if (umidadeAr > 100) alerta += "⚠️ Umidade do ar muito alta! ";
        if (umidadeAr < 10) alerta += "⚠️ Umidade do ar muito baixa! ";
        if (umidadeSolo > 100) alerta += "⚠️ Umidade do solo muito alta! ";
        if (umidadeSolo < 10) alerta += "⚠️ Umidade do solo muito baixa! ";

        if (!alerta.isEmpty()) {
            statusLabel.setText(alerta);
            statusLabel.setForeground(Color.RED);
            Toolkit.getDefaultToolkit().beep(); // Alerta sonoro
        } else {
            statusLabel.setText("Status: Normal");
            statusLabel.setForeground(Color.BLACK);
        }
    }

    private static void exportarCSV() {
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

                JOptionPane.showMessageDialog(null, "Arquivo salvo em: " + fileToSave.getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

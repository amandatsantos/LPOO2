package MonitoramentoGUI;

import DatabaseConnection.DatabaseConnection;
import Sensor.Sensor;
import SerialCommunication.SerialCommunication;
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

public class MonitoramentoGUI {

    private JFrame frame;
    private JLabel lblStatus;
    private JTable leituraTable;
    private DefaultTableModel tableModel;
    private LinkedList<String[]> ultimasLeituras;
    private SerialCommunication minhaSerial;

    public MonitoramentoGUI() {
      //  Sensor ArdSensor = new Sensor("DHT11");
        minhaSerial = new SerialCommunication();
        ultimasLeituras = new LinkedList<>();
        criarInterface();
    }

    private void criarInterface() {
        frame = new JFrame("Monitoramento de Temperatura e Umidade");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

        // Tabela com as últimas leituras
        String[] colunas = {"Data/Hora", "Temp (°C)", "Umi Ar (%)", "Umi Solo (%)"};
        tableModel = new DefaultTableModel(colunas, 0);
        leituraTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(leituraTable);

        lblStatus = new JLabel("Status: Normal", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnExportarCSV = new JButton("Exportar CSV");
        btnExportarCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarCSV();
            }
        });

        painel.add(scrollPane, BorderLayout.CENTER);
        painel.add(lblStatus, BorderLayout.NORTH);
        painel.add(btnExportarCSV, BorderLayout.SOUTH);

        frame.add(painel);
        frame.setVisible(true);

        // Inicia a atualização dos dados
        Timer timer = new Timer(5000, e -> atualizarDadosMonitoramento());
        timer.start();
    }

    private void atualizarDadosMonitoramento() {
        String dados = minhaSerial.readData();

        if (!dados.equals("Nenhum dado recebido")) {
            System.out.println("? Dados recebidos: " + dados);

            String[] linhas = dados.split("\n");
            float temperatura = 0, umidadeAr = 0, umidadeSolo = 0;
            for (String linha : linhas) {
                linha = linha.trim();
                if (linha.startsWith("Temperatura:")) {
                    temperatura = Float.parseFloat(linha.replace("Temperatura:", "").replace("°C", "").trim());
                } else if (linha.startsWith("Umidade do ar:")) {
                    umidadeAr = Float.parseFloat(linha.replace("Umidade do ar:", "").replace("%", "").trim());
                } else if (linha.startsWith("Umidade do solo:")) {
                    umidadeSolo = Float.parseFloat(linha.replace("Umidade do solo:", "").replace("%", "").trim());
                }
            }

            // Adicionar nova leitura com timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (ultimasLeituras.size() >= 10) {
                ultimasLeituras.removeFirst();
            }
            ultimasLeituras.add(new String[]{timestamp, String.valueOf(temperatura), String.valueOf(umidadeAr), String.valueOf(umidadeSolo)});

            atualizarTabela();
            verificarAlertas(temperatura, umidadeAr, umidadeSolo);
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0); // Limpa a tabela
        for (String[] leitura : ultimasLeituras) {
            tableModel.addRow(leitura);
        }
    }

    private void verificarAlertas(float temperatura, float umidadeAr, float umidadeSolo) {
        String alerta = "";
        
        if (temperatura > 100) alerta += "️ Temperatura alta! ";
        if (temperatura < 10) alerta += "️ Temperatura baixa! ";
        if (umidadeAr > 100) alerta += "️ Umidade do ar muito alta! ";
        if (umidadeAr < 10) alerta += "️ Umidade do ar muito baixa! ";
        if (umidadeSolo > 100) alerta += "️ Umidade do solo muito alta! ";
        if (umidadeSolo < 10) alerta += "️ Umidade do solo muito baixa! ";

        if (!alerta.isEmpty()) {
            lblStatus.setText(alerta);
            lblStatus.setForeground(Color.RED);
            Toolkit.getDefaultToolkit().beep(); // Alerta sonoro
        } else {
            lblStatus.setText("Status: Normal");
            lblStatus.setForeground(Color.BLACK);
        }
    }

    private void exportarCSV() {
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

    public static void main(String[] args) {
        new MonitoramentoGUI();
    }
}

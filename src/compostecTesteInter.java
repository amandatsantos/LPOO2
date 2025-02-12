/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author amd
 */




        import com.fazecast.jSerialComm.SerialPort;
import DatabaseConnection.DatabaseConnection; // Certifique-se de que esse import está correto

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class compostecTesteInter extends JFrame {

    // Componentes da interface
    private JLabel lblTemperatura;
    private JLabel lblUmidade;
    private JLabel lblAlerta;
    private JTextArea textAreaAlertas;
    private JTextArea textAreaDados;
    private JButton btnEnviarAlerta;
    
    // Objeto que guarda os dados atuais dos sensores
    private Monitoramento monitoramento;
    // Lista local para armazenar as últimas 10 leituras (opcional, para exibição imediata)
    private final LinkedList<String> ultimasLeituras;
    
    public compostecTesteInter() {
        super("Monitoramento de Sensores");
        monitoramento = new Monitoramento();
        ultimasLeituras = new LinkedList<>();
        initComponents();
        iniciarLeituraSerial();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // Painel para os dados dos sensores
        JPanel panelStatus = new JPanel(new GridLayout(3, 1, 5, 5));
        lblTemperatura = new JLabel("Temperatura: -- °C");
        lblUmidade    = new JLabel("Umidade: -- %");
        lblAlerta     = new JLabel("Alerta: --");
        panelStatus.add(lblTemperatura);
        panelStatus.add(lblUmidade);
        panelStatus.add(lblAlerta);
        
        // Área para exibir os alertas
        textAreaAlertas = new JTextArea(3, 20);
        textAreaAlertas.setEditable(false);
        textAreaAlertas.setBorder(BorderFactory.createTitledBorder("Alertas"));
        
        // Área para exibir as últimas leituras (consultadas do banco de dados)
        textAreaDados = new JTextArea(10, 20);
        textAreaDados.setEditable(false);
        textAreaDados.setBorder(BorderFactory.createTitledBorder("Últimas Leituras"));
        
        // Botão para enviar alerta (opcional)
        btnEnviarAlerta = new JButton("Enviar Alerta");
        btnEnviarAlerta.addActionListener((ActionEvent e) -> enviarAlerta());
        
        JPanel panelBotoes = new JPanel();
        panelBotoes.add(btnEnviarAlerta);
        
        // Organizando os painéis na janela
        JPanel panelPrincipal = new JPanel(new BorderLayout(5, 5));
        panelPrincipal.add(panelStatus, BorderLayout.NORTH);
        panelPrincipal.add(new JScrollPane(textAreaAlertas), BorderLayout.CENTER);
        panelPrincipal.add(new JScrollPane(textAreaDados), BorderLayout.SOUTH);
        
        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);
        getContentPane().add(panelBotoes, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    // Inicia a thread de leitura da porta serial
    private void iniciarLeituraSerial() {
        new Thread(() -> {
            // Altere "COM3" para a porta correta do seu Arduino
            SerialPort serialPort = SerialPort.getCommPort("COM9");
            serialPort.setBaudRate(9600);
            
            if (serialPort.openPort()) {
                System.out.println("Porta serial aberta com sucesso!");
            } else {
                System.out.println("Falha ao abrir a porta serial.");
                return;
            }
            
            // Aguarda 2 segundos para o Arduino reiniciar (se necessário)
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            Scanner scanner = new Scanner(serialPort.getInputStream());
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                processarLinhaSerial(linha);
            }
            scanner.close();
            serialPort.closePort();
        }).start();
    }
    
    /**
     * Processa a linha recebida pela porta serial.
     * Espera-se o formato:
     * "Temperatura: 25°C, Umidade: 50%, Umidade do solo: 70%"
     */
    private void processarLinhaSerial(String linha) {
        try {
            String[] partes = linha.split(",");
            double temperatura = 0.0;
            double umidade = 0.0;
            int umidadeSolo = 0;
            
            for (String parte : partes) {
                parte = parte.trim();
                if (parte.startsWith("Temperatura:")) {
                    String tempStr = parte.replace("Temperatura:", "")
                                          .replace("°C", "")
                                          .trim();
                    temperatura = Double.parseDouble(tempStr);
                } else if (parte.startsWith("Umidade:") && !parte.contains("solo")) {
                    String umidadeStr = parte.replace("Umidade:", "")
                                             .replace("%", "")
                                             .trim();
                    umidade = Double.parseDouble(umidadeStr);
                } else if (parte.startsWith("Umidade do solo:")) {
                    String soloStr = parte.replace("Umidade do solo:", "")
                                          .replace("%", "")
                                          .trim();
                    umidadeSolo = Integer.parseInt(soloStr);
                }
            }
            
            // Atualiza os dados do monitoramento
            monitoramento.atualizarSensor(temperatura, umidade, umidadeSolo);
            atualizarDadosMonitoramento();
            
            // Insere os dados no banco convertendo para float
            DatabaseConnection.insertLeitura((float) temperatura, (float) umidade, (float) umidadeSolo);
            
            // Após inserir, atualiza a exibição das últimas 10 leituras
            atualizarLeituras();
            
        } catch (Exception e) {
            System.out.println("Erro ao processar a linha serial: " + linha);
            e.printStackTrace();
        }
    }
    
    /**
     * Atualiza os componentes da interface com os dados atuais.
     * Também mantém uma lista local (opcional) com as últimas leituras para exibição imediata.
     */
    private void atualizarDadosMonitoramento() {
        lblTemperatura.setText("Temperatura: " + monitoramento.getTemperatura() + " °C");
        lblUmidade.setText("Umidade: " + monitoramento.getUmidade() + " %");
        String alerta = monitoramento.verificarAlertas();
        lblAlerta.setText("Alerta: " + alerta);
        textAreaAlertas.setText(alerta);
        
        // Cria uma string representando a leitura atual
        String leitura = "Temperatura: " + monitoramento.getTemperatura() + " °C, " +
                         "Umidade: " + monitoramento.getUmidade() + " %, " +
                         "Umidade do solo: " + monitoramento.getUmidadeSolo() + "%";
        
        // Atualiza a lista local com as últimas 10 leituras
        synchronized (ultimasLeituras) {
            if (ultimasLeituras.size() >= 10) {
                ultimasLeituras.removeFirst();
            }
            ultimasLeituras.add(leitura);
        }
        // Atualiza a área de texto (opcional, se você quiser mostrar a lista local também)
        SwingUtilities.invokeLater(() -> {
            List<String> copiaLeituras;
            synchronized (ultimasLeituras) {
                copiaLeituras = new ArrayList<>(ultimasLeituras);
            }
            textAreaDados.setText("");
            for (String leituraAnterior : copiaLeituras) {
                textAreaDados.append(leituraAnterior + "\n");
            }
        });
    }
    
    /**
     * Consulta a base de dados para recuperar as últimas 10 leituras inseridas
     * e atualiza a área de exibição.
     *
     * Na sua classe DatabaseConnection, você pode ajustar a query SQL
     * para trazer somente as últimas 10 leituras (usando LIMIT 10 e ORDER BY).
     */
    private void atualizarLeituras() {
        List<String> leituras = DatabaseConnection.getLeituras();
        SwingUtilities.invokeLater(() -> {
            textAreaDados.setText("");
            for (String leitura : leituras) {
                textAreaDados.append(leitura + "\n");
            }
        });
    }
    
    private void enviarAlerta() {
        String alerta = monitoramento.verificarAlertas();
        JOptionPane.showMessageDialog(this, "Alerta Enviado: " + alerta);
        System.out.println("Alerta Enviado: " + alerta);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new compostecTesteInter());
    }
}


// Classe para gerenciar os dados dos sensores
class Monitoramento {
    private double temperatura;
    private double umidade;
    private int umidadeSolo;
    
    public void atualizarSensor(double temperatura, double umidade, int umidadeSolo) {
        this.temperatura = temperatura;
        this.umidade = umidade;
        this.umidadeSolo = umidadeSolo;
    }
    
    public double getTemperatura() {
        return temperatura;
    }
    
    public double getUmidade() {
        return umidade;
    }
    
    public int getUmidadeSolo() {
        return umidadeSolo;
    }
    
    /**
     * Verifica se os valores ultrapassam determinados limites e retorna uma mensagem de alerta.
     */
    public String verificarAlertas() {
        String alerta = "";
        if (temperatura > 35) {
            alerta += "Temperatura alta! ";
        }
        if (umidade < 30) {
            alerta += "Umidade do ar baixa! ";
        }
        if (umidadeSolo < 40) {
            alerta += "Umidade do solo baixa! ";
        }
        if (alerta.isEmpty()) {
            alerta = "Tudo normal.";
        }
        return alerta;
    }
}
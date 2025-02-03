/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MonitoramentoGUI;

/**
 *
 * @author amd
 */
import DatabaseConnection.DatabaseConnection;
import Monitoramento.Monitoramento;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;




public class MonitoramentoGUI {
    private JFrame frame;
    private JLabel lblTemperatura;
    private JLabel lblUmidade;
    private JLabel lblAlerta;
    private JTextArea textAreaDados;
    private JTextArea textAreaAlertas; // Novo espaço para exibir alertas
    private LinkedList<String> ultimasLeituras; // Lista para armazenar as últimas 10 leituras

    private Monitoramento monitoramento;

    public MonitoramentoGUI() {
        monitoramento = new Monitoramento("DHT11");
        ultimasLeituras = new LinkedList<>();
        criarInterface();
    }

    private void criarInterface() {
        frame = new JFrame("Monitoramento de Temperatura e Umidade");
        frame.setSize(600, 600);  // Aumentando o tamanho da janela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        JPanel painelSuperior = new JPanel(new GridLayout(3, 1));
        lblTemperatura = new JLabel("Temperatura: -- °C", SwingConstants.CENTER);
        lblUmidade = new JLabel("Umidade: -- %", SwingConstants.CENTER);
        lblAlerta = new JLabel("Status: Tudo normal", SwingConstants.CENTER);

        painelSuperior.add(lblTemperatura);
        painelSuperior.add(lblUmidade);
        painelSuperior.add(lblAlerta);
        frame.add(painelSuperior, BorderLayout.NORTH);


        textAreaDados = new JTextArea();
        textAreaDados.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textAreaDados);
        frame.add(scrollPane, BorderLayout.CENTER);


        JPanel painelAlertas = new JPanel(new BorderLayout());
        textAreaAlertas = new JTextArea();
        textAreaAlertas.setEditable(false);
        textAreaAlertas.setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollAlertas = new JScrollPane(textAreaAlertas);
        painelAlertas.add(scrollAlertas, BorderLayout.CENTER);
        painelAlertas.setBorder(BorderFactory.createTitledBorder("Alertas"));
        frame.add(painelAlertas, BorderLayout.SOUTH);


        JPanel painelInferior = new JPanel(new FlowLayout());


        JButton btnAtualizarDados = new JButton("Atualizar Dados");
        btnAtualizarDados.addActionListener(e -> atualizarLeituras());
        painelInferior.add(btnAtualizarDados);


        JButton btnEnviarAlerta = new JButton("Enviar Alerta");
        btnEnviarAlerta.addActionListener(e -> enviarAlerta());
        painelInferior.add(btnEnviarAlerta);

        frame.add(painelInferior, BorderLayout.SOUTH);

        frame.setVisible(true);

// aumentar tempo se necessario talvez 10s dps penso nisso
        Timer timer = new Timer(2000, e -> atualizarDadosMonitoramento());
        timer.start();
    }

    private void atualizarDadosMonitoramento() {
        monitoramento.atualizarSensor();

        lblTemperatura.setText("Temperatura: " + monitoramento.getTemperatura() + " °C");
        lblUmidade.setText("Umidade: " + monitoramento.getUmidade() + " %");


        String alerta = monitoramento.verificarAlertas();
        lblAlerta.setText(alerta);  // Atualiza o alerta geral
        textAreaAlertas.setText(alerta);  // Exibe os alertas na nova área de texto


        String leitura = "Temperatura: " + monitoramento.getTemperatura() + " °C, Umidade: " + monitoramento.getUmidade() + " %";
        if (ultimasLeituras.size() >= 10) {
            ultimasLeituras.removeFirst();
        }
        ultimasLeituras.add(leitura);


        textAreaDados.setText("");
        for (String leituraAnterior : ultimasLeituras) {
            textAreaDados.append(leituraAnterior + "\n");
        }
    }

    private void atualizarLeituras() {
        var leituras = DatabaseConnection.getLeituras();
        textAreaDados.setText("");
        for (String leitura : leituras) {
            textAreaDados.append(leitura + "\n");
        }
    }

    private void enviarAlerta() {
        String alerta = monitoramento.verificarAlertas();


        JOptionPane.showMessageDialog(frame, "Alerta Enviado: " + alerta);
        System.out.println("Alerta Enviado: " + alerta);
    }

    public static void main(String[] args) {
        new MonitoramentoGUI();
    }
}
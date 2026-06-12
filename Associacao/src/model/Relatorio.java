package model;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Relatorio {
    private Date periodoInicial;
    private Date periodoFinal;
    private double totalEntradas;
    private double totalSaidas;
    private double saldoFinal;

    public Relatorio() {}

    public void gerarMensal() {
        System.out.println("Relatório Mensal Processado.");
    }

    public void gerarAnual() {
        System.out.println("Relatório Anual Processado.");
    }

    /**
     * Gera um arquivo de relatório financeiro real em formato HTML/PDF
     */
    public void emitirPDF() {
        System.out.println("Exportando para PDF...");

        // Janela para o usuário escolher onde quer salvar o relatório
        JFileChooser salvarArquivo = new JFileChooser();
        salvarArquivo.setDialogTitle("Salvar Relatório Financeiro");
        salvarArquivo.setSelectedFile(new File("Relatorio_Financeiro.html")); // Extensão amigável para navegadores

        int resultado = salvarArquivo.showSaveDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = salvarArquivo.getSelectedFile();
            SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");

            // Construindo um layout bonito em HTML que abre em qualquer navegador e aceita impressão direta em PDF
            String conteudoHTML = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: 'Segoe UI', sans-serif; margin: 40px; color: #333; background-color: #F8F5F0; }"
                    + ".container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); border: 1px solid #E6E6E6; }"
                    + "h1 { color: #46280F; border-bottom: 2px solid #CD9137; padding-bottom: 10px; }"
                    + ".periodo { color: gray; margin-bottom: 30px; font-size: 14px; }"
                    + "table { width: 100%; border-collapse: collapse; margin-top: 20px; }"
                    + "th { background-color: #CD9137; color: white; padding: 12px; font-weight: bold; text-align: left; }"
                    + "td { padding: 12px; border-bottom: 1px solid #EBEBEB; }"
                    + ".linha-par { background-color: #F8F5F0; }"
                    + ".entrada { color: #5A9628; font-weight: bold; }"
                    + ".saida { color: red; font-weight: bold; }"
                    + ".saldo { color: blue; font-weight: bold; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "<h1>Resumo Financeiro</h1>"
                    + "<div class='periodo'><b>Período:</b> " + formatador.format(periodoInicial) + " até " + formatador.format(periodoFinal) + "</div>"
                    + "<table>"
                    + "  <tr><th>Descrição</th><th>Valor</th></tr>"
                    + "  <tr class='linha-par'><td>Total de Entradas</td><td class='entrada'>R$ " + String.format("%.2f", totalEntradas) + "</td></tr>"
                    + "  <tr><td>Total de Saídas</td><td class='saida'>R$ " + String.format("%.2f", totalSaidas) + "</td></tr>"
                    + "  <tr class='linha-par'><td><b>Saldo Final</b></td><td class='saldo'><b>R$ " + String.format("%.2f", saldoFinal) + "</b></td></tr>"
                    + "</table>"
                    + "<p style='margin-top:40px; font-size:11px; color:gray;'>Documento gerado automaticamente pelo sistema em: " + formatador.format(new Date()) + "</p>"
                    + "</div>"
                    + "<script>window.onload = function() { window.print(); }</script>" // Abre a caixa de diálogo de impressão/Salvar em PDF automaticamente
                    + "</body>"
                    + "</html>";

            try (FileWriter escritor = new FileWriter(arquivo)) {
                escritor.write(conteudoHTML);
                escritor.flush();

                // Abre o arquivo gerado automaticamente no navegador padrão do usuário
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(arquivo);
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao criar o arquivo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // GETTERS E SETTERS
    public Date getPeriodoInicial() { return periodoInicial; }
    public void setPeriodoInicial(Date periodoInicial) { this.periodoInicial = periodoInicial; }
    public Date getPeriodoFinal() { return periodoFinal; }
    public void setPeriodoFinal(Date periodoFinal) { this.periodoFinal = periodoFinal; }
    public double getTotalEntradas() { return totalEntradas; }
    public void setTotalEntradas(double totalEntradas) { this.totalEntradas = totalEntradas; }
    public double getTotalSaidas() { return totalSaidas; }
    public void setTotalSaidas(double totalSaidas) { this.totalSaidas = totalSaidas; }
    public double getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(double saldoFinal) { this.saldoFinal = saldoFinal; }
}
package util;

import model.Associado;
import model.Atividade;
import model.Financeiro;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class GeradorPdfRelatorio {

    private static final Color COR_PRIMARIA = new Color(80, 50, 20);
    private static final Color COR_SECUNDARIA = new Color(245, 166, 35);
    private static final Color COR_ZEBRA = new Color(250, 248, 245);
    private static final Color COR_TEXTO = new Color(51, 51, 51);

    public static void gerarRelatorioAtividades(List<Atividade> atividades) throws Exception {
        String home = System.getProperty("user.home");
        String caminhoArquivo = home + File.separator + "Downloads" + File.separator + "Relatorio_Atividades_Recentes.pdf";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
        document.open();

        adicionarCabecalho(document, "RELATÓRIO DE ATIVIDADES RECENTES",
                "Resumo unificado de novos associados e movimentações financeiras da comunidade");

        PdfPTable tabela = new PdfPTable(new float[]{22f, 20f, 58f});
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(20);

        adicionarCelulaCabecalho(tabela, "Data / Hora");
        adicionarCelulaCabecalho(tabela, "Categoria");
        adicionarCelulaCabecalho(tabela, "Descrição da Atividade");

        Font fontDados = FontFactory.getFont(FontFactory.HELVETICA, 10, COR_TEXTO);

        if (atividades == null || atividades.isEmpty()) {
            PdfPCell cell = new PdfPCell(new Phrase("Nenhuma atividade recente encontrada.", fontDados));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            tabela.addCell(cell);
        } else {
            boolean linhaPar = false;
            for (Atividade atividade : atividades) {
                String dataStr = atividade.getDataHora() != null ? sdf.format(atividade.getDataHora()) : "---";
                String catStr = atividade.getCategoria() != null ? atividade.getCategoria() : "Geral";
                String descStr = atividade.getDescricao() != null ? atividade.getDescricao() : "";

                adicionarCelulaDado(tabela, dataStr, linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, catStr, linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, descStr, linhaPar, Element.ALIGN_LEFT);

                linhaPar = !linhaPar;
            }
        }

        document.add(tabela);
        document.close();
        abrirArquivoOS(caminhoArquivo);
    }

    public static void gerarRelatorioAssociados(List<Associado> associados) throws Exception {
        String home = System.getProperty("user.home");
        String caminhoArquivo = home + File.separator + "Downloads" + File.separator + "Relatorio_Lista_Associados.pdf";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
        document.open();

        adicionarCabecalho(document, "RELAÇÃO GERAL DE ASSOCIADOS",
                "Listagem de membros ativos e informações detalhadas de endereço registradas no sistema");

        PdfPTable tabela = new PdfPTable(new float[]{15f, 25f, 15f, 13f, 32f});
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(20);

        adicionarCelulaCabecalho(tabela, "CPF");
        adicionarCelulaCabecalho(tabela, "Nome");
        adicionarCelulaCabecalho(tabela, "Perfil");
        adicionarCelulaCabecalho(tabela, "Adesão");
        adicionarCelulaCabecalho(tabela, "Endereço Completo");

        Font fontDados = FontFactory.getFont(FontFactory.HELVETICA, 10, COR_TEXTO);

        if (associados == null || associados.isEmpty()) {
            PdfPCell cell = new PdfPCell(new Phrase("Nenhum associado localizado na base de dados.", fontDados));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            tabela.addCell(cell);
        } else {
            boolean linhaPar = false;
            for (Associado a : associados) {
                String dataAdesao = a.getDataCadastro() != null ? sdf.format(a.getDataCadastro()) : "---";

                String enderecoTxt = "Não Informado";
                if (a.getEndereco() != null) {
                    model.Endereco end = a.getEndereco();
                    enderecoTxt = (end.getLogradouro() != null ? end.getLogradouro() : "") + " " +
                            (end.getCidade() != null ? end.getCidade() : "") + "-" +
                            (end.getEstado() != null ? end.getEstado() : "");
                }

                adicionarCelulaDado(tabela, a.getCpf(), linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, a.getNome(), linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, a.getTipoAssociado(), linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, dataAdesao, linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, enderecoTxt, linhaPar, Element.ALIGN_LEFT);

                linhaPar = !linhaPar;
            }
        }

        document.add(tabela);
        document.close();
        abrirArquivoOS(caminhoArquivo);
    }

    public static void gerarRelatorioFinanceiro(List<Financeiro> movimentacoes) throws Exception {
        String home = System.getProperty("user.home");
        String caminhoArquivo = home + File.separator + "Downloads" + File.separator + "Relatorio_Financeiro_Comunidade.pdf";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
        document.open();

        adicionarCabecalho(document, "DEMONSTRATIVO FINANCEIRO E FLUXO DE CAIXA",
                "Listagem consolidada de receitas, despesas e balanço geral da comunidade");

        PdfPTable tabela = new PdfPTable(new float[]{15f, 40f, 15f, 12f, 18f});
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(20);

        adicionarCelulaCabecalho(tabela, "Data");
        adicionarCelulaCabecalho(tabela, "Descrição");
        adicionarCelulaCabecalho(tabela, "Categoria");
        adicionarCelulaCabecalho(tabela, "Fluxo");
        adicionarCelulaCabecalho(tabela, "Valor (R$)");

        double acumuloEntradas = 0.0;
        double acumuloSaidas = 0.0;
        Font fontDados = FontFactory.getFont(FontFactory.HELVETICA, 10, COR_TEXTO);

        if (movimentacoes == null || movimentacoes.isEmpty()) {
            PdfPCell cell = new PdfPCell(new Phrase("Nenhuma movimentação financeira encontrada.", fontDados));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            tabela.addCell(cell);
        } else {
            boolean linhaPar = false;
            for (Financeiro f : movimentacoes) {
                String dataFormatada = f.getData() != null ? sdf.format(f.getData()) : "---";
                String tipo = f.getTipo() != null ? f.getTipo() : "Entrada";
                double valor = f.getValor();
                String descricao = f.getDesc() != null ? f.getDesc() : "Sem descrição";
                String categoria = f.getCat() != null ? f.getCat() : "Geral";

                String sinal = "+ ";
                if (tipo.equalsIgnoreCase("Saída") || tipo.equalsIgnoreCase("Saida")) {
                    sinal = "- ";
                    acumuloSaidas += valor;
                } else {
                    acumuloEntradas += valor;
                }

                adicionarCelulaDado(tabela, dataFormatada, linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, descricao, linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, categoria, linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, tipo, linhaPar, Element.ALIGN_LEFT);
                adicionarCelulaDado(tabela, sinal + String.format("%.2f", valor), linhaPar, Element.ALIGN_RIGHT);

                linhaPar = !linhaPar;
            }
        }

        document.add(tabela);

        double saldoLiquido = acumuloEntradas - acumuloSaidas;
        Font fontResumoLabel = FontFactory.getFont(FontFactory.HELVETICA, 10, COR_TEXTO);
        Font fontResumoValor = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13,
                (saldoLiquido >= 0 ? new Color(46, 125, 50) : new Color(198, 40, 40)));

        Paragraph resumoBox = new Paragraph();
        resumoBox.setSpacingBefore(15);
        resumoBox.setAlignment(Element.ALIGN_RIGHT);
        resumoBox.add(new Chunk("Total Entradas: R$ " + String.format("%.2f", acumuloEntradas) + "  |  Total Saídas: R$ " + String.format("%.2f", acumuloSaidas) + "\n", fontResumoLabel));
        resumoBox.add(new Chunk("Saldo Líquido do Período: R$ " + String.format("%.2f", saldoLiquido), fontResumoValor));

        document.add(resumoBox);
        document.close();
        abrirArquivoOS(caminhoArquivo);
    }

    private static void adicionarCabecalho(Document doc, String titulo, String sub) throws DocumentException {
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COR_PRIMARIA);
        Paragraph pTitulo = new Paragraph(titulo, fontTitulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(pTitulo);

        Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(120, 120, 120));
        Paragraph pSub = new Paragraph(sub, fontSub);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(8);
        doc.add(pSub);

        PdfPTable linha = new PdfPTable(1);
        linha.setWidthPercentage(100);
        PdfPCell cellLinha = new PdfPCell();
        cellLinha.setBorder(Rectangle.BOTTOM);
        cellLinha.setBorderWidthBottom(2f);
        cellLinha.setBorderColor(COR_SECUNDARIA);
        cellLinha.setPadding(0);
        linha.addCell(cellLinha);
        doc.add(linha);
    }

    private static void adicionarCelulaCabecalho(PdfPTable table, String texto) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(COR_PRIMARIA);
        cell.setPadding(7);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private static void adicionarCelulaDado(PdfPTable table, String texto, boolean zebra, int alinhamento) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 9, COR_TEXTO);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(zebra ? COR_ZEBRA : Color.WHITE);
        cell.setPadding(7);
        cell.setHorizontalAlignment(alinhamento);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColorBottom(new Color(230, 220, 205));
        table.addCell(cell);
    }

    private static void abrirArquivoOS(String caminho) {
        try {
            File file = new File(caminho);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            System.err.println("Não foi possível abrir o PDF automaticamente: " + e.getMessage());
        }
    }
}
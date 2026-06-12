package view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollBarCustomUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {

        thumbColor = new Color(185,120,30); // dourado do sistema
        trackColor = new Color(240,235,228); // fundo claro
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return criarBotaoVazio();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return criarBotaoVazio();
    }

    private JButton criarBotaoVazio() {
        JButton botao = new JButton();
        botao.setPreferredSize(new Dimension(0,0));
        return botao;
    }

    @Override
    protected void paintThumb(
            Graphics g,
            JComponent c,
            Rectangle thumbBounds
    ) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(
                new Color(185,120,30)
        );

        g2.fillRoundRect(
                thumbBounds.x + 2,
                thumbBounds.y + 2,
                thumbBounds.width - 4,
                thumbBounds.height - 4,
                12,
                12
        );

        g2.dispose();
    }

    @Override
    protected void paintTrack(
            Graphics g,
            JComponent c,
            Rectangle trackBounds
    ) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setColor(
                new Color(240,235,228)
        );

        g2.fillRect(
                trackBounds.x,
                trackBounds.y,
                trackBounds.width,
                trackBounds.height
        );

        g2.dispose();
    }
}
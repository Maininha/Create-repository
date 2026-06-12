package view;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class BordaSombra extends CompoundBorder {

    public BordaSombra() {
        super(
                // Sombra (externa)
                new AbstractBorder() {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int shadowSize = 8; // Tamanho total da sombra
                        int shadowOpacity = 30; // Opacidade inicial (0-255)

                        // Criando a difusão
                        for (int i = 0; i < shadowSize; i++) {
                            // Cor preta com opacidade que diminui a cada passo
                            Color shadowColor = new Color(0, 0, 0, shadowOpacity / shadowSize * (shadowSize - i));
                            g2.setColor(shadowColor);
                            // Desenha retângulos concêntricos com cantos ligeiramente arredondados
                            g2.drawRoundRect(x + i, y + i, width - 2*i - 1, height - 2*i - 1, 15, 15);
                        }
                        g2.dispose();
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        // Reserva espaço para a sombra
                        return new Insets(8, 8, 8, 8);
                    }
                },
                // Borda interna opcional (se quiser manter a borda sutil)
                null
        );
    }
}
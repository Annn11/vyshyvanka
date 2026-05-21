package ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int cornerRadius;
    private final Color backgroundColor;

    public RoundedPanel(int radius, Color bgColor) {
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Малювання м'якої тіні
        g2.setColor(new Color(0, 0, 0, 12));
        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);

        // Малювання основної картки
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
        g2.dispose();
    }
}


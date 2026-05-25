package ui;

import javax.swing.*;
import java.awt.*;

public class PatternPreviewPanel extends JPanel {
    private final Color[][] pattern;

    public PatternPreviewPanel(Color[][] pattern) {
        this.pattern = pattern;
        setOpaque(false);
        setPreferredSize(new Dimension(50, 50));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (pattern == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rows = pattern.length;
        int cols = pattern[0].length;

        // Вираховуємо розмір однієї клітинки вишивки всередині 50x50 пікселів
        int cellSize = Math.min(getWidth() / cols, getHeight() / rows);

        // Центруємо малюнок
        int offsetX = (getWidth() - (cols * cellSize)) / 2;
        int offsetY = (getHeight() - (rows * cellSize)) / 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pattern[r][c] != null) {
                    g2.setColor(pattern[r][c]);
                    // Малюємо заповнений квадратик (хрестик)
                    g2.fillRect(offsetX + c * cellSize + 1, offsetY + r * cellSize + 1, cellSize - 1, cellSize - 1);
                } else {
                    // Малюємо світлу клітинку канви, якщо нитки немає
                    g2.setColor(new Color(235, 230, 220));
                    g2.drawRect(offsetX + c * cellSize, offsetY + r * cellSize, cellSize, cellSize);
                }
            }
        }
        g2.dispose();
    }
}


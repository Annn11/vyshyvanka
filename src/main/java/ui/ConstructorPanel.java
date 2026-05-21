package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConstructorPanel extends JPanel {
    private final int GRID_SIZE = 24; // Розмір сітки 24x24 клітинки
    private final Color[][] gridData = new Color[GRID_SIZE][GRID_SIZE];
    private Color selectedColor = new Color(211, 47, 47); // Активний колір (червоний за замовчуванням)

    public ConstructorPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(30, 0));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // --- ЛІВА ЧАСТИНА: КАНВА ДЛЯ МАЛЮВАННЯ ---
        JPanel canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight());
                int cellSize = size / GRID_SIZE;
                int offsetX = (getWidth() - (GRID_SIZE * cellSize)) / 2;
                int offsetY = (getHeight() - (GRID_SIZE * cellSize)) / 2;

                // Малюємо закруглене тло для самої канви
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(offsetX, offsetY, GRID_SIZE * cellSize, GRID_SIZE * cellSize, 20, 20);

                // Малюємо сітку та зафарбовані клітинки
                for (int r = 0; r < GRID_SIZE; r++) {
                    for (int c = 0; c < GRID_SIZE; c++) {
                        int cellX = offsetX + c * cellSize;
                        int cellY = offsetY + r * cellSize;

                        if (gridData[r][c] != null) {
                            g2.setColor(gridData[r][c]);
                            // Імітація стібка хрестиком або заповненого квадратика
                            g2.fillRect(cellX + 1, cellY + 1, cellSize - 1, cellSize - 1);
                        } else {
                            g2.setColor(new Color(235, 230, 220));
                            g2.drawRect(cellX, cellY, cellSize, cellSize);
                        }
                    }
                }
                g2.dispose();
            }
        };
        canvasPanel.setOpaque(false);
        canvasPanel.setPreferredSize(new Dimension(550, 550));

        // Обробка кліків мишки по канві
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int size = Math.min(canvasPanel.getWidth(), canvasPanel.getHeight());
                int cellSize = size / GRID_SIZE;
                int offsetX = (canvasPanel.getWidth() - (GRID_SIZE * cellSize)) / 2;
                int offsetY = (canvasPanel.getHeight() - (GRID_SIZE * cellSize)) / 2;

                int c = (e.getX() - offsetX) / cellSize;
                int r = (e.getY() - offsetY) / cellSize;

                if (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                    // Якщо клікнули правою кнопкою — стираємо, лівою — малюємо
                    if (SwingUtilities.isRightMouseButton(e) || gridData[r][c] == selectedColor) {
                        gridData[r][c] = null;
                    } else {
                        gridData[r][c] = selectedColor;
                    }
                    canvasPanel.repaint();
                }
            }
        });

        // Для підтримки малювання затиснутою мишкою
        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int size = Math.min(canvasPanel.getWidth(), canvasPanel.getHeight());
                int cellSize = size / GRID_SIZE;
                int offsetX = (canvasPanel.getWidth() - (GRID_SIZE * cellSize)) / 2;
                int offsetY = (canvasPanel.getHeight() - (GRID_SIZE * cellSize)) / 2;

                int c = (e.getX() - offsetX) / cellSize;
                int r = (e.getY() - offsetY) / cellSize;

                if (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        gridData[r][c] = selectedColor;
                        canvasPanel.repaint();
                    }
                }
            }
        });

        add(canvasPanel, BorderLayout.CENTER);

        // --- ПРАВА ЧАСТИНА: ІНСТРУМЕНТИ ТА ПАЛІТРА ---
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(320, 0));

        JLabel titleLabel = new JLabel("Інструменти конструктора");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(40, 40, 40));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Палітра кольорів
        JLabel paletteLabel = new JLabel("Вибір кольору нитки:");
        paletteLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        paletteLabel.setForeground(new Color(80, 80, 80));
        paletteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(paletteLabel);
        rightPanel.add(Box.createVerticalStrut(10));

        JPanel colorsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        colorsPanel.setOpaque(false);
        colorsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        colorsPanel.add(createColorIndicator(new Color(211, 47, 47), "Червоний"));
        colorsPanel.add(createColorIndicator(new Color(40, 40, 40), "Чорний"));
        colorsPanel.add(createColorIndicator(new Color(140, 110, 80), "Бежевий"));
        colorsPanel.add(createColorIndicator(new Color(30, 120, 60), "Зелений"));

        rightPanel.add(colorsPanel);
        rightPanel.add(Box.createVerticalStrut(35));

        // Функціональні кнопки
        rightPanel.add(createActionButton("Очистити полотно", () -> {
            for (int r = 0; r < GRID_SIZE; r++) {
                for (int c = 0; c < GRID_SIZE; c++) gridData[r][c] = null;
            }
            canvasPanel.repaint();
        }, false));

        rightPanel.add(Box.createVerticalStrut(12));

        rightPanel.add(createActionButton("Зберегти орнамент", () -> {
            JOptionPane.showMessageDialog(this, "Орнамент успішно збережено до ваших проектів!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
        }, true));

        add(rightPanel, BorderLayout.EAST);
    }

    private JPanel createColorIndicator(Color color, String tooltip) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);

                // Якщо колір вибрано, малюємо білу цятку всередині
                if (selectedColor.equals(color)) {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(getWidth()/2 - 4, getHeight()/2 - 4, 8, 8);
                }
                g2.dispose();
            }
        };
        p.setPreferredSize(new Dimension(40, 40));
        p.setToolTipText(tooltip);
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedColor = color;
                p.getParent().repaint();
            }
        });
        return p;
    }

    private JButton createActionButton(String text, Runnable action, boolean isPrimary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isPrimary ? new Color(211, 47, 47) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (!isPrimary) {
                    g2.setColor(new Color(220, 215, 205));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(isPrimary ? Color.WHITE : new Color(60, 60, 60));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(280, 42));
        btn.setPreferredSize(new Dimension(280, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }
}

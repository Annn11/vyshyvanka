package ui;

import javax.swing.*;
import java.awt.*;

public class HistorySidebar extends JPanel {
    private final JPanel contentTargetPanel;
    private final CardLayout cardLayout;

    // Конструктор приймає контейнер сторінок CardLayout та унікальний текстовий ключ поточної активної вкладки
    public HistorySidebar(JPanel contentTargetPanel, CardLayout cardLayout, String activeKey) {
        this.contentTargetPanel = contentTargetPanel;
        this.cardLayout = cardLayout;

        setPreferredSize(new Dimension(240, 0));
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Тонка лінія роздільника праворуч
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 225, 218)));

        // Логотип програми
        JLabel logoLabel = new JLabel("Вишивай легко");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        logoLabel.setForeground(new Color(211, 47, 47));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));
        add(logoLabel);

        // Генерація елементів бокового меню
        add(createMenuButton("Головна", "MAIN_PAGE", activeKey.equals("MAIN_PAGE")));
        add(createMenuButton("Конструктор", "CONSTRUCTOR", activeKey.equals("CONSTRUCTOR")));
        add(createMenuButton("Імена", "IDEAS", activeKey.equals("IDEAS")));
        add(createMenuButton("Історія вишиванки", "HISTORY_PAGE", activeKey.equals("HISTORY_PAGE")));
        add(createMenuButton("Збережені проєкти", "SAVED", activeKey.equals("SAVED")));
    }

    private JButton createMenuButton(String text, String pageKey, boolean isActive) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Якщо кнопка активна — малюємо ніжне червонувате тло із закругленням навколо тексту
                if (isActive) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(253, 241, 241));
                    g2.fillRoundRect(10, 2, getWidth() - 20, getHeight() - 4, 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SansSerif", isActive ? Font.BOLD : Font.PLAIN, 14));
        button.setForeground(isActive ? new Color(211, 47, 47) : new Color(60, 60, 60));
        button.setMaximumSize(new Dimension(240, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Перемикач сторінок в реальному часі
        button.addActionListener(e -> {
            if (pageKey.equals("MAIN_PAGE") || pageKey.equals("HISTORY_PAGE") || pageKey.equals("CONSTRUCTOR") || pageKey.equals("IDEAS") || pageKey.equals("SAVED")) {
                cardLayout.show(contentTargetPanel, pageKey);

                // Динамічно оновлюємо стан бічної панелі, щоб переключити активний фокус підсвічування
                JPanel parent = (JPanel) getParent();
                if (parent != null) {
                    parent.remove(this);
                    parent.add(new HistorySidebar(contentTargetPanel, cardLayout, pageKey), BorderLayout.WEST);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        });

        return button;
    }
}

package ui;

import javax.swing.*;
import java.awt.*;

public class HistoryPage extends JPanel {

    public HistoryPage(JPanel pagesContainer, CardLayout cardLayout) {
        setBackground(new Color(250, 248, 245));
        setLayout(new BorderLayout());

        // ==========================================
        // 1. ЛІВА ПАНЕЛЬ (Не чіпаємо, 160px)
        // ==========================================
        JPanel leftMenu = new JPanel();
        leftMenu.setBackground(new Color(250, 248, 245));
        leftMenu.setPreferredSize(new Dimension(160, 0));
        leftMenu.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        leftMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 225, 218)));

        leftMenu.add(Box.createVerticalStrut(20));
        leftMenu.add(createMenuBtn("🏠  Огляд", true));
        leftMenu.add(createMenuBtn("📜  Зародження", false));
        leftMenu.add(createMenuBtn("📍  Різні епохи", false));
        leftMenu.add(createMenuBtn("🗺  Регіони", false));
        leftMenu.add(createMenuBtn("⚙  Відмінності", false));
        leftMenu.add(createMenuBtn("✨  Символіка", false));
        leftMenu.add(createMenuBtn("🖼  Галерея", false));

        add(leftMenu, BorderLayout.WEST);

        // ==========================================
        // КОНТЕЙНЕР ДЛЯ ЦЕНТРУ ТА САЙДБАРУ
        // ==========================================
        JPanel mainArea = new JPanel(new GridBagLayout());
        mainArea.setOpaque(false);

        // --- А. ЦЕНТРАЛЬНА КОЛОНКА (620px) ---
        JPanel centerContent = new JPanel(new GridBagLayout());
        centerContent.setOpaque(false);
        centerContent.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 15));

        GridBagConstraints gbcC = new GridBagConstraints();
        gbcC.gridx = 0;
        gbcC.weightx = 1.0;
        gbcC.fill = GridBagConstraints.HORIZONTAL;
        gbcC.anchor = GridBagConstraints.NORTHWEST; // ЗАЛІЗОБЕТОННО Притискаємо все вліво і вгору

        gbcC.gridy = 0;
        gbcC.insets = new Insets(0, 0, 30, 0);
        centerContent.add(new HistoryHeroPanel(), gbcC);

        gbcC.gridy = 1;
        gbcC.insets = new Insets(0, 0, 15, 0);
        // Заголовок "Хронологія" тепер автоматично прилипне вліво
        centerContent.add(createTitle("Хронологія розвитку вишивки"), gbcC);

        gbcC.gridy = 2;
        gbcC.insets = new Insets(0, 0, 30, 0);
        centerContent.add(new TimelinePanel(), gbcC);

        gbcC.gridy = 3;
        gbcC.insets = new Insets(0, 0, 15, 0);
        // Заголовок "Регіони" прилипне вліво
        centerContent.add(createTitle("Регіональні особливості та вишиванки"), gbcC);

        gbcC.gridy = 4;
        gbcC.insets = new Insets(0, 0, 0, 0);
        centerContent.add(new RegionCardsPanel(), gbcC);

        gbcC.gridy = 5;
        gbcC.weighty = 1.0;
        gbcC.fill = GridBagConstraints.BOTH;
        centerContent.add(Box.createGlue(), gbcC);

        JScrollPane centerScroll = new JScrollPane(centerContent);
        centerScroll.setBorder(BorderFactory.createEmptyBorder());
        centerScroll.setOpaque(false);
        centerScroll.getViewport().setOpaque(false);
        centerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerScroll.getVerticalScrollBar().setUnitIncrement(16);
        centerScroll.setPreferredSize(new Dimension(620, 600));

        // --- Б. ПРАВИЙ САЙДБАР (340px) ---
        JPanel rightSidebar = new JPanel();
        rightSidebar.setLayout(new BoxLayout(rightSidebar, BoxLayout.Y_AXIS));
        rightSidebar.setOpaque(false);
        rightSidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 20));

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.setPreferredSize(new Dimension(340, 600));
        rightWrapper.add(rightSidebar, BorderLayout.NORTH);

        // Заголовок "Цікаві факти" - тепер рівно по лівому краю
        rightSidebar.add(createTitle("Цікаві факти"));
        rightSidebar.add(Box.createVerticalStrut(10));
        FactsPanel facts = new FactsPanel();
        facts.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко притискаємо блок вліво
        rightSidebar.add(facts);
        rightSidebar.add(Box.createVerticalStrut(25));

        // Заголовок "Символіка" - рівно по лівому краю
        rightSidebar.add(createTitle("Символіка орнаментів"));
        rightSidebar.add(Box.createVerticalStrut(10));
        SymbolInfoPanel symbols = new SymbolInfoPanel();
        symbols.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко притискаємо блок вліво
        rightSidebar.add(symbols);
        rightSidebar.add(Box.createVerticalStrut(25));

        // Заголовок "Попередній перегляд" - рівно по лівому краю
        rightSidebar.add(createTitle("Попередній перегляд"));
        rightSidebar.add(Box.createVerticalStrut(10));
        Color[][] grid = new Color[32][32];
        for (int i=0; i<32; i++) for (int j=0; j<32; j++) grid[i][j] = Color.WHITE;
        PatternPreviewPanel preview = new PatternPreviewPanel(grid);
        preview.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко притискаємо блок вліво
        rightSidebar.add(preview);

        // --- В. ЗБИРАЄМО ЇХ РАЗОМ ---
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridy = 0;
        gbcMain.fill = GridBagConstraints.VERTICAL;
        gbcMain.anchor = GridBagConstraints.WEST;

        gbcMain.gridx = 0;
        gbcMain.weightx = 0.0;
        mainArea.add(centerScroll, gbcMain);

        gbcMain.gridx = 1;
        gbcMain.weightx = 0.0;
        mainArea.add(rightWrapper, gbcMain);

        gbcMain.gridx = 2;
        gbcMain.weightx = 1.0;
        gbcMain.fill = GridBagConstraints.BOTH;
        mainArea.add(Box.createGlue(), gbcMain);

        add(mainArea, BorderLayout.CENTER);
    }

    /**
     * Уніфікований метод створення заголовків зі строгим лівим вирівнюванням
     */
    private JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(new Color(40, 40, 40));
        label.setHorizontalAlignment(SwingConstants.LEFT); // Текст всередині лейбла - ВЛІВО
        label.setAlignmentX(Component.LEFT_ALIGNMENT); // Весь лейбл всередині BoxLayout - ВЛІВО
        return label;
    }

    private JButton createMenuBtn(String text, boolean isActive) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (isActive) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(253, 241, 241));
                    g2.fillRoundRect(5, 2, getWidth() - 10, getHeight() - 4, 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", isActive ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(isActive ? new Color(211, 47, 47) : new Color(80, 80, 80));
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

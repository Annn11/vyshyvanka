package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    public MainMenuPanel() {
        // Загальний фон сторінки
        setBackground(new Color(253, 251, 248));
        setLayout(new BorderLayout());

        // 1. ВЕРХНЄ МЕНЮ (Top Navigation)
        add(createTopNavigation(), BorderLayout.NORTH);

        // Головний контейнер для контенту (з відступами по краях)
        JPanel contentArea = new JPanel(new BorderLayout(25, 0));
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(25, 40, 25, 40));

        // ==========================================
        // 2. ЛІВА ЧАСТИНА (Банер + 4 Картки)
        // ==========================================
        JPanel leftColumn = new JPanel(new GridBagLayout());
        leftColumn.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Герой-банер
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0); // Відступ знизу
        leftColumn.add(createHeroBanner(), gbc);

        // 4 Квадратні картки
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        leftColumn.add(createFourCardsRow(), gbc);

        // Пружина, що штовхає контент догори
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        leftColumn.add(Box.createGlue(), gbc);

        contentArea.add(leftColumn, BorderLayout.CENTER);

        // ==========================================
        // 3. ПРАВА ЧАСТИНА (Останні проєкти + Натхнення)
        // ==========================================
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setOpaque(false);
        rightColumn.setPreferredSize(new Dimension(320, 0)); // Жорстка ширина сайдбара

        rightColumn.add(createSectionHeader("Останні проєкти", "Показати всі"));
        rightColumn.add(Box.createVerticalStrut(15));
        rightColumn.add(createRecentProjects());
        rightColumn.add(Box.createVerticalStrut(30));

        rightColumn.add(createSectionHeader("Натхнення для тебе", "Показати всі"));
        rightColumn.add(Box.createVerticalStrut(15));
        rightColumn.add(createInspirationGrid());

        contentArea.add(rightColumn, BorderLayout.EAST);

        // ==========================================
        // 4. НИЖНЯ ПАНЕЛЬ (Переваги програми)
        // ==========================================
        JPanel bottomFeatures = new JPanel(new GridLayout(1, 5, 15, 0));
        bottomFeatures.setOpaque(false);
        bottomFeatures.setBorder(new EmptyBorder(20, 40, 20, 40));

        bottomFeatures.add(createFeatureItem("🎨", "Палітра кольорів", "Обирай та комбінуй улюблені кольори"));
        bottomFeatures.add(createFeatureItem("⧉", "Зручні інструменти", "Малюй, редагуй та створюй легко"));
        bottomFeatures.add(createFeatureItem("👕", "Попередній перегляд", "Дивись, як виглядатиме твоя вишиванка"));
        bottomFeatures.add(createFeatureItem("📥", "Експорт схем", "Зберігай у PNG, PDF та інших форматах"));
        bottomFeatures.add(createFeatureItem("🔗", "Поділись проєктом", "Ділись своїм дизайном з друзями"));

        // Обгортаємо контент у скрол
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contentArea, BorderLayout.CENTER);
        wrapper.add(bottomFeatures, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Створення верхньої навігації (Top Menu)
     */
    private JPanel createTopNavigation() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        nav.setPreferredSize(new Dimension(0, 70));

        // Логотип та назва
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        logoPanel.setOpaque(false);
        JLabel logo = new JLabel("<html><span style='font-size: 20px; color: #C1272D;'>❖</span> <span style='font-size: 16px; font-weight: bold;'>Вишивай легко</span><br><span style='font-size: 10px; color: #888888;'>створюй свою вишиванку</span></html>");
        logoPanel.add(logo);
        nav.add(logoPanel, BorderLayout.WEST);

        // Центральні вкладки
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        tabsPanel.setOpaque(false);

        // Активна вкладка "Головна"
        JLabel tabMain = new JLabel("Головна");
        tabMain.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabMain.setForeground(new Color(211, 47, 47));
        tabMain.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(250, 230, 230), 1, true),
                new EmptyBorder(5, 15, 5, 15)
        ));

        tabsPanel.add(tabMain);
        tabsPanel.add(createNavTab("Конструктор"));
        tabsPanel.add(createNavTab("Орнаменти"));
        tabsPanel.add(createNavTab("Ідеї"));
        tabsPanel.add(createNavTab("Історія"));
        tabsPanel.add(createNavTab("Моя колекція"));
        nav.add(tabsPanel, BorderLayout.CENTER);

        // Праві іконки
        JPanel iconsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        iconsPanel.setOpaque(false);
        String[] icons = {"🔍", "↶", "↷", "⚙", "❓", "OK"};
        for (String icon : icons) {
            JLabel iconLbl = new JLabel(icon);
            iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
            iconLbl.setForeground(new Color(100, 100, 100));
            iconLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            iconsPanel.add(iconLbl);
        }
        nav.add(iconsPanel, BorderLayout.EAST);

        return nav;
    }

    private JLabel createNavTab(String text) {
        JLabel tab = new JLabel(text);
        tab.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tab.setForeground(new Color(60, 60, 60));
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return tab;
    }

    /**
     * Головний Hero-банер (Створи свою унікальну вишиванку)
     */
    private JPanel createHeroBanner() {
        RoundedPanel banner = new RoundedPanel(20, new Color(248, 244, 238)); // Теплий бежевий фон
        banner.setLayout(new BorderLayout());
        banner.setBorder(new EmptyBorder(40, 50, 40, 50));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("<html><span style='font-family: Georgia, serif; font-size: 34px; color: #222222;'>Створи свою<br><font color='#C1272D'>унікальну вишиванку</font></span></html>");
        JLabel desc = new JLabel("<html><span style='font-family: SansSerif; font-size: 13px; color: #555555;'>Малюй орнаменти, обирай кольори, надихайся<br>традиціями та створюй вишиванку, яка відображає<br>твою історію та стиль.</span></html>");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton startBtn = createSolidButton("Почати створення  →", new Color(193, 39, 45), Color.WHITE);
        JButton infoBtn = createOutlineButton("📖 Дізнатися більше про вишиванку");

        buttons.add(startBtn);
        buttons.add(infoBtn);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(15));
        textPanel.add(desc);
        textPanel.add(Box.createVerticalStrut(30));
        textPanel.add(buttons);

        // Заглушка для фото сорочки справа
        JLabel shirtImage = new JLabel("<html><div style='text-align: center; color: #AAAAAA; font-size: 14px;'>[Тут буде фото<br>білої сорочки]</div></html>", SwingConstants.CENTER);
        shirtImage.setPreferredSize(new Dimension(300, 0));

        banner.add(textPanel, BorderLayout.WEST);
        banner.add(shirtImage, BorderLayout.EAST);

        return banner;
    }

    /**
     * Створення рядка з 4 карток
     */
    private JPanel createFourCardsRow() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 20, 0));
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(0, 220));

        grid.add(createSquareCard("📝", "Конструктор", "Створи вишиванку своєї мрії крок за кроком у зручному редакторі.", "Відкрити конструктор →"));
        grid.add(createSquareCard("❖", "Орнаменти", "Велика бібліотека орнаментів на будь-який смак та регіон.", "Перейти до орнаментів →"));
        grid.add(createSquareCard("💡", "Ідеї", "Готові дизайни, сучасні рішення та приклади для натхнення.", "Переглянути ідеї →"));
        grid.add(createSquareCard("📖", "Історія", "Дізнайся більше про традиції, символіку та історію.", "Відкрити історію →"));

        return grid;
    }

    private JPanel createSquareCard(String iconStr, String title, String desc, String linkText) {
        RoundedPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel icon = new JLabel(iconStr);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 32));
        icon.setForeground(new Color(211, 47, 47));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLbl.setForeground(new Color(40, 40, 40));
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea(desc);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descArea.setForeground(new Color(100, 100, 100));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Вирівнювання тексту по центру в JTextArea
        descArea.setMargin(new Insets(10, 0, 10, 0));

        JLabel link = new JLabel(linkText);
        link.setFont(new Font("SansSerif", Font.BOLD, 12));
        link.setForeground(new Color(211, 47, 47));
        link.setAlignmentX(Component.CENTER_ALIGNMENT);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.add(Box.createVerticalGlue());
        card.add(icon);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLbl);
        card.add(descArea);
        card.add(link);
        card.add(Box.createVerticalGlue());

        return card;
    }

    /**
     * Створення списку останніх проєктів (Правий сайдбар)
     */
    private JPanel createRecentProjects() {
        RoundedPanel panel = new RoundedPanel(15, Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        panel.add(createProjectListItem("❖", "Червоний орнамент", "Змінено 2 год тому"));
        panel.add(createDivider());
        panel.add(createProjectListItem("⌘", "Симетричний візерунок", "Змінено вчора"));
        panel.add(createDivider());
        panel.add(createProjectListItem("👕", "Вишиванка для мами", "Змінено 3 дні тому"));
        panel.add(createDivider());
        panel.add(createProjectListItem("💠", "Орнамент на рукави", "Змінено 5 днів тому"));

        panel.add(Box.createVerticalStrut(15));

        JButton openColBtn = createOutlineButton("📂 Відкрити мою колекцію");
        openColBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        openColBtn.setMaximumSize(new Dimension(250, 40));
        panel.add(openColBtn);

        return panel;
    }

    private JPanel createProjectListItem(String iconStr, String title, String subtitle) {
        JPanel item = new JPanel(new BorderLayout(15, 0));
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel icon = new JLabel(iconStr);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        icon.setForeground(new Color(211, 47, 47));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        tLbl.setForeground(new Color(60, 60, 60));

        JLabel sLbl = new JLabel(subtitle);
        sLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sLbl.setForeground(new Color(150, 150, 150));

        textPanel.add(tLbl);
        textPanel.add(sLbl);

        item.add(icon, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);

        return item;
    }

    private JSeparator createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    /**
     * Сітка натхнення 2х2 (Правий сайдбар)
     */
    private JPanel createInspirationGrid() {
        RoundedPanel panel = new RoundedPanel(15, Color.WHITE);
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 300)); // Жорстко задаємо висоту під 4 фото

        for (int i = 0; i < 4; i++) {
            RoundedPanel imgBox = new RoundedPanel(10, new Color(245, 245, 245));
            imgBox.setLayout(new BorderLayout());
            JLabel lbl = new JLabel("📷", SwingConstants.CENTER);
            lbl.setForeground(new Color(200, 200, 200));
            imgBox.add(lbl, BorderLayout.CENTER);
            panel.add(imgBox);
        }
        return panel;
    }

    /**
     * Елементи нижньої панелі (Bottom Features)
     */
    private JPanel createFeatureItem(String iconStr, String title, String desc) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel icon = new JLabel(iconStr);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        icon.setForeground(new Color(211, 47, 47));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        tLbl.setForeground(new Color(40, 40, 40));

        JLabel dLbl = new JLabel("<html><div style='width: 120px;'>" + desc + "</div></html>");
        dLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        dLbl.setForeground(new Color(120, 120, 120));

        textPanel.add(tLbl);
        textPanel.add(dLbl);

        panel.add(icon, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSectionHeader(String titleText, String linkText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(new Color(40, 40, 40));

        JLabel link = new JLabel(linkText);
        link.setFont(new Font("SansSerif", Font.BOLD, 12));
        link.setForeground(new Color(211, 47, 47));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(title, BorderLayout.WEST);
        panel.add(link, BorderLayout.EAST);
        return panel;
    }

    private JButton createSolidButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(new Color(60, 60, 60));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

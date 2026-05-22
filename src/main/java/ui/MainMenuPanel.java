package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.function.Consumer;

public class MainMenuPanel extends JPanel {
    private static final Color BG = new Color(253, 251, 247);
    private static final Color CARD = Color.WHITE;
    private static final Color MILK = new Color(250, 246, 239);
    private static final Color RED = new Color(203, 23, 35);
    private static final Color RED_DARK = new Color(165, 24, 31);
    private static final Color TEXT = new Color(43, 43, 43);
    private static final Color MUTED = new Color(122, 115, 107);
    private static final Color LINE = new Color(235, 228, 219);

    private final Consumer<String> navigator;

    public MainMenuPanel() { this(key -> {}); }

    public MainMenuPanel(Consumer<String> navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel page = new JPanel(new BorderLayout(18, 0));
        page.setOpaque(false);
        page.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(createHeroBlock());
        left.add(Box.createVerticalStrut(20));
        left.add(createFeatureCards());
        left.add(Box.createVerticalStrut(20));
        left.add(createBenefitsStrip());

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(360, 700));
        right.add(createRecentProjectsCard());
        right.add(Box.createVerticalStrut(22));
        right.add(createInspirationCard());

        page.add(left, BorderLayout.CENTER);
        page.add(right, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createHeroBlock() {
        ImageHeroPanel hero = new ImageHeroPanel("/hero_vyshyvanka_16x9.png");
        hero.setLayout(new BorderLayout());
        hero.setBorder(new EmptyBorder(34, 38, 30, 38));
        hero.setPreferredSize(new Dimension(930, 340));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 345));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setPreferredSize(new Dimension(520, 270));

        JLabel t1 = new JLabel("Створи свою");
        t1.setFont(new Font("Serif", Font.PLAIN, 46));
        t1.setForeground(TEXT);
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t2 = new JLabel("унікальну вишиванку");
        t2.setFont(new Font("Serif", Font.BOLD, 46));
        t2.setForeground(RED);
        t2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea desc = new JTextArea("Малюй орнаменти, обирай кольори, надихайся\n" +
                "традиціями та створюй вишиванку, яка відображає\n" +
                "твою історію та стиль.");
        desc.setOpaque(false);
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 17));
        desc.setForeground(new Color(98, 91, 84));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton start = solidButton("Почати створення   →", 205, 50);
        start.addActionListener(e -> navigator.accept("CONSTRUCTOR"));
        JButton more = outlineButton("▱  Дізнатися більше про вишиванку", 270, 50, 12);
        more.addActionListener(e -> navigator.accept("HISTORY_PAGE"));
        buttons.add(start);
        buttons.add(more);

        text.add(t1);
        text.add(Box.createVerticalStrut(2));
        text.add(t2);
        text.add(Box.createVerticalStrut(22));
        text.add(desc);
        text.add(Box.createVerticalStrut(22));
        text.add(buttons);

        hero.add(text, BorderLayout.WEST);
        return hero;
    }

    private JPanel createFeatureCards() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 18, 0));
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(930, 238));
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 245));
        grid.add(sectionCard("✎", "Конструктор", "Створи вишиванку своєї мрії крок за кроком у зручному редакторі.", "Відкрити", "CONSTRUCTOR"));
        grid.add(sectionCard("✣", "Орнаменти", "Велика бібліотека орнаментів на будь-який смак та регіон.", "Перейти", "ORNAMENTS"));
        grid.add(sectionCard("♢", "Ідеї", "Готові дизайни, сучасні рішення та приклади для натхнення.", "Переглянути", "IDEAS"));
        grid.add(sectionCard("▤", "Історія", "Дізнайся більше про традиції, символіку та історію української вишиванки.", "Відкрити", "HISTORY_PAGE"));
        return grid;
    }

    private JPanel sectionCard(String iconText, String title, String desc, String btnText, String target) {
        RoundedPanel card = new RoundedPanel(22, CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 18, 18, 18));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { navigator.accept(target); }});

        JLabel icon = new JLabel(iconText, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 249, 245));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.setColor(new Color(232, 218, 204));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Serif", Font.BOLD, 38));
        icon.setForeground(RED);
        icon.setPreferredSize(new Dimension(76, 62));
        icon.setMaximumSize(new Dimension(76, 62));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(title);
        name.setFont(new Font("SansSerif", Font.BOLD, 21));
        name.setForeground(TEXT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea description = new JTextArea(desc);
        description.setOpaque(false);
        description.setEditable(false);
        description.setFocusable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));
        description.setForeground(MUTED);
        description.setRows(3);
        description.setMaximumSize(new Dimension(200, 58));
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton link = outlineButton(btnText + "  →", 170, 38, 12);
        link.addActionListener(e -> navigator.accept(target));
        link.setForeground(RED);
        link.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(name);
        card.add(Box.createVerticalStrut(8));
        card.add(description);
        card.add(Box.createVerticalGlue());
        card.add(link);
        return card;
    }

    private JPanel createRecentProjectsCard() {
        RoundedPanel card = sideCard("Останні проєкти", "Показати всі", "SAVED", 350);
        card.add(projectItem(new PixelBadge(48, 0), "Червоний орнамент", "Змінено 2 год тому", "CONSTRUCTOR"));
        card.add(thinLine());
        card.add(projectItem(new PixelBadge(48, 1), "Симетричний візерунок", "Змінено вчора", "CONSTRUCTOR"));
        card.add(thinLine());
        card.add(projectItem(new ShirtMiniIcon(), "Вишиванка для мами", "Змінено 3 дні тому", "CONSTRUCTOR"));
        card.add(thinLine());
        card.add(projectItem(new PixelBadge(48, 2), "Орнамент на рукави", "Змінено 5 днів тому", "CONSTRUCTOR"));
        card.add(Box.createVerticalStrut(12));
        JButton open = outlineButton("▱  Відкрити мою колекцію", 300, 44, 13);
        open.addActionListener(e -> navigator.accept("SAVED"));
        open.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(open);
        return card;
    }

    private RoundedPanel sideCard(String title, String link, String target, int width) {
        RoundedPanel card = new RoundedPanel(22, CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 20, 22, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(width, 1000));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 19));
        t.setForeground(TEXT);
        JLabel l = new JLabel(link);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(RED);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { navigator.accept(target); }});
        head.add(t, BorderLayout.WEST);
        head.add(l, BorderLayout.EAST);
        head.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        card.add(head);
        card.add(Box.createVerticalStrut(14));
        return card;
    }

    private JPanel projectItem(JComponent preview, String title, String sub, String target) {
        JPanel item = new JPanel(new BorderLayout(14, 0));
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(7, 0, 7, 0));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { navigator.accept(target); }});

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(new Color(70, 65, 60));
        JLabel subLabel = new JLabel(sub);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLabel.setForeground(new Color(155, 148, 140));
        text.add(Box.createVerticalGlue());
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(3));
        text.add(subLabel);
        text.add(Box.createVerticalGlue());
        item.add(preview, BorderLayout.WEST);
        item.add(text, BorderLayout.CENTER);
        return item;
    }

    private JPanel createInspirationCard() {
        RoundedPanel card = sideCard("Натхнення для тебе", "Показати всі", "IDEAS", 350);
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(300, 265));
        grid.setMaximumSize(new Dimension(300, 265));
        grid.add(new InspirationTile(0));
        grid.add(new InspirationTile(1));
        grid.add(new InspirationTile(2));
        grid.add(new InspirationTile(3));
        card.add(grid);
        return card;
    }

    private JPanel createBenefitsStrip() {
        RoundedPanel strip = new RoundedPanel(22, CARD);
        strip.setLayout(new GridLayout(1, 5, 12, 0));
        strip.setBorder(new EmptyBorder(18, 18, 18, 18));
        strip.setPreferredSize(new Dimension(930, 92));
        strip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        strip.add(benefit("◌", "Палітра кольорів", "Обирай кольори"));
        strip.add(benefit("▦", "Зручні інструменти", "Малюй і редагуй"));
        strip.add(benefit("♙", "Попередній перегляд", "Дивись результат"));
        strip.add(benefit("⇩", "Експорт схем", "PNG та PDF"));
        strip.add(benefit("⌯", "Поділись проєктом", "Надішли друзям"));
        return strip;
    }

    private JPanel benefit(String iconText, String title, String desc) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setOpaque(false);
        JLabel icon = new JLabel(iconText, SwingConstants.CENTER);
        icon.setFont(new Font("Serif", Font.BOLD, 28));
        icon.setForeground(RED);
        icon.setPreferredSize(new Dimension(38, 38));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setForeground(TEXT);
        JLabel d = new JLabel(desc);
        d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        d.setForeground(MUTED);
        text.add(Box.createVerticalGlue());
        text.add(t);
        text.add(Box.createVerticalStrut(3));
        text.add(d);
        text.add(Box.createVerticalGlue());
        item.add(icon, BorderLayout.WEST);
        item.add(text, BorderLayout.CENTER);
        return item;
    }

    private JSeparator thinLine() {
        JSeparator sep = new JSeparator();
        sep.setForeground(LINE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JButton solidButton(String text, int w, int h) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, RED, 0, getHeight(), RED_DARK));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(w, h));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton outlineButton(String text, int w, int h, int fontSize) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 253, 250));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 13, 13);
                g2.setColor(new Color(230, 221, 212));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 13, 13);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        b.setForeground(new Color(70, 65, 60));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(w, h));
        b.setMaximumSize(new Dimension(w, h));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static BufferedImage loadImage(String path) {
        // 1) Якщо resources правильно підключений як Resources Root
        try {
            java.net.URL url = MainMenuPanel.class.getResource(path);
            if (url != null) return ImageIO.read(url);
        } catch (IOException ignored) {}

        // 2) Якщо IntelliJ не бачить resources, читаємо файл напряму з папки проєкту
        String clean = path.startsWith("/") ? path.substring(1) : path;
        String[] variants = {
                "src/main/resources/" + clean,
                "resources/" + clean,
                clean
        };
        for (String v : variants) {
            try {
                java.io.File file = new java.io.File(v);
                if (file.exists()) return ImageIO.read(file);
            } catch (IOException ignored) {}
        }

        System.out.println("Не знайдено фото для головної: " + clean);
        return null;
    }

    private static class ImageHeroPanel extends RoundedPanel {
        private final BufferedImage image;

        ImageHeroPanel(String imagePath) {
            super(24, MILK);
            image = loadImage(imagePath);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int w = getWidth();
            int h = getHeight();
            Shape oldClip = g2.getClip();
            g2.setClip(new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 24, 24));

            if (image != null) {
                double scale = Math.max(w / (double) image.getWidth(), h / (double) image.getHeight());
                int iw = (int) Math.ceil(image.getWidth() * scale);
                int ih = (int) Math.ceil(image.getHeight() * scale);
                int x = w - iw;
                int y = (h - ih) / 2;
                g2.drawImage(image, x, y, iw, ih, null);
            } else {
                g2.setColor(MILK);
                g2.fillRoundRect(0, 0, w, h, 24, 24);
            }

            // Легка молочна підкладка зліва, щоб текст читався поверх фото.
            GradientPaint fade = new GradientPaint(
                    0, 0, new Color(250, 246, 239, 255),
                    (int)(w * 0.70), 0, new Color(250, 246, 239, 40)
            );
            g2.setPaint(fade);
            g2.fillRect(0, 0, w, h);

            g2.setClip(oldClip);
            g2.dispose();
        }
    }

    private static class PixelBadge extends JComponent {
        private final int size; private final int variant;
        PixelBadge(int size, int variant) { this.size = size; this.variant = variant; setPreferredSize(new Dimension(size, size)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 250, 247));
            g2.fillRoundRect(0, 0, size - 1, size - 1, 10, 10);
            g2.setColor(new Color(232, 222, 213));
            g2.drawRoundRect(0, 0, size - 1, size - 1, 10, 10);
            g2.setColor(RED);
            int c = size / 2, s = 4;
            for (int i = -4; i <= 4; i++) {
                g2.fillRect(c + i * s, c, s, s);
                g2.fillRect(c, c + i * s, s, s);
                if (Math.abs(i) <= 2 + variant % 2) {
                    g2.fillRect(c + i * s, c + i * s, s, s);
                    g2.fillRect(c + i * s, c - i * s, s, s);
                }
            }
            g2.dispose();
        }
    }

    private static class ShirtMiniIcon extends JComponent {
        ShirtMiniIcon() { setPreferredSize(new Dimension(48, 48)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 250, 247));
            g2.fillRoundRect(0, 0, 47, 47, 10, 10);
            g2.setColor(new Color(242, 242, 239));
            g2.fillRoundRect(12, 9, 24, 31, 7, 7);
            g2.setColor(RED);
            g2.drawLine(24, 11, 24, 37);
            for (int y = 15; y < 36; y += 6) { g2.fillRect(18, y, 4, 4); g2.fillRect(27, y, 4, 4); }
            g2.dispose();
        }
    }

    private static class InspirationTile extends JComponent {
        private final int type;
        InspirationTile(int type) { this.type = type; setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(type == 3 ? new Color(247, 238, 230) : new Color(248, 248, 245));
            g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
            g2.setColor(new Color(229, 220, 212));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
            if (type < 3) {
                int cx = w / 2;
                g2.setColor(new Color(255, 255, 252));
                g2.fillRoundRect(cx - 36, 18, 72, h - 28, 18, 18);
                g2.setColor(type == 1 ? new Color(45,45,45) : RED);
                for (int y = 30; y < h - 18; y += 8) {
                    g2.fillRect(cx - 7, y, 5, 5);
                    g2.fillRect(cx + 3, y, 5, 5);
                    if (type == 2) { g2.setColor(new Color(95, 130, 78)); g2.fillOval(cx - 22, y, 7, 5); g2.setColor(RED); }
                }
            } else {
                g2.setColor(RED);
                for (int y = 18; y < h - 14; y += 12) {
                    for (int x = 18; x < w - 14; x += 12) {
                        if ((x + y) % 24 == 0) g2.fillRect(x, y, 8, 8); else g2.drawRect(x, y, 7, 7);
                    }
                }
            }
            g2.dispose();
        }
    }
}

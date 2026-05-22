package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TopNavigationPanel extends JPanel {
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private final Consumer<String> navigator;
    private final Color RED = new Color(203, 23, 35);
    private final Color TEXT = new Color(43, 43, 43);
    private String activeKey;

    public TopNavigationPanel(String activeKey, Consumer<String> navigator) {
        this.activeKey = activeKey;
        this.navigator = navigator;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 82));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(232, 226, 218)));

        add(createLogoBlock(), BorderLayout.WEST);
        add(createTabs(), BorderLayout.CENTER);
        add(createActionIcons(), BorderLayout.EAST);
        refreshActiveState();
    }

    public void setActiveKey(String key) {
        activeKey = key;
        refreshActiveState();
    }

    private JPanel createLogoBlock() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(8, 18, 8, 8));
        wrap.setPreferredSize(new Dimension(292, 82));
        wrap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        wrap.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigator.accept("MAIN_PAGE"); }
        });

        wrap.add(new HeaderLogoImage(), BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createTabs() {
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 13));
        tabs.setOpaque(false);
        addTab(tabs, "Головна", "MAIN_PAGE", 108);
        addTab(tabs, "Конструктор", "CONSTRUCTOR", 116);
        addTab(tabs, "Орнаменти", "ORNAMENTS", 112);
        addTab(tabs, "Ідеї", "IDEAS", 72);
        addTab(tabs, "Історія", "HISTORY_PAGE", 92);
        addTab(tabs, "Моя колекція", "SAVED", 124);
        return tabs;
    }

    private void addTab(JPanel tabs, String text, String key, int width) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (key.equals(activeKey)) {
                    g2.setColor(new Color(255, 250, 247));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() - 5, 16, 16);
                    g2.setColor(RED);
                    g2.fillRoundRect(18, getHeight() - 5, getWidth() - 36, 3, 3, 3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(key.equals(activeKey) ? RED : TEXT);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(width, 54));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navigator.accept(key));
        navButtons.put(key, btn);
        tabs.add(btn);
    }

    private JPanel createActionIcons() {
        JPanel icons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 21));
        icons.setOpaque(false);
        icons.setBorder(new EmptyBorder(0, 0, 0, 18));
        icons.setPreferredSize(new Dimension(252, 82));
        icons.add(iconButton(0, "Пошук"));
        icons.add(iconButton(1, "Назад"));
        icons.add(iconButton(2, "Вперед"));
        icons.add(iconButton(3, "Налаштування"));
        icons.add(iconButton(4, "Допомога"));
        icons.add(profileButton());
        return icons;
    }

    private JButton iconButton(int type, String tip) {
        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(54, 54, 54));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getWidth(), h = getHeight();
                int cx = w / 2, cy = h / 2;
                if (type == 0) { // search
                    g2.drawOval(cx - 8, cy - 9, 13, 13);
                    g2.drawLine(cx + 3, cy + 3, cx + 10, cy + 10);
                } else if (type == 1) { // undo
                    g2.drawArc(cx - 8, cy - 9, 18, 18, 35, 255);
                    g2.drawLine(cx - 9, cy - 6, cx - 15, cy - 6);
                    g2.drawLine(cx - 9, cy - 6, cx - 9, cy - 12);
                } else if (type == 2) { // redo
                    g2.drawArc(cx - 10, cy - 9, 18, 18, -110, 255);
                    g2.drawLine(cx + 9, cy - 6, cx + 15, cy - 6);
                    g2.drawLine(cx + 9, cy - 6, cx + 9, cy - 12);
                } else if (type == 3) { // settings simple gear
                    g2.drawOval(cx - 7, cy - 7, 14, 14);
                    for (int i = 0; i < 8; i++) {
                        double a = Math.PI * i / 4.0;
                        g2.drawLine(cx + (int)(Math.cos(a) * 10), cy + (int)(Math.sin(a) * 10),
                                cx + (int)(Math.cos(a) * 14), cy + (int)(Math.sin(a) * 14));
                    }
                    g2.fillOval(cx - 2, cy - 2, 4, 4);
                } else { // help
                    g2.drawOval(cx - 12, cy - 12, 24, 24);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 17));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("?", cx - fm.stringWidth("?") / 2, cy + 6);
                }
                g2.dispose();
            }
        };
        b.setToolTipText(tip);
        b.setPreferredSize(new Dimension(32, 34));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> JOptionPane.showMessageDialog(this, tip + " буде відкрито у наступній версії."));
        return b;
    }

    private JButton profileButton() {
        JButton b = new JButton("OK") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(247, 240, 232));
                g2.fillOval(0, 0, 38, 38);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(new Color(70, 64, 58));
        b.setPreferredSize(new Dimension(42, 38));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void refreshActiveState() {
        for (Map.Entry<String, JButton> e : navButtons.entrySet()) {
            boolean active = e.getKey().equals(activeKey);
            e.getValue().setForeground(active ? RED : TEXT);
            e.getValue().repaint();
        }
    }

    private static class HeaderLogoImage extends JComponent {
        private BufferedImage logo;

        HeaderLogoImage() {
            setPreferredSize(new Dimension(260, 64));
            logo = loadLogo();
        }

        private BufferedImage loadLogo() {
            try {
                URL url = TopNavigationPanel.class.getResource("/app_logo.png");
                if (url != null) {
                    return ImageIO.read(url);
                }

                File file = new File("src/main/resources/app_logo.png");
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            } catch (IOException ignored) {
            }

            System.out.println("Не знайдено логотип: app_logo.png");
            return null;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            if (logo != null) {
                int maxW = getWidth();
                int maxH = getHeight();
                double scale = Math.min((double) maxW / logo.getWidth(), (double) maxH / logo.getHeight());
                int w = (int) Math.round(logo.getWidth() * scale);
                int h = (int) Math.round(logo.getHeight() * scale);
                int x = 0;
                int y = (getHeight() - h) / 2;
                g2.drawImage(logo, x, y, w, h, null);
            } else {
                drawFallbackLogo(g2);
            }
            g2.dispose();
        }

        private void drawFallbackLogo(Graphics2D g2) {
            g2.setColor(new Color(203, 23, 35));
            int cx = 28, cy = getHeight() / 2;
            g2.fillOval(cx - 6, cy - 6, 12, 12);
            for (int i = 0; i < 8; i++) {
                double a = Math.PI * i / 4.0;
                int x = cx + (int) (Math.cos(a) * 21);
                int y = cy + (int) (Math.sin(a) * 21);
                g2.fillRoundRect(x - 5, y - 5, 10, 10, 4, 4);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(cx, cy, cx + (int)(Math.cos(a) * 14), cy + (int)(Math.sin(a) * 14));
            }
            g2.setFont(new Font("SansSerif", Font.BOLD, 21));
            g2.setColor(new Color(43, 43, 43));
            g2.drawString("Вишивай легко", 64, cy - 4);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(new Color(140, 132, 123));
            g2.drawString("створюй свою вишиванку", 64, cy + 16);
        }
    }

}

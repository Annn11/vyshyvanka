package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TopNavigationPanel extends JPanel {
    private final Map<String, NavItem> navButtons = new LinkedHashMap<>();
    private final Consumer<String> navigator;
    private final Color RED = new Color(203, 23, 35);
    private final Color TEXT = new Color(43, 43, 43);
    private String activeKey;

    public TopNavigationPanel(String activeKey, Consumer<String> navigator) {
        this.activeKey = activeKey;
        this.navigator = navigator;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 88));
        setMinimumSize(new Dimension(900, 88));
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
        wrap.setBorder(new EmptyBorder(8, 20, 8, 6));
        wrap.setPreferredSize(new Dimension(285, 88));
        wrap.setMinimumSize(new Dimension(290, 88));
        wrap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        wrap.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                navigator.accept("MAIN_PAGE");
            }
        });

        wrap.add(new HeaderLogoImage(), BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createTabs() {
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 16));
        tabs.setOpaque(false);

        addTab(tabs, "Головна", "MAIN_PAGE", 96);
        addTab(tabs, "Конструктор", "CONSTRUCTOR", 112);
        addTab(tabs, "Орнаменти", "ORNAMENTS", 106);
        addTab(tabs, "Ідеї", "IDEAS", 64);
        addTab(tabs, "Історія", "HISTORY_PAGE", 82);
        addTab(tabs, "Моя колекція", "SAVED", 118);

        return tabs;
    }

    private void addTab(JPanel tabs, String text, String key, int width) {
        NavItem item = new NavItem(text, key, width);
        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                navigator.accept(key);
            }

            @Override public void mouseEntered(MouseEvent e) {
                item.setHover(true);
            }

            @Override public void mouseExited(MouseEvent e) {
                item.setHover(false);
            }
        });
        navButtons.put(key, item);
        tabs.add(item);
    }

    private JPanel createActionIcons() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(6, 0, 6, 20));
        wrap.setPreferredSize(new Dimension(285, 88));
        wrap.setMinimumSize(new Dimension(270, 88));

        RightTopAuthorImage image = new RightTopAuthorImage();
        image.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        image.setToolTipText("Шкут Анна ІПЗ-1. Усі права захищені");
        image.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        TopNavigationPanel.this,
                        "Шкут Анна ІПЗ-1\nУсі права захищені",
                        "Про автора",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        wrap.add(image, BorderLayout.CENTER);
        return wrap;
    }

    private JButton iconButton(int type, String tip) {
        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(52, 52, 52));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getWidth(), h = getHeight();
                int cx = w / 2, cy = h / 2;

                if (type == 0) {
                    g2.drawOval(cx - 8, cy - 9, 14, 14);
                    g2.drawLine(cx + 3, cy + 3, cx + 11, cy + 11);
                } else if (type == 1) {
                    g2.drawArc(cx - 8, cy - 9, 20, 20, 35, 255);
                    g2.drawLine(cx - 8, cy - 6, cx - 15, cy - 6);
                    g2.drawLine(cx - 8, cy - 6, cx - 8, cy - 13);
                } else if (type == 2) {
                    g2.drawArc(cx - 12, cy - 9, 20, 20, -110, 255);
                    g2.drawLine(cx + 8, cy - 6, cx + 15, cy - 6);
                    g2.drawLine(cx + 8, cy - 6, cx + 8, cy - 13);
                } else if (type == 3) {
                    g2.drawOval(cx - 7, cy - 7, 14, 14);
                    for (int i = 0; i < 8; i++) {
                        double a = Math.PI * i / 4.0;
                        g2.drawLine(cx + (int) (Math.cos(a) * 10), cy + (int) (Math.sin(a) * 10),
                                cx + (int) (Math.cos(a) * 15), cy + (int) (Math.sin(a) * 15));
                    }
                    g2.fillOval(cx - 2, cy - 2, 4, 4);
                } else {
                    g2.drawOval(cx - 12, cy - 12, 24, 24);
                    g2.setFont(new Font("Arial", Font.BOLD, 17));
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
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setForeground(new Color(70, 64, 58));
        b.setPreferredSize(new Dimension(42, 38));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void refreshActiveState() {
        for (Map.Entry<String, NavItem> e : navButtons.entrySet()) {
            e.getValue().setActive(e.getKey().equals(activeKey));
        }
    }

    private class NavItem extends JComponent {
        private final String text;
        private final String key;
        private boolean active;
        private boolean hover;

        NavItem(String text, String key, int width) {
            this.text = text;
            this.key = key;
            setPreferredSize(new Dimension(width, 56));
            setMinimumSize(new Dimension(width, 56));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(text);
        }

        void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        void setHover(boolean hover) {
            this.hover = hover;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (active || hover) {
                g2.setColor(active ? new Color(255, 250, 247) : new Color(252, 248, 243));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 7, 16, 16);
            }

            if (active) {
                g2.setColor(RED);
                g2.fillRoundRect(18, getHeight() - 7, getWidth() - 36, 3, 3, 3);
            }

            g2.setColor(active ? RED : TEXT);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - 7 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, x, y);
            g2.dispose();
        }
    }


    private static class RightTopAuthorImage extends JComponent {
        private BufferedImage image;

        RightTopAuthorImage() {
            setPreferredSize(new Dimension(270, 64));
            setMinimumSize(new Dimension(250, 64));
            image = loadImage();
        }

        private BufferedImage loadImage() {
            try {
                URL url = TopNavigationPanel.class.getResource("/logo_vyshyvanka_right_top.png");
                if (url != null) {
                    return cropWhiteMargins(ImageIO.read(url));
                }

                File file = new File("src/main/resources/logo_vyshyvanka_right_top.png");
                if (file.exists()) {
                    return cropWhiteMargins(ImageIO.read(file));
                }
            } catch (IOException ignored) {
            }

            System.out.println("Не знайдено зображення: logo_vyshyvanka_right_top.png");
            return null;
        }

        private BufferedImage cropWhiteMargins(BufferedImage source) {
            int minX = source.getWidth();
            int minY = source.getHeight();
            int maxX = -1;
            int maxY = -1;

            for (int y = 0; y < source.getHeight(); y++) {
                for (int x = 0; x < source.getWidth(); x++) {
                    int argb = source.getRGB(x, y);
                    int a = (argb >>> 24) & 0xff;
                    int r = (argb >>> 16) & 0xff;
                    int g = (argb >>> 8) & 0xff;
                    int b = argb & 0xff;

                    boolean isNotWhite = a > 20 && !(r > 242 && g > 242 && b > 242);
                    if (isNotWhite) {
                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                    }
                }
            }

            if (maxX < minX || maxY < minY) {
                return source;
            }

            int padding = 8;
            minX = Math.max(0, minX - padding);
            minY = Math.max(0, minY - padding);
            maxX = Math.min(source.getWidth() - 1, maxX + padding);
            maxY = Math.min(source.getHeight() - 1, maxY + padding);

            return source.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            if (image != null) {
                int maxW = getWidth();
                int maxH = 50;

                double scale = Math.min((double) maxW / image.getWidth(), (double) maxH / image.getHeight());
                int w = (int) Math.round(image.getWidth() * scale);
                int h = (int) Math.round(image.getHeight() * scale);

                int x = getWidth() - w;
                int y = (getHeight() - h) / 2;

                g2.drawImage(image, x, y, w, h, null);
            } else {
                drawFallback(g2);
            }

            g2.dispose();
        }

        private void drawFallback(Graphics2D g2) {
            g2.setColor(new Color(245, 238, 230));
            g2.fillRoundRect(0, 10, getWidth() - 1, getHeight() - 20, 18, 18);

            g2.setColor(new Color(203, 23, 35));
            g2.setFont(new Font("Serif", Font.BOLD, 18));
            g2.drawString("Шкут Анна", 24, 35);

            g2.setColor(new Color(70, 62, 55));
            g2.setFont(new Font("Serif", Font.PLAIN, 13));
            g2.drawString("ІПЗ-1     Усі права захищені", 24, 55);
        }
    }

    private static class HeaderLogoImage extends JComponent {
        private BufferedImage logo;

        HeaderLogoImage() {
            setPreferredSize(new Dimension(270, 64));
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
                int maxH = 50;
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
                g2.drawLine(cx, cy, cx + (int) (Math.cos(a) * 14), cy + (int) (Math.sin(a) * 14));
            }
            g2.setFont(new Font("Arial", Font.BOLD, 21));
            g2.setColor(new Color(43, 43, 43));
            g2.drawString("Вишивай легко", 64, cy - 4);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(new Color(140, 132, 123));
            g2.drawString("створюй свою вишиванку", 64, cy + 16);
        }
    }
}

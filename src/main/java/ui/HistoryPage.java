package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class HistoryPage extends JPanel {

    private final Color BG = new Color(250, 248, 245);
    private final Color CARD = Color.WHITE;
    private final Color RED = new Color(211, 47, 47);
    private final Color TEXT = new Color(35, 32, 30);
    private final Color MUTED = new Color(92, 88, 82);
    private final Color SOFT_RED = new Color(253, 241, 241);
    private final Color BEIGE = new Color(248, 241, 232);

    public HistoryPage(JPanel pagesContainer, CardLayout cardLayout) {
        setBackground(BG);
        setLayout(new BorderLayout());

        add(createLeftMenu(), BorderLayout.WEST);
        add(createScrollableContent(), BorderLayout.CENTER);
    }

    private JComponent createScrollableContent() {
        JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 22, 28, 0));

        ScrollableMainPanel mainArea = new ScrollableMainPanel(new GridBagLayout());
        mainArea.setOpaque(false);
        mainArea.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel centerColumn = new JPanel();
        centerColumn.setOpaque(false);
        centerColumn.setLayout(new BoxLayout(centerColumn, BoxLayout.Y_AXIS));

        centerColumn.add(createImageBlock("/hero_history.png", 255, true));
        centerColumn.add(Box.createVerticalStrut(14));
        centerColumn.add(createImageBlock("/epochs_history.png", 185, true));
        centerColumn.add(Box.createVerticalStrut(14));
        centerColumn.add(createImageBlock("/origin_history.png", 155, true));
        centerColumn.add(Box.createVerticalStrut(14));
        centerColumn.add(createRegionsBlock());
        centerColumn.add(Box.createVerticalStrut(14));
        centerColumn.add(createDifferencesBlock());
        centerColumn.add(Box.createVerticalStrut(30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        mainArea.add(centerColumn, gbc);

        JScrollPane scrollPane = new JScrollPane(mainArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(22);
        scrollPane.getVerticalScrollBar().setBlockIncrement(130);

        JPanel rightColumn = createRightColumn();
        rightColumn.setBorder(new EmptyBorder(0, 0, 0, 0));

        content.add(scrollPane, BorderLayout.CENTER);
        content.add(rightColumn, BorderLayout.EAST);

        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
        return content;
    }

    private JPanel createLeftMenu() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setPreferredSize(new Dimension(210, 0));
        outer.setBorder(new EmptyBorder(0, 0, 0, 18));

        RoundedPanel menu = new RoundedPanel(22, CARD);
        menu.setLayout(new BorderLayout());
        menu.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Порожня ліва панель без картинки, написів, іконок і кнопок.
        outer.add(menu, BorderLayout.CENTER);
        return outer;
    }

    private JButton createMenuButton(String icon, String text, boolean active) {
        JButton btn = new JButton(icon + "   " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(RED);
                    g2.fillRoundRect(0, 7, 4, getHeight() - 14, 4, 4);

                    g2.setColor(SOFT_RED);
                    g2.fillRoundRect(12, 0, getWidth() - 24, getHeight(), 12, 12);

                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(active ? RED : new Color(70, 70, 70));
        btn.setPreferredSize(new Dimension(185, 46));
        btn.setMaximumSize(new Dimension(185, 46));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(0, 18, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private JPanel createImageBlock(String imagePath, int height) {
        return createImageBlock(imagePath, height, false);
    }

    private JPanel createImageBlock(String imagePath, int height, boolean fillBlock) {
        RoundedPanel block = new RoundedPanel(22, CARD);
        block.setLayout(new BorderLayout());
        block.setBorder(new EmptyBorder(0, 0, 0, 0));

        block.setPreferredSize(new Dimension(850, height));
        block.setMinimumSize(new Dimension(560, height));
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

        block.add(new ImageBanner(imagePath, height, fillBlock), BorderLayout.CENTER);
        return block;
    }

    private JPanel createRegionsBlock() {
        return createImageBlock("/regions_history.png", 185, true);
    }

    private JPanel createRegionCard(String name, String desc, String tag, int type) {
        RoundedPanel card = new RoundedPanel(16, new Color(255, 253, 250));
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(9, 9, 9, 9));

        PatternSquare pattern = new PatternSquare(type);
        pattern.setPreferredSize(new Dimension(72, 72));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(TEXT);

        JTextArea text = smallText(desc, 9);
        text.setMaximumSize(new Dimension(160, 48));

        JLabel tagLabel = new JLabel(tag);
        tagLabel.setFont(new Font("SansSerif", Font.BOLD, 9));
        tagLabel.setForeground(RED);
        tagLabel.setOpaque(true);
        tagLabel.setBackground(SOFT_RED);
        tagLabel.setBorder(new EmptyBorder(3, 8, 3, 8));

        textBlock.add(nameLabel);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(text);
        textBlock.add(Box.createVerticalGlue());
        textBlock.add(tagLabel);

        card.add(pattern, BorderLayout.WEST);
        card.add(textBlock, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDifferencesBlock() {
        return createImageBlock("/differences_history.png", 210, true);
    }

    private JPanel createDifferenceItem(String icon, String name, String desc) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Serif", Font.BOLD, 30));
        iconLabel.setForeground(RED);
        iconLabel.setPreferredSize(new Dimension(42, 60));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(TEXT);

        textBlock.add(nameLabel);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(smallText(desc, 10));

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textBlock, BorderLayout.CENTER);
        return item;
    }

    private JPanel createRightColumn() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        right.setPreferredSize(new Dimension(285, 660));
        right.setMinimumSize(new Dimension(285, 660));
        right.setMaximumSize(new Dimension(285, Integer.MAX_VALUE));

        right.add(createFactsCard());
        right.add(Box.createVerticalStrut(14));
        right.add(createSymbolsCard());
        return right;
    }

    private JPanel createFactsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(285, 356));
        card.setMinimumSize(new Dimension(285, 356));
        card.setMaximumSize(new Dimension(285, 356));
        card.add(new FixedImageCard("/interesting_facts_history.png", 285, 356, 24), BorderLayout.CENTER);
        return card;
    }

    private JPanel createSymbolsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(285, 278));
        card.setMinimumSize(new Dimension(285, 278));
        card.setMaximumSize(new Dimension(285, 278));
        card.add(new FixedImageCard("/symbolism_history.png", 285, 278, 24), BorderLayout.CENTER);
        return card;
    }

    private JPanel createInfoRow(String icon, String text) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel iconBox = new JLabel(icon, SwingConstants.CENTER);
        iconBox.setFont(new Font("Serif", Font.BOLD, 22));
        iconBox.setForeground(RED);
        iconBox.setOpaque(true);
        iconBox.setBackground(new Color(255, 250, 247));
        iconBox.setBorder(BorderFactory.createLineBorder(new Color(245, 231, 225)));
        iconBox.setPreferredSize(new Dimension(42, 42));

        JTextArea body = smallText(text, 9);
        body.setForeground(new Color(60, 60, 60));

        row.add(iconBox, BorderLayout.WEST);
        row.add(body, BorderLayout.CENTER);
        return row;
    }

    private JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(TEXT);
        label.setBorder(new EmptyBorder(0, 0, 10, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextArea smallText(String text, int size) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("SansSerif", Font.PLAIN, size));
        area.setForeground(MUTED);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private BufferedImage loadImage(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (IOException ignored) {
        }

        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        String[] possiblePaths = {
                "src/main/resources/" + cleanPath,
                "resources/" + cleanPath,
                cleanPath
        };

        for (String filePath : possiblePaths) {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            } catch (IOException ignored) {
            }
        }

        return null;
    }

    private class ImageBanner extends JComponent {
        private final BufferedImage img;
        private final String imagePath;
        private final boolean fillBlock;

        ImageBanner(String imagePath, int height, boolean fillBlock) {
            this.imagePath = imagePath;
            this.fillBlock = fillBlock;
            this.img = loadImage(imagePath);
            setPreferredSize(new Dimension(850, height));
            setMinimumSize(new Dimension(560, height));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, 22, 22));

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            if (img != null) {
                if (fillBlock) {
                    drawImageCover(g2, img, w, h);
                } else {
                    drawImageContain(g2, img, w, h);
                }
            } else {
                g2.setColor(RED);
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                g2.drawString("Зображення не знайдено", 30, 65);

                g2.setColor(MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.drawString("Додай файл: src/main/resources/" + imagePath.replace("/", ""), 30, 95);
            }

            g2.setClip(oldClip);
            g2.dispose();
        }

        private void drawImageCover(Graphics2D g2, BufferedImage image, int w, int h) {
            double scale = Math.max((double) w / image.getWidth(), (double) h / image.getHeight());

            int drawW = (int) Math.round(image.getWidth() * scale);
            int drawH = (int) Math.round(image.getHeight() * scale);
            int x = (w - drawW) / 2;
            int y = (h - drawH) / 2;

            g2.drawImage(image, x, y, drawW, drawH, null);
        }

        private void drawImageContain(Graphics2D g2, BufferedImage image, int w, int h) {
            double scale = Math.min((double) w / image.getWidth(), (double) h / image.getHeight());

            int drawW = (int) Math.round(image.getWidth() * scale);
            int drawH = (int) Math.round(image.getHeight() * scale);
            int x = (w - drawW) / 2;
            int y = (h - drawH) / 2;

            g2.drawImage(image, x, y, drawW, drawH, null);
        }

    }


    private class FactsImage extends JComponent {
        private final BufferedImage img;

        FactsImage(String imagePath) {
            this.img = loadImage(imagePath);
            setPreferredSize(new Dimension(270, 350));
            setMinimumSize(new Dimension(270, 350));
            setMaximumSize(new Dimension(270, 350));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, 24, 24));

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 24, 24);

            if (img != null) {
                double scale = Math.max((double) w / img.getWidth(), (double) h / img.getHeight());
                int drawW = (int) Math.round(img.getWidth() * scale);
                int drawH = (int) Math.round(img.getHeight() * scale);
                int x = (w - drawW) / 2;
                int y = (h - drawH) / 2;
                g2.drawImage(img, x, y, drawW, drawH, null);
            } else {
                g2.setColor(RED);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString("Додай файл interesting_facts_history.png", 16, 40);
            }

            g2.setClip(oldClip);

            g2.dispose();
        }
    }


    private class FixedImageCard extends JComponent {
        private final BufferedImage img;
        private final int cardWidth;
        private final int cardHeight;
        private final int radius;

        FixedImageCard(String imagePath, int cardWidth, int cardHeight, int radius) {
            this.img = loadImage(imagePath);
            this.cardWidth = cardWidth;
            this.cardHeight = cardHeight;
            this.radius = radius;
            setPreferredSize(new Dimension(cardWidth, cardHeight));
            setMinimumSize(new Dimension(cardWidth, cardHeight));
            setMaximumSize(new Dimension(cardWidth, cardHeight));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, radius, radius));
            g2.setColor(BG);
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            if (img != null) {
                double scale = Math.min((double) w / img.getWidth(), (double) h / img.getHeight());
                int drawW = (int) Math.round(img.getWidth() * scale);
                int drawH = (int) Math.round(img.getHeight() * scale);
                int x = (w - drawW) / 2;
                int y = (h - drawH) / 2;
                g2.drawImage(img, x, y, drawW, drawH, null);
            } else {
                g2.setColor(RED);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.drawString("Додай файл symbolism_history.png", 16, 36);
            }

            g2.setClip(oldClip);
            g2.dispose();
        }
    }

    private static class ScrollableMainPanel extends JPanel implements Scrollable {
        ScrollableMainPanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 22;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 130;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private class SideOrnament extends JComponent {
        SideOrnament() {
            setPreferredSize(new Dimension(170, 150));
            setMaximumSize(new Dimension(170, 150));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(233, 206, 185));

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int step = 10;

            for (int i = -5; i <= 5; i++) {
                drawDiamond(g2, cx + i * step, cy, 4);
                drawDiamond(g2, cx, cy + i * step, 4);
                drawDiamond(g2, cx + i * step, cy + i * step, 4);
                drawDiamond(g2, cx + i * step, cy - i * step, 4);
            }

            g2.setColor(new Color(220, 178, 154));
            drawDiamond(g2, cx, cy, 18);
            drawDiamond(g2, cx, cy, 9);

            g2.dispose();
        }

        private void drawDiamond(Graphics2D g2, int x, int y, int r) {
            Polygon p = new Polygon();
            p.addPoint(x, y - r);
            p.addPoint(x + r, y);
            p.addPoint(x, y + r);
            p.addPoint(x - r, y);
            g2.fillPolygon(p);
        }
    }

    private class PatternSquare extends JComponent {
        private final int type;

        PatternSquare(int type) {
            this.type = type;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 247, 243));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            int cell = 5;
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            Color second = type == 1 ? new Color(42, 85, 50) : new Color(45, 45, 45);

            if (type == 2) {
                second = Color.BLACK;
            }

            for (int y = -5; y <= 5; y++) {
                for (int x = -5; x <= 5; x++) {
                    boolean red = Math.abs(x) + Math.abs(y) == 4 || Math.abs(x) == Math.abs(y) && Math.abs(x) < 5;
                    boolean dark = Math.abs(x) + Math.abs(y) == 6 || x == 0 || y == 0;

                    if (red || dark) {
                        g2.setColor(red ? RED : second);
                        g2.fillRect(cx + x * cell, cy + y * cell, cell - 1, cell - 1);
                    }
                }
            }

            g2.dispose();
        }
    }
}

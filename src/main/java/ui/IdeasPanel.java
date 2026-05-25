package ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class IdeasPanel extends JPanel {
    private static final Color TEXT = new Color(45, 45, 45);
    private static final Color MUTED = new Color(100, 94, 88);
    private static final Color RED = new Color(198, 35, 41);
    private static final Color BLACK = new Color(55, 55, 55);
    private static final Color LINE = new Color(232, 226, 218);

    private final JPanel grid = new JPanel();
    private final JTextField searchField = new JTextField();
    private final Consumer<String> navigator;
    private final ConstructorPanel constructorPanel;
    private final List<NamePatternRepository.NamePattern> allPatterns;

    public IdeasPanel(Consumer<String> navigator, ConstructorPanel constructorPanel) {
        this.navigator = navigator;
        this.constructorPanel = constructorPanel;
        this.allPatterns = new ArrayList<>(NamePatternRepository.all());

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        add(createHeader(), BorderLayout.NORTH);

        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        add(grid, BorderLayout.CENTER);

        rebuildGrid();
    }

    public IdeasPanel() {
        this(key -> {}, null);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Імена для вишиванки");
        title.setFont(new Font("Serif", Font.BOLD, 38));
        title.setForeground(TEXT);

        JLabel desc = new JLabel("Це не картинки: кожне ім’я збережене як схема з клітинок, яку можна відкрити у конструкторі.");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        desc.setForeground(MUTED);

        texts.add(title);
        texts.add(Box.createVerticalStrut(8));
        texts.add(desc);
        header.add(texts, BorderLayout.WEST);

        JPanel searchPanel = new RoundedPanel(18, Color.WHITE);
        searchPanel.setLayout(new BorderLayout(10, 0));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        searchPanel.setPreferredSize(new Dimension(330, 46));

        JLabel searchIcon = new JLabel("⌕");
        searchIcon.setFont(new Font("Serif", Font.BOLD, 24));
        searchIcon.setForeground(RED);

        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        searchField.setForeground(TEXT);
        searchField.setToolTipText("Пошук імені");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { rebuildGrid(); }
            @Override public void removeUpdate(DocumentEvent e) { rebuildGrid(); }
            @Override public void changedUpdate(DocumentEvent e) { rebuildGrid(); }
        });

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private void rebuildGrid() {
        grid.removeAll();

        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        List<NamePatternRepository.NamePattern> filtered = new ArrayList<>();

        for (NamePatternRepository.NamePattern pattern : allPatterns) {
            if (query.isEmpty() || pattern.name().toLowerCase(Locale.ROOT).contains(query)) {
                filtered.add(pattern);
            }
        }

        int columns = 4;
        int rows = Math.max(1, (int) Math.ceil(filtered.size() / (double) columns));
        grid.setLayout(new GridLayout(rows, columns, 20, 20));

        for (NamePatternRepository.NamePattern pattern : filtered) {
            grid.add(card(pattern));
        }

        int empty = rows * columns - filtered.size();
        for (int i = 0; i < empty; i++) {
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            grid.add(spacer);
        }

        revalidate();
        repaint();
    }

    private JPanel card(NamePatternRepository.NamePattern pattern) {
        RoundedPanel p = new RoundedPanel(8, Color.WHITE);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 227, 220), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        p.setPreferredSize(new Dimension(215, 295));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        PatternPreview preview = new PatternPreview(pattern);
        preview.setPreferredSize(new Dimension(185, 170));
        p.add(preview, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(spacedName(pattern.name().toUpperCase(Locale.ROOT)), SwingConstants.CENTER);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setForeground(TEXT);

        MiniSymbols symbols = new MiniSymbols();
        symbols.setPreferredSize(new Dimension(155, 26));
        symbols.setMaximumSize(new Dimension(155, 26));
        symbols.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel action = new JLabel("натисни, щоб відкрити у конструкторі", SwingConstants.CENTER);
        action.setFont(new Font("SansSerif", Font.PLAIN, 10));
        action.setForeground(MUTED);
        action.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottom.add(Box.createVerticalStrut(4));
        bottom.add(name);
        bottom.add(Box.createVerticalStrut(7));
        bottom.add(symbols);
        bottom.add(Box.createVerticalStrut(7));
        bottom.add(action);

        p.add(bottom, BorderLayout.SOUTH);

        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openInConstructor(pattern);
            }
        });

        return p;
    }

    private void openInConstructor(NamePatternRepository.NamePattern pattern) {
        if (constructorPanel == null) {
            JOptionPane.showMessageDialog(this, "Конструктор не підключений до цієї вкладки.");
            return;
        }

        constructorPanel.loadNamePattern(pattern);
        navigator.accept("CONSTRUCTOR");
    }

    private String spacedName(String name) {
        StringBuilder sb = new StringBuilder("<html><div style='text-align:center;'>");
        for (int i = 0; i < name.length(); i++) {
            sb.append(name.charAt(i));
            if (i < name.length() - 1) sb.append("&nbsp;&nbsp;");
        }
        sb.append("</div></html>");
        return sb.toString();
    }

    private static class PatternPreview extends JPanel {
        private final NamePatternRepository.NamePattern pattern;

        PatternPreview(NamePatternRepository.NamePattern pattern) {
            this.pattern = pattern;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            int[][] cells = pattern.cells();
            int rows = pattern.rows();
            int cols = pattern.cols();

            int cell = Math.max(3, Math.min((getWidth() - 20) / cols, (getHeight() - 12) / rows));
            int startX = (getWidth() - cols * cell) / 2;
            int startY = (getHeight() - rows * cell) / 2;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int value = cells[r][c];
                    if (value == NamePatternRepository.RED_CELL || value == NamePatternRepository.BLACK_CELL) {
                        g2.setColor(value == NamePatternRepository.RED_CELL ? RED : BLACK);
                        g2.fillRect(startX + c * cell, startY + r * cell, Math.max(1, cell - 1), Math.max(1, cell - 1));
                    }
                }
            }

            g2.dispose();
        }
    }

    private static class MiniSymbols extends JPanel {
        MiniSymbols() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.1f));
            g2.setColor(new Color(205, 200, 193));

            int count = 5;
            int size = 15;
            int gap = 12;
            int total = count * size + (count - 1) * gap;
            int x = (getWidth() - total) / 2;
            int y = 5;

            for (int i = 0; i < count; i++) {
                int cx = x + i * (size + gap);
                if (i % 4 == 0) {
                    int[] xs = {cx + size / 2, cx + size, cx + size / 2, cx};
                    int[] ys = {y, y + size / 2, y + size, y + size / 2};
                    g2.drawPolygon(xs, ys, 4);
                } else if (i % 4 == 1) {
                    g2.drawOval(cx + 1, y + 1, size - 2, size - 2);
                    g2.drawLine(cx + size / 2, y + 3, cx + size / 2, y + size - 3);
                    g2.drawLine(cx + 3, y + size / 2, cx + size - 3, y + size / 2);
                } else if (i % 4 == 2) {
                    g2.drawRoundRect(cx + 1, y + 1, size - 2, size - 2, 4, 4);
                    g2.drawLine(cx + 3, y + 3, cx + size - 3, y + size - 3);
                    g2.drawLine(cx + size - 3, y + 3, cx + 3, y + size - 3);
                } else {
                    g2.drawArc(cx + 1, y + 1, size - 2, size - 2, 20, 320);
                }
            }

            g2.dispose();
        }
    }
}

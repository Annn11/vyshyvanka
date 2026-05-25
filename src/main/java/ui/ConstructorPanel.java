package ui;

import model.ProjectPreview;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ConstructorPanel extends JPanel {
    private static final Color BG = new Color(253, 251, 247);
    private static final Color CARD = Color.WHITE;
    private static final Color CARD_SOFT = new Color(255, 252, 248);
    private static final Color RED = new Color(203, 23, 35);
    private static final Color RED_DARK = new Color(160, 18, 28);
    private static final Color TEXT = new Color(48, 48, 48);
    private static final Color MUTED = new Color(132, 124, 116);
    private static final Color LINE = new Color(232, 226, 218);
    private static final Color GRID = new Color(238, 232, 224);

    private static final int ROWS = 56;
    private static final int COLS = 56;

    private enum Tool { SELECT, PENCIL, FILL, LINE, ERASER }

    private final Color[][] cells = new Color[ROWS][COLS];
    private final Deque<Color[][]> undoStack = new ArrayDeque<>();
    private final Deque<Color[][]> redoStack = new ArrayDeque<>();
    private final CanvasPanel canvas = new CanvasPanel();
    private JScrollPane canvasScrollPane;
    private final JPanel toolsBox = new JPanel();
    private final JLabel zoomValue = new JLabel("150%", SwingConstants.CENTER);
    private final JLabel cellValue = new JLabel("10 × 10 ▾", SwingConstants.CENTER);

    private Tool activeTool = Tool.PENCIL;
    private Color selectedColor = RED;
    private final Color[] customColors = new Color[4];

    // Обраний готовий елемент, який користувач ставить кліком на полотні
    private boolean[][] pendingStamp = null;
    private Color pendingStampColor = RED;
    private String pendingStampName = null;
    private boolean pendingStampReusable = false;
    private boolean[][] selectedOrnamentBase = null;
    private String selectedOrnamentName = null;
    private int selectedOrnamentScale = 2;

    private boolean verticalSymmetry = false;
    private boolean horizontalSymmetry = false;
    private Point lineStart;
    private int zoom = 150;
    private int visualCell = 10;

    public ConstructorPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setOpaque(true);

        JLayeredPane root = new JLayeredPane();
        root.setOpaque(false);
        add(root, BorderLayout.CENTER);

        JPanel page = new JPanel(new BorderLayout(22, 0));
        page.setOpaque(false);
        page.setBorder(new EmptyBorder(34, 28, 34, 28));
        page.add(createLeftColumn(), BorderLayout.WEST);
        page.add(createWorkspace(), BorderLayout.CENTER);
        page.add(createRightSidebar(), BorderLayout.EAST);
        root.add(page, JLayeredPane.DEFAULT_LAYER);

        root.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                page.setBounds(0, 0, root.getWidth(), root.getHeight());
            }
        });

        saveState();
        loadDefaultNamePattern();
    }


    public void loadSavedProject(ProjectPreview project) {
        if (project == null) return;

        saveState();

        for (int r = 0; r < ROWS; r++) {
            Arrays.fill(cells[r], null);
        }

        Color[][] savedPattern = project.getFullPattern();
        int rows = Math.min(ROWS, savedPattern.length);
        for (int r = 0; r < rows; r++) {
            int cols = Math.min(COLS, savedPattern[r].length);
            for (int c = 0; c < cols; c++) {
                cells[r][c] = savedPattern[r][c];
            }
        }

        pendingStamp = null;
        pendingStampName = null;
        pendingStampReusable = false;
        selectedOrnamentBase = null;
        selectedOrnamentName = null;
        lineStart = null;
        activeTool = Tool.PENCIL;
        refreshToolButtons();
        refreshCanvasSize();
        repaintAll();
    }

    public void loadNamePattern(NamePatternRepository.NamePattern namePattern) {
        if (namePattern == null) return;

        saveState();

        for (int r = 0; r < ROWS; r++) {
            Arrays.fill(cells[r], null);
        }

        int[][] pattern = namePattern.cells();
        int patternRows = pattern.length;
        int patternCols = pattern[0].length;

        int startR = Math.max(0, (ROWS - patternRows) / 2);
        int startC = Math.max(0, (COLS - patternCols) / 2);

        for (int r = 0; r < patternRows; r++) {
            for (int c = 0; c < patternCols; c++) {
                int value = pattern[r][c];
                int rr = startR + r;
                int cc = startC + c;

                if (rr >= 0 && rr < ROWS && cc >= 0 && cc < COLS) {
                    if (value == NamePatternRepository.RED_CELL) {
                        cells[rr][cc] = RED;
                    } else if (value == NamePatternRepository.BLACK_CELL) {
                        cells[rr][cc] = new Color(55, 55, 55);
                    } else {
                        cells[rr][cc] = null;
                    }
                }
            }
        }

        pendingStamp = null;
        pendingStampName = null;
        pendingStampReusable = false;
        selectedOrnamentBase = null;
        selectedOrnamentName = null;
        lineStart = null;
        activeTool = Tool.PENCIL;
        refreshToolButtons();
        refreshCanvasSize();
        repaintAll();
    }


    private JPanel createLeftColumn() {
        JPanel column = new JPanel(null);
        column.setOpaque(false);
        column.setPreferredSize(new Dimension(250, 0));

        JPanel tools = createLeftTools();
        JPanel palette = createPalettePanel();
        column.add(tools);
        column.add(palette);

        column.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int h = column.getHeight();
                int toolsH = Math.min(500, Math.max(350, h - 185));

                tools.setBounds(0, 0, 150, toolsH);

                // Палітра завжди прикріплена до лівого нижнього кута вкладки
                palette.setBounds(0, Math.max(0, h - 158), 250, 158);
            }
        });

        return column;
    }

    private JComponent createWorkspace() {
        JLayeredPane layer = new JLayeredPane();
        layer.setOpaque(false);
        layer.setPreferredSize(new Dimension(900, 700));

        canvas.setOpaque(false);
        canvas.setPreferredSize(canvasPreferredSize());

        canvasScrollPane = new JScrollPane(canvas);
        canvasScrollPane.setBorder(null);
        canvasScrollPane.setOpaque(false);
        canvasScrollPane.getViewport().setOpaque(false);

        // Щоб було як у дизайні: без сірих смуг прокрутки.
        // Схему можна рухати трекпадом/колесом або Alt + перетягуванням.
        canvasScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        canvasScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        canvasScrollPane.getHorizontalScrollBar().setUnitIncrement(24);
        canvasScrollPane.getVerticalScrollBar().setUnitIncrement(24);
        canvasScrollPane.getHorizontalScrollBar().setBlockIncrement(140);
        canvasScrollPane.getVerticalScrollBar().setBlockIncrement(140);
        layer.add(canvasScrollPane, JLayeredPane.DEFAULT_LAYER);

        JPanel topFloating = createTopFloatingPanel();
        layer.add(topFloating, JLayeredPane.PALETTE_LAYER);

        JPanel bottomActions = createBottomActionsPanel();
        layer.add(bottomActions, JLayeredPane.PALETTE_LAYER);

        layer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = layer.getWidth();
                int h = layer.getHeight();

                int topW = 520;
                topFloating.setBounds((w - topW) / 2, 10, topW, 78);

                // Сітка починається нижче панелі масштабу, як на дизайні.
                canvasScrollPane.setBounds(0, 105, w, Math.max(100, h - 105));

                int bottomW = 540;
                bottomActions.setBounds((w - bottomW) / 2, Math.max(10, h - 72), bottomW, 58);
            }
        });

        return layer;
    }

    private int currentCellSize() {
        return Math.max(4, visualCell * zoom / 100);
    }

    private Dimension canvasPreferredSize() {
        int cell = currentCellSize();
        int gridW = COLS * cell;
        int gridH = ROWS * cell;
        return new Dimension(gridW + 180, gridH + 90);
    }

    private void refreshCanvasSize() {
        canvas.setPreferredSize(canvasPreferredSize());
        canvas.revalidate();
        canvas.repaint();
    }

    private JPanel createTopFloatingPanel() {
        RoundedPanel p = new RoundedPanel(18, CARD);
        p.setLayout(new GridBagLayout());
        p.setBorder(new EmptyBorder(14, 22, 14, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 8, 0, 8);
        gbc.fill = GridBagConstraints.VERTICAL;

        gbc.gridx = 0;
        p.add(controlGroup("Масштаб", minusPlusPanel("−", zoomValue, "+", () -> changeZoom(-25), () -> changeZoom(25))), gbc);
        gbc.gridx = 1;
        p.add(verticalDivider(), gbc);
        gbc.gridx = 2;
        p.add(controlGroup("Клітинка", cellSizeChooser()), gbc);
        gbc.gridx = 3;
        p.add(verticalDivider(), gbc);
        gbc.gridx = 4;
        p.add(symmetryIconButton(true), gbc);
        gbc.gridx = 5;
        p.add(symmetryIconButton(false), gbc);
        return p;
    }

    private JPanel controlGroup(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(90, 82, 74));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(label, BorderLayout.NORTH);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel minusPlusPanel(String minus, JLabel value, String plus, Runnable minusAction, Runnable plusAction) {
        JPanel p = new JPanel(new GridLayout(1, 3, 0, 0));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(132, 38));
        p.add(smallButton(minus, minusAction));
        value.setFont(new Font("Arial", Font.BOLD, 13));
        value.setForeground(TEXT);
        value.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, LINE));
        p.add(value);
        p.add(smallButton(plus, plusAction));
        return p;
    }

    private JComponent cellSizeChooser() {
        final JButton[] buttonRef = new JButton[1];

        JButton b = softButton(cellValue.getText(), () -> {
            String[] values = {
                    "6 × 6",
                    "8 × 8",
                    "10 × 10",
                    "12 × 12",
                    "15 × 15",
                    "18 × 18",
                    "20 × 20",
                    "24 × 24"
            };

            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    "Оберіть розмір клітинки",
                    "Клітинка",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    values,
                    visualCell + " × " + visualCell
            );

            if (selected != null) {
                int newSize = Integer.parseInt(selected.substring(0, selected.indexOf(" ")));
                visualCell = newSize;

                String newText = visualCell + " × " + visualCell + " ▾";
                cellValue.setText(newText);

                if (buttonRef[0] != null) {
                    buttonRef[0].setText(newText);
                }

                refreshCanvasSize();
            }
        });

        buttonRef[0] = b;
        b.setPreferredSize(new Dimension(120, 38));
        return b;
    }

    private JButton smallButton(String text, Runnable action) {
        JButton b = softButton(text, action);
        b.setFont(new Font("Arial", Font.BOLD, 17));
        return b;
    }

    private JComponent verticalDivider() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(1, 52));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, LINE));
        return p;
    }

    private JButton iconSquare(String text, String tip, Runnable action) {
        JButton b = softButton(text, action);
        b.setToolTipText(tip);
        b.setFont(new Font("Arial", Font.BOLD, 20));
        b.setPreferredSize(new Dimension(44, 38));
        return b;
    }

    private JButton symmetryIconButton(boolean vertical) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean active = vertical ? verticalSymmetry : horizontalSymmetry;

                int w = getWidth();
                int h = getHeight();

                g2.setColor(active ? new Color(255, 246, 244) : Color.WHITE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);

                g2.setColor(active ? RED : LINE);
                g2.setStroke(new BasicStroke(active ? 1.5f : 1.1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

                g2.setColor(new Color(73, 70, 67));
                g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cx = w / 2;
                int cy = h / 2;

                if (vertical) {
                    // Маленька іконка вертикальної симетрії
                    int lineTop = 9;
                    int lineBottom = h - 9;
                    int triW = 8;
                    int triH = 9;

                    g2.drawLine(cx, lineTop, cx, lineBottom);

                    Polygon left = new Polygon(
                            new int[]{cx - 16, cx - 8, cx - 16},
                            new int[]{cy - triH, cy, cy + triH},
                            3
                    );
                    Polygon right = new Polygon(
                            new int[]{cx + 16, cx + 8, cx + 16},
                            new int[]{cy - triH, cy, cy + triH},
                            3
                    );
                    g2.drawPolygon(left);
                    g2.drawPolygon(right);
                } else {
                    // Маленька іконка горизонтальної симетрії
                    int lineLeft = cx - 18;
                    int lineRight = cx + 18;
                    int triW = 9;

                    g2.drawLine(lineLeft, cy, lineRight, cy);

                    Polygon top = new Polygon(
                            new int[]{cx - triW, cx, cx + triW},
                            new int[]{cy - 15, cy - 7, cy - 15},
                            3
                    );
                    Polygon bottom = new Polygon(
                            new int[]{cx - triW, cx, cx + triW},
                            new int[]{cy + 15, cy + 7, cy + 15},
                            3
                    );
                    g2.drawPolygon(top);
                    g2.drawPolygon(bottom);
                }

                g2.dispose();
            }
        };

        b.setToolTipText(vertical ? "Вертикальна симетрія" : "Горизонтальна симетрія");
        b.setPreferredSize(new Dimension(54, 38));
        b.setMinimumSize(new Dimension(54, 38));
        b.setMaximumSize(new Dimension(54, 38));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            if (vertical) verticalSymmetry = !verticalSymmetry;
            else horizontalSymmetry = !horizontalSymmetry;
            refreshToolButtons();
            canvas.repaint();
        });
        return b;
    }

    private JPanel createLeftTools() {
        RoundedPanel box = new RoundedPanel(18, CARD);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(14, 12, 14, 12));
        box.setPreferredSize(new Dimension(150, 500));
        box.setMaximumSize(new Dimension(150, 500));

        toolsBox.setOpaque(false);
        toolsBox.setLayout(new BoxLayout(toolsBox, BoxLayout.Y_AXIS));

        addToolButton("cursor", "Вибір", Tool.SELECT);
        addToolButton("pencil", "Олівець", Tool.PENCIL);
        addToolButton("eraser", "Ластик", Tool.ERASER);
        addToolButton("line", "Лінія", Tool.LINE);
        addToolButton("symmetry", "Симетрія", null);

        toolsBox.add(Box.createVerticalStrut(6));
        toolsBox.add(separator());
        toolsBox.add(Box.createVerticalStrut(6));

        addSideAction("undo", "Відмінити", this::undo);
        addSideAction("redo", "Повернути", this::redo);
        addSideAction("zoom_in", "Збільшити", () -> changeZoom(25));
        addSideAction("zoom_out", "Зменшити", () -> changeZoom(-25));

        box.add(toolsBox);
        return box;
    }

    private void addToolButton(String iconName, String label, Tool tool) {
        JButton b = sideButton(iconName, label, () -> {
            if (tool == null) {
                boolean newState = !(verticalSymmetry || horizontalSymmetry);
                verticalSymmetry = newState;
                horizontalSymmetry = newState;
            } else {
                activeTool = tool;
                pendingStamp = null;
                pendingStampName = null;
                pendingStampReusable = false;
                selectedOrnamentBase = null;
                selectedOrnamentName = null;
                updateCanvasCursor();
            }
            refreshToolButtons();
            canvas.repaint();
        });
        b.putClientProperty("tool", tool);
        toolsBox.add(b);
        toolsBox.add(Box.createVerticalStrut(6));
        refreshToolButton(b);
    }

    private void updateCanvasCursor() {
        // Під час створення ConstructorPanel поле canvas ще може бути не готове.
        // Через це без перевірки програма падала при запуску.
        if (canvas == null) {
            return;
        }

        if (activeTool == Tool.SELECT) {
            canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else {
            canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    private void addSideAction(String iconName, String label, Runnable action) {
        toolsBox.add(sideButton(iconName, label, action));
        toolsBox.add(Box.createVerticalStrut(6));
    }

    private JButton sideButton(String iconName, String label, Runnable action) {
        JButton b = new JButton(label, loadToolIcon("tool_" + iconName + ".png")) {
            @Override
            protected void paintComponent(Graphics g) {
                Object t = getClientProperty("tool");
                boolean active = (t != null && t == activeTool)
                        || (t == null && label.equals("Симетрія") && (verticalSymmetry || horizontalSymmetry));

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(active ? new Color(250, 244, 237) : new Color(0, 0, 0, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                if (active) {
                    g2.setColor(RED);
                    g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setForeground(new Color(77, 72, 68));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setIconTextGap(12);
        b.setMaximumSize(new Dimension(132, 38));
        b.setPreferredSize(new Dimension(132, 38));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run());
        return b;
    }

    private ImageIcon loadToolIcon(String fileName) {
        try {
            BufferedImage img = null;

            String[] possibleNames = possibleToolIconNames(fileName);

            for (String name : possibleNames) {
                java.net.URL url = getClass().getResource("/icons/" + name);

                // У тебе іконки лежать прямо в resources, тому додатково шукаємо /tool_pencil.png
                if (url == null) {
                    url = getClass().getResource("/" + name);
                }

                if (url != null) {
                    img = ImageIO.read(url);
                    break;
                }
            }

            // Додатково для IntelliJ, якщо resources ще не підхопився як Resource Root.
            if (img == null) {
                for (String name : possibleNames) {
                    File f = new File("src/main/resources/icons/" + name);

                    // У тебе іконки лежать прямо тут: src/main/resources/
                    if (!f.exists()) {
                        f = new File("src/main/resources/" + name);
                    }

                    if (f.exists()) {
                        img = ImageIO.read(f);
                        break;
                    }
                }
            }

            // Якщо назви файлів у тебе інші — шукаємо будь-яку картинку в папці icons
            // за ключовими словами: pencil/olivets/олівець тощо.
            if (img == null) {
                File found = findIconFile(fileName);
                if (found != null) {
                    img = ImageIO.read(found);
                }
            }

            if (img == null) {
                return fallbackToolIcon(fileName);
            }

            img = removeWhiteBackgroundAndCrop(img);

            BufferedImage fitted = new BufferedImage(22, 22, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fitted.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int size = Math.min(22, Math.max(1, Math.min(img.getWidth(), img.getHeight())));
            double scale = Math.min(22.0 / img.getWidth(), 22.0 / img.getHeight());
            int w = Math.max(1, (int) Math.round(img.getWidth() * scale));
            int h = Math.max(1, (int) Math.round(img.getHeight() * scale));
            int x = (22 - w) / 2;
            int y = (22 - h) / 2;

            g2.drawImage(img, x, y, w, h, null);
            g2.dispose();

            return new ImageIcon(fitted);
        } catch (Exception e) {
            return fallbackToolIcon(fileName);
        }
    }

    private File findIconFile(String fileName) {
        File[] dirs = {
                new File("src/main/resources/icons"),
                new File("src/main/resources"),
                new File("resources/icons"),
                new File("resources"),
                new File("icons")
        };

        String[] keys = iconSearchKeys(fileName);

        for (File iconsDir : dirs) {
            if (!iconsDir.exists()) continue;

            File[] files = iconsDir.listFiles((dir, name) -> {
                String n = name.toLowerCase(Locale.ROOT);
                return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".webp");
            });

            if (files == null) continue;

            for (String key : keys) {
                for (File f : files) {
                    String n = f.getName().toLowerCase(Locale.ROOT);
                    if (n.contains(key.toLowerCase(Locale.ROOT))) {
                        return f;
                    }
                }
            }
        }

        return null;
    }

    private String[] iconSearchKeys(String fileName) {
        if (fileName.contains("cursor")) {
            return new String[]{"cursor", "select", "choice", "arrow", "pointer", "vybir", "vibir", "вибір", "курсор", "стрілка"};
        }
        if (fileName.contains("pencil")) {
            return new String[]{"pencil", "pen", "brush", "olivets", "oliv", "олівець", "карандаш", "пензлик"};
        }
        if (fileName.contains("eraser")) {
            return new String[]{"eraser", "rubber", "lastik", "ластик", "гумка"};
        }
        if (fileName.contains("line")) {
            return new String[]{"line", "linia", "лінія", "линия"};
        }
        if (fileName.contains("symmetry")) {
            return new String[]{"symmetry", "mirror", "simetriya", "sym", "симетрія", "зеркало"};
        }
        if (fileName.contains("undo")) {
            return new String[]{"undo", "back", "vidmin", "відмінити", "назад"};
        }
        if (fileName.contains("redo")) {
            return new String[]{"redo", "return", "refresh", "povern", "повернути", "вперед"};
        }
        if (fileName.contains("zoom_in")) {
            return new String[]{"zoom_in", "zoomin", "zoom-plus", "plus", "increase", "збільшити"};
        }
        if (fileName.contains("zoom_out")) {
            return new String[]{"zoom_out", "zoomout", "zoom-minus", "minus", "decrease", "зменшити"};
        }
        return new String[]{fileName};
    }

    private String[] possibleToolIconNames(String fileName) {
        switch (fileName) {
            case "tool_cursor.png":
                return new String[]{"tool_cursor.png", "01_cursor.png", "cursor.png", "select.png", "tool_select.png", "vybir.png", "vibir.png"};
            case "tool_pencil.png":
                return new String[]{"tool_pencil.png", "02_pencil.png", "pencil.png", "pen.png", "brush.png", "olivets.png"};
            case "tool_eraser.png":
                return new String[]{"tool_eraser.png", "03_eraser.png", "eraser.png", "rubber.png", "lastik.png"};
            case "tool_line.png":
                return new String[]{"tool_line.png", "04_line.png", "line.png", "linia.png"};
            case "tool_symmetry.png":
                return new String[]{"tool_symmetry.png", "05_symmetry.png", "symmetry.png", "mirror.png", "simetriya.png"};
            case "tool_undo.png":
                return new String[]{"tool_undo.png", "06_undo.png", "undo.png", "back.png", "vidminyty.png"};
            case "tool_redo.png":
                return new String[]{"tool_redo.png", "07_redo_refresh.png", "redo.png", "refresh.png", "povernuty.png"};
            case "tool_zoom_in.png":
                return new String[]{"tool_zoom_in.png", "08_zoom_in.png", "zoom_in.png", "zoomin.png", "zoom-plus.png", "zoom_plus.png"};
            case "tool_zoom_out.png":
                return new String[]{"tool_zoom_out.png", "09_zoom_out.png", "zoom_out.png", "zoomout.png", "zoom-minus.png", "zoom_minus.png"};
            default:
                return new String[]{fileName};
        }
    }

    private ImageIcon fallbackToolIcon(String fileName) {
        // Якщо файл іконки не знайдено, нічого не малюємо.
        // Так на лівій панелі не з’являються старі намальовані іконки.
        BufferedImage img = new BufferedImage(22, 22, BufferedImage.TYPE_INT_ARGB);
        return new ImageIcon(img);
    }

    private void refreshToolButtons() {
        for (Component c : toolsBox.getComponents()) c.repaint();
    }

    private void refreshToolButton(JButton b) { b.repaint(); }

    private JComponent separator() {
        JSeparator s = new JSeparator();
        s.setMaximumSize(new Dimension(100, 1));
        s.setForeground(LINE);
        return s;
    }

    private JPanel createPalettePanel() {
        RoundedPanel p = new RoundedPanel(18, CARD);
        p.setPreferredSize(new Dimension(250, 158));
        p.setLayout(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel title = new JLabel("Палітра кольорів");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(new Color(80, 70, 62));
        p.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 4, 14, 12));
        grid.setOpaque(false);

        // 4 базові кольори як у дизайні: білий, бежевий, коричневий, темно-коричневий
        Color[] baseColors = {
                Color.WHITE,
                new Color(246, 236, 222),
                new Color(190, 154, 116),
                new Color(116, 70, 38)
        };

        for (Color c : baseColors) {
            grid.add(colorCircle(c));
        }

        // 4 пусті комірки з плюсом для власних кольорів
        for (int i = 0; i < customColors.length; i++) {
            grid.add(customColorSlot(i));
        }

        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JComponent colorCircle(Color c) {
        JComponent comp = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                g2.setColor(new Color(220, 214, 207));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawOval(2, 2, getWidth() - 5, getHeight() - 5);
                if (selectedColor.equals(c)) {
                    g2.setColor(RED);
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                }
                g2.dispose();
            }
        };
        comp.setPreferredSize(new Dimension(38, 38));
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        comp.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                selectedColor = c;
                repaint();
            }
        });
        return comp;
    }

    private JComponent customColorSlot(int index) {
        JComponent comp = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = customColors[index];

                if (color == null) {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(3, 3, getWidth() - 6, getHeight() - 6);

                    g2.setColor(new Color(215, 207, 198));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(3, 3, getWidth() - 7, getHeight() - 7);

                    g2.setColor(new Color(120, 110, 100));
                    g2.setFont(new Font("Arial", Font.BOLD, 20));
                    FontMetrics fm = g2.getFontMetrics();
                    String plus = "+";
                    int x = (getWidth() - fm.stringWidth(plus)) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(plus, x, y);
                } else {
                    g2.setColor(color);
                    g2.fillOval(3, 3, getWidth() - 6, getHeight() - 6);

                    g2.setColor(new Color(215, 207, 198));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(3, 3, getWidth() - 7, getHeight() - 7);

                    if (selectedColor.equals(color)) {
                        g2.setColor(RED);
                        g2.setStroke(new BasicStroke(3f));
                        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                    }
                }

                g2.dispose();
            }
        };

        comp.setPreferredSize(new Dimension(38, 38));
        comp.setToolTipText("Клік — додати/вибрати колір. Два пальці або правий клік — замінити колір.");
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleCustomColorClick(e, index);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    handleCustomColorClick(e, index);
                }
            }
        };
        comp.addMouseListener(adapter);

        return comp;
    }

    private void handleCustomColorClick(MouseEvent e, int index) {
        boolean replaceClick = SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger() || e.isControlDown();

        // Порожній кружечок: звичайний клік додає колір.
        // Заповнений кружечок: звичайний клік вибирає колір, два пальці/ПКМ замінює.
        if (customColors[index] == null || replaceClick) {
            Color startColor = customColors[index] == null ? selectedColor : customColors[index];
            Color newColor = JColorChooser.showDialog(
                    ConstructorPanel.this,
                    customColors[index] == null ? "Додати колір у палітру" : "Замінити колір у палітрі",
                    startColor
            );

            if (newColor != null) {
                customColors[index] = newColor;
                selectedColor = newColor;
                repaint();
            }
        } else {
            selectedColor = customColors[index];
            repaint();
        }
    }

    private JPanel createBottomActionsPanel() {
        RoundedPanel p = new RoundedPanel(18, CARD);
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 10));

        p.add(bottomAction("bottom_clear.png", "Очистити", this::clearCanvas));
        p.add(bottomAction("bottom_duplicate.png", "Дублювати", this::duplicatePattern));
        p.add(bottomAction("bottom_align_center.png", "Вирівняти по центру", this::centerPattern));

        return p;
    }

    private JButton bottomAction(String iconName, String text, Runnable action) {
        JButton b = new JButton(text, loadBottomActionIcon(iconName));
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setForeground(new Color(90, 82, 74));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setIconTextGap(8);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run());
        return b;
    }

    private ImageIcon loadBottomActionIcon(String fileName) {
        try {
            BufferedImage img = null;

            String[] possibleNames = {
                    fileName,
                    "bottom_icons/" + fileName,
                    "icons/" + fileName
            };

            for (String name : possibleNames) {
                java.net.URL url = getClass().getResource("/" + name);
                if (url != null) {
                    img = ImageIO.read(url);
                    break;
                }
            }

            if (img == null) {
                for (String name : possibleNames) {
                    File f = new File("src/main/resources/" + name);
                    if (f.exists()) {
                        img = ImageIO.read(f);
                        break;
                    }
                }
            }

            if (img == null) {
                return new ImageIcon(new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB));
            }

            img = removeWhiteBackgroundAndCrop(img);

            BufferedImage fitted = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fitted.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            double scale = Math.min(18.0 / img.getWidth(), 18.0 / img.getHeight());
            int w = Math.max(1, (int) Math.round(img.getWidth() * scale));
            int h = Math.max(1, (int) Math.round(img.getHeight() * scale));
            int x = (18 - w) / 2;
            int y = (18 - h) / 2;

            g2.drawImage(img, x, y, w, h, null);
            g2.dispose();

            return new ImageIcon(fitted);
        } catch (Exception e) {
            return new ImageIcon(new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB));
        }
    }

    private JPanel createRightSidebar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setPreferredSize(new Dimension(370, 0));

        JPanel side = new JPanel();
        side.setOpaque(false);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(0, 4, 0, 4));

        side.add(sectionNameBuilder());
        side.add(Box.createVerticalStrut(16));
        side.add(sectionOrnaments());
        side.add(Box.createVerticalStrut(16));
        side.add(sectionLetters());
        side.add(Box.createVerticalStrut(16));
        side.add(sectionExport());
        side.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(side);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    private JPanel sectionNameBuilder() {
        RoundedPanel p = cardSection("Додати ім’я", "Схема одразу відкриється на полотні", null);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputWrap = new RoundedPanel(14, new Color(255, 252, 248));
        inputWrap.setLayout(new BorderLayout(10, 0));
        inputWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(238, 229, 218), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        inputWrap.setPreferredSize(new Dimension(320, 44));
        inputWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        inputWrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel inputIcon = new JLabel("✦");
        inputIcon.setFont(new Font("Serif", Font.BOLD, 18));
        inputIcon.setForeground(RED);

        JTextField nameField = new JTextField("Анна");
        nameField.setFont(new Font("Arial", Font.BOLD, 15));
        nameField.setForeground(TEXT);
        nameField.setOpaque(false);
        nameField.setBorder(null);
        nameField.addActionListener(e -> addNameFromField(nameField));

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 11));
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(RED);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> addNameFromField(nameField));

        inputWrap.add(inputIcon, BorderLayout.WEST);
        inputWrap.add(nameField, BorderLayout.CENTER);
        inputWrap.add(okButton, BorderLayout.EAST);

        JLabel readyLabel = new JLabel("Готові імена");
        readyLabel.setFont(new Font("Arial", Font.BOLD, 12));
        readyLabel.setForeground(new Color(120, 108, 99));
        readyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel quick = new JPanel(new GridLayout(2, 3, 8, 8));
        quick.setOpaque(false);
        quick.setPreferredSize(new Dimension(320, 76));
        quick.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        quick.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] examples = {"Анна", "Марія", "Оксана", "Софія", "Андрій", "Артем"};
        for (String name : examples) {
            quick.add(nameChip(name, () -> {
                nameField.setText(name);
                addNameFromField(nameField);
            }));
        }

        box.add(inputWrap);
        box.add(Box.createVerticalStrut(10));
        box.add(readyLabel);
        box.add(Box.createVerticalStrut(7));
        box.add(quick);

        p.add(box, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(360, 255));
        p.setPreferredSize(new Dimension(360, 255));
        return p;
    }

    private void loadDefaultNamePattern() {
        SwingUtilities.invokeLater(() -> {
            if (!isCanvasEmpty()) return;

            NamePatternRepository.NamePattern anna = NamePatternRepository.findByName("Анна");
            if (anna != null) {
                loadNamePattern(anna);
            } else {
                loadNamePattern(new NamePatternRepository.NamePattern("Анна", buildNameFromLetters("Анна")));
            }
        });
    }

    private void addNameFromField(JTextField field) {
        String name = field.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введіть ім’я.", "Додавання імені", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NamePatternRepository.NamePattern exact = NamePatternRepository.findByName(name);
        if (exact != null) {
            loadNamePattern(exact);
            return;
        }

        int[][] generated = buildNameFromLetters(name);
        loadNamePattern(new NamePatternRepository.NamePattern(name, generated));
        JOptionPane.showMessageDialog(
                this,
                "Точної схеми для імені ‘" + name + "’ немає у базі, тому я створила ім’я з літер.\nЙого можна редагувати на сітці.",
                "Ім’я додано",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private JPanel sectionOrnaments() {
        RoundedPanel p = cardSection("Орнаменти", "обери орнамент - обери розмір - став на сітку", null);

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        JPanel row = new JPanel(new GridLayout(2, 4, 10, 10));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 138));

        List<boolean[][]> patterns = Arrays.asList(
                diamondPattern(),
                flowerPattern(),
                starPattern(),
                smallCrossPattern(),
                wavePattern(),
                leafPattern(),
                sunPattern(),
                borderPattern()
        );

        String[] labels = {"Ромб", "Квітка", "Зірка", "Хрест", "Хвиля", "Листок", "Сонце", "Бордюр"};

        JPanel sizeRow = new JPanel(new BorderLayout(10, 0));
        sizeRow.setOpaque(false);
        sizeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel sizeLabel = new JLabel("Розмір");
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sizeLabel.setForeground(new Color(120, 108, 99));

        JPanel sizeButtons = new JPanel(new GridLayout(1, 4, 6, 0));
        sizeButtons.setOpaque(false);
        sizeButtons.setPreferredSize(new Dimension(210, 30));

        JButton smallSize = sizeChoiceButton("S", 1);
        JButton mediumSize = sizeChoiceButton("M", 2);
        JButton largeSize = sizeChoiceButton("L", 3);
        JButton extraLargeSize = sizeChoiceButton("XL", 4);

        sizeButtons.add(smallSize);
        sizeButtons.add(mediumSize);
        sizeButtons.add(largeSize);
        sizeButtons.add(extraLargeSize);
        refreshSizeButtons(sizeButtons, selectedOrnamentScale);

        sizeRow.add(sizeLabel, BorderLayout.WEST);
        sizeRow.add(sizeButtons, BorderLayout.EAST);

        for (int i = 0; i < patterns.size(); i++) {
            boolean[][] pattern = patterns.get(i);
            String label = labels[i];

            row.add(patternButton(pattern, label, () -> selectOrnament(pattern, label)));
        }

        wrap.add(row);
        wrap.add(Box.createVerticalStrut(10));
        wrap.add(sizeRow);

        p.add(wrap, BorderLayout.CENTER);
        return p;
    }



    private JButton sizeChoiceButton(String label, int scale) {
        JButton b = new JButton(label);
        b.setFont(new Font("Arial", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setBorderPainted(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            selectedOrnamentScale = scale;
            if (selectedOrnamentBase != null) {
                pendingStamp = scaleOrnament(selectedOrnamentBase, selectedOrnamentScale);
                pendingStampName = selectedOrnamentName;
                pendingStampReusable = true;
                activeTool = Tool.PENCIL;
                refreshToolButtons();
                updateCanvasCursor();
                canvas.repaint();
            }
            refreshSizeButtons((JPanel) b.getParent(), selectedOrnamentScale);
        });
        b.putClientProperty("scale", scale);
        return b;
    }

    private void refreshSizeButtons(JPanel panel, int selectedScale) {
        for (Component component : panel.getComponents()) {
            if (!(component instanceof JButton)) continue;

            JButton button = (JButton) component;
            Object value = button.getClientProperty("scale");
            boolean selected = value instanceof Integer && ((Integer) value) == selectedScale;

            button.setForeground(selected ? RED : new Color(112, 96, 86));
            button.setBackground(selected ? new Color(255, 238, 238) : new Color(255, 250, 246));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(selected ? RED : new Color(238, 221, 215), selected ? 2 : 1),
                    new EmptyBorder(selected ? 4 : 5, 8, selected ? 4 : 5, 8)
            ));
        }
    }

    private boolean[][] scaleOrnament(boolean[][] source, int scale) {
        if (scale <= 1) return source;

        int rows = source.length * scale;
        int cols = source[0].length * scale;
        boolean[][] result = new boolean[rows][cols];

        for (int r = 0; r < source.length; r++) {
            for (int c = 0; c < source[r].length; c++) {
                if (source[r][c]) {
                    for (int rr = 0; rr < scale; rr++) {
                        for (int cc = 0; cc < scale; cc++) {
                            result[r * scale + rr][c * scale + cc] = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    private JPanel sectionLetters() {
        RoundedPanel p = cardSection("Літери та цифри", "Натисни символ і постав його на полотні", null);

        JPanel grid = new JPanel(new GridLayout(2, 5, 9, 9));
        grid.setOpaque(false);
        String[] chars = {"А", "Б", "В", "Г", "Д", "1", "2", "3", "4", "5"};
        for (String ch : chars) {
            grid.add(letterButton(ch));
        }

        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JPanel sectionExport() {
        RoundedPanel p = cardSection("Експорт", "Збереження готової схеми", null);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        list.add(exportButton("export_save.png", "Зберегти проект", "збережеться у вкладці Моя колекція", this::saveProjectToCollection));
        list.add(Box.createVerticalStrut(9));
        list.add(exportButton("export_file.png", "Експортувати схему", "PNG файл для перегляду або друку", this::exportPNG));
        list.add(Box.createVerticalStrut(9));
        list.add(exportButton("export_file.png", "Відкрити PNG", "імпорт PNG назад на сітку", this::importPNG));

        p.add(list, BorderLayout.CENTER);
        return p;
    }

    private RoundedPanel cardSection(String title, String subtitle, String action) {
        RoundedPanel p = new RoundedPanel(24, CARD);
        p.setLayout(new BorderLayout(0, 14));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(236, 229, 221), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        p.setMaximumSize(new Dimension(360, 1000));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel head = new JPanel(new BorderLayout(12, 0));
        head.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel h = new JLabel("⌄  " + title);
        h.setFont(new Font("Arial", Font.BOLD, 18));
        h.setForeground(new Color(66, 58, 52));

        JLabel s = new JLabel(subtitle == null ? " " : subtitle);
        s.setFont(new Font("Arial", Font.PLAIN, 11));
        s.setForeground(new Color(155, 145, 136));

        titleBox.add(h);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(s);
        head.add(titleBox, BorderLayout.WEST);

        if (action != null) {
            JButton a = new JButton(action);
            a.setFont(new Font("Arial", Font.BOLD, 12));
            a.setForeground(RED);
            a.setContentAreaFilled(false);
            a.setFocusPainted(false);
            a.setBorderPainted(false);
            a.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            a.addActionListener(e -> JOptionPane.showMessageDialog(this, action));
            head.add(a, BorderLayout.EAST);
        }

        p.add(head, BorderLayout.NORTH);
        return p;
    }

    private JButton primarySidebarButton(String text, Runnable action) {
        JButton b = new JButton(text + "  →");
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(RED);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        b.addActionListener(e -> action.run());
        return b;
    }

    private JButton nameChip(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 11));
        b.setForeground(new Color(82, 72, 64));
        b.setBackground(new Color(255, 248, 244));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(239, 221, 216), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run());
        return b;
    }

    private JButton patternButton(boolean[][] pattern, String label, Runnable action) {
        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 252, 248));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(235, 226, 216));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                drawMiniPattern(g2, pattern, 8, 6, getWidth() - 16, getHeight() - 24, RED);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Arial", Font.BOLD, 9));
                g2.setColor(new Color(116, 105, 95));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, (getWidth() - fm.stringWidth(label)) / 2, getHeight() - 8);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(72, 62));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run());
        return b;
    }

    private JButton letterButton(String ch) {
        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(255, 252, 248));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(new Color(235, 226, 216));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                drawStitchedChar(g2, ch, getWidth(), getHeight());
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(58, 50));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> selectStamp(letterPattern(ch), "Літера " + ch));
        return b;
    }

    private void drawStitchedChar(Graphics2D g2, String ch, int w, int h) {
        BufferedImage img = new BufferedImage(22, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = img.createGraphics();
        ig.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        ig.setColor(Color.BLACK);
        ig.setFont(new Font("Serif", Font.BOLD, 24));
        FontMetrics fm = ig.getFontMetrics();
        ig.drawString(ch, (22 - fm.stringWidth(ch)) / 2, 20);
        ig.dispose();

        int cell = 3;
        int ox = (w - 22 * cell) / 2;
        int oy = (h - 24 * cell) / 2;
        g2.setColor(RED);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if ((img.getRGB(x, y) >>> 24) > 0) {
                    g2.fillRect(ox + x * cell, oy + y * cell, Math.max(1, cell - 1), Math.max(1, cell - 1));
                }
            }
        }
    }

    private JButton exportButton(String iconFileName, String title, String sub, Runnable action) {
        ImageIcon icon = loadExportIcon(iconFileName);

        JButton b = new JButton(
                "<html><b>" + title + "</b><br>" +
                        "<span style='font-size:10px;color:#8f877f'>" + sub + "</span></html>",
                icon
        );

        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setIconTextGap(13);
        b.setForeground(new Color(86, 78, 70));
        b.setBackground(new Color(255, 252, 248));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 226, 216), 1),
                new EmptyBorder(11, 14, 11, 14)
        ));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        b.setPreferredSize(new Dimension(318, 62));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.addActionListener(e -> action.run());
        return b;
    }

    private ImageIcon loadExportIcon(String fileName) {
        try {
            BufferedImage img = null;

            java.net.URL url = getClass().getResource("/icons/" + fileName);
            if (url != null) {
                img = ImageIO.read(url);
            }

            // Додатковий шлях для IntelliJ, якщо resources ще не підхопився як Resource Root
            if (img == null) {
                File f = new File("src/main/resources/icons/" + fileName);
                if (f.exists()) {
                    img = ImageIO.read(f);
                }
            }

            if (img == null) {
                return fallbackExportIcon(fileName);
            }

            img = cropWhiteMargins(img);

            Image scaled = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return fallbackExportIcon(fileName);
        }
    }

    private BufferedImage cropWhiteMargins(BufferedImage img) {
        return removeWhiteBackgroundAndCrop(img);
    }

    private BufferedImage removeWhiteBackgroundAndCrop(BufferedImage source) {
        BufferedImage transparent = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

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

                // Прибираємо білий/майже білий фон із фото іконки.
                boolean whiteBackground = a < 20 || (r > 235 && g > 235 && b > 235);

                if (whiteBackground) {
                    transparent.setRGB(x, y, 0x00000000);
                } else {
                    transparent.setRGB(x, y, argb);
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return transparent;
        }

        int padding = 1;
        minX = Math.max(0, minX - padding);
        minY = Math.max(0, minY - padding);
        maxX = Math.min(transparent.getWidth() - 1, maxX + padding);
        maxY = Math.min(transparent.getHeight() - 1, maxY + padding);

        BufferedImage cropped = transparent.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);

        BufferedImage copy = new BufferedImage(cropped.getWidth(), cropped.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = copy.createGraphics();
        g2.drawImage(cropped, 0, 0, null);
        g2.dispose();

        return copy;
    }

    private ImageIcon fallbackExportIcon(String fileName) {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(72, 68, 64));
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (fileName.contains("save")) {
            g2.drawRoundRect(4, 3, 16, 18, 2, 2);
            g2.drawLine(7, 3, 7, 9);
            g2.drawLine(7, 9, 16, 9);
            g2.drawRect(8, 14, 8, 5);
        } else if (fileName.contains("file")) {
            g2.drawRoundRect(6, 3, 12, 18, 2, 2);
            g2.drawLine(14, 3, 18, 7);
            g2.drawLine(18, 7, 14, 7);
            g2.drawLine(9, 13, 15, 13);
            g2.drawLine(12, 10, 12, 16);
        } else {
            g2.drawOval(4, 9, 5, 5);
            g2.drawOval(16, 4, 5, 5);
            g2.drawOval(16, 15, 5, 5);
            g2.drawLine(9, 11, 16, 7);
            g2.drawLine(9, 13, 16, 17);
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    private JButton softButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setForeground(new Color(74, 66, 58));
        b.setBackground(CARD_SOFT);
        b.setBorder(BorderFactory.createLineBorder(LINE));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run());
        return b;
    }

    private void changeZoom(int delta) {
        zoom = Math.max(50, Math.min(300, zoom + delta));
        zoomValue.setText(zoom + "%");
        refreshCanvasSize();
    }

    private void saveState() {
        undoStack.push(copyCells());
        while (undoStack.size() > 40) undoStack.removeLast();
        redoStack.clear();
    }

    private Color[][] copyCells() {
        Color[][] copy = new Color[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) System.arraycopy(cells[r], 0, copy[r], 0, COLS);
        return copy;
    }

    private void restore(Color[][] state) {
        for (int r = 0; r < ROWS; r++) System.arraycopy(state[r], 0, cells[r], 0, COLS);
        repaintAll();
    }

    private void undo() {
        if (undoStack.size() <= 1) return;
        redoStack.push(undoStack.pop());
        restore(undoStack.peek());
    }

    private void redo() {
        if (redoStack.isEmpty()) return;
        Color[][] state = redoStack.pop();
        undoStack.push(state);
        restore(state);
    }

    private void clearCanvas() {
        int answer = JOptionPane.showConfirmDialog(this, "Ви точно хочете очистити полотно?", "Очистити", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        for (int r = 0; r < ROWS; r++) Arrays.fill(cells[r], null);
        saveState();
        repaintAll();
    }

    private void repaintAll() {
        canvas.repaint();
    }

    private void drawCell(int r, int c, Color color) {
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS) return;
        cells[r][c] = color;

        if (verticalSymmetry) {
            cells[r][COLS - 1 - c] = color;
        }
        if (horizontalSymmetry) {
            cells[ROWS - 1 - r][c] = color;
        }
        if (verticalSymmetry && horizontalSymmetry) {
            cells[ROWS - 1 - r][COLS - 1 - c] = color;
        }
    }

    private void fill(int sr, int sc) {
        Color target = cells[sr][sc];
        if (Objects.equals(target, selectedColor)) return;
        saveState();
        boolean[][] seen = new boolean[ROWS][COLS];
        ArrayDeque<Point> q = new ArrayDeque<>();
        q.add(new Point(sc, sr));
        seen[sr][sc] = true;
        while (!q.isEmpty()) {
            Point p = q.removeFirst();
            drawCell(p.y, p.x, selectedColor);
            int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] d : dirs) {
                int nr = p.y + d[1], nc = p.x + d[0];
                if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && !seen[nr][nc] && Objects.equals(cells[nr][nc], target)) {
                    seen[nr][nc] = true;
                    q.add(new Point(nc, nr));
                }
            }
        }
        repaintAll();
    }

    private void drawLine(Point a, Point b, Color color) {
        int x0 = a.x, y0 = a.y, x1 = b.x, y1 = b.y;
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            drawCell(y0, x0, color);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx) { err += dx; y0 += sy; }
        }
    }

    private void duplicatePattern() {
        if (isCanvasEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Спочатку намалюйте або поставте основний фрагмент орнаменту.",
                    "Дублювання",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] options = {
                "Вертикальна симетрія",
                "Горизонтальна симетрія",
                "Обидві симетрії",
                "Дублювати праворуч"
        };

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Оберіть спосіб автоматичного дублювання фрагмента:",
                "Дублювання орнаменту",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected == null) return;

        saveState();
        Color[][] source = copyCells();

        if (selected.equals(options[0])) {
            duplicateWithVerticalSymmetry(source);
            verticalSymmetry = true;
        } else if (selected.equals(options[1])) {
            duplicateWithHorizontalSymmetry(source);
            horizontalSymmetry = true;
        } else if (selected.equals(options[2])) {
            duplicateWithVerticalSymmetry(source);
            duplicateWithHorizontalSymmetry(source);
            duplicateWithBothSymmetries(source);
            verticalSymmetry = true;
            horizontalSymmetry = true;
        } else {
            duplicateFragmentToRight(source);
        }

        refreshToolButtons();
        repaintAll();
    }

    private void duplicateWithVerticalSymmetry(Color[][] source) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (source[r][c] != null) {
                    cells[r][COLS - 1 - c] = source[r][c];
                }
            }
        }
    }

    private void duplicateWithHorizontalSymmetry(Color[][] source) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (source[r][c] != null) {
                    cells[ROWS - 1 - r][c] = source[r][c];
                }
            }
        }
    }

    private void duplicateWithBothSymmetries(Color[][] source) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (source[r][c] != null) {
                    cells[ROWS - 1 - r][COLS - 1 - c] = source[r][c];
                }
            }
        }
    }

    private void duplicateFragmentToRight(Color[][] source) {
        Rectangle bounds = findFilledBounds(source);
        if (bounds == null) return;

        int offset = bounds.width + 2;
        boolean copied = false;
        while (bounds.x + offset + bounds.width <= COLS) {
            for (int r = bounds.y; r < bounds.y + bounds.height; r++) {
                for (int c = bounds.x; c < bounds.x + bounds.width; c++) {
                    if (source[r][c] != null) {
                        int nc = c + offset;
                        if (nc >= 0 && nc < COLS) {
                            cells[r][nc] = source[r][c];
                            copied = true;
                        }
                    }
                }
            }
            offset += bounds.width + 2;
        }

        if (!copied) {
            JOptionPane.showMessageDialog(
                    this,
                    "Праворуч замало місця. Спробуйте вирівняти фрагмент лівіше або оберіть симетрію.",
                    "Дублювання",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private Rectangle findFilledBounds(Color[][] source) {
        int minR = ROWS, minC = COLS, maxR = -1, maxC = -1;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (source[r][c] != null) {
                    minR = Math.min(minR, r);
                    minC = Math.min(minC, c);
                    maxR = Math.max(maxR, r);
                    maxC = Math.max(maxC, c);
                }
            }
        }
        if (maxR < 0) return null;
        return new Rectangle(minC, minR, maxC - minC + 1, maxR - minR + 1);
    }

    private void centerPattern() {
        int minR = ROWS, minC = COLS, maxR = -1, maxC = -1;
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) if (cells[r][c] != null) {
            minR = Math.min(minR, r); minC = Math.min(minC, c); maxR = Math.max(maxR, r); maxC = Math.max(maxC, c);
        }
        if (maxR < 0) return;
        saveState();
        Color[][] old = copyCells();
        for (int r = 0; r < ROWS; r++) Arrays.fill(cells[r], null);
        int h = maxR - minR + 1, w = maxC - minC + 1;
        int offR = (ROWS - h) / 2 - minR;
        int offC = (COLS - w) / 2 - minC;
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            if (old[r][c] != null) {
                int nr = r + offR, nc = c + offC;
                if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS) cells[nr][nc] = old[r][c];
            }
        }
        repaintAll();
    }

    private void selectStamp(boolean[][] pattern, String name) {
        pendingStamp = pattern;
        pendingStampColor = selectedColor;
        pendingStampName = name;
        pendingStampReusable = false;
        activeTool = Tool.PENCIL;
        refreshToolButtons();
        updateCanvasCursor();
        canvas.repaint();
    }

    private void selectOrnament(boolean[][] pattern, String name) {
        selectedOrnamentBase = pattern;
        selectedOrnamentName = name;
        pendingStamp = scaleOrnament(pattern, selectedOrnamentScale);
        pendingStampColor = selectedColor;
        pendingStampName = name;
        pendingStampReusable = true;
        activeTool = Tool.PENCIL;
        refreshToolButtons();
        updateCanvasCursor();
        canvas.repaint();
    }

    private void placePendingStamp(Point cell) {
        if (pendingStamp == null || cell == null) return;

        saveState();

        // Ставимо елемент так, щоб клік був приблизно в центрі обраного орнаменту/літери
        int startR = cell.y - pendingStamp.length / 2;
        int startC = cell.x - pendingStamp[0].length / 2;

        for (int r = 0; r < pendingStamp.length; r++) {
            for (int c = 0; c < pendingStamp[r].length; c++) {
                if (pendingStamp[r][c]) {
                    drawCell(startR + r, startC + c, pendingStampColor);
                }
            }
        }

        if (!pendingStampReusable) {
            pendingStamp = null;
            pendingStampName = null;
        }

        repaintAll();
    }

    private boolean[][] letterPattern(String ch) {
        BufferedImage img = new BufferedImage(15, 18, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Serif", Font.BOLD, 17));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(ch, (15 - fm.stringWidth(ch)) / 2, 15);
        g.dispose();

        boolean[][] pattern = new boolean[img.getHeight()][img.getWidth()];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                pattern[y][x] = (img.getRGB(x, y) >>> 24) > 0;
            }
        }
        return pattern;
    }

    private int[][] buildNameFromLetters(String name) {
        String cleaned = name.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", "");
        if (cleaned.isEmpty()) cleaned = "ІМЯ";

        return buildLargeNameForCanvas(cleaned);
    }

    private int[][] buildLargeNameForCanvas(String text) {
        int maxWidth = Math.max(24, COLS - 10);
        int maxTextHeight = Math.max(10, Math.min(16, ROWS - 28));

        BufferedImage measureImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measure = measureImg.createGraphics();
        measure.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        Font bestFont = new Font("Dialog", Font.PLAIN, 10);
        FontMetrics bestFm = measure.getFontMetrics(bestFont);

        for (int size = maxTextHeight; size >= 8; size--) {
            Font font = new Font("Dialog", Font.BOLD, size);
            FontMetrics fm = measure.getFontMetrics(font);
            int width = fm.stringWidth(text);
            int height = fm.getAscent();

            if (width <= maxWidth && height <= maxTextHeight) {
                bestFont = font;
                bestFm = fm;
                break;
            }
        }

        int imageW = Math.min(maxWidth, Math.max(1, bestFm.stringWidth(text) + 2));
        int imageH = Math.max(10, bestFm.getAscent() + bestFm.getDescent() + 2);

        BufferedImage img = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setColor(Color.BLACK);
        g.setFont(bestFont);

        FontMetrics fm = g.getFontMetrics();
        int x = Math.max(0, (imageW - fm.stringWidth(text)) / 2);
        int y = Math.max(fm.getAscent(), 1 + fm.getAscent());
        g.drawString(text, x, y);
        g.dispose();
        measure.dispose();

        int[][] letters = imageToPattern(img);
        return addNameOrnamentFrame(letters);
    }

    private int[][] imageToPattern(BufferedImage img) {
        int[][] pattern = new int[img.getHeight()][img.getWidth()];

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int alpha = (img.getRGB(x, y) >>> 24);
                if (alpha > 0) {
                    pattern[y][x] = NamePatternRepository.RED_CELL;
                }
            }
        }

        return trimEmptyBorders(pattern);
    }

    private int[][] addNameOrnamentFrame(int[][] letters) {
        int maxWidth = Math.max(24, COLS - 6);
        int maxHeight = Math.max(20, ROWS - 14);

        int paddingX = 4;
        int paddingY = 6;

        int rows = Math.min(maxHeight, letters.length + paddingY * 2);
        int cols = Math.min(maxWidth, letters[0].length + paddingX * 2);
        int[][] result = new int[rows][cols];

        int startR = Math.max(0, (rows - letters.length) / 2);
        int startC = Math.max(0, (cols - letters[0].length) / 2);

        for (int r = 0; r < letters.length; r++) {
            for (int c = 0; c < letters[r].length; c++) {
                int rr = startR + r;
                int cc = startC + c;
                if (rr >= 0 && rr < rows && cc >= 0 && cc < cols && letters[r][c] != NamePatternRepository.EMPTY_CELL) {
                    result[rr][cc] = NamePatternRepository.RED_CELL;
                }
            }
        }

        int top = 1;
        int bottom = rows - 2;

        for (int c = 3; c < cols - 3; c += 5) {
            result[top][c] = NamePatternRepository.BLACK_CELL;
            result[bottom][c] = NamePatternRepository.BLACK_CELL;
        }

        for (int c = 5; c < cols - 5; c += 8) {
            result[top + 1][c] = NamePatternRepository.RED_CELL;
            result[bottom - 1][c] = NamePatternRepository.RED_CELL;
        }

        return result;
    }

    private int[][] trimEmptyBorders(int[][] pattern) {
        int top = 0;
        int bottom = pattern.length - 1;
        int left = 0;
        int right = pattern[0].length - 1;

        while (top <= bottom && isEmptyRow(pattern, top)) top++;
        while (bottom >= top && isEmptyRow(pattern, bottom)) bottom--;
        while (left <= right && isEmptyCol(pattern, left)) left++;
        while (right >= left && isEmptyCol(pattern, right)) right--;

        if (top > bottom || left > right) {
            return new int[][]{{NamePatternRepository.EMPTY_CELL}};
        }

        int[][] trimmed = new int[bottom - top + 1][right - left + 1];
        for (int r = top; r <= bottom; r++) {
            for (int c = left; c <= right; c++) {
                trimmed[r - top][c - left] = pattern[r][c];
            }
        }
        return trimmed;
    }

    private boolean isEmptyRow(int[][] pattern, int row) {
        for (int value : pattern[row]) {
            if (value != NamePatternRepository.EMPTY_CELL) return false;
        }
        return true;
    }

    private boolean isEmptyCol(int[][] pattern, int col) {
        for (int[] rows : pattern) {
            if (rows[col] != NamePatternRepository.EMPTY_CELL) return false;
        }
        return true;
    }

    private void insertOrnamentAtCenter(boolean[][] pattern, Color color) {
        saveState();
        int startR = ROWS / 2 - pattern.length / 2;
        int startC = COLS / 2 - pattern[0].length / 2;
        for (int r = 0; r < pattern.length; r++) {
            for (int c = 0; c < pattern[r].length; c++) if (pattern[r][c]) drawCell(startR + r, startC + c, color);
        }
        repaintAll();
    }

    private void insertLetter(String ch) {
        saveState();
        BufferedImage img = new BufferedImage(15, 18, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Serif", Font.BOLD, 17));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(ch, (15 - fm.stringWidth(ch)) / 2, 15);
        g.dispose();
        int startR = ROWS / 2 - 9;
        int startC = COLS / 2 - 7;
        for (int y = 0; y < img.getHeight(); y++) for (int x = 0; x < img.getWidth(); x++) if ((img.getRGB(x, y) >>> 24) > 0) {
            drawCell(startR + y, startC + x, selectedColor);
        }
        repaintAll();
    }

    private void saveProjectToCollection() {
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel form = new JPanel(new BorderLayout(0, 8));
        form.setBorder(new EmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Введіть назву проєкту:"), BorderLayout.NORTH);
        form.add(nameField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Зберегти проект",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String projectName = nameField.getText().trim();
        if (projectName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Назва проєкту не може бути порожньою.",
                    "Помилка",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (isCanvasEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Полотно порожнє. Все одно зберегти проєкт?",
                    "Порожнє полотно",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        ProjectPreview project = new ProjectPreview(
                projectName,
                "Збережено щойно",
                createProjectPreview(),
                copyCurrentPattern()
        );

        SavedProjectsPanel.addProjectToCollection(project);

        JOptionPane.showMessageDialog(
                this,
                "Проєкт \"" + projectName + "\" збережено у вкладці «Моя колекція».",
                "Збережено",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private Color[][] copyCurrentPattern() {
        Color[][] copy = new Color[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(cells[r], 0, copy[r], 0, COLS);
        }
        return copy;
    }

    private boolean isCanvasEmpty() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (cells[r][c] != null) return false;
            }
        }
        return true;
    }

    private Color[][] createProjectPreview() {
        int minR = ROWS, minC = COLS, maxR = -1, maxC = -1;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (cells[r][c] != null) {
                    minR = Math.min(minR, r);
                    minC = Math.min(minC, c);
                    maxR = Math.max(maxR, r);
                    maxC = Math.max(maxC, c);
                }
            }
        }

        if (maxR < 0) {
            return new Color[8][8];
        }

        int srcRows = maxR - minR + 1;
        int srcCols = maxC - minC + 1;

        int maxPreviewSize = 14;
        int previewRows = Math.min(maxPreviewSize, Math.max(1, srcRows));
        int previewCols = Math.min(maxPreviewSize, Math.max(1, srcCols));

        Color[][] preview = new Color[previewRows][previewCols];

        for (int pr = 0; pr < previewRows; pr++) {
            for (int pc = 0; pc < previewCols; pc++) {
                int r1 = minR + pr * srcRows / previewRows;
                int r2 = minR + (pr + 1) * srcRows / previewRows;
                int c1 = minC + pc * srcCols / previewCols;
                int c2 = minC + (pc + 1) * srcCols / previewCols;

                Color found = null;

                for (int r = r1; r < Math.max(r1 + 1, r2); r++) {
                    for (int c = c1; c < Math.max(c1 + 1, c2); c++) {
                        if (r >= 0 && r < ROWS && c >= 0 && c < COLS && cells[r][c] != null) {
                            found = cells[r][c];
                            break;
                        }
                    }
                    if (found != null) break;
                }

                preview[pr][pc] = found;
            }
        }

        return preview;
    }

    private void exportPNG() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PNG зображення (*.png)", "png"));
        chooser.setSelectedFile(new File("vyshyvanka_pattern.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
            file = new File(file.getParentFile(), file.getName() + ".png");
        }

        BufferedImage img = new BufferedImage(COLS * 16, ROWS * 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            g.setColor(cells[r][c] == null ? Color.WHITE : cells[r][c]);
            g.fillRect(c * 16, r * 16, 16, 16);
            g.setColor(new Color(230, 224, 216));
            g.drawRect(c * 16, r * 16, 16, 16);
        }
        g.dispose();
        try {
            ImageIO.write(img, "png", file);
            JOptionPane.showMessageDialog(this, "PNG успішно збережено.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Не вдалося зберегти PNG: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importPNG() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PNG зображення (*.png)", "png"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            BufferedImage img = ImageIO.read(chooser.getSelectedFile());
            if (img == null) {
                JOptionPane.showMessageDialog(this, "Не вдалося прочитати PNG файл.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isCanvasEmpty()) {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Поточну схему буде замінено даними з PNG. Продовжити?",
                        "Відкрити PNG",
                        JOptionPane.YES_NO_OPTION
                );
                if (answer != JOptionPane.YES_OPTION) return;
            }

            saveState();
            for (int r = 0; r < ROWS; r++) {
                Arrays.fill(cells[r], null);
            }

            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    cells[r][c] = readImportedCellColor(img, r, c);
                }
            }

            pendingStamp = null;
            pendingStampName = null;
            pendingStampReusable = false;
            selectedOrnamentBase = null;
            selectedOrnamentName = null;
            lineStart = null;
            activeTool = Tool.PENCIL;
            refreshToolButtons();
            refreshCanvasSize();
            repaintAll();

            JOptionPane.showMessageDialog(this, "PNG відкрито та перенесено на сітку.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Не вдалося відкрити PNG: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Color readImportedCellColor(BufferedImage img, int row, int col) {
        int x1 = col * img.getWidth() / COLS;
        int x2 = (col + 1) * img.getWidth() / COLS;
        int y1 = row * img.getHeight() / ROWS;
        int y2 = (row + 1) * img.getHeight() / ROWS;

        int padX = Math.max(0, (x2 - x1) / 4);
        int padY = Math.max(0, (y2 - y1) / 4);
        x1 = Math.min(img.getWidth() - 1, x1 + padX);
        x2 = Math.max(x1 + 1, x2 - padX);
        y1 = Math.min(img.getHeight() - 1, y1 + padY);
        y2 = Math.max(y1 + 1, y2 - padY);

        long sumR = 0, sumG = 0, sumB = 0;
        int count = 0;

        for (int y = y1; y < y2 && y < img.getHeight(); y++) {
            for (int x = x1; x < x2 && x < img.getWidth(); x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24) & 0xff;
                int r = (argb >>> 16) & 0xff;
                int g = (argb >>> 8) & 0xff;
                int b = argb & 0xff;

                if (a < 40) continue;
                if (r > 238 && g > 238 && b > 238) continue;
                if (Math.abs(r - 230) < 12 && Math.abs(g - 224) < 12 && Math.abs(b - 216) < 12) continue;

                sumR += r;
                sumG += g;
                sumB += b;
                count++;
            }
        }

        if (count == 0) return null;
        return new Color((int) (sumR / count), (int) (sumG / count), (int) (sumB / count));
    }

    private boolean[][] diamondPattern() {
        String[] rows = {
                "0000001000000", "0000011100000", "0000110110000", "0001100011000", "0011001001100",
                "0110011100110", "1100111110011", "0110011100110", "0011001001100", "0001100011000",
                "0000110110000", "0000011100000", "0000001000000"};
        return parse(rows);
    }

    private boolean[][] flowerPattern() {
        String[] rows = {
                "0001000", "0101010", "0011100", "1111111", "0011100", "0101010", "0001000"};
        return parse(rows);
    }

    private boolean[][] starPattern() {
        String[] rows = {
                "000010000", "010010010", "001111100", "000111000", "111111111", "000111000", "001111100", "010010010", "000010000"};
        return parse(rows);
    }

    private boolean[][] smallCrossPattern() {
        String[] rows = {"00100", "00100", "11111", "00100", "00100"};
        return parse(rows);
    }

    private boolean[][] wavePattern() {
        String[] rows = {
                "1000001000001",
                "1100011100011",
                "0110110110110",
                "0011100011100",
                "0110110110110",
                "1100011100011",
                "1000001000001"
        };
        return parse(rows);
    }

    private boolean[][] leafPattern() {
        String[] rows = {
                "000010000",
                "000111000",
                "001101100",
                "011000110",
                "110111011",
                "011000110",
                "001101100",
                "000111000",
                "000010000"
        };
        return parse(rows);
    }

    private boolean[][] sunPattern() {
        String[] rows = {
                "100010001",
                "010010010",
                "001111100",
                "001101100",
                "111111111",
                "001101100",
                "001111100",
                "010010010",
                "100010001"
        };
        return parse(rows);
    }

    private boolean[][] borderPattern() {
        String[] rows = {
                "1010101010101",
                "0101010101010",
                "0010001000100",
                "0111011101110",
                "0010001000100",
                "0101010101010",
                "1010101010101"
        };
        return parse(rows);
    }

    private boolean[][] parse(String[] rows) {
        boolean[][] a = new boolean[rows.length][rows[0].length()];
        for (int r = 0; r < rows.length; r++) for (int c = 0; c < rows[r].length(); c++) a[r][c] = rows[r].charAt(c) == '1';
        return a;
    }

    private void drawMiniPattern(Graphics2D g2, boolean[][] pattern, int x, int y, int w, int h, Color color) {
        int cell = Math.max(2, Math.min(w / pattern[0].length, h / pattern.length));
        int ox = x + (w - pattern[0].length * cell) / 2;
        int oy = y + (h - pattern.length * cell) / 2;
        g2.setColor(color);
        for (int r = 0; r < pattern.length; r++) for (int c = 0; c < pattern[r].length; c++) if (pattern[r][c]) {
            g2.fillRect(ox + c * cell, oy + r * cell, Math.max(1, cell - 1), Math.max(1, cell - 1));
        }
    }

    private class CanvasPanel extends JPanel {
        private Point lastDragCell;
        private Point lastPanPoint;

        CanvasPanel() {
            MouseAdapter adapter = new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    // Пересування сітки:
                    // 1) інструмент "Вибір" — перетягуванням мишки;
                    // 2) Alt + перетягування;
                    // 3) середня кнопка мишки.
                    if (activeTool == Tool.SELECT || e.isAltDown() || SwingUtilities.isMiddleMouseButton(e)) {
                        lastPanPoint = SwingUtilities.convertPoint(canvas, e.getPoint(), canvasScrollPane.getViewport());
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        return;
                    }

                    Point cell = toCell(e.getPoint());
                    if (cell == null) return;

                    // Якщо користувач обрав готовий орнамент/літеру/цифру,
                    // наступний клік по полотну ставить його саме в це місце.
                    if (pendingStamp != null) {
                        if (SwingUtilities.isRightMouseButton(e) || e.isControlDown()) {
                            pendingStamp = null;
                            pendingStampName = null;
                            pendingStampReusable = false;
                            selectedOrnamentBase = null;
                            selectedOrnamentName = null;
                            repaintAll();
                            return;
                        }
                        placePendingStamp(cell);
                        return;
                    }

                    if (activeTool == Tool.LINE) {
                        lineStart = cell;
                        return;
                    }
                    if (activeTool == Tool.FILL) {
                        fill(cell.y, cell.x);
                        return;
                    }
                    saveState();
                    Color color = activeTool == Tool.ERASER || SwingUtilities.isRightMouseButton(e) ? null : selectedColor;
                    drawCell(cell.y, cell.x, color);
                    lastDragCell = cell;
                    repaintAll();
                }

                @Override public void mouseDragged(MouseEvent e) {
                    if (lastPanPoint != null && canvasScrollPane != null) {
                        Point now = SwingUtilities.convertPoint(canvas, e.getPoint(), canvasScrollPane.getViewport());
                        JViewport viewport = canvasScrollPane.getViewport();
                        Point view = viewport.getViewPosition();
                        int dx = lastPanPoint.x - now.x;
                        int dy = lastPanPoint.y - now.y;
                        view.translate(dx, dy);
                        view.x = Math.max(0, Math.min(view.x, canvas.getWidth() - viewport.getWidth()));
                        view.y = Math.max(0, Math.min(view.y, canvas.getHeight() - viewport.getHeight()));
                        viewport.setViewPosition(view);
                        return;
                    }

                    if (activeTool != Tool.PENCIL && activeTool != Tool.ERASER) return;
                    Point cell = toCell(e.getPoint());
                    if (cell == null || cell.equals(lastDragCell)) return;
                    Color color = activeTool == Tool.ERASER || SwingUtilities.isRightMouseButton(e) ? null : selectedColor;
                    drawCell(cell.y, cell.x, color);
                    lastDragCell = cell;
                    repaintAll();
                }

                @Override public void mouseReleased(MouseEvent e) {
                    if (lastPanPoint != null) {
                        lastPanPoint = null;
                        updateCanvasCursor();
                        return;
                    }

                    Point cell = toCell(e.getPoint());
                    if (activeTool == Tool.LINE && lineStart != null && cell != null) {
                        saveState();
                        drawLine(lineStart, cell, selectedColor);
                        lineStart = null;
                        repaintAll();
                    }
                    lastDragCell = null;
                }
            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);

            // Прокрутка трекпадом/колесом рухає збільшену схему.
            // Shift + колесо рухає по горизонталі.
            addMouseWheelListener(e -> {
                if (canvasScrollPane == null) return;
                JScrollBar bar = e.isShiftDown()
                        ? canvasScrollPane.getHorizontalScrollBar()
                        : canvasScrollPane.getVerticalScrollBar();

                int step = Math.max(18, bar.getUnitIncrement());
                bar.setValue(bar.getValue() + e.getUnitsToScroll() * step);
            });

            updateCanvasCursor();
        }

        private Rectangle gridBounds() {
            int cell = currentCellSize();
            int gridW = COLS * cell;
            int gridH = ROWS * cell;
            int x = Math.max(90, (getWidth() - gridW) / 2);
            int y = 15;
            return new Rectangle(x, y, gridW, gridH);
        }

        private Point toCell(Point p) {
            Rectangle b = gridBounds();
            int cell = currentCellSize();
            if (!b.contains(p)) return null;
            int c = (p.x - b.x) / cell;
            int r = (p.y - b.y) / cell;
            return new Point(c, r);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Rectangle b = gridBounds();
            int cell = currentCellSize();
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(b.x, b.y, b.width, b.height, 22, 22);

            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    int x = b.x + c * cell;
                    int y = b.y + r * cell;
                    Color color = cells[r][c];
                    if (color != null) {
                        g2.setColor(color);
                        g2.fillRect(x + 1, y + 1, Math.max(1, cell - 1), Math.max(1, cell - 1));
                    }
                    g2.setColor(GRID);
                    g2.drawRect(x, y, cell, cell);
                }
            }
            if (verticalSymmetry || horizontalSymmetry) {
                g2.setColor(new Color(203, 23, 35, 90));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{8, 8}, 0));
                if (verticalSymmetry) {
                    g2.drawLine(b.x + b.width / 2, b.y, b.x + b.width / 2, b.y + b.height);
                }
                if (horizontalSymmetry) {
                    g2.drawLine(b.x, b.y + b.height / 2, b.x + b.width, b.y + b.height / 2);
                }
            }
            drawCornerDecoration(g2, b.x + b.width - 70, b.y + b.height - 70);
            g2.dispose();
        }

        private void drawCornerDecoration(Graphics2D g2, int x, int y) {
            g2.setColor(new Color(224, 211, 195, 80));
            g2.setStroke(new BasicStroke(2f));
            for (int i = 0; i < 5; i++) {
                g2.drawLine(x + i * 10, y + 64, x + 56, y + i * 10);
            }
        }
    }

    private static class ToolIcon implements Icon {
        private final String name;

        ToolIcon(String name) {
            this.name = name;
        }

        @Override
        public int getIconWidth() {
            return 22;
        }

        @Override
        public int getIconHeight() {
            return 22;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(70, 67, 63));
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = x + 11;

            if (name.contains("cursor")) {
                Polygon p = new Polygon(
                        new int[]{x + 4, x + 4, x + 16},
                        new int[]{y + 3, y + 18, y + 10},
                        3
                );
                g2.fillPolygon(p);
            } else if (name.contains("pencil")) {
                g2.drawLine(x + 5, y + 17, x + 17, y + 5);
                g2.drawLine(x + 14, y + 4, x + 18, y + 8);
                g2.drawLine(x + 5, y + 17, x + 4, y + 19);
            } else if (name.contains("eraser")) {
                g2.drawRoundRect(x + 5, y + 11, 12, 6, 3, 3);
                g2.drawLine(x + 8, y + 11, x + 13, y + 17);
            } else if (name.contains("line")) {
                g2.drawLine(x + 5, y + 17, x + 17, y + 5);
            } else if (name.contains("symmetry")) {
                g2.drawLine(cx, y + 4, cx, y + 18);
                g2.drawLine(x + 6, y + 7, x + 16, y + 7);
                g2.drawLine(x + 6, y + 15, x + 16, y + 15);
            } else if (name.contains("undo")) {
                // Жирна іконка "Відмінити", намальована лініями, не шрифтом
                g2.setStroke(new BasicStroke(3.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // дуга назад
                g2.drawArc(x + 6, y + 5, 11, 12, 55, 260);

                // стрілка
                g2.drawLine(x + 7, y + 6, x + 3, y + 10);
                g2.drawLine(x + 7, y + 6, x + 11, y + 9);
            } else if (name.contains("redo")) {
                // Жирна іконка "Повернути", намальована лініями, не шрифтом
                g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // кругова стрілка
                g2.drawArc(x + 5, y + 5, 12, 12, -55, 275);

                // наконечник стрілки справа
                g2.drawLine(x + 15, y + 6, x + 19, y + 9);
                g2.drawLine(x + 15, y + 6, x + 15, y + 11);
            } else if (name.contains("zoom_in")) {
                g2.drawOval(x + 4, y + 4, 10, 10);
                g2.drawLine(x + 13, y + 13, x + 18, y + 18);
                g2.drawLine(x + 9, y + 6, x + 9, y + 12);
                g2.drawLine(x + 6, y + 9, x + 12, y + 9);
            } else if (name.contains("zoom_out")) {
                g2.drawOval(x + 4, y + 4, 10, 10);
                g2.drawLine(x + 13, y + 13, x + 18, y + 18);
                g2.drawLine(x + 6, y + 9, x + 12, y + 9);
            }

            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;

        RoundedPanel(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 12));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 6, radius, radius);

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);

            g2.setColor(new Color(238, 230, 220));
            g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

}

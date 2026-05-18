import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class MainFrame extends JFrame {
    private JPanel rootPanel;
    private CardLayout cardLayout;
    private DrawingPanel drawingPanel;
    private JPanel canvasHolder;
    private JLabel gridInfoLabel;

    private final Color bg = new Color(250, 247, 240);
    private final Color dark = new Color(45, 45, 45);
    private final Color red = new Color(170, 25, 35);

    public MainFrame() {
        setTitle("Редактор української вишиванки");
        setSize(1350, 880);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);
        rootPanel.add(createMainMenuPanel(), "menu");
        rootPanel.add(createCreatorScreen(), "creator");
        add(rootPanel);
        cardLayout.show(rootPanel, "menu");
    }

    private JPanel createMainMenuPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bg);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 60, 35, 60));

        JLabel title = new JLabel("Vyshyvanka Designer", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 38));
        title.setForeground(new Color(120, 20, 20));

        JLabel sub = new JLabel("Конструктор українського орнаменту з палітрою, літерами, цифрами, симетрією та PNG/JPG", JLabel.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 16));
        sub.setForeground(new Color(70, 70, 70));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 10));
        header.setBackground(bg);
        header.add(title); header.add(sub);

        JPanel buttons = new JPanel(new GridLayout(4, 1, 0, 14));
        buttons.setBackground(bg);
        buttons.setBorder(BorderFactory.createEmptyBorder(55, 210, 55, 210));
        JButton create = createMenuButton("Створити / малювати вишиванку");
        JButton name = createMenuButton("Згенерувати орнамент за текстом");
        JButton regions = createMenuButton("Вишиванки регіонів і символи");
        JButton exit = createMenuButton("Вийти");

        create.addActionListener(e -> cardLayout.show(rootPanel, "creator"));
        name.addActionListener(e -> {
            cardLayout.show(rootPanel, "creator");
            askAndGenerateText();
        });
        regions.addActionListener(e -> showRegionsWindow());
        exit.addActionListener(e -> System.exit(0));

        buttons.add(create); buttons.add(name); buttons.add(regions); buttons.add(exit);
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(buttons, BorderLayout.CENTER);
        mainPanel.add(new JLabel("Навчальна практика · Java Swing · піксельна вишивка", JLabel.CENTER), BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel createCreatorScreen() {
        JPanel screen = new JPanel(new BorderLayout());
        screen.setBackground(new Color(25, 25, 25));

        drawingPanel = new DrawingPanel(45, 60, 15);
        canvasHolder = new JPanel(new GridBagLayout());
        canvasHolder.setBackground(new Color(20, 20, 20));
        canvasHolder.add(drawingPanel);
        JScrollPane scroll = new JScrollPane(canvasHolder);
        scroll.getViewport().setBackground(new Color(20, 20, 20));

        screen.add(createTopToolbar(), BorderLayout.NORTH);
        screen.add(createOrnamentPanel(), BorderLayout.WEST);
        screen.add(scroll, BorderLayout.CENTER);
        screen.add(createPaletteAndToolsPanel(), BorderLayout.EAST);
        return screen;
    }

    private JPanel createTopToolbar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.setBackground(dark);
        top.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        gridInfoLabel = new JLabel();
        gridInfoLabel.setForeground(Color.WHITE);
        updateGridInfo();

        JButton sizeBtn = createSideButton("Розмір вікна / сітки");
        JButton textBtn = createSideButton("Текст → орнамент");
        JButton savePng = createSideButton("Зберегти PNG");
        JButton saveJpg = createSideButton("Зберегти JPG");
        JButton open = createSideButton("Відкрити PNG/JPG");
        JButton back = createSideButton("До меню");

        sizeBtn.addActionListener(e -> showGridSizeDialog());
        textBtn.addActionListener(e -> askAndGenerateText());
        savePng.addActionListener(e -> saveImage("png"));
        saveJpg.addActionListener(e -> saveImage("jpg"));
        open.addActionListener(e -> openImage());
        back.addActionListener(e -> cardLayout.show(rootPanel, "menu"));

        top.add(gridInfoLabel); top.add(sizeBtn); top.add(textBtn); top.add(savePng); top.add(saveJpg); top.add(open); top.add(back);
        return top;
    }

    private JPanel createOrnamentPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(245, 0));
        left.setBackground(dark);
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(dark);

        inner.add(makeSectionLabel("Базові елементи"));
        inner.add(createPatternButton("Пензлик", null));
        inner.add(createPatternButton("Ромб", PT_RHOMBUS));
        inner.add(createPatternButton("Хрест", PT_CROSS));
        inner.add(createPatternButton("Дерево життя", PT_TREE));
        inner.add(createPatternButton("Зірка", PT_STAR));
        inner.add(createPatternButton("Калина", PT_KALYNA));
        inner.add(createPatternButton("Хвиля", PT_WAVE));
        inner.add(createPatternButton("Листок", PT_LEAF));
        inner.add(createPatternButton("Серце", PT_HEART));
        inner.add(createPatternButton("Сонце", PT_SUN));
        inner.add(createPatternButton("Меандр", PT_MEANDER));

        inner.add(makeSectionLabel("Українська абетка: не літери, а символи"));
        for (char ch : UA_LETTERS) inner.add(createPatternButton(String.valueOf(ch), OrnamentAlphabet.getGlyph(ch)));
        inner.add(makeSectionLabel("Цифри"));
        for (char ch : "0123456789".toCharArray()) inner.add(createPatternButton(String.valueOf(ch), OrnamentAlphabet.getGlyph(ch)));
        inner.add(makeSectionLabel("English"));
        for (char ch : EN_LETTERS) inner.add(createPatternButton(String.valueOf(ch), OrnamentAlphabet.getGlyph(ch)));

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(dark);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JLabel title = new JLabel("Абетка та орнаменти");
        title.setFont(new Font("Arial", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        left.add(title, BorderLayout.NORTH);
        left.add(scroll, BorderLayout.CENTER);
        return left;
    }

    private JPanel createPaletteAndToolsPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(310, 0));
        right.setBackground(dark);
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(dark);
        content.add(makeSectionLabel("Палітра кольорів"));
        addPalette(content);

        content.add(makeSectionLabel("Інструменти"));
        JPanel tools = new JPanel(new GridLayout(0, 2, 6, 6));
        tools.setBackground(dark);
        tools.setMaximumSize(new Dimension(300, 360));
        tools.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton pencil = createSideButton("Пензлик");
        JButton eraser = createSideButton("Ластик");
        JButton undo = createSideButton("Назад");
        JButton clear = createSideButton("Стерти все");
        JButton rotL = createSideButton("⟲ Повернути");
        JButton rotR = createSideButton("⟳ Повернути");
        JButton flipH = createSideButton("↔ Дзеркало");
        JButton flipV = createSideButton("↕ Дзеркало");
        JButton dupH = createSideButton("Дубль праворуч");
        JButton dupV = createSideButton("Дубль вниз");
        JButton tile = createSideButton("Заповнити");

        pencil.addActionListener(e -> drawingPanel.setPattern(null));
        eraser.addActionListener(e -> drawingPanel.setEraser(true));
        undo.addActionListener(e -> drawingPanel.undo());
        clear.addActionListener(e -> drawingPanel.clearGrid());
        rotL.addActionListener(e -> drawingPanel.rotatePatternLeft());
        rotR.addActionListener(e -> drawingPanel.rotatePatternRight());
        flipH.addActionListener(e -> drawingPanel.flipPatternHorizontal());
        flipV.addActionListener(e -> drawingPanel.flipPatternVertical());
        dupH.addActionListener(e -> drawingPanel.duplicateToRight());
        dupV.addActionListener(e -> drawingPanel.duplicateToBottom());
        tile.addActionListener(e -> drawingPanel.tileSelectedPattern());

        for (JButton b : new JButton[]{pencil, eraser, undo, clear, rotL, rotR, flipH, flipV, dupH, dupV, tile}) tools.add(b);
        content.add(tools);

        content.add(makeSectionLabel("Автосиметрія"));
        JCheckBox vert = createCheckBox("Вертикальна вісь");
        JCheckBox horiz = createCheckBox("Горизонтальна вісь");
        vert.addActionListener(e -> drawingPanel.setVerticalSymmetry(vert.isSelected()));
        horiz.addActionListener(e -> drawingPanel.setHorizontalSymmetry(horiz.isSelected()));
        content.add(vert); content.add(horiz);

        JLabel hint = new JLabel("<html><font color='#d0d0d0'>1) Обери колір.<br>2) Обери символ/літеру/цифру.<br>3) Клацай по сітці.<br><br>Літери й цифри тут зроблені як окремі симетричні орнаменти, а не простий шрифт.</font></html>");
        hint.setFont(new Font("Arial", Font.PLAIN, 12));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        hint.setBorder(BorderFactory.createEmptyBorder(10, 2, 2, 2));
        content.add(hint);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(dark);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        right.add(scroll, BorderLayout.CENTER);
        return right;
    }

    private void addPalette(JPanel content) {
        Color[][] palette = {
                {c(90,0,0), c(140,0,0), c(180,0,0), c(220,20,30), c(255,75,75), c(255,150,150), c(255,210,210)},
                {c(130,55,0), c(180,80,0), c(230,110,0), c(255,150,40), c(255,190,100), c(255,220,160), c(255,235,205)},
                {c(130,105,0), c(190,150,0), c(235,185,0), c(255,215,0), c(255,235,80), c(255,245,150), c(255,252,205)},
                {c(0,80,20), c(0,120,30), c(30,160,50), c(70,200,80), c(130,230,120), c(190,248,170), c(225,255,215)},
                {c(0,75,130), c(0,110,190), c(20,145,220), c(70,180,240), c(125,210,250), c(180,230,255), c(220,245,255)},
                {c(25,25,150), c(50,65,215), c(80,100,250), c(120,145,255), c(165,180,255), c(200,210,255), c(230,235,255)},
                {c(75,0,125), c(120,0,180), c(165,30,225), c(205,70,245), c(225,130,250), c(240,185,255), c(248,220,255)},
                {c(170,0,85), c(220,30,110), c(250,70,140), c(255,120,175), c(255,165,200), c(255,205,225), c(255,235,245)},
                {c(55,30,5), c(100,55,15), c(145,85,25), c(190,125,55), c(220,165,100), c(235,205,160), c(248,230,205)},
                {c(20,20,20), c(55,55,55), c(95,95,95), c(140,140,140), c(185,185,185), c(225,225,225), c(255,255,255)}
        };
        for (Color[] row : palette) {
            JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
            line.setBackground(dark);
            line.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (Color color : row) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(29, 29));
                b.setBackground(color);
                b.setOpaque(true); b.setContentAreaFilled(true); b.setFocusPainted(false);
                b.setBorder(BorderFactory.createLineBorder(new Color(220,220,220), 1));
                b.addActionListener(e -> drawingPanel.setCurrentColor(color));
                line.add(b);
            }
            content.add(line);
        }
    }

    private void showGridSizeDialog() {
        JSpinner rows = new JSpinner(new SpinnerNumberModel(drawingPanel.getRowsCount(), 10, 120, 1));
        JSpinner cols = new JSpinner(new SpinnerNumberModel(drawingPanel.getColsCount(), 10, 160, 1));
        JSpinner cell = new JSpinner(new SpinnerNumberModel(drawingPanel.getCellSizeValue(), 6, 32, 1));
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Висота сітки (рядки):")); p.add(rows);
        p.add(new JLabel("Ширина сітки (стовпці):")); p.add(cols);
        p.add(new JLabel("Розмір клітинки:")); p.add(cell);
        int res = JOptionPane.showConfirmDialog(this, p, "Вибір розміру вишивки", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            drawingPanel.setGridSize((int) rows.getValue(), (int) cols.getValue(), (int) cell.getValue());
            updateGridInfo();
        }
    }

    private void askAndGenerateText() {
        String text = JOptionPane.showInputDialog(this, "Введи ім'я, слово або цифри:", "Анна");
        if (text != null && !text.trim().isEmpty()) drawingPanel.generateTextOrnament(text);
    }

    private void saveImage(String format) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(format.toUpperCase() + " image", format));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith("." + format)) f = new File(f.getAbsolutePath() + "." + format);
        try {
            drawingPanel.saveAsImage(f, format);
            JOptionPane.showMessageDialog(this, "Файл збережено:\n" + f.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Помилка збереження: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PNG/JPG images", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            drawingPanel.openImage(chooser.getSelectedFile());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Помилка відкриття: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGridInfo() {
        if (gridInfoLabel != null) gridInfoLabel.setText("Сітка: " + drawingPanel.getColsCount() + " × " + drawingPanel.getRowsCount() + "   ");
    }

    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel("▸ " + text);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(255, 200, 100));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 2, 4, 2));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton createPatternButton(String text, int[][] pattern) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBackground(new Color(70, 70, 70));
        b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(100, 100, 100)); }
            public void mouseExited(MouseEvent e) { b.setBackground(new Color(70, 70, 70)); }
        });
        b.addActionListener(e -> drawingPanel.setPattern(pattern));
        return b;
    }

    private JButton createSideButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBackground(red);
        b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setContentAreaFilled(true); b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return b;
    }

    private JButton createMenuButton(String text) {
        JButton b = createSideButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        return b;
    }

    private JCheckBox createCheckBox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(new Font("Arial", Font.BOLD, 13));
        cb.setBackground(dark); cb.setForeground(Color.WHITE); cb.setFocusPainted(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }

    private static Color c(int r, int g, int b) { return new Color(r, g, b); }

    private void showRegionsWindow() {
        JFrame f = new JFrame("Регіони та символи української вишивки");
        f.setSize(720, 520); f.setLocationRelativeTo(this);
        JTextArea ta = new JTextArea();
        ta.setEditable(false); ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setFont(new Font("Arial", Font.PLAIN, 16));
        ta.setBackground(new Color(255, 252, 245));
        ta.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        ta.setText("Регіони України\n\n" +
                "Полтавщина — ніжні геометричні та рослинні мотиви.\n" +
                "Гуцульщина — яскраві кольори, контрасти, багато червоного, жовтого й зеленого.\n" +
                "Поділля — насичені темні лінії, ромби, хрести, щільні орнаменти.\n" +
                "Буковина — багатоколірність, бісерні мотиви, складні композиції.\n" +
                "Київщина — поєднання червоного й чорного, рослинні символи.\n\n" +
                "Значення символів\n\n" +
                "Ромб — земля, достаток, родючість.\n" +
                "Коло — сонце, енергія, вічність.\n" +
                "Калина — рід, краса, Україна.\n" +
                "Хрест — оберіг і захист.\n" +
                "Дерево життя — розвиток, родина, зв'язок поколінь.\n" +
                "Зоря — гармонія, захист від зла.");
        f.add(new JScrollPane(ta)); f.setVisible(true);
    }

    static final int[][] PT_SMALL_DIAMOND = {{-1,0},{0,-1},{0,0},{0,1},{1,0}};
    static final int[][] PT_RHOMBUS = {{-4,0},{-3,-1},{-3,1},{-2,-2},{-2,2},{-1,-3},{-1,3},{0,-4},{0,4},{1,-3},{1,3},{2,-2},{2,2},{3,-1},{3,1},{4,0},{0,0}};
    static final int[][] PT_CROSS = {{-4,0},{-3,0},{-2,0},{-1,0},{0,-4},{0,-3},{0,-2},{0,-1},{0,0},{0,1},{0,2},{0,3},{0,4},{1,0},{2,0},{3,0},{4,0}};
    static final int[][] PT_STAR = {{-5,0},{-4,0},{-3,-1},{-3,0},{-3,1},{-2,-2},{-2,0},{-2,2},{-1,-3},{-1,-1},{-1,0},{-1,1},{-1,3},{0,-5},{0,-4},{0,-2},{0,-1},{0,0},{0,1},{0,2},{0,4},{0,5},{1,-3},{1,-1},{1,0},{1,1},{1,3},{2,-2},{2,0},{2,2},{3,-1},{3,0},{3,1},{4,0},{5,0}};
    static final int[][] PT_TREE = {{-5,0},{-4,-1},{-4,0},{-4,1},{-3,-2},{-3,0},{-3,2},{-2,-3},{-2,-1},{-2,0},{-2,1},{-2,3},{-1,0},{0,0},{1,0},{2,-2},{2,-1},{2,0},{2,1},{2,2},{3,0},{4,-1},{4,0},{4,1},{5,0}};
    static final int[][] PT_KALYNA = {{-4,0},{-3,-1},{-3,0},{-3,1},{-2,-3},{-2,-2},{-2,0},{-2,2},{-2,3},{-1,-3},{-1,-2},{-1,-1},{-1,0},{-1,1},{-1,2},{-1,3},{0,-2},{0,-1},{0,0},{0,1},{0,2},{1,-1},{1,0},{1,1},{2,0},{3,-2},{3,0},{3,2},{4,-2},{4,2}};
    static final int[][] PT_WAVE = {{0,-5},{-1,-4},{-1,-3},{0,-2},{1,-1},{1,0},{0,1},{-1,2},{-1,3},{0,4},{1,5}};
    static final int[][] PT_LEAF = {{-5,0},{-4,0},{-4,1},{-3,0},{-3,1},{-3,2},{-2,-1},{-2,0},{-2,1},{-2,2},{-1,-2},{-1,-1},{-1,0},{-1,1},{0,-3},{0,-2},{0,-1},{0,0},{1,-2},{1,-1},{1,0},{2,-1},{2,0},{3,0},{4,0}};
    static final int[][] PT_HEART = {{-3,-2},{-3,-1},{-3,1},{-3,2},{-2,-3},{-2,-2},{-2,-1},{-2,0},{-2,1},{-2,2},{-2,3},{-1,-3},{-1,-2},{-1,-1},{-1,0},{-1,1},{-1,2},{-1,3},{0,-2},{0,-1},{0,0},{0,1},{0,2},{1,-1},{1,0},{1,1},{2,0}};
    static final int[][] PT_SUN = {{-4,0},{-3,-3},{-3,0},{-3,3},{-2,-1},{-2,0},{-2,1},{-1,-2},{-1,-1},{-1,0},{-1,1},{-1,2},{0,-4},{0,-3},{0,-2},{0,-1},{0,0},{0,1},{0,2},{0,3},{0,4},{1,-2},{1,-1},{1,0},{1,1},{1,2},{2,-1},{2,0},{2,1},{3,-3},{3,0},{3,3},{4,0}};
    static final int[][] PT_MEANDER = {{-2,-5},{-2,-4},{-2,-3},{-1,-3},{0,-3},{0,-2},{0,-1},{-1,-1},{-2,-1},{-2,0},{-2,1},{-1,1},{0,1},{0,2},{0,3},{1,3},{2,3},{2,4},{2,5}};

    static final char[] UA_LETTERS = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ".toCharArray();
    static final char[] EN_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
}

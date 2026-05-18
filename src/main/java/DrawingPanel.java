import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class DrawingPanel extends JPanel {
    private int rows;
    private int cols;
    private int cellSize;
    private Color[][] cells;

    private Color currentColor = new Color(190, 20, 35);
    private boolean eraser = false;
    private boolean verticalSymmetry = false;
    private boolean horizontalSymmetry = false;
    private int[][] currentPattern = null;

    private final Deque<Color[][]> history = new ArrayDeque<>();
    private static final int MAX_HISTORY = 60;

    public DrawingPanel(int rows, int cols, int cellSize) {
        setGridSize(rows, cols, cellSize);
        setBackground(Color.WHITE);

        MouseAdapter mouse = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                saveHistory();
                drawByMouse(e);
            }
            @Override public void mouseDragged(MouseEvent e) {
                drawByMouse(e);
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    public void setGridSize(int rows, int cols, int cellSize) {
        this.rows = Math.max(10, Math.min(rows, 120));
        this.cols = Math.max(10, Math.min(cols, 160));
        this.cellSize = Math.max(6, Math.min(cellSize, 32));
        this.cells = new Color[this.rows][this.cols];
        history.clear();
        setPreferredSize(new Dimension(this.cols * this.cellSize, this.rows * this.cellSize));
        revalidate();
        repaint();
    }

    public int getRowsCount() { return rows; }
    public int getColsCount() { return cols; }
    public int getCellSizeValue() { return cellSize; }

    private void drawByMouse(MouseEvent e) {
        int col = e.getX() / cellSize;
        int row = e.getY() / cellSize;
        if (!isInside(row, col)) return;
        if (currentPattern == null) drawCellWithSymmetry(row, col);
        else drawPattern(row, col, currentColor);
        repaint();
    }

    public void drawPattern(int centerRow, int centerCol, Color color) {
        Color old = currentColor;
        currentColor = color;
        for (int[] point : currentPattern) drawCellWithSymmetry(centerRow + point[0], centerCol + point[1]);
        currentColor = old;
    }

    private void drawCellWithSymmetry(int row, int col) {
        paintOneCell(row, col);
        if (verticalSymmetry) paintOneCell(row, cols - 1 - col);
        if (horizontalSymmetry) paintOneCell(rows - 1 - row, col);
        if (verticalSymmetry && horizontalSymmetry) paintOneCell(rows - 1 - row, cols - 1 - col);
    }

    private void paintOneCell(int row, int col) {
        if (!isInside(row, col)) return;
        cells[row][col] = (eraser || Color.WHITE.equals(currentColor)) ? null : currentColor;
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void setCurrentColor(Color color) { currentColor = color; eraser = false; }
    public Color getCurrentColor() { return currentColor; }
    public void setEraser(boolean eraser) { this.eraser = eraser; if (eraser) currentPattern = null; }
    public void setVerticalSymmetry(boolean v) { verticalSymmetry = v; repaint(); }
    public void setHorizontalSymmetry(boolean h) { horizontalSymmetry = h; repaint(); }
    public void setPattern(int[][] pattern) { currentPattern = pattern == null ? null : copyPattern(pattern); eraser = false; }
    public int[][] getCurrentPatternCopy() { return currentPattern == null ? null : copyPattern(currentPattern); }

    public void clearGrid() {
        saveHistory();
        cells = new Color[rows][cols];
        repaint();
    }

    public void rotatePatternRight() {
        if (currentPattern == null) return;
        for (int[] pt : currentPattern) { int r = pt[0]; pt[0] = pt[1]; pt[1] = -r; }
    }
    public void rotatePatternLeft() {
        if (currentPattern == null) return;
        for (int[] pt : currentPattern) { int r = pt[0]; pt[0] = -pt[1]; pt[1] = r; }
    }
    public void flipPatternHorizontal() { if (currentPattern != null) for (int[] pt : currentPattern) pt[1] = -pt[1]; }
    public void flipPatternVertical() { if (currentPattern != null) for (int[] pt : currentPattern) pt[0] = -pt[0]; }

    public void duplicateToRight() {
        saveHistory();
        int mid = cols / 2;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < mid; c++) {
                if (cells[r][c] != null) cells[r][cols - 1 - c] = cells[r][c];
            }
        }
        repaint();
    }

    public void duplicateToBottom() {
        saveHistory();
        int mid = rows / 2;
        for (int r = 0; r < mid; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] != null) cells[rows - 1 - r][c] = cells[r][c];
            }
        }
        repaint();
    }

    public void tileSelectedPattern() {
        if (currentPattern == null) return;
        saveHistory();
        int step = 12;
        for (int r = 6; r < rows; r += step) {
            for (int c = 6; c < cols; c += step) {
                for (int[] p : currentPattern) {
                    int rr = r + p[0];
                    int cc = c + p[1];
                    if (isInside(rr, cc)) cells[rr][cc] = currentColor;
                }
            }
        }
        repaint();
    }

    private void saveHistory() {
        Color[][] snap = new Color[rows][cols];
        for (int r = 0; r < rows; r++) snap[r] = cells[r].clone();
        if (history.size() >= MAX_HISTORY) history.pollFirst();
        history.addLast(snap);
    }

    public void undo() {
        if (history.isEmpty()) return;
        cells = history.pollLast();
        repaint();
    }

    public void generateTextOrnament(String text) {
        if (text == null || text.trim().isEmpty()) return;
        saveHistory();
        cells = new Color[rows][cols];
        String clean = text.trim().toUpperCase();
        int spacing = 12;
        int totalWidth = clean.length() * spacing;
        int startCol = Math.max(6, (cols - totalWidth) / 2 + 5);
        int centerRow = rows / 2;
        Color red = new Color(190, 20, 35);
        Color black = new Color(45, 45, 45);
        Color gray = new Color(120, 120, 120);

        for (int i = 0; i < clean.length(); i++) {
            char ch = clean.charAt(i);
            if (Character.isWhitespace(ch)) continue;
            int[][] glyph = OrnamentAlphabet.getGlyph(ch);
            Color color = (i % 3 == 0) ? red : (i % 3 == 1 ? black : gray);
            paintRawPattern(glyph, centerRow, startCol + i * spacing, color);
        }
        addBorderOrnament(red, black);
        repaint();
    }

    private void paintRawPattern(int[][] pattern, int centerRow, int centerCol, Color color) {
        for (int[] p : pattern) {
            int r = centerRow + p[0];
            int c = centerCol + p[1];
            if (isInside(r, c)) cells[r][c] = color;
        }
    }

    private void addBorderOrnament(Color red, Color black) {
        for (int c = 2; c < cols - 2; c += 6) {
            paintRawPattern(MainFrame.PT_SMALL_DIAMOND, 3, c, c % 12 == 2 ? red : black);
            paintRawPattern(MainFrame.PT_SMALL_DIAMOND, rows - 4, c, c % 12 == 2 ? red : black);
        }
    }

    public void saveAsImage(File file, String format) throws IOException {
        BufferedImage image = toImage(true);
        ImageIO.write(image, format, file);
    }

    public void openImage(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) throw new IOException("Не вдалося прочитати файл");
        saveHistory();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = Math.min(image.getWidth() - 1, c * image.getWidth() / cols + image.getWidth() / cols / 2);
                int y = Math.min(image.getHeight() - 1, r * image.getHeight() / rows + image.getHeight() / rows / 2);
                Color pixel = new Color(image.getRGB(x, y));
                cells[r][c] = isAlmostWhite(pixel) ? null : pixel;
            }
        }
        repaint();
    }

    private boolean isAlmostWhite(Color color) {
        return color.getRed() > 238 && color.getGreen() > 238 && color.getBlue() > 238;
    }

    private BufferedImage toImage(boolean withoutGrid) {
        int w = cols * cellSize;
        int h = rows * cellSize;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] != null) {
                    g2.setColor(cells[r][c]);
                    g2.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                }
            }
        }
        if (!withoutGrid) drawGrid(g2);
        g2.dispose();
        return img;
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2.setColor(cells[r][c] == null ? Color.WHITE : cells[r][c]);
                g2.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
        }
        drawGrid(g2);
        drawSymmetryAxes(g2);
    }

    private void drawGrid(Graphics2D g2) {
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(218, 218, 218));
        for (int r = 0; r <= rows; r++) g2.drawLine(0, r * cellSize, cols * cellSize, r * cellSize);
        for (int c = 0; c <= cols; c++) g2.drawLine(c * cellSize, 0, c * cellSize, rows * cellSize);
        g2.setColor(new Color(60, 60, 60));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(0, 0, cols * cellSize, rows * cellSize);
    }

    private void drawSymmetryAxes(Graphics2D g2) {
        g2.setColor(new Color(60, 160, 190, 120));
        g2.setStroke(new BasicStroke(1.4f));
        if (verticalSymmetry) {
            int x = (cols / 2) * cellSize;
            g2.drawLine(x, 0, x, rows * cellSize);
        }
        if (horizontalSymmetry) {
            int y = (rows / 2) * cellSize;
            g2.drawLine(0, y, cols * cellSize, y);
        }
    }

    private int[][] copyPattern(int[][] p) {
        int[][] copy = new int[p.length][2];
        for (int i = 0; i < p.length; i++) { copy[i][0] = p[i][0]; copy[i][1] = p[i][1]; }
        return copy;
    }
}

class OrnamentAlphabet {
    public static int[][] getGlyph(char ch) {
        int seed = Math.abs(Character.toUpperCase(ch) * 97 + 31);
        java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>();
        add(set, 0, 0);
        add(set, -1, 0); add(set, 1, 0); add(set, 0, -1); add(set, 0, 1);

        int radius = 3 + seed % 3;
        for (int i = 0; i < 9; i++) {
            int r = -radius + Math.abs(seed + i * 7) % (radius * 2 + 1);
            int c = -radius + Math.abs(seed / 3 + i * 11) % (radius * 2 + 1);
            if (Math.abs(r) + Math.abs(c) <= radius + 1) {
                addSym(set, r, c);
            }
        }
        if ((seed & 1) == 0) { addSym(set, -4, 0); addSym(set, 0, -4); }
        if ((seed & 2) == 0) { addSym(set, -3, -2); addSym(set, -2, -3); }
        if ((seed & 4) == 0) { addSym(set, -5, 1); addSym(set, -1, 5); }
        if (Character.isDigit(ch)) {
            int d = ch - '0';
            for (int i = -d % 3; i <= d % 3; i++) addSym(set, i, 5 - (d % 2));
        }
        int[][] result = new int[set.size()][2];
        int i = 0;
        for (String s : set) {
            String[] parts = s.split(",");
            result[i][0] = Integer.parseInt(parts[0]);
            result[i][1] = Integer.parseInt(parts[1]);
            i++;
        }
        return result;
    }

    private static void addSym(java.util.Set<String> set, int r, int c) {
        add(set, r, c); add(set, r, -c); add(set, -r, c); add(set, -r, -c);
    }
    private static void add(java.util.Set<String> set, int r, int c) { set.add(r + "," + c); }
}

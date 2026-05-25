package ui;

import model.ProjectPreview;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SavedProjectsPanel extends JPanel {
    private static final List<ProjectPreview> USER_PROJECTS = new ArrayList<>();
    private static final List<SavedProjectsPanel> OPEN_PANELS = new ArrayList<>();

    private final JPanel gridContainer;
    private final ConstructorPanel constructorPanel;
    private final Consumer<String> navigator;

    public SavedProjectsPanel(ConstructorPanel constructorPanel, Consumer<String> navigator) {
        this.constructorPanel = constructorPanel;
        this.navigator = navigator;
        OPEN_PANELS.add(this);

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        JLabel headerTitle = new JLabel("Моя колекція");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerTitle.setForeground(new Color(40, 40, 40));
        headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(headerTitle);

        add(Box.createVerticalStrut(4));

        JLabel headerDesc = new JLabel("Тут зберігатимуться орнаменти, які ви створили у конструкторі.");
        headerDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDesc.setForeground(new Color(110, 110, 110));
        headerDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(headerDesc);

        add(Box.createVerticalStrut(30));

        gridContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 24));
        gridContainer.setOpaque(false);
        gridContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        renderGrid();
        add(gridContainer);
    }

    public static void addProjectToCollection(ProjectPreview project) {
        if (project == null) return;
        USER_PROJECTS.add(0, project);
        renderAllPanels();
    }

    private void renderGrid() {
        gridContainer.removeAll();

        if (USER_PROJECTS.isEmpty()) {
            gridContainer.add(createEmptyPanel());
        } else {
            for (ProjectPreview project : new ArrayList<>(USER_PROJECTS)) {
                gridContainer.add(createSavedProjectCard(project));
            }
        }

        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private JPanel createEmptyPanel() {
        RoundedPanel empty = new RoundedPanel(22, Color.WHITE);
        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
        empty.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(236, 229, 221), 1),
                BorderFactory.createEmptyBorder(28, 34, 28, 34)
        ));
        empty.setPreferredSize(new Dimension(520, 150));

        JLabel title = new JLabel("Колекція поки що порожня");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(55, 48, 42));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel text = new JLabel("Створіть орнамент у вкладці «Конструктор» і натисніть «Зберегти проект».");
        text.setFont(new Font("SansSerif", Font.PLAIN, 13));
        text.setForeground(new Color(120, 112, 104));
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        empty.add(title);
        empty.add(Box.createVerticalStrut(8));
        empty.add(text);
        return empty;
    }

    private JPanel createSavedProjectCard(ProjectPreview project) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setPreferredSize(new Dimension(360, 105));

        PatternPreviewPanel preview = new PatternPreviewPanel(project.getPatternPreview());
        card.add(preview, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(project.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(new Color(40, 40, 40));

        JLabel dateLabel = new JLabel(project.getLastModified());
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);

        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(dateLabel);
        card.add(textPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(2, 1, 0, 6));
        controls.setOpaque(false);

        JButton openBtn = new JButton("✎") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 242, 235));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        openBtn.setToolTipText("Відкрити в конструкторі");
        configureIconButton(openBtn, new Color(140, 110, 80));
        openBtn.addActionListener(e -> openProjectInConstructor(project));

        JButton deleteBtn = new JButton("✕") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(253, 241, 241));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        deleteBtn.setToolTipText("Видалити проєкт");
        configureIconButton(deleteBtn, new Color(211, 47, 47));

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Ви дійсно хочете видалити проєкт \"" + project.getName() + "\"?",
                    "Підтвердження видалення",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                USER_PROJECTS.remove(project);
                renderAllPanels();
            }
        });

        controls.add(openBtn);
        controls.add(deleteBtn);
        card.add(controls, BorderLayout.EAST);

        return card;
    }

    private void openProjectInConstructor(ProjectPreview project) {
        if (constructorPanel != null) {
            constructorPanel.loadSavedProject(project);
        }
        if (navigator != null) {
            navigator.accept("CONSTRUCTOR");
        }
    }

    private static void renderAllPanels() {
        for (SavedProjectsPanel panel : new ArrayList<>(OPEN_PANELS)) {
            panel.renderGrid();
        }
    }

    private void configureIconButton(JButton btn, Color fgColor) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(fgColor);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

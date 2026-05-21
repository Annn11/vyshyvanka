package ui;

import model.ProjectPreview;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SavedProjectsPanel extends JPanel {
    private final List<ProjectPreview> userProjects = new ArrayList<>();
    private final JPanel gridContainer;

    public SavedProjectsPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // Традиційні тестові візерунки для заповнення сторінки
        Color R = new Color(211, 47, 47);   // Червоний
        Color B = new Color(40, 40, 40);    // Чорний
        Color W = new Color(220, 215, 205); // Білий/кремовий
        Color G = new Color(30, 120, 60);   // Зелений

        Color[][] pattern1 = {
                {null, W,    null, null, W,    null},
                {W,    W,    W,    W,    W,    W   },
                {null, W,    W,    W,    W,    null},
                {W,    W,    W,    W,    W,    W   },
                {null, W,    null, null, W,    null}
        };

        Color[][] pattern2 = {
                {B,    null, B,    B,    null, B   },
                {null, R,    null, null, R,    null},
                {B,    null, B,    B,    null, B   },
                {null, R,    null, null, R,    null},
                {B,    null, B,    B,    null, B   }
        };

        Color[][] pattern3 = {
                {G,    G,    null, null, G,    G   },
                {null, null, R,    R,    null, null},
                {G,    G,    null, null, G,    G   }
        };

        userProjects.add(new ProjectPreview("Моя сорочка (Полтавський стиль)", "Збережено: 2 години тому", pattern1));
        userProjects.add(new ProjectPreview("Борщівський рукав v2", "Збережено: вчора", pattern2));
        userProjects.add(new ProjectPreview("Весняний орнамент манжету", "Збережено: 3 дні тому", pattern3));

        // Заголовок сторінки
        JLabel headerTitle = new JLabel("Ваша майстерня");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerTitle.setForeground(new Color(40, 40, 40));
        headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(headerTitle);

        add(Box.createVerticalStrut(4));
        JLabel headerDesc = new JLabel("Тут зберігаються всі створені вами схеми вишивки. Ви можете продовжити редагування у будь-який момент.");
        headerDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDesc.setForeground(new Color(110, 110, 110));
        headerDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(headerDesc);

        add(Box.createVerticalStrut(30));

        // Контейнер-сітка для карток проєктів
        gridContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 24));
        gridContainer.setOpaque(false);
        gridContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        renderGrid();
        add(gridContainer);
    }

    private void renderGrid() {
        gridContainer.removeAll();
        for (ProjectPreview project : userProjects) {
            gridContainer.add(createSavedProjectCard(project));
        }
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private JPanel createSavedProjectCard(ProjectPreview project) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setPreferredSize(new Dimension(340, 95));

        // Прев'ю сітки вишивки ліворуч
        PatternPreviewPanel preview = new PatternPreviewPanel(project.getPatternPreview());
        card.add(preview, BorderLayout.WEST);

        // Текстовий блок по центру
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

        // Керування карткою праворуч (Кнопка редагування та видалення)
        JPanel controls = new JPanel(new GridLayout(2, 1, 0, 6));
        controls.setOpaque(false);

        // Кнопка відкриття (олівець / стрілочка)
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

        // Кнопка видалення (кошик / хрестик)
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
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ви дійсно хочете видалити проєкт \"" + project.getName() + "\"?",
                    "Підтвердження видалення", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                userProjects.remove(project);
                renderGrid();
            }
        });

        controls.add(openBtn);
        controls.add(deleteBtn);
        card.add(controls, BorderLayout.EAST);

        return card;
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

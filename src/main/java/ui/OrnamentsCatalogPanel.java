package ui;

import model.OrnamentPattern;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrnamentsCatalogPanel extends JPanel {
    private final List<OrnamentPattern> catalog = new ArrayList<>();

    public OrnamentsCatalogPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // Створюємо кольори та кілька тестових схем вишивки
        Color R = new Color(211, 47, 47);   // Червоний
        Color B = new Color(40, 40, 40);    // Чорний
        Color G = new Color(30, 120, 60);   // Зелений

        // Схема 1: Восьмикутна зірка (Алатир)
        Color[][] alatyr = {
                {null, null, R,    R,    null, null},
                {null, R,    B,    B,    R,    null},
                {R,    B,    null, null, B,    R   },
                {R,    B,    null, null, B,    R   },
                {null, R,    B,    B,    R,    null},
                {null, null, R,    R,    null, null}
        };

        // Схема 2: Дерево життя (Рослинний)
        Color[][] treeOfLife = {
                {null, null, G,    G,    null, null},
                {null, G,    R,    R,    G,    null},
                {G,    null, R,    R,    null, G   },
                {null, null, B,    B,    null, null},
                {null, B,    B,    B,    B,    null},
                {null, null, B,    B,    null, null}
        };

        // Схема 3: Геометричний безкінечник (Меандр)
        Color[][] meander = {
                {B,    B,    B,    B,    B,    B   },
                {B,    null, null, null, null, B   },
                {B,    null, B,    B,    null, B   },
                {B,    null, B,    B,    null, B   },
                {B,    null, null, null, null, B   },
                {B,    B,    B,    B,    B,    B   }
        };

        catalog.add(new OrnamentPattern("Прадавній Алатир", "Геометричний", "Середня", alatyr));
        catalog.add(new OrnamentPattern("Дерево Життя", "Рослинний", "Складна", treeOfLife));
        catalog.add(new OrnamentPattern("Подільський Меандр", "Геометричний", "Легка", meander));

        // Заголовок сторінки
        JLabel headerTitle = new JLabel("Каталог традиційних орнаментів");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerTitle.setForeground(new Color(40, 40, 40));
        headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(headerTitle);

        add(Box.createVerticalStrut(8));
        JLabel headerDesc = new JLabel("Оберіть автентичний узор для детального вивчення або завантаження в інтерактивний конструктор.");
        headerDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerDesc.setForeground(new Color(110, 110, 110));
        headerDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(headerDesc);

        add(Box.createVerticalStrut(30));

        // Сітка карток орнаментів (FlowLayout, щоб тримати форму карток)
        JPanel gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 24));
        gridPanel.setOpaque(false);
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (OrnamentPattern pattern : catalog) {
            gridPanel.add(createPatternCard(pattern));
        }

        add(gridPanel);
    }

    private JPanel createPatternCard(OrnamentPattern pattern) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(280, 240));

        // Велика зона прев'ю орнаменту по центру
        JPanel previewContainer = new JPanel(new GridBagLayout());
        previewContainer.setOpaque(false);
        previewContainer.setPreferredSize(new Dimension(244, 90));
        previewContainer.setMaximumSize(new Dimension(244, 90));

        // Збільшуємо прев'ю для каталогу
        PatternPreviewPanel preview = new PatternPreviewPanel(pattern.getGridData());
        preview.setPreferredSize(new Dimension(80, 80));
        previewContainer.add(preview);

        card.add(previewContainer);
        card.add(Box.createVerticalStrut(12));

        // Назва
        JLabel nameLabel = new JLabel(pattern.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setForeground(new Color(40, 40, 40));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        card.add(Box.createVerticalStrut(4));

        // Характеристики (Тип та Складність)
        JLabel infoLabel = new JLabel(pattern.getType() + " • Складність: " + pattern.getDifficulty());
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(infoLabel);

        card.add(Box.createVerticalStrut(14));

        // Кнопка відкриття
        JButton actionBtn = new JButton("Редагувати в конструкторі") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(211, 47, 47));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        actionBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setContentAreaFilled(false);
        actionBtn.setFocusPainted(false);
        actionBtn.setBorderPainted(false);
        actionBtn.setPreferredSize(new Dimension(244, 35));
        actionBtn.setMaximumSize(new Dimension(244, 35));
        actionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.add(actionBtn);

        return card;
    }
}


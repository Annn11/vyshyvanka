package ui;

import javax.swing.*;
import java.awt.*;

public class IdeasPanel extends JPanel {
    public IdeasPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Ідеї для вишиванки");
        title.setFont(new Font("Serif", Font.BOLD, 38));
        title.setForeground(new Color(45, 45, 45));
        JLabel desc = new JLabel("Готові дизайни, сучасні рішення та приклади для натхнення.");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        desc.setForeground(new Color(100, 94, 88));
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(title); texts.add(Box.createVerticalStrut(8)); texts.add(desc);
        header.add(texts, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        String[] names = {"Червоно-біла класика", "Чорно-білий мінімалізм", "Рослинні мотиви", "Орнамент на рукави", "Комір із ромбами", "Подільський стиль"};
        for (String name : names) grid.add(card(name));
        add(grid, BorderLayout.CENTER);
    }

    private JPanel card(String name) {
        RoundedPanel p = new RoundedPanel(22, Color.WHITE);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel img = new JLabel("✣", SwingConstants.CENTER);
        img.setFont(new Font("Serif", Font.BOLD, 70));
        img.setForeground(new Color(198, 35, 41));
        JLabel t = new JLabel(name);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        t.setForeground(new Color(50, 50, 50));
        JLabel d = new JLabel("<html>Натисни, щоб переглянути дизайн і використати як основу.</html>");
        d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        d.setForeground(new Color(115, 110, 105));
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.add(t); bottom.add(Box.createVerticalStrut(6)); bottom.add(d);
        p.add(img, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }
}


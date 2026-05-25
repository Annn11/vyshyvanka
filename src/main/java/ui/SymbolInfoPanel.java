package ui;

import javax.swing.*;
import java.awt.*;

public class SymbolInfoPanel extends JPanel {
    public SymbolInfoPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(createSymbolCard("Ромб", "Символ родючості та жіночого начала."));
        add(Box.createVerticalStrut(10));
        add(createSymbolCard("Восьмикутна зірка", "Алатир. Уособлює Творця та гармонію."));
        add(Box.createVerticalStrut(10));
        add(createSymbolCard("Калина", "Символ безсмертя роду та любові."));
    }

    private JPanel createSymbolCard(String name, String desc) {
        RoundedPanel card = new RoundedPanel(12, Color.WHITE);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel iconLabel = new JLabel("✦");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        iconLabel.setForeground(new Color(211, 47, 47));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(name);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Вліво

        JTextArea descArea = new JTextArea(desc);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 10));
        descArea.setForeground(Color.GRAY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT); // Вліво

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(descArea);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }
}

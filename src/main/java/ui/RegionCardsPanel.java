package ui;

import javax.swing.*;
import java.awt.*;

public class RegionCardsPanel extends JPanel {
    public RegionCardsPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 3, 10, 0));

        add(createRegionCard("Полтавщина", "Білим по білому", "Ніжні та повітряні орнаменти. Техніка гладі та вирізування."));
        add(createRegionCard("Борщівщина", "Густа чорна нитка", "Масивні вовняні узори. Символ туги та сили рідної землі."));
        add(createRegionCard("Гуцульщина", "Яскравий геометризм", "Різнобарвні ромби, розети з додаванням бісеру."));
    }

    private JPanel createRegionCard(String region, String feature, String details) {
        RoundedPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel regLabel = new JLabel(region);
        regLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        regLabel.setForeground(new Color(40, 40, 40));
        regLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко вліво

        JLabel featLabel = new JLabel(feature);
        featLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        featLabel.setForeground(new Color(211, 47, 47));
        featLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко вліво

        JTextArea detArea = new JTextArea(details);
        detArea.setFont(new Font("SansSerif", Font.PLAIN, 10));
        detArea.setForeground(new Color(90, 90, 90));
        detArea.setLineWrap(true);
        detArea.setWrapStyleWord(true);
        detArea.setEditable(false);
        detArea.setOpaque(false);
        detArea.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко вліво

        card.add(regLabel);
        card.add(featLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(detArea);

        return card;
    }
}

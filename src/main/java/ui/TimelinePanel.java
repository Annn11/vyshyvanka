package ui;

import javax.swing.*;
import java.awt.*;

public class TimelinePanel extends JPanel {
    public TimelinePanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 3, 10, 0));

        add(createEpochCard("V-X ст.", "Давні витоки", "Перші згадки про скіфський одяг. Знайдено металеві бляшки."));
        add(createEpochCard("XVII-XIX ст.", "Золотий вік", "Формування унікальних регіональних стилів. Вишивка стає побутом."));
        add(createEpochCard("XXI ст.", "Сучасність", "Трансформація в елемент високої моди. Всесвітнє визнання."));
    }

    private JPanel createEpochCard(String time, String title, String desc) {
        RoundedPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timeLabel.setForeground(new Color(211, 47, 47));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко вліво

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(new Color(40, 40, 40));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко вліво

        JTextArea descArea = new JTextArea(desc);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 10));
        descArea.setForeground(Color.GRAY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко вліво

        card.add(timeLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(descArea);

        return card;
    }
}

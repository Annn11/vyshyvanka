package ui;

import javax.swing.*;
import java.awt.*;

public class GalleryPanel extends JPanel {
    public GalleryPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));

        JLabel title = new JLabel("Галерея автентичного вбрання");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(new Color(40, 40, 40));
        card.add(title, BorderLayout.NORTH);

        // Візуальна імітація рамки фоторамки або вітрини музею
        JPanel photoPlaceholder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 242, 236));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(new Color(180, 170, 160));
                g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
                String text = "[ Експонат: Старовинний костюм Поділля, XIX ст. ]";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() / 2 + 5);
                g2.dispose();
            }
        };
        photoPlaceholder.setOpaque(false);
        photoPlaceholder.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        card.add(photoPlaceholder, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }
}

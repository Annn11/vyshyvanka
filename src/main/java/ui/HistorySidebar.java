package ui;

import javax.swing.*;
import java.awt.*;

public class HistorySidebar extends JPanel {
    public HistorySidebar(JPanel contentTargetPanel, CardLayout cardLayout, String activeKey) {
        setPreferredSize(new Dimension(240, 0));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 225, 218)));
        // Порожня ліва панель без написів і картинки.
    }
}

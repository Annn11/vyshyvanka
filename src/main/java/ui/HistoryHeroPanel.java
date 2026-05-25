package ui;

import javax.swing.*;
import java.awt.*;

public class HistoryHeroPanel extends JPanel {
    public HistoryHeroPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        RoundedPanel banner = new RoundedPanel(15, new Color(211, 47, 47)); // Трохи менше заокруглення
        banner.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Зменшили відступи по краях банера
        gbc.insets = new Insets(15, 20, 5, 20);

        JLabel title = new JLabel("Історія української вишиванки");
        // Зменшили шрифт заголовка з 24 до 20
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JTextArea desc = new JTextArea("Вишиванка — це не просто одяг, а генетичний код нації, оберіг та історія, що закарбована в нитках. Кожен стібок, колір та геометрична фігура несуть у собі глибокий сакральний зміст пращурів.");
        // Зменшили шрифт тексту з 14 до 12
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        desc.setForeground(new Color(255, 230, 230));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);

        gbc.gridy = 0;
        banner.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 15, 20);
        banner.add(desc, gbc);

        add(banner, BorderLayout.CENTER);
    }
}
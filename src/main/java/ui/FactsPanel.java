package ui;

import javax.swing.*;
import java.awt.*;

public class FactsPanel extends JPanel {
    public FactsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        RoundedPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Чи знаєте ви, що...");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко вліво

        JTextArea factsArea = new JTextArea(
                "• Першим, хто поєднав вишиванку з повсякденним піджаком, був Іван Франко.\n\n" +
                        "• Давні майстрині ніколи не копіювали чужі схеми — кожен узор створювався індивідуально як оберіг.\n\n" +
                        "• На Борщівських сорочках зазвичай не знайти рослинних мотивів — лише суворий рельєфний геометризм вовною."
        );
        factsArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        factsArea.setForeground(Color.GRAY);
        factsArea.setLineWrap(true);
        factsArea.setWrapStyleWord(true);
        factsArea.setEditable(false);
        factsArea.setOpaque(false);
        factsArea.setAlignmentX(Component.LEFT_ALIGNMENT); // Жорстко вліво

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(factsArea);

        add(card, BorderLayout.CENTER);
    }
}

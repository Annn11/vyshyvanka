import ui.*;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static final Color APP_BG = new Color(253, 251, 247);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Вишивай легко");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1360, 860);
            frame.setMinimumSize(new Dimension(1120, 720));
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(APP_BG);

            CardLayout cardLayout = new CardLayout();
            JPanel pages = new JPanel(cardLayout);
            pages.setOpaque(false);

            final TopNavigationPanel[] topNavHolder = new TopNavigationPanel[1];
            java.util.function.Consumer<String> navigate = key -> {
                cardLayout.show(pages, key);
                if (topNavHolder[0] != null) topNavHolder[0].setActiveKey(key);
            };

            topNavHolder[0] = new TopNavigationPanel("MAIN_PAGE", navigate);
            root.add(topNavHolder[0], BorderLayout.NORTH);

            pages.add(new MainMenuPanel(navigate), "MAIN_PAGE");
            ConstructorPanel constructorPanel = new ConstructorPanel();
            pages.add(constructorPanel, "CONSTRUCTOR");
            pages.add(wrapScrollable(new IdeasPanel(navigate, constructorPanel)), "IDEAS");
            pages.add(wrapScrollable(new SavedProjectsPanel()), "SAVED");
            pages.add(new HistoryPage(pages, cardLayout), "HISTORY_PAGE");

            root.add(pages, BorderLayout.CENTER);
            frame.add(root);
            frame.setVisible(true);
        });
    }

    private static JScrollPane wrapScrollable(JPanel page) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        outer.add(page, gbc);

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(22);
        scroll.getViewport().setBackground(APP_BG);
        scroll.setOpaque(false);
        return scroll;
    }
}
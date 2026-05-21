import ui.*;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Налаштування системного стилю вікон під ОС для гарного рендерингу шрифтів
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Вишивай легко");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 820);
            frame.setMinimumSize(new Dimension(1050, 700));
            frame.setLocationRelativeTo(null);

            // Запускаємо додаток одразу на весь екран
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            JPanel rootPanel = new JPanel(new BorderLayout());
            rootPanel.setBackground(new Color(253, 251, 247)); // Світле лляне тло

            // Головний менеджер перемикання екранів програми
            CardLayout cardLayout = new CardLayout();
            JPanel pagesContainer = new JPanel(cardLayout);
            pagesContainer.setOpaque(false);

            // ----------------------------------------------------
            // ВКЛАДКА 1: ГОЛОВНЕ МЕНЮ ПРОГРАМИ (MAIN_PAGE)
            // ----------------------------------------------------
            MainMenuPanel mainPage = new MainMenuPanel();
            JPanel outerWrapperMain = new JPanel(new GridBagLayout());
            outerWrapperMain.setOpaque(false);

            GridBagConstraints gbcMain = new GridBagConstraints();
            gbcMain.anchor = GridBagConstraints.NORTH;
            gbcMain.weighty = 1.0;
            gbcMain.fill = GridBagConstraints.HORIZONTAL;
            outerWrapperMain.add(mainPage, gbcMain);

            JScrollPane scrollMain = new JScrollPane(outerWrapperMain);
            scrollMain.setBorder(BorderFactory.createEmptyBorder());
            scrollMain.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollMain.getVerticalScrollBar().setUnitIncrement(22);
            scrollMain.getViewport().setBackground(new Color(253, 251, 247));

            pagesContainer.add(scrollMain, "MAIN_PAGE");

            // ----------------------------------------------------
            // ВКЛАДКА 2: КОНСТРУКТОР ОРНАМЕНТІВ (CONSTRUCTOR)
            // ----------------------------------------------------
            ConstructorPanel constructorPage = new ConstructorPanel();
            pagesContainer.add(constructorPage, "CONSTRUCTOR");

            // ----------------------------------------------------
            // ВКЛАДКА 3: КАТАЛОГ ОРНАМЕНТІВ (ORNAMENTS)
            // ----------------------------------------------------
            OrnamentsCatalogPanel ornamentsPage = new OrnamentsCatalogPanel();
            JPanel outerWrapperOrnaments = new JPanel(new GridBagLayout());
            outerWrapperOrnaments.setOpaque(false);

            GridBagConstraints gbcOrnaments = new GridBagConstraints();
            gbcOrnaments.anchor = GridBagConstraints.NORTH;
            gbcOrnaments.weighty = 1.0;
            gbcOrnaments.fill = GridBagConstraints.HORIZONTAL;
            outerWrapperOrnaments.add(ornamentsPage, gbcOrnaments);

            JScrollPane scrollOrnaments = new JScrollPane(outerWrapperOrnaments);
            scrollOrnaments.setBorder(BorderFactory.createEmptyBorder());
            scrollOrnaments.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollOrnaments.getVerticalScrollBar().setUnitIncrement(22);
            scrollOrnaments.getViewport().setBackground(new Color(253, 251, 247));

            pagesContainer.add(scrollOrnaments, "ORNAMENTS");

            // ----------------------------------------------------
            // ВКЛАДКА 4: ЗБЕРЕЖЕНІ ПРОЄКТИ (SAVED)
            // ----------------------------------------------------
            SavedProjectsPanel savedPage = new SavedProjectsPanel();
            JPanel outerWrapperSaved = new JPanel(new GridBagLayout());
            outerWrapperSaved.setOpaque(false);

            GridBagConstraints gbcSaved = new GridBagConstraints();
            gbcSaved.anchor = GridBagConstraints.NORTH;
            gbcSaved.weighty = 1.0;
            gbcSaved.fill = GridBagConstraints.HORIZONTAL;
            outerWrapperSaved.add(savedPage, gbcSaved);

            JScrollPane scrollSaved = new JScrollPane(outerWrapperSaved);
            scrollSaved.setBorder(BorderFactory.createEmptyBorder());
            scrollSaved.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollSaved.getVerticalScrollBar().setUnitIncrement(22);
            scrollSaved.getViewport().setBackground(new Color(253, 251, 247));

            pagesContainer.add(scrollSaved, "SAVED");

            // ----------------------------------------------------
            // ВКЛАДКА 5: ІСТОРІЯ ВИШИВАНКИ (HISTORY_PAGE)
            // ----------------------------------------------------
            HistoryPage historyPageContent = new HistoryPage(pagesContainer, cardLayout);
            JPanel outerWrapperHistory = new JPanel(new GridBagLayout());
            outerWrapperHistory.setOpaque(false);

            GridBagConstraints gbcHist = new GridBagConstraints();
            gbcHist.anchor = GridBagConstraints.NORTH;
            gbcHist.weighty = 1.0;
            gbcHist.fill = GridBagConstraints.HORIZONTAL;
            outerWrapperHistory.add(historyPageContent, gbcHist);

            JScrollPane scrollHistory = new JScrollPane(outerWrapperHistory);
            scrollHistory.setBorder(BorderFactory.createEmptyBorder());
            scrollHistory.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollHistory.getVerticalScrollBar().setUnitIncrement(22);
            scrollHistory.getViewport().setBackground(new Color(253, 251, 247));

            pagesContainer.add(scrollHistory, "HISTORY_PAGE");

            // ----------------------------------------------------
            // БІЧНЕ МЕНЮ (САЙДБАР) ТА ЗАПУСК ПРОГРАМИ
            // ----------------------------------------------------
            // На старті програми виділяємо вкладку "MAIN_PAGE" (Головна)
            HistorySidebar sidebar = new HistorySidebar(pagesContainer, cardLayout, "MAIN_PAGE");

            rootPanel.add(sidebar, BorderLayout.WEST);
            rootPanel.add(pagesContainer, BorderLayout.CENTER);

            frame.add(rootPanel);
            frame.setVisible(true);
        });
    }
}
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Редактор української вишиванки");
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(250, 247, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Редактор української вишиванки", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(120, 20, 20));

        JLabel subtitleLabel = new JLabel(
                "Java-програма для створення схем української вишивки",
                JLabel.CENTER
        );
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(80, 80, 80));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        headerPanel.setBackground(new Color(250, 247, 240));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 0, 12));
        buttonPanel.setBackground(new Color(250, 247, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(35, 160, 35, 160));

        JButton drawButton = createMenuButton("Намалювати вишиванку");
        JButton nameButton = createMenuButton("Орнамент за іменем");
        JButton regionsButton = createMenuButton("Вишиванки регіонів України");
        JButton historyButton = createMenuButton("Історія української вишиванки");
        JButton exitButton = createMenuButton("Вийти");

        drawButton.addActionListener(e -> showMessage("Розділ малювання буде додано на 2 день."));

        nameButton.addActionListener(e -> showMessage("Генерація орнаменту за іменем буде додана на 4 день."));

        regionsButton.addActionListener(e -> showMessage("Довідник про регіони України буде додано на 6 день."));

        historyButton.addActionListener(e -> showHistoryWindow());

        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(drawButton);
        buttonPanel.add(nameButton);
        buttonPanel.add(regionsButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(exitButton);

        JLabel footerLabel = new JLabel(
                "Навчальна практика · Створення схем української вишивки засобами Java",
                JLabel.CENTER
        );
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        footerLabel.setForeground(new Color(100, 100, 100));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 17));
        button.setFocusPainted(false);

        button.setBackground(new Color(150, 35, 35));
        button.setForeground(Color.WHITE);

        // Це потрібно, щоб на Mac кнопки нормально показували колір
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);

        button.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        return button;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Інформація",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showHistoryWindow() {
        JFrame historyFrame = new JFrame("Історія української вишиванки");
        historyFrame.setSize(650, 450);
        historyFrame.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setBackground(new Color(255, 252, 245));
        textArea.setForeground(new Color(50, 50, 50));
        textArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        textArea.setText(
                "Історія української вишиванки\n\n" +
                        "Українська вишиванка — це не просто елемент одягу, а важлива частина культури та традицій українського народу. " +
                        "З давніх часів вишивка використовувалася для прикрашання сорочок, рушників, скатертин та інших речей побуту.\n\n" +

                        "Орнаменти у вишивці мали символічне значення. Вони могли означати захист, родючість, силу, красу, зв’язок із родом " +
                        "та природою. Часто у вишивці використовували геометричні фігури, рослинні мотиви, зображення калини, дубового листя, " +
                        "зірок, хрестів та ромбів.\n\n" +

                        "У різних регіонах України вишиванки відрізнялися кольорами, технікою виконання та орнаментами. " +
                        "Наприклад, для Полтавщини характерні світлі й ніжні вишивки, для Гуцульщини — яскраві кольори, " +
                        "а для Поділля — контрастні геометричні мотиви.\n\n" +

                        "У моєму проєкті вишиванка подається у вигляді піксельної схеми. Користувач може створити власний орнамент, " +
                        "намалювати його на сітці, обрати кольори та згенерувати візерунок за іменем."
        );

        JScrollPane scrollPane = new JScrollPane(textArea);
        historyFrame.add(scrollPane);

        historyFrame.setVisible(true);
    }
}
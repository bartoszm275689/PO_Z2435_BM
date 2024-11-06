import javax.swing.*;
import java.awt.*;

public class SimpleWindowApp {
    public static final String FRAME_TITLE = "Prosta Aplikacja Okienkowa";
    public static final String WELCOME_TEXT = "Witaj w mojej aplikacji!";
    public static final String CLOSE_BUTTON_TEXT = "Zamknij ";

    public static void createAndShowGUI() {
        JFrame frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        JLabel welcomeLabel = createWelcomeLabel();
        frame.getContentPane().add(welcomeLabel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel(frame);
        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JLabel createWelcomeLabel() {
        JLabel jlabel = new JLabel(WELCOME_TEXT, JLabel.CENTER);
        Font font = new Font("Comic Sans MS", Font.BOLD, 40);
        jlabel.setBackground(Color.blue);
        jlabel.setOpaque(true);
        jlabel.setFont(font);
        jlabel.setLayout(new FlowLayout());
        return jlabel;
    }


    private static JPanel createControlPanel(JFrame frame) {
        JPanel panel = new JPanel();
        JButton closeButton = new JButton(CLOSE_BUTTON_TEXT);
        closeButton.addActionListener(e -> frame.dispose());
        panel.setBackground(Color.blue);
        panel.add(closeButton);
        closeButton.setPreferredSize(new Dimension(150, 50));
        closeButton.setBorder(BorderFactory.createLineBorder(Color.black, 5));

        panel.add(closeButton);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleWindowApp::createAndShowGUI);
    }
}
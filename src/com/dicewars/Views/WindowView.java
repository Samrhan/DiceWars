package com.dicewars.Views;

import com.dicewars.Controllers.WindowController;
import com.dicewars.Models.WindowModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class WindowView {
    private static final int RESOLUTION = 30;

    private WindowController parent;
    private GuiView guiView;
    private CarteView carteView;

    private final JFrame window;
    private final JPanel contentFrame;

    private JPanel titleScreenPane;
    private JPanel gameScreenPane;
    private JPanel endScreenPane;

    private ArrayList<JButton> colorButtons;

    private JButton selectedRadioButton;
    private int nombreDeplayers = 2;

    public WindowView(WindowModel windowModel) {

        window = new Window();
        contentFrame = (JPanel) window.getContentPane();

        createTitleScreen();
        displayTitleScreen();
    }

    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    public void setCarteView(CarteView carteView) {
        this.carteView = carteView;
        createGameScreen();
    }

    public void setEventListener(WindowController parent) {
        this.parent = parent;
    }

    private void createTitleScreen() {
        titleScreenPane = new JPanel();
        titleScreenPane.setLayout(new BoxLayout(titleScreenPane, BoxLayout.PAGE_AXIS));
        titleScreenPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // DICE WARS
        JLabel titleLabel = new JLabel("DICE WAR");
        titleLabel.setFont(titleLabel.getFont().deriveFont(50.0f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titleScreenPane.add(titleLabel);

        // Nombre de players
        JPanel playerNumberSelectionPanel = new JPanel();
        playerNumberSelectionPanel.setLayout(new BoxLayout(playerNumberSelectionPanel, BoxLayout.PAGE_AXIS));
        playerNumberSelectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleScreenPane.add(playerNumberSelectionPanel);

        // Titre
        JLabel sectionTitle = new JLabel("Nombre de joueurs");
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerNumberSelectionPanel.add(sectionTitle);

        // Boutons
        JPanel selectionNombreplayersContainer = new JPanel();
        selectionNombreplayersContainer.setLayout(new FlowLayout());
        for (int i = 2; i < 7; i++) {
            JButton radioButton = new JButton(Integer.toString(i));
            if (i == 2) {
                radioButton.setBackground(Color.ORANGE);
                selectedRadioButton = radioButton;
            }
            radioButton.addActionListener(e -> {
                selectedRadioButton.setBackground(null);
                selectedRadioButton = (JButton) e.getSource();
                nombreDeplayers = Integer.parseInt(selectedRadioButton.getText());
                selectedRadioButton.setBackground(Color.ORANGE);
            });
            selectionNombreplayersContainer.add(radioButton);
        }
        playerNumberSelectionPanel.add(selectionNombreplayersContainer);

        // Lancement de partie
        JPanel gameLaunchButtons = new JPanel();
        gameLaunchButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameLaunchButtons.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        titleScreenPane.add(gameLaunchButtons);

        JButton startGame = new JButton("Commencer à jouer");
        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.startGame(nombreDeplayers, 5 * nombreDeplayers);
            }
        });
        gameLaunchButtons.add(startGame);
        /* PAS IMPLEMENTE
        JLabel ouLabel = new JLabel("OU");
        ouLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ouLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        gameLaunchButtons.add(ouLabel);
        // Chargement d'une sauvegarde
        JButton loadGame = new JButton("Charger une partie");
        gameLaunchButtons.add(loadGame);*/
    }

    private void createGameScreen() {
        gameScreenPane = new JPanel();
        gameScreenPane.setLayout(new BoxLayout(gameScreenPane, BoxLayout.PAGE_AXIS));
        gameScreenPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        carteView.setPreferredSize(new Dimension(nombreDeplayers * 5 * RESOLUTION, nombreDeplayers * 5 * RESOLUTION));
        carteView.setMaximumSize(new Dimension(nombreDeplayers * 5 * RESOLUTION, nombreDeplayers * 5 * RESOLUTION));
        carteView.setMinimumSize(new Dimension(nombreDeplayers * 5 * RESOLUTION, nombreDeplayers * 5 * RESOLUTION));
        carteView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gameScreenPane.add(carteView, BorderLayout.CENTER);
    }

    public void displayTitleScreen() {
        contentFrame.removeAll();
        window.repaint();
        window.revalidate();
        contentFrame.setLayout(new BorderLayout());
        contentFrame.add(titleScreenPane, BorderLayout.CENTER);
        window.repaint();
        window.revalidate();
    }

    public void displayGameScreen() {
        contentFrame.removeAll();
        window.repaint();
        window.revalidate();

        contentFrame.setLayout(new BorderLayout());

        contentFrame.add(guiView.getTopGui(), BorderLayout.NORTH);
        contentFrame.add(gameScreenPane);
        contentFrame.add(guiView.getBottomGui(), BorderLayout.SOUTH);

        window.repaint();
        window.revalidate();
    }


    /**
     * Cette vue n'est pas crée à l'avance car elle doit être régénérée à chaque fin de partie
     *
     * @param winner
     */
    public void createAndDisplayEndScreen(int winner) {
        contentFrame.removeAll();

        contentFrame.setLayout(new BoxLayout(contentFrame, BoxLayout.PAGE_AXIS));
        JLabel winLabel = new JLabel("Le joueur " + winner + " l'emporte !");
        winLabel.setFont(winLabel.getFont().deriveFont(50.0f));
        winLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        winLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        winLabel.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
        contentFrame.add(winLabel, BorderLayout.CENTER);

        contentFrame.repaint();
        contentFrame.revalidate();
    }

    public class Window extends JFrame {
        private static final int minHeight = 600;
        private static final int minWidth = 600;

        public Window() {
            super("Dice war");

            WindowListener l = new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            };

            addWindowListener(l);
            setSize(minWidth, minHeight);
            setMinimumSize(new Dimension(minWidth, minHeight));
            setVisible(true);
        }
    }
}

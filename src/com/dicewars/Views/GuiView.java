package com.dicewars.Views;

import com.dicewars.Controllers.PartieController;
import com.dicewars.Controllers.WindowController;
import com.dicewars.Models.PartieModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GuiView {
    private final WindowController windowController;
    private final JPanel topGui = new JPanel();
    private final JPanel bottomGui = new JPanel();
    private PartieModel partieModel;
    private PartieController partieController;
    private final JLabel currentPlayerTurn = new JLabel("");
    private final JLabel warningMessage = new JLabel("");
    private final JLabel infoMessage = new JLabel("");

    public GuiView(PartieModel partieModel, WindowController windowController) {
        this.partieModel = partieModel;
        this.windowController = windowController;

        topGui.setLayout(new FlowLayout());
        bottomGui.setLayout(new BoxLayout(bottomGui, BoxLayout.PAGE_AXIS));

        // ### Définition du GUI du haut
        currentPlayerTurn.setFont(new Font("SansSerif", Font.PLAIN, 20));
        topGui.add(currentPlayerTurn);


        // ### Définition du GUI du bas
        infoMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomGui.add(infoMessage);
        warningMessage.setForeground(Color.RED);
        warningMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomGui.add(warningMessage);

        JPanel lowerButtons = new JPanel();
        lowerButtons.setLayout(new FlowLayout());
        bottomGui.add(lowerButtons);

        JButton exitButton = new JButton("Retourner au menu");
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment retourner au menu ? Toute la progression non sauvegardée sera perdue.");
            if (confirm == JOptionPane.YES_OPTION) {
                windowController.exit();
            }
        });
        lowerButtons.add(exitButton);

        JButton endTurnButton = new JButton("Finir son tour");
        endTurnButton.addActionListener(e -> {
            partieController.endTurn();
            setInfoMessage("Fin du tour. Vous gagnez " + partieModel.getCurrentPlayer().mostAdjascentTeritories() + " dés.");
        });
        lowerButtons.add(endTurnButton);

        JButton saveButton = new JButton("Sauvegarder la partie");
        saveButton.addActionListener(e -> partieController.saveGame());
        lowerButtons.add(saveButton);
    }

    private static float CalculateLuminance(ArrayList<Integer> rgb) {
        return (float) (0.2126 * rgb.get(0) + 0.7152 * rgb.get(1) + 0.0722 * rgb.get(2));
    }

    public static boolean displayBlackBackground(int r, int g, int b) {
        ArrayList<Integer> rgb = new ArrayList<>(3);
        rgb.add(r);
        rgb.add(g);
        rgb.add(b);
        float luminance = CalculateLuminance(rgb);
        return !(luminance < 140);
    }

    public static boolean displayBlackBackground(Color color) {
        return displayBlackBackground(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Indique le nom du player dont c'est le tour
     *
     * @return
     */
    public JPanel getTopGui() {
        return topGui;
    }

    /**
     * Boutons de retour au menu, sauvegarde, et fin de tour
     *
     * @return
     */
    public JPanel getBottomGui() {
        return bottomGui;
    }

    public void setCurrentPlayerTurn(int playerNum, int color) {
        currentPlayerTurn.setText("      Au tour du joueur " + (playerNum + 1) + "      ");
        Color foreground = new Color(color);
        currentPlayerTurn.setForeground(foreground);
        // On vient calculer s'il est nécéssaire d'afficher un arrière plan foncé pour que la couleur contraste bien
        currentPlayerTurn.setBackground(Color.gray);
        currentPlayerTurn.setOpaque(displayBlackBackground(foreground));
    }

    public void setEventListener(PartieController partieController) {
        this.partieController = partieController;
    }

    public void refresh() {
        topGui.repaint();
        topGui.revalidate();

        bottomGui.repaint();
        bottomGui.revalidate();
    }

    public void hideWarningMessage() {
        warningMessage.setText("");
        bottomGui.repaint();
        bottomGui.revalidate();
    }

    public void setWarningMessage(String text) {
        warningMessage.setText(text);
        bottomGui.repaint();
        bottomGui.revalidate();
    }

    public void hideInfoMessage() {
        infoMessage.setText("");
        bottomGui.repaint();
        bottomGui.revalidate();
    }

    public void setInfoMessage(String text) {
        infoMessage.setText(text);
        bottomGui.repaint();
        bottomGui.revalidate();
    }
}

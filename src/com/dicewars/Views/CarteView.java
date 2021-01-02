package com.dicewars.Views;

import com.dicewars.Controllers.PartieController;
import com.dicewars.Models.CarteModel;
import com.dicewars.Models.Coordonnee;
import com.dicewars.Models.PartieModel;
import com.dicewars.Models.TerritoireModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public class CarteView extends JPanel implements MouseListener {
    private final CarteModel carteModel;
    private final PartieModel partieModel;

    private int carteWidth;
    private int carteHeight;
    private int spacingHor;
    private int spacingVert;

    private PartieController bind;

    public CarteView(PartieModel partieModel) {
        super();
        this.partieModel = partieModel;
        this.carteModel = partieModel.getMap();
        addMouseListener(this);
    }

    private void drawTerritories(Graphics2D graphics2D) {
        graphics2D.setStroke(new BasicStroke(1));

        for (ArrayList<Coordonnee> iter : carteModel.getComposants()) {
            for (Coordonnee composant : iter) {
                graphics2D.setColor(new Color(composant.getParent().getPlayer().getColor()));
                graphics2D.fillRect(composant.getX() * spacingHor, composant.getY() * spacingVert, spacingHor, spacingVert);
            }
        }
    }

    private void drawTerritoryBorders(Graphics2D graphics2D) {
        graphics2D.setStroke(new BasicStroke(4));
        graphics2D.setColor(Color.BLACK);

        for (ArrayList<Coordonnee> iter : carteModel.getComposants()) {
            for (Coordonnee composant : iter) {
                try {
                    if (composant.getParent() != carteModel.getComposants().get(composant.getX()).get(composant.getY() - 1).getParent()) {
                        graphics2D.drawLine(composant.getX() * spacingHor, composant.getY() * spacingVert, composant.getX() * spacingHor + spacingHor, composant.getY() * spacingVert);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    if (composant.getParent() != carteModel.getComposants().get(composant.getX() - 1).get(composant.getY()).getParent()) {
                        graphics2D.drawLine(composant.getX() * spacingHor, composant.getY() * spacingVert, composant.getX() * spacingHor, composant.getY() * spacingVert + spacingVert);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
                // Pas besoin de tout dessiner
            }
        }

        // Bordure si territoire désigné comme attaquant
        if (partieModel.getAttacking() != null) {
            graphics2D.setColor(Color.ORANGE);
            for (Coordonnee composant : partieModel.getAttacking().getComposants()) {
                try {
                    if (composant.getParent() != carteModel.getComposants().get(composant.getX()).get(composant.getY() - 1).getParent()) {
                        graphics2D.drawLine(composant.getX() * spacingHor, composant.getY() * spacingVert, composant.getX() * spacingHor + spacingHor, composant.getY() * spacingVert);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    if (composant.getParent() != carteModel.getComposants().get(composant.getX()).get(composant.getY() + 1).getParent()) {
                        graphics2D.drawLine(composant.getX() * spacingHor, composant.getY() * spacingVert + spacingVert, composant.getX() * spacingHor + spacingHor, composant.getY() * spacingVert + spacingVert);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    if (composant.getParent() != carteModel.getComposants().get(composant.getX() - 1).get(composant.getY()).getParent()) {
                        graphics2D.drawLine(composant.getX() * spacingHor, composant.getY() * spacingVert, composant.getX() * spacingHor, composant.getY() * spacingVert + spacingVert);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    if (composant.getParent() != carteModel.getComposants().get(composant.getX() + 1).get(composant.getY()).getParent()) {
                        graphics2D.drawLine(composant.getX() * spacingHor + spacingHor, composant.getY() * spacingVert, composant.getX() * spacingHor + spacingHor, composant.getY() * spacingVert + spacingVert);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
    }

    private void drawBorder(Graphics2D graphics2D) {
        graphics2D.setStroke(new BasicStroke(4));
        graphics2D.setColor(Color.BLACK);
        // Tracé du contour
        graphics2D.drawLine(0, 0, 0, carteWidth);
        graphics2D.drawLine(0, 0, carteHeight, 0);
        graphics2D.drawLine(0, carteWidth - 1, carteHeight - 1, carteWidth - 1);
        graphics2D.drawLine(carteHeight - 1, carteWidth - 1, carteHeight - 1, 0);
    }

    private void drawTerritoryStrenght(Graphics2D graphics2D) {
        for (TerritoireModel territoireModel : carteModel.getTerritoires()) {
            Coordonnee capitale = territoireModel.getCapitale();
            Color bg = new Color(territoireModel.getPlayer().getColor());
            if (GuiView.displayBlackBackground(bg)) {
                graphics2D.setColor(Color.BLACK);
            } else {
                graphics2D.setColor(Color.WHITE);
            }
            graphics2D.drawString(String.valueOf(territoireModel.getDice()), capitale.getX() * spacingHor + 10, capitale.getY() * spacingVert + 20);
        }
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        drawTerritories(g2d);
        drawTerritoryBorders(g2d);
        drawBorder(g2d);
        drawTerritoryStrenght(g2d);
    }

    public void setEventListener(PartieController partieController) {
        this.bind = partieController;
    }

    @Override
    public void setPreferredSize(Dimension dimension) {
        super.setPreferredSize(dimension);

        var insets = getInsets();

        carteWidth = dimension.width - insets.left - insets.right;
        carteHeight = dimension.height - insets.top - insets.bottom;

        spacingVert = carteWidth / carteModel.getNombreComposants();
        spacingHor = carteHeight / carteModel.getNombreComposants();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / spacingHor;
        int y = e.getY() / spacingVert;

        bind.territoryClicked(carteModel.getComposants().get(x).get(y).getParent());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

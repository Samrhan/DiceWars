package com.dicewars.Controllers;

import com.dicewars.Models.PartieModel;
import com.dicewars.Models.WindowModel;
import com.dicewars.Views.CarteView;
import com.dicewars.Views.GuiView;
import com.dicewars.Views.WindowView;

public class WindowController {
    private final WindowModel windowModel;
    private final WindowView windowView;
    private PartieController partieController;

    public WindowController(WindowModel windowModel, WindowView windowView) {
        this.windowModel = windowModel;
        this.windowView = windowView;

        windowView.setEventListener(this);
    }

    public void startGame(int nPlayer, int mapSize) {
        /**
          Ordre de création des instances pour l'initialisation :
          1. Créer les modèles principaux (ceux qui ne sont pas crées par d'autres modèles, ex : PartieModel)
          2. Créer les vues et leurs passer les modèles
          3. Créer les controlleurs et leur passer les modèles et les vues
          4. Lier les events des vues aux controlleurs
         */
        PartieModel partieModel = new PartieModel(nPlayer, mapSize);

        CarteView carteView = new CarteView(partieModel);
        GuiView guiView = new GuiView(partieModel, this);
        windowView.setGuiView(guiView);
        windowView.setCarteView(carteView);

        CarteController carteController = new CarteController(partieModel.getMap(), carteView);
        partieController = new PartieController(partieModel, guiView, carteController, this);

        carteView.setEventListener(partieController);
        guiView.setEventListener(partieController);
        guiView.setCurrentPlayerTurn(partieModel.getCurrentPlayerId(), partieModel.getCurrentPlayer().getColor());
        windowView.displayGameScreen();
    }

    public void displayEndScreen(int winner) {
        windowView.createAndDisplayEndScreen(winner);
    }

    public void exit() {
        windowView.displayTitleScreen();
    }
}

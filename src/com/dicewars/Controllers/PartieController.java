package com.dicewars.Controllers;

import com.dicewars.Models.PlayerModel;
import com.dicewars.Models.PartieModel;
import com.dicewars.Models.TerritoireModel;
import com.dicewars.Views.GuiView;

public class PartieController {
    private final PartieModel partieModel;
    private final GuiView guiView;
    private final CarteController carteController;
    private final WindowController windowController;

    public PartieController(PartieModel partieModel, GuiView guiView, CarteController carteController, WindowController windowController) {
        this.partieModel = partieModel;
        this.guiView = guiView;
        this.carteController = carteController;
        this.windowController = windowController;
    }

    public void territoryClicked(TerritoireModel territoireModel) {
        guiView.hideWarningMessage();
        guiView.hideInfoMessage();
        if (territoireModel.getPlayer() == partieModel.getCurrentPlayer()) {
            if (territoireModel.getDice() <= 1) {
                guiView.setWarningMessage("Ce territoire n'a pas assez de force pour attaquer, sélection impossible");
            } else {
                partieModel.setAttacking(territoireModel);
                carteController.refresh();
            }
        } else if (partieModel.getAttacked() == null
                && partieModel.getAttacking() != null
                && territoireModel.getPlayer() != partieModel.getCurrentPlayer()) {
            partieModel.setAttacked(territoireModel);

            try {
                partieModel.getCurrentPlayer().attaquerTerritoire(partieModel.getAttacking(), partieModel.getAttacked());
                if (partieModel.getCurrentPlayer().cannotAttack()) {
                    guiView.setInfoMessage("Vous ne pouvez plus attaquer, fin du tour. Vous gagnez " + partieModel.getCurrentPlayer().mostAdjascentTeritories() + " dés.");
                    endTurn();
                }
            } catch (PlayerModel.CannotAttackOwnTerritory ignored) {
                guiView.setWarningMessage("Vous ne pouvez pas attaquer un territoire qui vous appartient.\n");
            } catch (PlayerModel.DoesntOwnTerritory ignored) {
                guiView.setWarningMessage("Vous ne possédez pas le territoire attaquant");
            } catch (PlayerModel.TerritoryTooFar ignored) {
                guiView.setWarningMessage("Vous ne pouvez attaquer que des territoires voisins à celui attaquant.\n");
            } catch (PlayerModel.NotEnoughForce ignored) {
                guiView.setWarningMessage("Le territoire attaquant n'a pas assez de force");
            } finally {
                partieModel.resetSelectedTerritories();
                carteController.refresh();
                guiView.setCurrentPlayerTurn(partieModel.getCurrentPlayerId(), partieModel.getCurrentPlayer().getColor());
            }
        }
    }

    public void checkEndGame() {
        if (!partieModel.moreThanOnePlayerAlive()) {
            windowController.displayEndScreen(partieModel.getCurrentPlayerId() + 1);
        }
    }

    public void endTurn() {
        checkEndGame();

        partieModel.resetSelectedTerritories();
        partieModel.getCurrentPlayer().terminerTour();
        partieModel.incrementCurrentPlayer();
        guiView.setCurrentPlayerTurn(partieModel.getCurrentPlayerId(), partieModel.getCurrentPlayer().getColor());

        guiView.refresh();
        carteController.refresh();
    }

    public void saveGame() {
        System.out.println("Not implemented yet");
    }
}

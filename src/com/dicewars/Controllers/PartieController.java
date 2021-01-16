package com.dicewars.Controllers;

import com.dicewars.Models.PlayerModel;
import com.dicewars.Models.GameModel;
import com.dicewars.Models.TerritoryModel;
import com.dicewars.Views.GuiView;

public class PartieController {
    private final GameModel gameModel;
    private final GuiView guiView;
    private final CarteController carteController;
    private final WindowController windowController;

    public PartieController(GameModel gameModel, GuiView guiView, CarteController carteController, WindowController windowController) {
        this.gameModel = gameModel;
        this.guiView = guiView;
        this.carteController = carteController;
        this.windowController = windowController;
    }

    public void territoryClicked(TerritoryModel territoryModel) {
        guiView.hideWarningMessage();
        guiView.hideInfoMessage();
        if (territoryModel.getPlayer() == gameModel.getCurrentPlayer()) {
            if (territoryModel.getDice() <= 1) {
                guiView.setWarningMessage("Ce territoire n'a pas assez de force pour attaquer, sélection impossible");
            } else {
                gameModel.setAttacking(territoryModel);
                carteController.refresh();
            }
        } else if (gameModel.getAttacked() == null
                && gameModel.getAttacking() != null
                && territoryModel.getPlayer() != gameModel.getCurrentPlayer()) {
            gameModel.setAttacked(territoryModel);

            try {
                gameModel.getCurrentPlayer().attaquerTerritoire(gameModel.getAttacking(), gameModel.getAttacked());
                if (gameModel.getCurrentPlayer().cannotAttack()) {
                    guiView.setInfoMessage("Vous ne pouvez plus attaquer, fin du tour. Vous gagnez " + gameModel.getCurrentPlayer().mostAdjascentTeritories() + " dés.");
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
                gameModel.resetSelectedTerritories();
                carteController.refresh();
                guiView.setCurrentPlayerTurn(gameModel.getCurrentPlayerId(), gameModel.getCurrentPlayer().getColor());
            }
        }
    }

    public void checkEndGame() {
        if (!gameModel.moreThanOnePlayerAlive()) {
            windowController.displayEndScreen(gameModel.getCurrentPlayerId() + 1);
        }
    }

    public void endTurn() {
        checkEndGame();

        gameModel.resetSelectedTerritories();
        gameModel.getCurrentPlayer().terminerTour();
        gameModel.incrementCurrentPlayer();
        guiView.setCurrentPlayerTurn(gameModel.getCurrentPlayerId(), gameModel.getCurrentPlayer().getColor());

        guiView.refresh();
        carteController.refresh();
    }

    public void saveGame() {
        System.out.println("Not implemented yet");
    }
}

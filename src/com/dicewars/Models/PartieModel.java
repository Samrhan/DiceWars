package com.dicewars.Models;

import java.util.ArrayList;
import java.util.HashSet;

public class PartieModel {
    static int[] DEFAULT_COLORS = {0xff0000, 0xff80ed, 0xffa500, 0x00ffff, 0x800080, 0xffff66};
    static int DEFAULT_PLAYER_TERRITORY_COUNT = 6;
    private CarteModel map;
    private ArrayList<PlayerModel> players;
    private int currentPlayer = 0;

    private TerritoireModel attacking = null;
    private TerritoireModel attacked = null;

    public PartieModel(int nbplayers, int mapSize) {
        // Génération aléatoire de la map
        map = new CarteModel(mapSize, nbplayers);

        // Création des players
        ArrayList<TerritoireModel> territoiresPotentiels = (ArrayList<TerritoireModel>) map.getTerritoires();
        players = new ArrayList<>(nbplayers);
        for (int i = 0; i < nbplayers; i++) {
            // On attribue aléatoirement DEFAULT_PLAYER_TERRITORY_COUNT territoires à chaque player au début de la partie
            ArrayList<TerritoireModel> territoiresplayer = new ArrayList<>(DEFAULT_PLAYER_TERRITORY_COUNT);
            for (int n = 0; n < DEFAULT_PLAYER_TERRITORY_COUNT; n++) {
                territoiresplayer.add(territoiresPotentiels.remove((int) (Math.random() * territoiresPotentiels.size())));
            }
            players.add(new PlayerModel(i, DEFAULT_COLORS[i % 6], new HashSet<>(territoiresplayer)));
        }
    }

    public CarteModel getMap() {
        return map;
    }

    public int getCurrentPlayerId() {
        return currentPlayer;
    }

    public PlayerModel getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public int getNPlayers() {
        return players.size();
    }

    public void incrementCurrentPlayer() {
        currentPlayer = (currentPlayer + 1) % getNPlayers();
    }

    public boolean moreThanOnePlayerAlive() {
        boolean yes = false;
        for (PlayerModel player : players) {
            if (player.isAlive()) {
                if (yes) {
                    return true;
                } else {
                    yes = true;
                }
            }
        }
        return false;
    }

    public PlayerModel getWinner() throws NoWinnerYet, NoPlayerAlive {
        if (moreThanOnePlayerAlive()) {
            throw new NoWinnerYet();
        }
        for (PlayerModel player : players) {
            if (player.isAlive()) {
                return player;
            }
        }
        throw new NoPlayerAlive();
    }

    public TerritoireModel getAttacking() {
        return attacking;
    }

    public void setAttacking(TerritoireModel attacking) {
        this.attacking = attacking;
    }

    public TerritoireModel getAttacked() {
        return attacked;
    }

    public void setAttacked(TerritoireModel attacked) {
        this.attacked = attacked;
    }

    public void resetSelectedTerritories() {
        attacking = null;
        attacked = null;
    }

    /**
     * Cette exception est levée si l'on demande à la classe de trouver un gagnant mais que la partie n'est pas encore terminée
     */
    public static class NoWinnerYet extends Exception {
        NoWinnerYet() {
            super();
        }
    }

    /**
     * Cette exception est levée si l'on demande à la classe de trouver un gagnant mais qu'aucun player n'est en vie
     */
    public static class NoPlayerAlive extends Exception {
        NoPlayerAlive() {
            super();
        }
    }
}

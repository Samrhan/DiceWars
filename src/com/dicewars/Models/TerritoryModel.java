package com.dicewars.Models;

import java.util.ArrayList;
import java.util.HashSet;

public class TerritoryModel {
    private final int id;
    private final ArrayList<CoordinateModel> composants;
    private final HashSet<TerritoryModel> voisins = new HashSet<>();
    private final CoordinateModel capitale;
    private PlayerModel playerModel;
    private int dice = 1; // Chaque territoire a un dé par défaut

    public TerritoryModel(int id, HashSet<CoordinateModel> composants) {
        this.id = id;

        // Copie superficielle des coordonnées des cases qui composent le territoire car on veut pouvoir itérer toutes les composantes depuis l'extérieur
        this.composants = new ArrayList<CoordinateModel>(composants); // Cast du hashset plus optimisé lors de la génération vers un Arraylist plus optimisé pour l'accès séquentiel
        this.composants.forEach((coordinateModel) -> {
            coordinateModel.setParent(this);
        });

        this.capitale = this.composants.get(0);
    }

    public CoordinateModel getCapitale() {
        return capitale;
    }

    public void addVoisin(TerritoryModel voisin) {
        if (voisin != this) {
            voisins.add(voisin);
        }
    }

    public void addDice() throws TooManyDices {
        this.dice += 1;
        if (this.dice > 8) {
            this.dice = 8;
            throw new TooManyDices();
        }
    }

    public void resetDice() {
        this.dice = 1;
    }

    public int rollDice() {
        int nbr = 0;
        for (int i = 0; i < dice; i++) {
            nbr += (int) (Math.random() * 6) + 1; // Résultat compris entre 1 et 6
        }
        return nbr;
    }

    public int getId() {
        return id;
    }

    public PlayerModel getPlayer() {
        return playerModel;
    }

    /**
     * Cette méthode vient supprimer ce territoire de la liste des territoires de l'ancien player, et vient l'ajouter dans la li
     *
     * @param playerModel
     */
    public void setplayer(PlayerModel playerModel) {
        try {
            this.playerModel.deleteTerritoire(this);
        } catch (NullPointerException ignored) {
        }
        this.playerModel = playerModel;
        this.playerModel.addTerritoire(this);
    }

    public int getDice() {
        return dice;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public ArrayList<CoordinateModel> getComposants() {
        ArrayList<CoordinateModel> new_list = new ArrayList<>(composants.size());
        for (CoordinateModel coordinateModel : composants) {
            new_list.add(coordinateModel.clone());
        }
        return new_list;
    }

    public HashSet<TerritoryModel> getVoisins() {
        return (HashSet<TerritoryModel>) voisins.clone();
    }

    public boolean hasEnemyNear() {
        for (TerritoryModel territoryModel : voisins) {
            if (territoryModel.getPlayer() != playerModel) {
                return true;
            }
        }
        return false;
    }

    static class TooManyDices extends Exception {
        TooManyDices() {
            super();

        }
    }
}

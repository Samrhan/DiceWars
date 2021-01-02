package com.dicewars.Models;

import java.util.ArrayList;
import java.util.HashSet;

public class TerritoireModel {
    private final int id;
    private final ArrayList<Coordonnee> composants;
    private final HashSet<TerritoireModel> voisins = new HashSet<>();
    private final Coordonnee capitale;
    private PlayerModel playerModel;
    private int dice = 1; // Chaque territoire a un dé par défaut

    public TerritoireModel(int id, HashSet<Coordonnee> composants) {
        this.id = id;

        // Copie superficielle des coordonnées des cases qui composent le territoire car on veut pouvoir itérer toutes les composantes depuis l'extérieur
        this.composants = new ArrayList<Coordonnee>(composants); // Cast du hashset plus optimisé lors de la génération vers un Arraylist plus optimisé pour l'accès séquentiel
        this.composants.forEach((coordonnee) -> {
            coordonnee.setParent(this);
        });

        this.capitale = this.composants.get(0);
    }

    public Coordonnee getCapitale() {
        return capitale;
    }

    public void addVoisin(TerritoireModel voisin) {
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

    public ArrayList<Coordonnee> getComposants() {
        ArrayList<Coordonnee> new_list = new ArrayList<>(composants.size());
        for (Coordonnee coordonnee : composants) {
            new_list.add(coordonnee.clone());
        }
        return new_list;
    }

    public HashSet<TerritoireModel> getVoisins() {
        return (HashSet<TerritoireModel>) voisins.clone();
    }

    public boolean hasEnemyNear() {
        for (TerritoireModel territoireModel : voisins) {
            if (territoireModel.getPlayer() != playerModel) {
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

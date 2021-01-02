package com.dicewars.Models;


import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CarteModel {
    static int DEFAULT_PLAYER_TERRITORY_COUNT = 6;

    private final ArrayList<TerritoireModel> territoires;
    private final ArrayList<ArrayList<com.dicewars.Models.Coordonnee>> composants; // Chaque coordonnée correspondra à sa position dans le tableau, et on pourra itérer toutes les coordonnées pour avoir les territoires parents

    /**
     * Régénération de la map à partir d'un fichier .csv
     *
     * @param path
     */
    public CarteModel(String path, int mapSize, int playerQtty) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("");
    }


    /**
     * Génération aléatoire de la map
     * /!\ il faut mapSize^2 >= DEFAULT_PLAYER_TERRITORY_COUNT*playerQtty
     *
     * @param mapSize
     * @param playerQtty
     */
    public CarteModel(int mapSize, int playerQtty) {
        // ### Initialisation des composants ###
        composants = new ArrayList<>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            composants.add(new ArrayList(mapSize));
            for (int n = 0; n < mapSize; n++) {
                composants.get(i).add(new com.dicewars.Models.Coordonnee(i, n));
            }
        }

        // ### Initilisation des territoires ###
        // On va d'abord préparer la liste des composants de chaque territoire avant de les construire
        ArrayList<HashSet<com.dicewars.Models.Coordonnee>> composantsTerritoires = new ArrayList<>(DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty);
        // Ensuite on place DEFAULT_PLAYER_TERRITORY_COUNT*playerQtty graines de territoires qui ne se chevauchent pas
        for (int i = 0; i < DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty; i++) {
            HashSet<com.dicewars.Models.Coordonnee> graine = new HashSet<>();
            composantsTerritoires.add(graine);
            int x = (int) (Math.random() * mapSize);
            int y = (int) (Math.random() * mapSize);
            while (!composants.get(x).get(y).isFree()) {
                x = (int) (Math.random() * mapSize);
                y = (int) (Math.random() * mapSize);
            }
            composants.get(x).get(y).occupy();
            graine.add(composants.get(x).get(y));
        }
        // On vient étendre chaque territoire petit à petit tant qu'il reste des cases vides
        int composantsVides = mapSize * mapSize - DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty;
        while (composantsVides > 0) { // Tant qu'il reste des cases vides
            for (int i = 0; i < DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty; i++) { // Pour chaque territoire
                HashSet<com.dicewars.Models.Coordonnee> toAdd = new HashSet<>();
                for (Iterator<com.dicewars.Models.Coordonnee> iterator = composantsTerritoires.get(i).iterator(); iterator.hasNext(); ) { // Pour chaque composant du territoire
                    // On étend ce composant
                    com.dicewars.Models.Coordonnee current = iterator.next();

                    int x = current.getX();
                    int y = current.getY();

                    // On essaye d'ajouter la case au dessus
                    if (y - 1 >= 0 && composants.get(x).get(y - 1).isFree()) {
                        composants.get(x).get(y - 1).occupy();
                        composantsVides--;
                        toAdd.add(composants.get(x).get(y - 1));
                    }

                    // On essaye d'ajouter la case en dessus
                    if (y + 1 < mapSize && composants.get(x).get(y + 1).isFree()) {
                        composants.get(x).get(y + 1).occupy();
                        composantsVides--;
                        toAdd.add(composants.get(x).get(y + 1));
                    }

                    // On essaye d'ajouter la case à droite
                    if (x + 1 < mapSize && composants.get(x + 1).get(y).isFree()) {
                        composants.get(x + 1).get(y).occupy();
                        composantsVides--;
                        toAdd.add(composants.get(x + 1).get(y));
                    }

                    // On essaye d'ajouter la case à gauche
                    if (x - 1 >= 0 && composants.get(x - 1).get(y).isFree()) {
                        composants.get(x - 1).get(y).occupy();
                        composantsVides--;
                        toAdd.add(composants.get(x - 1).get(y));
                    }
                }
                composantsTerritoires.get(i).addAll(toAdd);
            }
        }
        // Enfin, on crée les territoire avec leurs listes de composants
        territoires = new ArrayList<>(DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty);
        for (int i = 0; i < DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty; i++) {
            territoires.add(new TerritoireModel(i, composantsTerritoires.get(i)));
        }
        // On doit attendre que tous les territoires soient créés pour trouver les voisins de chaque territoire
        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                TerritoireModel parent = composants.get(x).get(y).getParent();
                try {
                    parent.addVoisin(composants.get(x + 1).get(y).getParent());
                } catch (IndexOutOfBoundsException ignore) {
                }
                try {
                    parent.addVoisin(composants.get(x - 1).get(y).getParent());
                } catch (IndexOutOfBoundsException ignore) {
                }
                try {
                    parent.addVoisin(composants.get(x).get(y + 1).getParent());
                } catch (IndexOutOfBoundsException ignore) {
                }
                try {
                    parent.addVoisin(composants.get(x).get(y - 1).getParent());
                } catch (IndexOutOfBoundsException ignore) {
                }
            }
        }
    }

    public ArrayList<TerritoireModel> getTerritoires() {
        return (ArrayList<TerritoireModel>) territoires.clone();
    }

    public ArrayList<ArrayList<com.dicewars.Models.Coordonnee>> getComposants() {
        return (ArrayList<ArrayList<com.dicewars.Models.Coordonnee>>) composants.clone();
    }

    public int getNombreTerritoires() {
        return territoires.size();
    }

    public int getNombreComposants() {
        return composants.size();
    }
}

package com.dicewars.Models;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MapModel {
    static int DEFAULT_PLAYER_TERRITORY_COUNT = 6;

    private final ArrayList<TerritoryModel> territoires;
    private final ArrayList<ArrayList<CoordinateModel>> composants; // Chaque coordonnée correspondra à sa position dans le tableau, et on pourra itérer toutes les coordonnées pour avoir les territoires parents

    /**
     * Régénération de la map à partir d'un fichier .csv
     *
     * @param
     */
    public MapModel(int mapSize, int playerQtty, List<int[]> points) throws FileNotFoundException {

        if (mapSize * mapSize < DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty) {
            // Si on a moins de cases que de territoires à placer alors on ne peut pas générer la carte
            throw new IllegalArgumentException("il faut mapSize^2 >= DEFAULT_PLAYER_TERRITORY_COUNT*playerQtty");
        }

        // ### Initialisation des variables finales ###
        territoires = new ArrayList<>(DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty);   // On les crée ici plutôt que quand on en a besoin car ce sont des variables finales
        composants = new ArrayList<>(mapSize);


        // ### Remplissage des composants ###
        for (int i = 0; i < mapSize; i++) {
            composants.add(new ArrayList(mapSize));
            for (int n = 0; n < mapSize; n++) {
                composants.get(i).add(new CoordinateModel(i, n));
            }
        }


        // ### Initilisation des territoires ###
        // On va d'abord préparer la liste des composants de chaque territoire avant de les construire
        ArrayList<HashSet<CoordinateModel>> composantsTerritoires = new ArrayList<>(DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty);
        // Ensuite on place DEFAULT_PLAYER_TERRITORY_COUNT*playerQtty graines de territoires qui ne se chevauchent pas
        for (int[] b : points) {
            HashSet<CoordinateModel> graine = new HashSet<>();
            composantsTerritoires.add(graine);
            int x = b[0];
            int y = b[1];
            composants.get(x).get(y).occupy();
            graine.add(composants.get(x).get(y));
        }

        generateMap(mapSize, playerQtty, composantsTerritoires);
    }



    /**
     * Génération aléatoire de la map
     * /!\ il faut mapSize^2 >= DEFAULT_PLAYER_TERRITORY_COUNT*playerQtty
     *
     * @param mapSize
     * @param playerQtty
     */
    public MapModel(int mapSize, int playerQtty) {
        if (mapSize * mapSize < DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty) {
            // Si on a moins de cases que de territoires à placer alors on ne peut pas générer la carte
            throw new IllegalArgumentException("il faut mapSize^2 >= DEFAULT_PLAYER_TERRITORY_COUNT*playerQtty");
        }

        // ### Initialisation des variables finales ###
        territoires = new ArrayList<>(DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty);   // On les crée ici plutôt que quand on en a besoin car ce sont des variables finales
        composants = new ArrayList<>(mapSize);

        // ### Remplissage des composants ###
        for (int i = 0; i < mapSize; i++) {
            composants.add(new ArrayList(mapSize));
            for (int n = 0; n < mapSize; n++) {
                composants.get(i).add(new CoordinateModel(i, n));
            }
        }

        // ### Initilisation des territoires ###
        // On va d'abord préparer la liste des composants de chaque territoire avant de les construire
        ArrayList<HashSet<CoordinateModel>> composantsTerritoires = new ArrayList<>(DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty);
        for (int i = 0; i < DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty; i++) {
            HashSet<CoordinateModel> graine = new HashSet<>();
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

        generateMap(mapSize, playerQtty, composantsTerritoires);
    }

    private void generateMap(int mapSize, int playerQtty, ArrayList<HashSet<CoordinateModel>> composantsTerritoires) {
        int composantsVides = mapSize * mapSize - DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty;
        while (composantsVides > 0) { // Tant qu'il reste des cases vides
            for (int i = 0; i < DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty; i++) { // Pour chaque territoire
                HashSet<CoordinateModel> toAdd = new HashSet<>();
                for (CoordinateModel current : composantsTerritoires.get(i)) { // Pour chaque composant du territoire
                    // On étend ce composant
                    int x = current.getX();
                    int y = current.getY();

                    if (y - 1 >= 0 && composants.get(x).get(y - 1).isFree()) {
                        composants.get(x).get(y - 1).occupy();
                        composantsVides--;
                        toAdd.add(composants.get(x).get(y - 1));
                    }
                    if (y + 1 < mapSize && composants.get(x).get(y + 1).isFree()) {
                        composants.get(x).get(y + 1).occupy();
                        composantsVides--;
                        toAdd.add(composants.get(x).get(y + 1));
                    }
                    if (x + 1 < mapSize && composants.get(x + 1).get(y).isFree()) {
                        composants.get(x + 1).get(y).occupy();
                        composantsVides--;
                        toAdd.add(composants.get(x + 1).get(y));
                    }
                    if (x - 1 >= 0 && composants.get(x - 1).get(y).isFree()) {
                        composants.get(x - 1).get(y).occupy();
                        composantsVides--;
                        toAdd.add(composants.get(x - 1).get(y));
                    }
                }
                composantsTerritoires.get(i).addAll(toAdd);
            }
        }

        for (int i = 0; i < DEFAULT_PLAYER_TERRITORY_COUNT * playerQtty; i++) {
            territoires.add(new TerritoryModel(i, composantsTerritoires.get(i)));
        }

        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                TerritoryModel parent = composants.get(x).get(y).getParent();
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

    public ArrayList<TerritoryModel> getTerritoires() {
        return (ArrayList<TerritoryModel>) territoires.clone();
    }

    public ArrayList<ArrayList<CoordinateModel>> getComposants() {
        return (ArrayList<ArrayList<CoordinateModel>>) composants.clone();
    }

    public int getNombreTerritoires() {
        return territoires.size();
    }

    public int getNombreComposants() {
        return composants.size();
    }
}

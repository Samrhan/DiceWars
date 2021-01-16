package com.dicewars.Models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameModel {
    // Couleur des joueurs
    static int[] COLORS = {0xff0000, 0xff80ed, 0xffa500, 0x00ffff, 0x800080, 0xffff66};
    // Nombre de territoire par joueur
    static int TERRITORY_COUNT = 6;
    private MapModel map;
    private final ArrayList<PlayerModel> players;
    private int currentPlayer = 0;

    private TerritoryModel attacking = null;
    private TerritoryModel attacked = null;

    public GameModel(int nbPlayers, int mapSize, boolean fromCSV) throws FileNotFoundException {

        List<int[]> points = null;

        if (fromCSV) {
            points = readPointsFromCSV("mapcsv/map" + nbPlayers + ".csv");
        }

        int nb;
        int na = 0;

        if (fromCSV) {
            map = new MapModel(mapSize, nbPlayers, points);
        } else {
            map = new MapModel(mapSize, nbPlayers);
        }

        // Génération aléatoire de la map
        map = new MapModel(mapSize, nbPlayers);

        // Création des players
        ArrayList<TerritoryModel> territoiresPotentiels = (ArrayList<TerritoryModel>) map.getTerritoires();
        players = new ArrayList<>(nbPlayers);
        for (int i = 0; i < nbPlayers; i++) {
            // On attribue aléatoirement DEFAULT_PLAYER_TERRITORY_COUNT territoires à chaque joueur au début de la partie
            ArrayList<TerritoryModel> territoiresPlayer = new ArrayList<>(TERRITORY_COUNT);
            if (!fromCSV) {
                for (int n = 0; n < TERRITORY_COUNT; n++) {
                    territoiresPlayer.add(territoiresPotentiels.remove((int) (Math.random() * territoiresPotentiels.size())));
                }
            } else {
                nb = 0;
                for (int[] p : points) {
                    if ((p[3] - 1) == i) {
                        territoiresPlayer.add(territoiresPotentiels.remove((int) (nb + na)));
                        na--;
                    }
                    nb++;
                }
            }
            players.add(new PlayerModel(i, COLORS[i], new HashSet<>(territoiresPlayer), fromCSV, points));
        }
    }

    public MapModel getMap() {
        return map;
    }

    public int getCurrentPlayerId() {
        return currentPlayer;
    }

    public PlayerModel getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public void incrementCurrentPlayer() {
        currentPlayer = (currentPlayer + 1) % players.size();
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

    public TerritoryModel getAttacking() {
        return attacking;
    }

    public void setAttacking(TerritoryModel attacking) {
        this.attacking = attacking;
    }

    public TerritoryModel getAttacked() {
        return attacked;
    }

    public void setAttacked(TerritoryModel attacked) {
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

    private static List<int[]> readPointsFromCSV(String fileName) {
        List<int[]> points = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {

            String line = br.readLine();
            line = br.readLine();
            while (line != null) {

                String[] metadata = line.split(";");
                int[] point = new int[5];
                point[0] = Integer.parseInt(metadata[0]);
                point[1] = Integer.parseInt(metadata[1]);
                point[2] = Integer.parseInt(metadata[2]);
                point[3] = Integer.parseInt(metadata[3]);
                point[4] = Integer.parseInt(metadata[4]);

                points.add(point);

                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return points;
    }

}

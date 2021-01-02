package com.dicewars.Models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PlayerModel {
    static int DEFAULT_NUMBER_OF_DICES = 8; // N'inclut pas le dé de base de chaque territoire
    private final Integer id;
    private final int color;
    private final HashSet<TerritoireModel> liste_territoireModel;

    public PlayerModel(int player, int color, HashSet<TerritoireModel> liste_territoireModel) {
        this.id = player;
        this.color = color;

        // Copie superficielle car on veut pouvoir modifier les territoires
        this.liste_territoireModel = (HashSet<TerritoireModel>) liste_territoireModel.clone();
        this.liste_territoireModel.forEach(territoireModel -> {
            territoireModel.setplayer(this);
        });

        // Attribution des dés au début
        for (int i = 0; i < DEFAULT_NUMBER_OF_DICES; i++) {
            boolean deOk = distribuerUnDe();
            if (!deOk) {
                break;
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public HashSet<TerritoireModel> getListe_territoireModel() {
        return (HashSet<TerritoireModel>) liste_territoireModel.clone();
    }

    /**
     * Cette méthode simule l'attaque de deux territoires
     *
     * @param attacking
     * @param attacked
     */
    public boolean attaquerTerritoire(TerritoireModel attacking, TerritoireModel attacked) throws NotEnoughForce, DoesntOwnTerritory, TerritoryTooFar, CannotAttackOwnTerritory {
        // Si le player ne possède pas le territoire, on lève une exception
        if (!liste_territoireModel.contains(attacking)) {
            throw new DoesntOwnTerritory();
        }

        // Si le player essaye d'attaquer l'un de ses territoires, on lève une exception
        if (liste_territoireModel.contains(attacked)) {
            throw new CannotAttackOwnTerritory();
        }

        // Si le player essaye d'attaquer un territoire depuis un qui n'est pas voisin, on lève une exception
        if (!attacking.getVoisins().contains(attacked)) {
            throw new TerritoryTooFar();
        }

        // Si le territoire attaquant a moins d'un dé, on lève une exception
        if (!(attacking.getDice() > 1)) {
            throw new NotEnoughForce();
        }

        // Autrement on effectue le calcul
        int attacking_score = attacking.rollDice();
        int attacked_score = attacked.rollDice();
        if (attacking_score > attacked_score) {
            attacked.setplayer(this); // Le territoire se charge de venir mettre à jour la liste des territoires des players
            attacked.setDice(attacking.getDice() - 1);
            attacking.resetDice();
            return true;
        } else {
            attacking.resetDice();
            return false;
        }
    }

    public void deleteTerritoire(TerritoireModel territoireModel) {
        liste_territoireModel.remove(territoireModel);
    }

    public void addTerritoire(TerritoireModel territoireModel) {
        // Si le territoire est déjà présent il n'est pas ajouté deux fois car on a un Set
        liste_territoireModel.add(territoireModel);
    }

    /**
     * Cette méthode calcule les dés de renfort
     */
    public void terminerTour() {
        // Dés de renfort
        for (int n = 0; n < mostAdjascentTeritories(); n++) {
            boolean deOk = distribuerUnDe();
            if (!deOk) {
                break;
            }
        }
    }

    public int mostAdjascentTeritories() {
        int maxAdjascent = 1;
        for (TerritoireModel territoireModel : liste_territoireModel) {
            maxAdjascent = Math.max(maxAdjascent, floodFill(territoireModel, new HashSet<>()));
        }
        return maxAdjascent;
    }

    private int floodFill(TerritoireModel source, HashSet<TerritoireModel> visited) {
        int start = 1;
        visited.add(source);
        for (TerritoireModel voisin : source.getVoisins()) {
            if (!visited.contains(voisin) && source.getPlayer() == voisin.getPlayer()) {
                start += floodFill(voisin, visited);
            }
        }
        return start;
    }

    /**
     * Cette méthode retourne true si le player ne peut plus attaquer
     */
    public boolean cannotAttack() {
        for (TerritoireModel territoireModel : liste_territoireModel) {
            if (territoireModel.getDice() > 1 && territoireModel.hasEnemyNear()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cette méthode ajoute un dé à un des territoires du player au hasard
     * Renvoie true si a pu ajouter un dé, false sinon
     *
     * @return
     */
    private boolean distribuerUnDe() {
        // On choisit un index aléatoire
        int index = (int) (Math.random() * liste_territoireModel.size());
        Iterator<TerritoireModel> iter = liste_territoireModel.iterator();
        // Et on parcoure l'itérateur jusqu'à lui
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        // Ensuite on tente d'ajouter un dé à ce territoire
        boolean addedDice = false;
        boolean goneThroughAll = false;
        while (!addedDice) {
            try {
                iter.next().addDice();
                addedDice = true; // Cette ligne ne s'exécute pas si une exception est levée
            } catch (TerritoireModel.TooManyDices ignored) {
            } catch (NoSuchElementException e) {
                if (goneThroughAll) {
                    // Si on a déjà fait une fois le tour de la liste sans parvenir à rajouter de dé c'est que tous les territoires sont pleins
                    return false;
                }
                goneThroughAll = true;
                iter = liste_territoireModel.iterator(); // Si on a atteint la fin on reset l'itérateur pour passer par les éventuels territoires manqués au début
            }
        }
        return true;
    }

    public boolean isAlive() {
        return this.liste_territoireModel.size() > 0;
    }

    /**
     * Exception levée lorsque le territoire attaquant a un seul dé
     */
    public static class NotEnoughForce extends Exception {
        NotEnoughForce() {
            super();
        }
    }

    /**
     * Exception levée lorsque le player essaye d'attaquer avec un territoire qu'il ne possède pas
     */
    public static class DoesntOwnTerritory extends Exception {
        DoesntOwnTerritory() {
            super();
        }
    }

    /**
     * Exception levée lorsque le player essaye d'attaquer un territoire qui n'est pas voisin
     */
    public static class TerritoryTooFar extends Exception {
        TerritoryTooFar() {
            super();
        }
    }

    /**
     * Exception levée lorsque le player essaye d'attaquer un territoire qui lui appartient
     */
    public static class CannotAttackOwnTerritory extends Exception {
        CannotAttackOwnTerritory() {
            super();
        }
    }
}

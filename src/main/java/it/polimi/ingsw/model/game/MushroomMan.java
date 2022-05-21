package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.protocol.updates.IslandsUpdate;
import it.polimi.ingsw.protocol.updates.SchoolBoardUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Character card Mushroom man. Effect: Choose a color of Student; during the influence calculation
 * this turn, that color adds no influence.
 */
public class MushroomMan extends CharacterCard
{
    /**
     * The selected color
     */
    private SchoolColor color;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    MushroomMan(Game game) throws NullPointerException
    {
        super(game);

        // MushroomMan's cost
        this.cost = 3;

        color = null;
    }

    @Override
    public boolean isPlayable()
    {
        // This card is playable only before the movement of mother nature
        return !instance.motherNatureMoved;
    }

    @Override
    public boolean isValidAction(ExpertGameAction action)
    {
        /**
         * This differs from the other cards because in this case you have to select a color and
         * continue with the active card until mother nature is actually moved. So you first need to
         * accept the color selection and, once it is selected, you have to accept only base
         * commands
         */
        // If it is activated I accept only the SELECT_COLOR action once and after only base actions
        return (action == ExpertGameAction.SELECT_COLOR && color == null)
                || (action == ExpertGameAction.BASE_ACTION && color != null);
    }

    @Override
    public void applyAction() throws NoSuchElementException
    {
        // I have to check if mother nature has been moved.
        // If so i can disable the card
        if (!activated)
            return;

        // If the color is not already selected, I take the player selection
        if (color == null)
        {
            Player selectedPlayer = instance.getSelectedPlayer()
                    .orElseThrow(() -> new NoSelectedPlayerException("[MushroomMan]"));
            if (selectedPlayer.getSelectedColors().size() != 1)
                throw new NoSelectedStudentsException("[MushroomMan]");

            color = selectedPlayer.getSelectedColors().get(0);
        }

        if (instance.motherNatureMoved)
            this.deactivate();
    }

    @Override
    public int computePlayerInfluence(Player player, int island)
            throws NoSuchElementException, IndexOutOfBoundsException, NullPointerException
    {
        // If the card is not active i ask the instance
        if (!activated)
            return instance.computePlayerInfluence(player, island);

        if (island < 0 || island > instance.islands.size())
            throw new IslandIndexOutOfBoundsException("[MushroomMan]");

        if (player == null)
            throw new NullPointerException("[MushroomMan] player null");

        if (color == null)
            throw new NoSelectedColorException("[MushroomMan]");

        Island currentIsland = instance.islands.get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream().map((p) -> {
            // Effect of the card: the selected color adds no influence
            if (p.getColor() != color)
                return currentIsland.getStudentsByColor(p.getColor());
            else
                return 0;
        }).reduce(0, Integer::sum);

        // Add the influence from the towers
        influence += currentIsland.getTowers().stream()
                .filter(t -> t.getColor().equals(player.getColor())).count();

        return influence;
    }

    @Override
    public void computeInfluence() throws NoSuchElementException
    {
        computeInfluence(instance.motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                "[MushroomMan] No mother nature index, is the game initialized?")));
    }

    @Override
    public void computeInfluence(int island) throws IndexOutOfBoundsException
    {
        if (island < 0 || island >= instance.islands.size())
            throw new IslandIndexOutOfBoundsException("[MushroomMan]");

        Island currentIsland = instance.islands.get(island);

        // Check if the island has a no entry tile
        // and if so i delete it and update the virtual view
        if (currentIsland.getNoEntryTiles() > 0)
        {
            currentIsland.removeNoEntryTile();

            // If the subscriber is present i have to notify
            if (instance.subscriber.isPresent())
            {
                instance.subscriber.get().onNext(
                        new IslandsUpdate(new ArrayList<Island>(instance.islands),instance.motherNatureIndex.get()));

                // I update also all the character cards payload
                for (CharacterCard card :instance.characterCards)
                    card.notifySubscriber();
            }
            return;
        }

        // Get the player with more influence, if there is any
        List<Player> sortedPlayers = instance.players.stream().sorted(
                        (p1, p2) -> computePlayerInfluence(p2, island) - computePlayerInfluence(p1, island))
                .collect(Collectors.toList());

        // Check if the first player has more influence than the second one
        if (computePlayerInfluence(sortedPlayers.get(0),
                island) > computePlayerInfluence(sortedPlayers.get(1), island))
        {
            // This player has more influence then all others
            Player influencer = sortedPlayers.get(0);

            // Remove all currently placed towers on the island
            List<Tower> towersToRemove = currentIsland.getTowers();
            currentIsland.removeAllTowers();

            // Move every tower to its original player's board
            towersToRemove.forEach(t -> {
                // Find the tower's player and put the tower in his board
                instance.players.forEach(p -> {
                    if (p.getColor().equals(t.getColor()))
                        p.getBoard().addTower(t);
                });
            });

            // Move the influencer's towers to the island
            List<Tower> towersToAdd = influencer.getBoard().getTowers().subList(0,
                    Integer.min(towersToRemove.size(), influencer.getBoard().getTowers().size()));

            if (towersToAdd.size() == 0)
            {
                // The island has no tower
                Tower towerToMove = influencer.getBoard().getTowers().get(0);
                currentIsland.addTower(towerToMove);
                influencer.getBoard().removeTower(towerToMove);
            } else
            {
                towersToAdd.forEach(t -> {
                    // Remove the tower from the player's board
                    influencer.getBoard().removeTower(t);

                    // Add the tower to the island
                    currentIsland.addTower(t);
                });
            }

            // Check if there are islands that can be merged
            for (int i = 0; i < instance.islands.size(); i++)
            {
                Island currIsland = instance.islands.get(i);
                Island nextIsland = instance.islands.get((i + 1) % instance.islands.size());

                // Check if two consecutive islands have the same color of towers
                if (currIsland.getTowers().size() > 0 && nextIsland.getTowers().size() > 0 &&
                        currIsland.getTowers().get(0).getColor() == nextIsland.getTowers().get(0).getColor())
                {
                    // Mother must step back if it is placed after the current island or
                    // if it is on the last island or if the current island is the last one
                    if (motherNatureIndex.get() > islands.indexOf(currIsland) ||
                            motherNatureIndex.get() == (islands.size() - 1) ||
                            islands.indexOf(currIsland) == (islands.size() - 1))
                    {
                        // In case of 0 index, mother nature goes to islands.size - 2 because we will remove an island
                        motherNatureIndex = getMotherNatureIndex().get() == 0 ?
                                Optional.of(islands.size() - 2) : Optional.of(getMotherNatureIndex().get() - 1);
                    }

                    // Merge the two islands and remove one
                    currIsland.mergeIsland(nextIsland);
                    instance.islands.remove(nextIsland);

                    // There could be three consecutive islands that could be merged
                    i--;
                }
            }

            // After moving the towers we need to notify the observer
            if (instance.subscriber.isPresent())
            {
                // I do that for all the players and not just the 2 because it is much simpler
                // and in the bast case we send 2 boards instead of 4
                for (Player player : instance.players)
                    instance.subscriber.get()
                            .onNext(new SchoolBoardUpdate(player.getBoard(), player.getNickname()));

                instance.subscriber.get().onNext(
                        new IslandsUpdate(new ArrayList<Island>(islands), motherNatureIndex.get()));
            }

            // If there are only 3 islands so the game ends
            if (instance.islands.size() <= 3)
                throw new EndGameException("[MushroomMan]");
        }
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MUSHROOM_MAN;
    }

    @Override
    public void deactivate()
    {
        color = null;
        super.deactivate();
    }
}

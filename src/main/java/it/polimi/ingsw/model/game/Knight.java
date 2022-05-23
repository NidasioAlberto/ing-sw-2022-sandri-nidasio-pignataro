package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.IslandIndexOutOfBoundsException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.protocol.updates.IslandsUpdate;
import it.polimi.ingsw.protocol.updates.SchoolBoardUpdate;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Character card Knight. Effect: During the influence calculation this turn, you count as having 2
 * more influence.
 */
public class Knight extends CharacterCard
{
    @Serial
    private static final long serialVersionUID = 1353974536712470505L;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Knight(Game game) throws NullPointerException
    {
        super(game);

        // Knight's cost
        this.cost = 2;
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
        // This card doesn't have a connected action, so as long as the action is a non-expert one,
        // it will be good
        return action == ExpertGameAction.BASE_ACTION;
    }

    @Override
    public void applyAction()
    {
        // If the card is not currently activated I do nothing
        if (!activated)
            return;

        // This card deactivates when mother nature has already been moved
        if (instance.motherNatureMoved)
            this.deactivate();
    }

    @Override
    public int computePlayerInfluence(Player player, int island)
            throws NoSuchElementException, IndexOutOfBoundsException, NullPointerException
    {
        if (island < 0 || island > instance.islands.size())
            throw new IslandIndexOutOfBoundsException("[Knight]");

        if (player == null)
            throw new NullPointerException("[Knight] player null");

        // I compute the player influence only if the card is active
        if (!activated)
            return instance.computePlayerInfluence(player, island);

        Island currentIsland = instance.islands.get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream()
                .map(p -> currentIsland.getStudentsByColor(p.getColor())).reduce(0, Integer::sum);

        // Add the influence from the towers
        influence += currentIsland.getTowers().stream()
                .filter(t -> t.getColor().equals(player.getColor())).count();

        Player currentPlayer = instance.getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[Knight]"));

        // Effect of the card: the current player gets 2 more influence points
        if (player == currentPlayer)
            influence += 2;

        return influence;
    }

    @Override
    public void computeInfluence() throws NoSuchElementException
    {
        computeInfluence(instance.motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                "[Knight] No mother nature index, is the game initialized?")));
    }

    @Override
    public void computeInfluence(int island) throws IndexOutOfBoundsException
    {
        if (island < 0 || island >= instance.islands.size())
            throw new IslandIndexOutOfBoundsException("[Knight]");

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
                throw new EndGameException("[Knight]");
        }
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.KNIGHT;
    }
}

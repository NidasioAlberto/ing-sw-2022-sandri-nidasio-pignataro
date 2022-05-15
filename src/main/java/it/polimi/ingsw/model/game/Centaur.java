package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.IslandIndexOutOfBoundsException;
import it.polimi.ingsw.protocol.updates.IslandsUpdate;
import it.polimi.ingsw.protocol.updates.SchoolBoardUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Character card Centaur. Effect: When resolving a computeInfluence on an Island, Towers do not
 * count towards influence.
 */
public class Centaur extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Centaur(Game game) throws NullPointerException
    {
        super(game);

        // Centaur's cost
        this.cost = 3;
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
        // As long as the action is base action, for this card is ok
        return action == ExpertGameAction.BASE_ACTION;
    }

    @Override
    public void applyAction()
    {
        // I have to check if mother nature has been moved.
        // If so i can disable the card
        if (!activated)
            return;

        if (instance.motherNatureMoved)
            this.deactivate();
    }

    @Override
    public int computePlayerInfluence(Player player, int island)
            throws NoSuchElementException, IndexOutOfBoundsException
    {
        // If the card is activated i apply the effect of this method
        if (!activated)
            return instance.computePlayerInfluence(player, island);

        if (island < 0 || island >= instance.islands.size())
            throw new IslandIndexOutOfBoundsException("[Centaur]");

        if (player == null)
            throw new NullPointerException("[Centaur] player null");

        Island currentIsland = instance.islands.get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream()
                .map(p -> currentIsland.getStudentsByColor(p.getColor())).reduce(0, Integer::sum);

        return influence;
    }

    public void computeInfluence() throws NoSuchElementException
    {
        computeInfluence(instance.motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                "[Game] No mother nature index, is the game initialized?")));
    }

    public void computeInfluence(int island) throws IndexOutOfBoundsException
    {
        if (island < 0 || island >= instance.islands.size())
            throw new IslandIndexOutOfBoundsException("[Game]");

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
                currentIsland.addTower(influencer.getBoard().getTowers().get(0));
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
                    // if it is in the last island
                    if (instance.motherNatureIndex.get() > instance.islands.indexOf(currIsland) ||
                            instance.motherNatureIndex.get() == (instance.islands.size() - 1))
                    {
                        instance.motherNatureIndex = Optional.of(getMotherNatureIndex().get() - 1);
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
                throw new EndGameException("[Game]");
        }
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.CENTAUR;
    }
}

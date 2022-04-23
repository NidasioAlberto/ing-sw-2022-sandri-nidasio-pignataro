package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.IslandIndexOutOfBoundsException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;

import java.util.NoSuchElementException;

/**
 * Character card Knight. Effect: During the influence calculation this turn, you count as having 2
 * more influence.
 */
public class Knight extends CharacterCard
{
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
    public CharacterCardType getCardType()
    {
        return CharacterCardType.KNIGHT;
    }
}

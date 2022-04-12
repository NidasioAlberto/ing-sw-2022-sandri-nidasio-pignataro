package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

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
    public boolean isPlayable() throws NoSuchElementException
    {
        // This card is playable only before the movement of mother nature
        return !instance.motherNatureMoved;
    }

    @Override
    public boolean isValidAction(ExpertGameAction action)
    {
        // This card doesn't have a connected action, so as long as the action is a non-expert one, it will be good
        return action == ExpertGameAction.ACTION_BASE;
    }

    @Override
    public void applyAction()
    {
        // If the card is not currently activated I do nothing
        if(!activated)
            return;

        //This card deactivates when mother nature has already been moved
        if(instance.motherNatureMoved)
            this.deactivate();
    }

    //TODO problema perchè le varie chiamate non fanno riferimento a instance
    @Override
    public int computePlayerInfluence(Player player, int island) throws NoSuchElementException, IndexOutOfBoundsException, NullPointerException
    {
        if(island < 0 || island > instance.islands.size())
            throw new IndexOutOfBoundsException("[Game] island index out of bounds");

        if(player == null)
            throw new NullPointerException("[Game] player null");

        Island currentIsland = instance.islands.get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream()
                .map(p -> currentIsland.getStudentsByColor(p.getColor())).reduce(0, Integer::sum);

        // Add the influence from the towers
        influence += currentIsland.getTowers().stream()
                .filter(t -> t.getColor().equals(player.getColor())).count();

        Player currentPlayer = instance.getSelectedPlayer().orElseThrow(
                () -> new NoSuchElementException("[Knight] No selected player")
        );

        // Effect of the card: the current player gets 2 more influence points
        if(player == currentPlayer)
            influence += 2;

        return influence;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.KNIGHT;
    }
}

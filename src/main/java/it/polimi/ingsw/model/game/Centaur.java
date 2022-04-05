package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

import java.util.NoSuchElementException;

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
        //This card is playable if the previous action is before the movement of mother nature
        //or if the previous action is empty
        if(instance.previousAction.isEmpty())
            return true;

        //It is the index of the game action enumeration indexing the previous action
        int indexPrevAction;
        //It is the index of the game action enumeration indexing the move mother nature action
        int indexMotherAction;

        //Take the indexes
        for(indexPrevAction = 0; GameAction.values()[indexPrevAction] != instance.previousAction.get(); indexPrevAction++);
        for(indexMotherAction = 0; GameAction.values()[indexMotherAction] != GameAction.MOVE_MOTHER_NATURE; indexMotherAction++);

        //If we are before the mother nature movement then we can call the card
        return indexPrevAction < indexMotherAction;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        //If the card is activated and we pass the mother nature movement I can deactivate the card
        if(activated && action == GameAction.SELECT_CLOUD_TILE)
            deactivate();
        return instance.isValidAction(action);
    }

    @Override
    public void applyAction() {}

    @Override
    public int computePlayerInfluence(Player player, int island) throws NoSuchElementException, IndexOutOfBoundsException
    {
        //If the card is activated i apply the effect of this method
        if(!activated)
            return instance.computePlayerInfluence(player, island);

        if(island < 0 || island > instance.islands.size())
            throw new IndexOutOfBoundsException("[Game] island index out of bounds");

        if(player == null)
            throw new NullPointerException("[Game] player null");

        Island currentIsland = instance.islands.get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream()
                .map(p -> currentIsland.getStudentsByColor(p.getColor())).reduce(0, Integer::sum);

        return influence;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.CENTAUR;
    }
}

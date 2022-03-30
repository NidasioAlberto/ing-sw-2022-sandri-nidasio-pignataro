package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

/**
 * Character card Shaman. Effect: During this turn, you take control of any number of Professors
 * even if you have the same number of Students as the player who currently controls them
 */
public class Shaman extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Shaman(Game game) throws NullPointerException
    {
        super(game);

        // Shaman's cost
        this.cost = 2;
    }

    @Override
    public boolean isPlayable()
    {
        //This card is playable if the previous action is before the movement of mother nature
        //or if the previous action is empty
        if(instance.getGameAction().isEmpty())
            return true;

        //It is the index of the game action enumeration indexing the previous aciton
        int indexPrevAction;
        //It is the index of the game action enumeration indexing the move mother nature action
        int indexMotherAction;

        //Take the indexes
        for(indexPrevAction = 0; GameAction.values()[indexPrevAction] != instance.getGameAction().get(); indexPrevAction++);
        for(indexMotherAction = 0; GameAction.values()[indexMotherAction] != GameAction.MOVE_MOTHER_NATURE; indexMotherAction++);

        //If we are before the mother nature movement then we can call the card
        return indexPrevAction < indexMotherAction;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        return false;
    }

    @Override
    public void applyAction()
    {}

    @Override
    public void conquerProfessors()
    {

    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.SHAMAN;
    }
}

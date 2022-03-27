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
        return false;
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
    public void computeInfluence()
    {

    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.SHAMAN;
    }
}

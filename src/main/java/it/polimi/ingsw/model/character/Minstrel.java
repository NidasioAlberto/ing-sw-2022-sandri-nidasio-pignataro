package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

/**
 * Character card Minstrel. Effect: You may exchange up to 2 Students between your Entrance and your
 * Dining Room.
 */
public class Minstrel extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Minstrel(Game game) throws NullPointerException
    {
        super(game);

        // Minstrel's cost
        this.cost = 1;
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
    public Game applyAction()
    {
        return this;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MINSTREL;
    }
}

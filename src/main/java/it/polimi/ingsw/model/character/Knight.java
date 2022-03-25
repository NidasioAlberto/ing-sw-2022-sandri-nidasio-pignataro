package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

import java.util.Optional;

/**
 * Character card Knight. Effect:
 * During the influence calculation this turn, you
 * count as having 2 more influence.
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

        //Knight's cost
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
    public Game applyAction()
    {
        return this;
    }

    @Override
    public void conquer()
    {

    }

    @Override
    public CharacterCardType getCardType() { return CharacterCardType.KNIGHT; }
}

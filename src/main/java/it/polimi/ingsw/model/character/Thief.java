package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

/**
 * Character card Thief. Effect: Choose a type of Student; every player (including yourself) must
 * return 3 Students of that type from their Dining Room to the bag. If any player has fewer than 3
 * Students of that type, return as many Students as they have.
 */
public class Thief extends CharacterCard
{

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Thief(Game game) throws NullPointerException
    {
        super(game);

        // Thief's cost
        this.cost = 3;
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
        return CharacterCardType.THIEF;
    }
}

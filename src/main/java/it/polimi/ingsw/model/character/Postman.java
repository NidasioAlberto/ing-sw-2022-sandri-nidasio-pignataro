package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

import java.util.Optional;

/**
 * Character card Postman. Effect:
 * You may move Mother Nature up to 2 additional Islands
 * than is indicated by the Assistant card you've played.
 */
public class Postman extends CharacterCard
{

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Postman(Game game) throws NullPointerException
    {
        super(game);

        //Postman's cost
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
    public void moveMotherNature(int steps)
    {

    }

    @Override
    public CharacterCardType getCardType() { return CharacterCardType.POSTMAN; }
}

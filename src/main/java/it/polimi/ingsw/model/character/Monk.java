package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

/**
 * Character card Monk. Effect:
 * Take 1 Student from this card and place it on an island
 * of your choice. Then, draw a new Student from the bag
 * and place it on this card
 */
public class Monk extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    public Monk(Game game) throws NullPointerException
    {
        super(game);

        //Monk's cost
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
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public CharacterCardType getCardType() { return CharacterCardType.MONK; }
}

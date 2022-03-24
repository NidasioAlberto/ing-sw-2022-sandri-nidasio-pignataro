package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

import java.util.Optional;

/**
 * Character card Shaman. Effect:
 * During this turn, you take control of any number of Professors
 * even if you have the same number of Students as the player
 * who currently controls them
 */
public class Shaman extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    public Shaman(Game game) throws NullPointerException
    {
        super(game);

        //Shaman's cost
        this.cost = 2;
    }

    @Override
    public boolean isPlayable()
    {
        return false;
    }

    @Override
    public Optional<Game> isValidAction(GameAction action)
    {
        return Optional.empty();
    }

    @Override
    public void activate()
    {

    }

    @Override
    public void deactivate()
    {

    }

    @Override
    public void conquer()
    {

    }

    @Override
    public CharacterCardType getCardType() { return CharacterCardType.SHAMAN; }
}

package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

import java.util.Optional;

/**
 * Character card Minstrel. Effect:
 * You may exchange up to 2 Students between your Entrance
 * and your Dining Room.
 */
public class Minstrel extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    public Minstrel(Game game) throws NullPointerException
    {
        super(game);

        //Minstrel's cost
        this.cost = 1;
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
    public CharacterCardType getCardType() { return CharacterCardType.MINSTREL; }
}

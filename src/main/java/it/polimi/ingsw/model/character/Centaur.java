package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

import java.util.Optional;

/**
 * Character card Centaur. Effect:
 * When resolving a Conquering on an Island,
 * Towers do not count towards influence.
 */
public class Centaur extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    public Centaur(Game game) throws NullPointerException
    {
        super(game);

        //Centaur's cost
        this.cost = 3;
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
    public CharacterCardType getCardType() { return CharacterCardType.CENTAUR; }
}

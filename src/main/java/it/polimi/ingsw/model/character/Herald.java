package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

import java.util.Optional;
/**
 * Character card Herald. Effect:
 * Choose an Island and resolve the Island as if
 * Mother Nature had ended her movement there.
 * Mother Nature will still move and the Island where she ends
 * her movement will also be resolved.
 */
public class Herald extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    public Herald(Game game) throws NullPointerException
    {
        super(game);

        //Herald's cost
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
    public CharacterCardType getCardType()
    {
        return CharacterCardType.HERALD;
    }
}

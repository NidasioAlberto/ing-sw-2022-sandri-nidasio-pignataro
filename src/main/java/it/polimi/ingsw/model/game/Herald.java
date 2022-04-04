package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.GameAction;

import java.util.NoSuchElementException;

/**
 * Character card Herald. Effect: Choose an Island and resolve the Island as if Mother Nature had
 * ended her movement there. Mother Nature will still move and the Island where she ends her
 * movement will also be resolved.
 */
public class Herald extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Herald(Game game) throws NullPointerException
    {
        super(game);

        // Herald's cost
        this.cost = 3;
    }

    @Override
    public boolean isPlayable()
    {
        //This card can be played at all time
        return true;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        //If the card is deactivated i return the instance isValidAction
        if(!activated)
        {
            return instance.isValidAction(action);
        }

        //Only the island selection is allowed
        return action == GameAction.SELECT_ISLAND;
    }

    @Override
    public void applyAction() throws NoSuchElementException
    {
        //Compute the influence in that island
        instance.computeInfluence(instance.getSelectedPlayer().orElseThrow(
                () -> new NoSuchElementException("[Herald] No player selected"))
                .getSelectedIsland().orElseThrow(() -> new NoSuchElementException("[Herald] No island selected")));

        //Disable the card
        deactivate();
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.HERALD;
    }
}

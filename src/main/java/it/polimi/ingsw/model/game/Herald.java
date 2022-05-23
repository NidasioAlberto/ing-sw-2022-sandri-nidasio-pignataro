package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.exceptions.NoSelectedIslandException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;

import java.io.Serial;
import java.util.NoSuchElementException;

/**
 * Character card Herald. Effect: Choose an Island and resolve the Island as if Mother Nature had
 * ended her movement there. Mother Nature will still move and the Island where she ends her
 * movement will also be resolved.
 */
public class Herald extends CharacterCard
{
    @Serial
    private static final long serialVersionUID = -804589700829197784L;

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
    public boolean isValidAction(ExpertGameAction action)
    {
        // If active I accept only SELECT_ISLAND
        return action == ExpertGameAction.SELECT_ISLAND;
    }

    @Override
    public void applyAction() throws NoSuchElementException
    {
        // If the card is not currently activated I do nothing
        if(!activated)
            return;

        //Compute the influence in that island
        instance.computeInfluence(instance.getSelectedPlayer().orElseThrow(
                () -> new NoSelectedPlayerException("[Herald]"))
                .getSelectedIsland().orElseThrow(() -> new NoSelectedIslandException("[Herald]")));

        //Disable the card
        deactivate();
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.HERALD;
    }
}

package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

import java.util.NoSuchElementException;

/**
 * Character card Knight. Effect: During the influence calculation this turn, you count as having 2
 * more influence.
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

        // Knight's cost
        this.cost = 2;
    }

    @Override
    public boolean isPlayable() throws NoSuchElementException
    {
        GameAction previousAction = instance.getGameAction().orElseThrow(
                () -> new NoSuchElementException("[Knight] There is no previous action")
        );

        // This card must be played before the action MOVE_MOTHER_NATURE
        return previousAction == GameAction.PLAY_ASSISTANT_CARD ||
                previousAction == GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING ||
                previousAction == GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        // This card doesn't have a connected action
        return instance.isValidAction(action);
    }

    @Override
    public void applyAction()
    {
        //TODO problema perchè potrebbe essere giocata prima del calcolo dell'influenza
    }

    @Override
    public void computeInfluence()
    {

    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.KNIGHT;
    }
}

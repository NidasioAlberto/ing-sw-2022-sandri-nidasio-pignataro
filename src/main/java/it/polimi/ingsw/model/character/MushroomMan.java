package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.SchoolColor;

import java.util.NoSuchElementException;

/**
 * Character card Mushroom man. Effect: Choose a color of Student; during the influence calculation
 * this turn, that color adds no influence.
 */
public class MushroomMan extends CharacterCard
{
    /**
     * The color selected by the player
     */
    SchoolColor color;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    MushroomMan(Game game) throws NullPointerException
    {
        super(game);

        // MushroomMan's cost
        this.cost = 3;

        color = null;
    }


    @Override
    public boolean isPlayable() throws NoSuchElementException
    {
        GameAction previousAction = instance.getGameAction().orElseThrow(
                () -> new NoSuchElementException("[MushroomMan] There is no previous action")
        );

        // This card must be played before the action MOVE_MOTHER_NATURE
        return previousAction == GameAction.PLAY_ASSISTANT_CARD ||
                previousAction == GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING ||
                previousAction == GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        // If the card is not active I return the instance validation
        if (!activated)
        {
            return instance.isValidAction(action);
        }

        // If it is activated I accept only the SELECT_COLOR action
        return action == GameAction.SELECT_COLOR;
    }

    @Override
    public void applyAction() throws NoSuchElementException
    {
        GameAction previousAction = instance.getGameAction().orElseThrow(
                () -> new NoSuchElementException("[MushroomMan] There is no previous action")
        );

        if(previousAction != GameAction.MOVE_MOTHER_NATURE)
            return;


    }

    @Override
    public void computeInfluence()
    {
        //TODO problema perchè potrebbe essere giocata prima del calcolo dell'influenza
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MUSHROOM_MAN;
    }

    @Override
    protected void deactivate()
    {
        color = null;
        super.deactivate();
    }
}

package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Player;

import java.util.NoSuchElementException;

/**
 * Character card Postman. Effect: You may move Mother Nature up to 2 additional Islands than is
 * indicated by the Assistant card you've played.
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

        // Postman's cost
        this.cost = 1;
    }

    @Override
    public boolean isPlayable()
    {
        // This card is playable only before the movement of mother nature
        return !instance.motherNatureMoved;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        // If the card is not activated the action isn't valid
        if(!activated)
        {
            return false;
        }

        // I don't have to intercept any action
        return true;
    }

    // TODO capire come gestirla
    @Override
    public void applyAction()
    {}

    @Override
    public boolean isValidMotherNatureMovement(int steps)
    {
        if(!activated)
            return instance.isValidMotherNatureMovement(steps);

        //I have to check if the current player can do this movement
        Player currentPlayer        = instance.getSelectedPlayer().orElseThrow(() -> new NoSuchElementException("[Postman] No player selected"));
        AssistantCard selectedCard  = currentPlayer.getSelectedCard()
                        .orElseThrow(() -> new NoSuchElementException("[Postman] Player didn't select assistant card"));

        //Here occurs the modification
        return selectedCard.getSteps() + 2 >= steps && steps >= 1;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.POSTMAN;
    }
}

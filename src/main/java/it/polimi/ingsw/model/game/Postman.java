package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedAssistantCardException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;

import java.io.Serial;
import java.util.NoSuchElementException;

/**
 * Character card Postman. Effect: You may move Mother Nature up to 2 additional Islands than is
 * indicated by the Assistant card you've played.
 */
public class Postman extends CharacterCard
{

    @Serial
    private static final long serialVersionUID = -6741083535931506838L;

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
    public boolean isValidAction(ExpertGameAction action)
    {
        // I don't have to intercept any action
        return action == ExpertGameAction.BASE_ACTION;
    }

    @Override
    public void applyAction()
    {
        // I have to check if mother nature has been moved.
        // If so i can disable the card
        if (!activated)
            return;

        if (instance.motherNatureMoved)
            this.deactivate();
    }

    @Override
    public boolean isValidMotherNatureMovement(int steps) throws NoSuchElementException
    {
        if (!activated)
            return instance.isValidMotherNatureMovement(steps);

        // I have to check if the current player can do this movement
        Player currentPlayer = instance.getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[Postman]"));
        AssistantCard selectedCard = currentPlayer.getSelectedCard().orElseThrow(
                () -> new NoSelectedAssistantCardException("[Postman]"));

        // Here occurs the modification
        return selectedCard.getSteps() + 2 >= steps && steps >= 1;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.POSTMAN;
    }
}

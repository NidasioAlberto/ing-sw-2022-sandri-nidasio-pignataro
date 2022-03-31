package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Player;

import java.util.NoSuchElementException;
import java.util.Optional;

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
        //This card is playable if the previous action is before the movement of mother nature
        //or if the previous action is empty
        if(instance.getGameAction().isEmpty())
            return true;

        //It is the index of the game action enumeration indexing the previous action
        int indexPrevAction;
        //It is the index of the game action enumeration indexing the move mother nature action
        int indexMotherAction;

        //Take the indexes
        for(indexPrevAction = 0; GameAction.values()[indexPrevAction] != instance.getGameAction().get(); indexPrevAction++);
        for(indexMotherAction = 0; GameAction.values()[indexMotherAction] != GameAction.MOVE_MOTHER_NATURE; indexMotherAction++);

        //If we are before the mother nature movement then we can call the card
        return indexPrevAction < indexMotherAction;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        //If it is activated we receive a select cloud tile action, then I deactivate the card
        if(activated && action == GameAction.SELECT_CLOUD_TILE)
        {
            deactivate();
        }

        //I don't have to intercept any action
        return instance.isValidAction(action);
    }

    @Override
    public void applyAction()
    {}

    @Override
    public boolean isValidMotherNatureMovement(int steps)
    {
        if(!activated)
            return instance.isValidMotherNatureMovement(steps);

        //I have to check if the current player can do this movement
        Player currentPlayer        = instance.getSelectedPlayer().orElseThrow(() -> new NoSuchElementException("[Game] No player selected"));
        AssistantCard selectedCard  = currentPlayer
                .getCardsList()
                .get(currentPlayer
                        .getSelectedCard()
                        .orElseThrow(() -> new NoSuchElementException("[Game] Player didn't select assistant card")));

        //Here occurs the modification
        return selectedCard.getSteps() + 2 >= steps;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.POSTMAN;
    }
}

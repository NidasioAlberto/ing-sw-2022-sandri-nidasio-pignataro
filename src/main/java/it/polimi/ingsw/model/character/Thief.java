package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

/**
 * Character card Thief. Effect: Choose a type of Student; every player (including yourself) must
 * return 3 Students of that type from their Dining Room to the bag. If any player has fewer than 3
 * Students of that type, return as many Students as they have.
 */
public class Thief extends CharacterCard
{

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Thief(Game game) throws NullPointerException
    {
        super(game);

        // Thief's cost
        this.cost = 3;
    }

    @Override
    public boolean isPlayable()
    {
        // This card can be played everytime
        return true;
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

    /**
     * Move max 3 students, of the color selected by the current player, from each player's dining to the bag.
     *
     * @throws NoSuchElementException If there isn't a current player an exception is thrown.
      */
    @Override
    public void applyAction() throws NoSuchElementException
    {
        // Get the player's list
        instance.getPlayerTableList().stream().forEach(p ->
        {
            // For each player remove max 3 students from the dining room
            IntStream.range(0, 4).forEach(i ->
            {
                p.getBoard().removeStudentFromDining(
                    instance.getSelectedPlayer().orElseThrow(
                    () -> new NoSuchElementException("[Thief] No selected player"))
                    // Of the color selected from the current player
                    .getSelectedColors().stream().findFirst().orElseThrow(
                    () -> new NoSuchElementException("[Thief] No selected color"))
                )
                // The student removed is replaced in the bag
                .ifPresent(s -> instance.addStudentToBag(s));
            });
        });

        if (!firstUsed)
        {
            cost += 1;
            firstUsed = true;
        }

        this.deactivate();
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.THIEF;
    }
}

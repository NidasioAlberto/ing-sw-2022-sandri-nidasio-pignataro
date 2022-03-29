package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;

import java.util.NoSuchElementException;

/**
 * Character card Minstrel. Effect: You may exchange up to 2 Students between your Entrance and your
 * Dining Room.
 */
public class Minstrel extends CharacterCard
{
    /**
     * To count the number of exchanges, it can't be greater than 2
     */
    private int exchangeCounter;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Minstrel(Game game) throws NullPointerException
    {
        super(game);

        // Minstrel's cost
        this.cost = 1;

        exchangeCounter = 0;
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

        if (exchangeCounter < 2)
        {
            // If it is activated I accept the
            // SWAP_STUDENT_FROM_ENTRANCE_TO_DINING action
            if (action == GameAction.SWAP_STUDENT_FROM_ENTRANCE_TO_DINING)
                return true;
            else
            {
                // The player doesn't want to do more exchanges
                this.deactivate();
                return instance.isValidAction(action);
            }

        }

        // If the player has already done 2 exchanges, the action isn't valid
        return false;
    }

    /**
     * Swap the selected students between entrance and dining.
     * The first color selected by the player must be the entrance student's color
     * and the second one must be the dining student's color.
     *
     * @throws NoSuchElementException If there isn't a current player
     * or the number of selected student is wrong
     * or there isn't a student of the selected color in the dining.
     */
    @Override
    public void applyAction() throws NoSuchElementException
    {
        // Get the current player
        Player currentPlayer = instance.getSelectedPlayer().orElseThrow(() -> new NoSuchElementException(
                "[Minstrel] No selected player"));

        // Check that the player has selected two students to swap
        if (currentPlayer.getSelectedColors().size() != 2)
        {
            throw new NoSuchElementException("[Minstrel] The number of students selected is not correct");
        }

        // Move the student from entrance to dining
        instance.putStudentToDining(instance.pickStudentFromEntrance());

        // Remove the student from the dining
        Student student = currentPlayer.getBoard().removeStudentFromDining(currentPlayer.getSelectedColors().get(1)).
                orElseThrow(() -> new NoSuchElementException
                        ("[Minstrel]  No students of the specified color inside the current player's dining"));

        // Add the student to the entrance
        currentPlayer.getBoard().addStudentToEntrance(student);

        if (!firstUsed)
        {
            cost += 1;
            firstUsed = true;
        }

        exchangeCounter += 1;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MINSTREL;
    }

    @Override
    protected void deactivate()
    {
        exchangeCounter = 0;
        super.deactivate();
    }
}

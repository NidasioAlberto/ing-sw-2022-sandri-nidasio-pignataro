package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.NoSelectedStudentsException;
import it.polimi.ingsw.model.exceptions.NoSuchStudentInDiningException;
import it.polimi.ingsw.model.exceptions.NoSuchStudentInEntranceException;
import it.polimi.ingsw.protocol.updates.SchoolBoardUpdate;

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
    public boolean isValidAction(ExpertGameAction action)
    {
        if (action == ExpertGameAction.SWAP_STUDENT_FROM_ENTRANCE_TO_DINING)
        {
            if (exchangeCounter < 2)
            {
                return true;
            }
        }

        // If he wants to do something different i can deactivate the card
        this.deactivate();

        // And accept if the action is a base action
        return action == ExpertGameAction.BASE_ACTION;
    }

    /**
     * Swap the selected students between entrance and dining. The first color selected by the
     * player must be the entrance student's color and the second one must be the dining student's
     * color.
     *
     * @throws NoSuchElementException If there isn't a current player or the number of selected
     *         student is wrong or there isn't a student of the selected color in the dining.
     */
    @Override
    public void applyAction() throws NoSuchElementException
    {
        // If the card is not currently activated i do nothing
        if (!activated)
            return;

        // Get the current player
        Player currentPlayer = instance.getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[Minstrel]"));

        // Check that the player has selected two students to swap
        if (currentPlayer.getSelectedColors().size() != 2)
            throw new NoSelectedStudentsException("[Minstrel]");

        // Take the student instance that needs to be moved.
        // IMPORTANT: I can't use pickStudentFromEntrance because if the second selected color
        // doesn't exist in dining, i would lose the entrance student due to immediate remove
        Student studentEntrance = currentPlayer.getBoard().getStudentsInEntrance().stream()
                .filter(s -> s.getColor() == currentPlayer.getSelectedColors().get(0)).findFirst()
                .orElseThrow(() -> new NoSuchStudentInEntranceException("[Minstrel]"));

        // Remove the student from the dining
        Student student = currentPlayer.getBoard()
                .removeStudentFromDining(currentPlayer.getSelectedColors().get(1))
                .orElseThrow(() -> new NoSuchStudentInDiningException("[Minstrel]"));

        // Add the student to the entrance
        currentPlayer.getBoard().addStudentToEntrance(student);

        // Add the student to the dining
        instance.putStudentToDining(studentEntrance);

        // Remove the entrance student from entrance
        currentPlayer.getBoard().removeStudentFromEntrance(studentEntrance);

        // Check if the player gain a professor
        instance.conquerProfessors();

        // I need to send the SchoolBoardUpdate because some students have changed
        if (instance.subscriber.isPresent())
        {
            for (Player player : instance.players)
                instance.subscriber.get()
                        .onNext(new SchoolBoardUpdate(player.getBoard(), player.getNickname()));

        }

        exchangeCounter += 1;

        // Deactivate in case we reached the max number of swaps
        if (exchangeCounter >= 2)
            this.deactivate();
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MINSTREL;
    }

    @Override
    public void deactivate()
    {
        exchangeCounter = 0;
        super.deactivate();
    }
}

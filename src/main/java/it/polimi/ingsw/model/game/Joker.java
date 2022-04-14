package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Character card Joker. Effect: You may take up to 3 Students from this card and replace them with
 * the same number of Students from your Entrance.
 */
public class Joker extends CharacterCard
{
    /**
     * To count the number of exchanges, it can't be greater than 3
     */
    private int exchangeCounter;

     /**
     * This class has a student list
     */
    private List<Student> students;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Joker(Game game) throws NullPointerException
    {
        super(game);

        // Joker's cost
        this.cost = 1;

        // Instance the list
        students = new ArrayList<Student>();
        students.add(instance.getStudentFromBag());
        students.add(instance.getStudentFromBag());
        students.add(instance.getStudentFromBag());
        students.add(instance.getStudentFromBag());
        students.add(instance.getStudentFromBag());
        students.add(instance.getStudentFromBag());

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
        if(action == ExpertGameAction.SWAP_STUDENT_FROM_CHARACTER_CARD_TO_ENTRANCE)
        {
            // If it is activated I accept the
            // SWAP_STUDENT_FROM_CHARACTER_CARD_TO_ENTRANCE
            if(exchangeCounter < 3)
            {
                return true;
            }
        }

        //If he wants to do something different i can deactivate the card
        this.deactivate();

        //And accept if the action is a base action
        return action == ExpertGameAction.ACTION_BASE;
    }

    /**
     *
     * @throws NoSuchElementException
     */
    @Override
    public void applyAction() throws NoSuchElementException
    {
        //If the card is not currently activated i do nothing
        if(!activated)
            return;

        // Get the current player
        Player currentPlayer = instance.getSelectedPlayer().orElseThrow(
                () -> new NoSuchElementException("[Joker] No selected player"));

        // Check that the player has selected two students to swap
        if(currentPlayer.getSelectedColors().size() != 2)
        {
            throw new NoSuchElementException("[Joker] The number of students selected is not correct");
        }

        // Get the selected student from the entrance
        Student entranceStudent = instance.pickStudentFromEntrance();

        // Get the selected student from the card
        Student cardStudent = students.stream().filter(s -> s.getColor() == currentPlayer.getSelectedColors().get(1))
                .findFirst().orElseThrow(
                () -> new NoSuchElementException("[Joker] No student of the selected color on the card")
        );

        // Remove the cardStudent from the card
        students.remove(cardStudent);

        // Add the cardStudent to the entrance
        currentPlayer.getBoard().addStudentToEntrance(cardStudent);

        // Add the entranceStudent to the card
        students.add(entranceStudent);

        exchangeCounter += 1;

        //If we hit 3 swaps i deactivate the card
        if(exchangeCounter >= 3)
            deactivate();
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.JOKER;
    }

    @Override
    protected void deactivate()
    {
        exchangeCounter = 0;
        super.deactivate();
    }
}

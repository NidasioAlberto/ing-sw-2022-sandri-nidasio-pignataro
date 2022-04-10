package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Character card Princess. Effect: Take 1 Student from this card and place it in your Dining Room.
 * Then, draw a new Student from the Bag and place it on this card.
 */
public class Princess extends CharacterCard
{
    /**
     * This card has a list of students as payload
     */
    private List<Student> students;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Princess(Game game) throws NullPointerException
    {
        super(game);

        // Princess' cost
        this.cost = 2;

        // Instance the list
        students = new ArrayList<Student>();
        students.add(instance.getStudentFromBag());
        students.add(instance.getStudentFromBag());
        students.add(instance.getStudentFromBag());
        students.add(instance.getStudentFromBag());
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

        // If it is activated I accept only the
        // MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING action
        return action == GameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING;
    }

    /**
     * Move a student from this card to the current player's dining room.
     *
     * @throws NoSuchElementException If there isn't a current player or a student,
     * of the color selected, on the card.
     */
    @Override
    public void applyAction() throws NoSuchElementException
    {
        // Take a student of the selected color from the card
        Student student = students.stream().filter(s -> s.getColor() == instance
                        .getSelectedPlayer().orElseThrow(
                            () -> new NoSuchElementException("[Princess] No selected player")
                        ).getSelectedColors().stream().findFirst().orElseThrow(
                            () -> new NoSuchElementException("[Princess] No selected color")
                        ))
                        .findFirst().orElseThrow(() -> new NoSuchElementException(
                            "[Princess] No students of the selected color on the card"));

        // Remove the student from the card
        students.remove(student);

        // Add the student to the current player's dining room
        instance.getSelectedPlayer().get().getBoard().addStudentToDiningRoom(student);

        // Check if the player gain a professor
        instance.conquerProfessors();

        // Add a new student on the card
        students.add(instance.getStudentFromBag());

        this.deactivate();
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.PRINCESS;
    }
}

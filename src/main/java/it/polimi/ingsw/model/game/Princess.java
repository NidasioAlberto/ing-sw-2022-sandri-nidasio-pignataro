package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.exceptions.NoSelectedColorException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.NoSuchStudentOnCardException;
import it.polimi.ingsw.protocol.updates.CharacterCardPayloadUpdate;

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
    public boolean isValidAction(ExpertGameAction action)
    {
        // If it is activated I accept only the
        // MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING action
        return action == ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING;
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
        //If the card is not currently activated i do nothing
        if(!activated)
            return;

        // Take a student of the selected color from the card
        Student student = students.stream().filter(s -> s.getColor() == instance
                        .getSelectedPlayer().orElseThrow(
                            () -> new NoSelectedPlayerException("[Princess]")
                        ).getSelectedColors().stream().findFirst().orElseThrow(
                            () -> new NoSelectedColorException("[Princess]")
                        ))
                        .findFirst().orElseThrow(() -> new NoSuchStudentOnCardException("[Princess]"));

        // Remove the student from the card
        students.remove(student);

        // Add the student to the current player's dining room
        instance.getSelectedPlayer().get().getBoard().addStudentToDiningRoom(student);

        // Check if the player gain a professor
        instance.conquerProfessors();

        // Add a new student on the card
        try
        {
            students.add(instance.getStudentFromBag());
        }
        catch(Exception e){}

        this.deactivate();

        // Notify the subscriber
        notifySubscriber();
    }

    @Override
    public void notifySubscriber()
    {
        // I have to find this character card index inside the list
        int index = 0;
        for(index = 0; index < instance.characterCards.size() && this != instance.characterCards.get(index); index++);

        if(instance.subscriber.isPresent())
            instance.subscriber.get().onNext(new CharacterCardPayloadUpdate(index, new ArrayList<Student>(students)));
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.PRINCESS;
    }

    public List<Student> getStudents()
    {
        return new ArrayList<>(students);
    }
}

package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.*;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Character card Monk. Effect: Take 1 Student from this card and place it on an island of your
 * choice. Then, draw a new Student from the bag and place it on this card
 */
public class Monk extends CharacterCard
{
    /**
     * Payload of this card is students that the player selects
     */
    private List<Student> students;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Monk(Game game) throws NullPointerException
    {
        super(game);

        // Monk's cost
        this.cost = 1;

        // Instance the list
        students = new ArrayList<Student>();

        //Draw 4 students from the bag
        //TODO think about how the view could be updated and could
        //access this list to show the players
        for(int i = 0; i < 4; i++)
        {
            students.add(game.getStudentFromBag());
        }
    }

    @Override
    public boolean isPlayable()
    {
        //This card can be played everytime
        return true;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        //If the card is not active I return the instance validation
        if(!activated)
        {
            return instance.isValidAction(action);
        }

        //If it is activated i accept only the
        //MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND action
        return action == GameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND;
    }

    @Override
    public void applyAction() throws NoSuchElementException
    {
        //Take the selected color
        SchoolColor selectedColor = instance.getSelectedPlayer()
                .orElseThrow(
                        () -> new NoSuchElementException("[Monk] No selected player")
                ).getSelectedColors().stream().findFirst().orElseThrow(
                        () -> new NoSuchElementException("[Monk] No selected color")
                );
        //Take the selected student from the card
        Student selectedStudent = students.stream()
                .filter(s -> s.getColor() == selectedColor)
                .findFirst().orElseThrow(
                        () -> new NoSuchElementException("[Monk] No suck student on card")
                );

        //Put the student to the island
        instance.putStudentToIsland(selectedStudent);
        //Remove the previous student from the card
        students.remove(selectedStudent);
        //Pick another student from the bag
        students.add(instance.getStudentFromBag());

        //Disable the card
        this.deactivate();
    }
    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MONK;
    }
}

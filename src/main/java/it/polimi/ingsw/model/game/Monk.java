package it.polimi.ingsw.model.game;

import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NoSelectedColorException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.NoSuchStudentOnCardException;
import it.polimi.ingsw.protocol.updates.CharacterCardPayloadUpdate;
import it.polimi.ingsw.protocol.updates.CharacterCardsUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Character card Monk. Effect: Take 1 Student from this card and place it on an island of your choice. Then, draw a new Student from the bag and
 * place it on this card
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
    }

    @Override
    public boolean isPlayable()
    {
        // This card can be played everytime
        return true;
    }

    @Override
    public void init()
    {
        // Draw 4 students from the bag
        // TODO think about how the view could be updated and could
        // access this list to show the players
        for (int i = 0; i < 4; i++)
        {
            students.add(instance.getStudentFromBag());
        }
    }

    @Override
    public CharacterCard clone()
    {
        // Create a new card
        Monk cloned = (Monk) createCharacterCard(this.getCardType(), instance);

        // Null the instance
        cloned.instance = null;
        // Copy the properties
        cloned.cost = this.cost;
        cloned.activated = this.activated;
        cloned.firstUsed = this.firstUsed;

        // Assign the students
        cloned.students = new ArrayList<>(students);

        return cloned;
    }

    @Override
    public boolean isValidAction(ExpertGameAction action)
    {
        // If active I accept only MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND
        return action == ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND;
    }

    @Override
    public void applyAction() throws NoSuchElementException
    {
        // If the card is not currently activated I do nothing
        if (!activated)
            return;

        // Take the selected color
        SchoolColor selectedColor = instance.getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[Monk]")).getSelectedColors()
                .stream().findFirst().orElseThrow(() -> new NoSelectedColorException("[Monk]"));
        // Take the selected student from the card
        Student selectedStudent = students.stream().filter(s -> s.getColor() == selectedColor).findFirst()
                .orElseThrow(() -> new NoSuchStudentOnCardException("[Monk]"));

        // Put the student to the island
        instance.putStudentToIsland(selectedStudent);
        // Remove the previous student from the card
        students.remove(selectedStudent);

        // Pick another student from the bag
        try
        {
            students.add(instance.getStudentFromBag());
        } catch (Exception e)
        {
        }

        // Disable the card
        this.deactivate();

        // Notify the subscriber
        notifySubscriber();
    }

    @Override
    public void notifySubscriber()
    {
        // I have to find this character card index inside the list
        int index = 0;
        for (index = 0; index < instance.characterCards.size() && this != instance.characterCards.get(index); index++);

        if (instance.subscriber.isPresent())
        {
            instance.subscriber.get().onNext(new CharacterCardPayloadUpdate(index, new ArrayList<Student>(students)));

            // I need to send also the CharacterCardsUpdate because CLI
            // doesn't use the CharacterCardPayloadUpdate
            List<CharacterCard> characterCardsList = new ArrayList<>();

            for (CharacterCard card : instance.characterCards)
            {
                // I clone all the character card to avoid serializing the game instance
                characterCardsList.add((CharacterCard) card.clone());
            }

            instance.subscriber.get().onNext(new CharacterCardsUpdate(new ArrayList<CharacterCard>(characterCardsList)));
        }
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MONK;
    }

    public List<Student> getStudents()
    {
        return new ArrayList<>(students);
    }

    /**
     * Draws the student at position index of students if present.
     * 
     * @param index of the student.
     * @return the student if present or blank.
     */
    private String drawStudent(int index)
    {
        if (students.size() > index)
        {
            return PrintHelper.drawColor(students.get(index).getColor(), GamePieces.STUDENT.toString());
        } else
            return " ";
    }

    @Override
    public String toString()
    {
        String rep = super.toString();

        rep += PrintHelper.moveCursorRelative(2, -7) + drawStudent(0);
        rep += PrintHelper.moveCursorRelative(0, 3) + drawStudent(1);
        rep += PrintHelper.moveCursorRelative(-1, -5) + drawStudent(2);
        rep += PrintHelper.moveCursorRelative(0, 3) + drawStudent(3);
        rep += PrintHelper.moveCursorRelative(-1, 2);

        return rep;
    }
}

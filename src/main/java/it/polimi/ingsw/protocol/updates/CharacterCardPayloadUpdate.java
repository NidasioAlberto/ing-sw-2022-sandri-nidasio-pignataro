package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GrandmaHerbs;
import java.util.List;
import java.util.Optional;

/**
 * This class has 2 optionals because a character card can have as a payload a number of students or
 * a number of entry tiles.
 */
public class CharacterCardPayloadUpdate extends ModelUpdate
{
    /**
     * Index of the updated character card
     */
    private int index;

    /**
     * Optional of students payload
     */
    private Optional<List<Student>> students;

    /**
     * Optional number of no entry over this card
     */
    private Optional<Integer> noEntryTiles;

    /**
     * Students payload constructor
     * 
     * @param index The character card index
     * @param students Collection of students over the card
     */
    public CharacterCardPayloadUpdate(int index, List<Student> students)
    {
        if (students == null)
            throw new NullPointerException("[CharacterCardPayloadUpdate] Null students list");
        if (students.contains(null))
            throw new NullPointerException("[CharacterCardPayloadUpdate] Null student inside list");
        if (index < 0 || index >= Game.CHARACTER_CARDS_NUMBER)
            throw new IndexOutOfBoundsException(
                    "[CharacterCardPayloadUpdate] Character card index out of bounds");

        this.index = index;
        this.students = Optional.of(students);
        this.noEntryTiles = Optional.empty();
    }

    /**
     * No entry tiles constructor
     * 
     * @param index The character card index
     * @param noEntryTiles Number of no entry tiles over the card
     */
    public CharacterCardPayloadUpdate(int index, int noEntryTiles)
    {
        if (index < 0 || index >= Game.CHARACTER_CARDS_NUMBER)
            throw new IndexOutOfBoundsException(
                    "[CharacterCardPayloadUpdate] Character card index out of bounds");
        if (noEntryTiles < 0 || noEntryTiles >= GrandmaHerbs.INITIAL_NO_ENTRY_NUMBER)
            throw new IllegalArgumentException(
                    "[CharacterCardPayloadUpdate] Number of noEntryTiles out of bounds");

        this.index = index;
        this.noEntryTiles = Optional.of(noEntryTiles);
        this.students = Optional.empty();
    }

    public int getIndex()
    {
        return index;
    }

    public Optional<List<Student>> getStudents()
    {
        return students;
    }

    public Optional<Integer> getNoEntryTiles()
    {
        return noEntryTiles;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }
}

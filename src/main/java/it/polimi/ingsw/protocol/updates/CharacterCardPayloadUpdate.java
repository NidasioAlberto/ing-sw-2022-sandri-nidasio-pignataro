package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GrandmaHerbs;

import java.util.ArrayList;
import java.util.List;

/**
 * This class has 2 optionals because a character card can have as a payload a number of students or a number of entry tiles.
 */
public class CharacterCardPayloadUpdate extends ModelUpdate
{
    /**
     * Index of the updated character card
     */
    private int index;

    /**
     * List of students payload, could be null
     */
    private List<Student> students;

    /**
     * Number of no entry over this card, could be null
     */
    private Integer noEntryTiles;

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
            throw new IndexOutOfBoundsException("[CharacterCardPayloadUpdate] Character card index out of bounds");

        this.index = index;
        this.students = new ArrayList<>(students);
        this.noEntryTiles = null;
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
            throw new IndexOutOfBoundsException("[CharacterCardPayloadUpdate] Character card index out of bounds");
        if (noEntryTiles < 0 || noEntryTiles > GrandmaHerbs.INITIAL_NO_ENTRY_NUMBER)
            throw new IllegalArgumentException("[CharacterCardPayloadUpdate] Number of noEntryTiles out of bounds");

        this.index = index;
        this.noEntryTiles = noEntryTiles;
        this.students = null;
    }

    public int getIndex()
    {
        return index;
    }

    public List<Student> getStudents()
    {
        return students;
    }

    public Integer getNoEntryTiles()
    {
        return noEntryTiles;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }

    /**
     * Allow to paint a string.
     *
     * @param color to paint the string.
     * @param content the string to paint.
     * @return the painted string.
     */
    private String drawColor(SchoolColor color, String content)
    {

        String rep = "\u001B[";

        switch (color)
        {
            case BLUE:
                rep += "34";
                break;
            case GREEN:
                rep += "32";
                break;
            case PINK:
                rep += "35";
                break;
            case RED:
                rep += "31";
                break;
            case YELLOW:
                rep += "33";
                break;
        }

        rep += "m" + content;

        // Reset the color
        rep += "\u001B[97m";

        return rep;
    }
}

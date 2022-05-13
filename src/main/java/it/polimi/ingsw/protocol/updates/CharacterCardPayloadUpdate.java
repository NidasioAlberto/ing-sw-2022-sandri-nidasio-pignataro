package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.game.CharacterCard;
import it.polimi.ingsw.model.game.CharacterCardType;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GrandmaHerbs;

import java.util.ArrayList;
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
            throw new IndexOutOfBoundsException(
                    "[CharacterCardPayloadUpdate] Character card index out of bounds");

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
            throw new IndexOutOfBoundsException(
                    "[CharacterCardPayloadUpdate] Character card index out of bounds");
        if (noEntryTiles < 0 || noEntryTiles >= GrandmaHerbs.INITIAL_NO_ENTRY_NUMBER)
            throw new IllegalArgumentException(
                    "[CharacterCardPayloadUpdate] Number of noEntryTiles out of bounds");

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

    @Override
    public String toString()
    {
        String rep = "";

        /*rep += card.getCardType();

            for (int i = 0; i < 14 - card.getCardType().toString().length(); i++)
                rep += " ";

        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += CardPiece.TOP_ROW + "  ";
        }
        rep += "\n";
        for (CharacterCard card : cards)
        {
            rep += "║ $" + card.getCost() + "       ║  ";
            //rep += "║ " + coinsSign[card.getCost() - 1] + "        ║ ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {

            rep += "║  "  + drawStudentCard(card, 0) + "    " + drawStudentCard(card, 1) + "  ║  ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            // ⦻ ⨂ ⨷ ⌧ ⮿ ⮾ ⮽ 🚫
            if (card.getCardType() == CharacterCardType.GRANDMA_HERBS)
            {
                rep += "║  "  +  ((GrandmaHerbs) card).getNoEntryTiles() + " no    ║  ";
            }
            else rep += "║  "  + drawStudentCard(card, 2) + "    " + drawStudentCard(card, 3) + "  ║  ";

        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += "║  "  + drawStudentCard(card, 4) + "    " + drawStudentCard(card, 5) + "  ║  ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += CardPiece.BOTTOM_ROW + "  ";
        }
        rep += "\n";*/

        return rep;
    }
}

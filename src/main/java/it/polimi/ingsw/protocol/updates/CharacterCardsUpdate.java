package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game.*;

import java.util.ArrayList;
import java.util.List;

public class CharacterCardsUpdate extends ModelUpdate
{
    /**
     * List of all the character cards
     */
    private List<CharacterCard> cards;

    /**
     * Color of active card (red)
     */
    private final String ACTIVE = "\u001B[31m";

    /**
     * Reset the color
     */
    private final String DEACTIVE = "\u001B[97m";

    /**
     * Constructor
     * 
     * @param cards Collection of all the character cards
     */
    public CharacterCardsUpdate(List<CharacterCard> cards)
    {
        if (cards == null)
            throw new NullPointerException("[CharacterCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException("[CharacterCardsUpdate] Null card inside the list");

        this.cards = cards;
    }

    public List<CharacterCard> getCards()
    {
        return cards;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }

    @Override
    public String toString()
    {
        // String[] coinsSign = new String[]{"\u2780", "\u2777", "\u2778", "\u2779", "\u2780"};
        String rep = "CHARACTER CARDS\n";

        for (CharacterCard card : cards)
        {
            rep += paintIfActive(card, card.getCardType().toString());

            for (int i = 0; i < 14 - card.getCardType().toString().length(); i++)
                rep += " ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += paintIfActive(card, CardPiece.TOP_ROW.toString()) + "  ";
        }
        rep += "\n";
        for (CharacterCard card : cards)
        {
            rep += paintIfActive(card, "║ $" + card.getCost() + "       ║  ");
            // rep += "║ " + coinsSign[card.getCost() - 1] + " ║ ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {

            rep += paintIfActive(card, "║  " + drawStudentCard(card, 0) + "    " + drawStudentCard(card, 1)) + paintIfActive(card, "  ║  ");
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            // ⦻ ⨂ ⨷ ⌧ ⮿ ⮾ ⮽ 🚫
            if (card.getCardType() == CharacterCardType.GRANDMA_HERBS)
            {
                rep += paintIfActive(card, "║  " + ((GrandmaHerbs) card).getNoEntryTiles() + " no    ║  ");
            } else
                rep += paintIfActive(card, "║  " + drawStudentCard(card, 2) + "    " + drawStudentCard(card, 3)) + paintIfActive(card, "  ║  ");

        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += paintIfActive(card, "║  " + drawStudentCard(card, 4) + "    " + drawStudentCard(card, 5)) + paintIfActive(card, "  ║  ");
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += paintIfActive(card, CardPiece.BOTTOM_ROW.toString()) + "  ";
        }
        rep += "\n";

        return rep;
    }


    /**
     * Allow to paint a student.
     * 
     * @param student to paint.
     * @return the painted student.
     */
    private String drawStudent(Student student)
    {
        return PrintHelper.drawColor(student.getColor(), "▪");
    }

    /**
     * Allow to paint a student of a CharacterCard.
     * 
     * @param card could contain a student to paint.
     * @param index of the student to paint.
     * @return the painted student if present.
     */
    private String drawStudentCard(CharacterCard card, int index)
    {
        if (card.getCardType() == CharacterCardType.JOKER && index < 6)
        {
            return drawStudent(((Joker) card).getStudents().get(index));
        } else if (card.getCardType() == CharacterCardType.MONK && index < 4)
        {
            return drawStudent(((Monk) card).getStudents().get(index));
        } else if (card.getCardType() == CharacterCardType.PRINCESS && index < 4)
        {
            return drawStudent(((Princess) card).getStudents().get(index));
        } else
            return " ";
    }

    /**
     * Allow to paint the string if the card is active.
     * 
     * @param card to check if active.
     * @param rep the string that could be painted.
     * @return the final string.
     */
    private String paintIfActive(CharacterCard card, String rep)
    {
        if (card.isActivated())
            return ACTIVE + rep + DEACTIVE;
        else
            return rep;
    }

    public static void main(String[] args)
    {
        List<CharacterCard> cards = new ArrayList<CharacterCard>();
        Game game = new Game(2, GameMode.EXPERT);
        try
        {
            game.addPlayer(new Player("player1", TowerColor.BLACK, GameMode.EXPERT));
            game.addPlayer(new Player("player2", TowerColor.BLACK, GameMode.EXPERT));
            game.addPlayer(new Player("player3", TowerColor.BLACK, GameMode.EXPERT));
        } catch (Exception e)
        {

        }
        game.setupGame();
        Game game1 = new Game();
        try
        {
            game1.addPlayer(new Player("player1", TowerColor.BLACK, GameMode.CLASSIC));
            game1.addPlayer(new Player("player2", TowerColor.BLACK, GameMode.CLASSIC));
        } catch (Exception e)
        {

        }
        game1.setupGame();
        cards.add(CharacterCard.createCharacterCard(CharacterCardType.JOKER, game1));
        cards.add(CharacterCard.createCharacterCard(CharacterCardType.GRANDMA_HERBS, game1));
        cards.add(CharacterCard.createCharacterCard(CharacterCardType.PRINCESS, game1));
        CharacterCardsUpdate update = new CharacterCardsUpdate(game.getCharacterCards());
        CharacterCardsUpdate update1 = new CharacterCardsUpdate(cards);

        game.selectPlayer(0);
        game.getSelectedPlayer().get().getBoard().addCoins(5);
        game.getCharacterCards().get(1).activate();
        System.out.println(update);
        System.out.println(update1);
    }
}

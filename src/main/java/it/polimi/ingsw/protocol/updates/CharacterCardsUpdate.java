package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game.CharacterCard;
import it.polimi.ingsw.model.game.CharacterCardType;
import it.polimi.ingsw.model.game.Game;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CharacterCardsUpdate extends ModelUpdate
{
    /**
     * List of all the character cards
     */
    private List<CharacterCard> cards;

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
        String[] coinsSign = new String[]{"\u2780", "\u2777", "\u2778", "\u2779", "\u2780"};
        String rep = "CHARACTER CARDS\n";

        for (CharacterCard card : cards)
        {
            rep += card.getCardType();

            for (int i = 0; i < 14 - card.getCardType().toString().length(); i++)
                rep += " ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += CardPiece.TOP_ROW + "  ";
        }
        rep += "\n";
        for (CharacterCard card : cards)
        {
            rep += "║ " + card.getCost() + "        ║  ";
             //rep += "║ " + coinsSign[card.getCost() - 1] + "        ║ ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += CardPiece.MIDDLE_ROW  + "  ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += CardPiece.MIDDLE_ROW  + "  ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += CardPiece.MIDDLE_ROW + "  ";
        }
        rep += "\n";

        for (CharacterCard card : cards)
        {
            rep += CardPiece.BOTTOM_ROW + "  ";
        }
        rep += "\n";

        return rep;
    }

    public static void main(String[] args)
    {
        List<CharacterCard> cards = new ArrayList<CharacterCard>();
        Game game = new Game(2, GameMode.EXPERT);
        try{
            game.addPlayer(new Player("player1", TowerColor.BLACK, GameMode.EXPERT));
            game.addPlayer(new Player("player2", TowerColor.BLACK, GameMode.EXPERT));
            game.addPlayer(new Player("player3", TowerColor.BLACK, GameMode.EXPERT));
        } catch (Exception e){

        }
        game.setupGame();
        Game game1 = new Game();
        try{
            game1.addPlayer(new Player("player1", TowerColor.BLACK, GameMode.CLASSIC));
            game1.addPlayer(new Player("player2", TowerColor.BLACK, GameMode.CLASSIC));
        } catch (Exception e){

        }
        game1.setupGame();
        cards.add(CharacterCard.createCharacterCard(CharacterCardType.PRINCESS, game1));
        cards.add(CharacterCard.createCharacterCard(CharacterCardType.GRANDMA_HERBS, game1));
        cards.add(CharacterCard.createCharacterCard(CharacterCardType.MUSHROOM_MAN, game1));
        CharacterCardsUpdate update = new CharacterCardsUpdate(game.getCharacterCards());
        CharacterCardsUpdate update1 = new CharacterCardsUpdate(cards);

        System.out.println(update);
        System.out.println(update1);
    }
}

package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.Visualizer;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game.*;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class CharacterCardsUpdate extends ModelUpdate
{
    @Serial
    private static final long serialVersionUID = 6697636575765084582L;

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
    public void handleUpdate(Visualizer handler)
    {
        handler.displayCharacterCards(this);
    }

    /**
     * Draws a 6x12 representation of each character card with 1 column separation.
     */
    @Override
    public String toString()
    {
        String rep = "";

        // Draw a character card
        for (CharacterCard card : cards)
        {
            // Draw the card
            rep += card.toString();

            // Draw its number
            rep += PrintHelper.moveCursorRelative(5, -8);
            rep += cards.indexOf(card);
            rep += PrintHelper.moveCursorRelative(-5, 7);

            // Move the cursor to the next card
            rep += PrintHelper.moveCursorRelative(6, 1);
        }

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
        cards.add(CharacterCard.createCharacterCard(CharacterCardType.MONK, game1));
        CharacterCardsUpdate update = new CharacterCardsUpdate(game.getCharacterCards());
        CharacterCardsUpdate update1 = new CharacterCardsUpdate(cards);

        game.selectPlayer(0);
        game.getSelectedPlayer().get().getBoard().addCoins(5);
        game.getCharacterCards().get(1).activate();

        PrintHelper.printM(0,2, PrintHelper.ERASE_ENTIRE_SCREEN + update);

        PrintHelper.printM(20,2, update1.toString());
    }
}

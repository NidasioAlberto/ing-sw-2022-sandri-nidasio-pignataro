package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.Visualizable;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.*;

import java.io.Serial;
import java.util.List;
import java.util.stream.IntStream;

public class AssistantCardsUpdate extends ModelUpdate
{

    @Serial
    private static final long serialVersionUID = -4060217661973644912L;

    /**
     * List of a single player assistant cards
     */
    private List<AssistantCard> cards;

    public List<AssistantCard> getCards()
    {
        return cards;
    }

    /**
     * Constructor that allows the player destination
     * 
     * @param playerDestination Name of the player that has to receive the message
     * @param cards Collection of specific player cards
     */
    public AssistantCardsUpdate(String playerDestination, List<AssistantCard> cards)
    {
        super(playerDestination);

        if (cards == null)
            throw new NullPointerException("[AssistantCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException("[AssistantCardsUpdate] Null assistant card inside the list");

        this.cards = cards;
    }

    /**
     * Constructor that doesn't allow any player destination
     * 
     * @param cards Collection of specific player cards
     */
    public AssistantCardsUpdate(List<AssistantCard> cards)
    {
        super();

        if (cards == null)
            throw new NullPointerException("[AssistantCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException("[AssistantCardsUpdate] Null assistant card inside the list");

        this.cards = cards;
    }

    @Override
    public void handleUpdate(Visualizable handler)
    {
        handler.displayAssistantCards(this);
    }

    /**
     * Draws a 4x6 representation of each card with one column separation.
     */
    @Override
    public String toString()
    {
        List<AssistantCard> cards = this.cards.stream().filter((card) -> !card.isUsed()).toList();

        String rep = "";

        // Clear the lines
        for (int i = 0; i < 4; i++)
        {
            rep += PrintHelper.ERASE_FROM_CURSOR_TILL_END_OF_LINE;
            rep += PrintHelper.moveCursorRelative(-1, 0);
        }
        rep += PrintHelper.moveCursorRelative(4, 0);


        for (AssistantCard card : cards)
        {
            rep += card.toString();
            rep += PrintHelper.moveCursorRelative(3, 1);
        }

        rep += PrintHelper.moveToBeginningOfLine(-4);

        return rep;
    }

    public static void main(String[] args)
    {
        Player player = new Player("pla", TowerColor.BLACK, GameMode.EXPERT);
        IntStream.range(0, 10).forEach(i -> {
            player.addCard(new AssistantCard(Wizard.WIZARD_3, i + 1, i / 2 + 1));
        });
        player.getCards().get(0).toggleUsed();
        player.getCards().get(4).toggleUsed();
        AssistantCardsUpdate update = new AssistantCardsUpdate("", player.getCards());

        System.out.print(update);
    }
}

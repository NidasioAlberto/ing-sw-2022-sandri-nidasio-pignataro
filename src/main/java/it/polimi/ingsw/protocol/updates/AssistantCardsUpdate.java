package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AssistantCardsUpdate extends ModelUpdate {

    /**
     * List of a single player assistant cards
     */
    private List<AssistantCard> cards;

    public List<AssistantCard> getCards() {
        return cards;
    }

    /**
     * Constructor that allows the player destination
     * 
     * @param playerDestination Name of the player that has to receive the message
     * @param cards             Collection of specific player cards
     */
    public AssistantCardsUpdate(String playerDestination, List<AssistantCard> cards) {
        super(playerDestination);

        if (cards == null)
            throw new NullPointerException("[AssistantCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException(
                    "[AssistantCardsUpdate] Null assistant card inside the list");

        this.cards = cards;
    }

    /**
     * Constructor that doesn't allow any player destination
     * 
     * @param cards Collection of specific player cards
     */
    public AssistantCardsUpdate(List<AssistantCard> cards) {
        super();

        if (cards == null)
            throw new NullPointerException("[AssistantCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException(
                    "[AssistantCardsUpdate] Null assistant card inside the list");

        this.cards = cards;
    }

    @Override
    public void handleUpdate(Object handler) {

    }

    @Override
    public String toString()
    {
        cards = cards.stream().filter((card) -> !card.isUsed()).toList();

        String rep = "ASSISTANT CARDS\n";

        for (AssistantCard card : cards)
        {
            rep += CardPiece.TOP_ROW + "  ";
        }
        rep += "\n";
        for (AssistantCard card : cards)
        {
            rep += CardPiece.MIDDLE_ROW_WITH_CIRCLE + "  ";
        }
        rep += "\n";

        for (AssistantCard card : cards)
        {
            rep += "║ " + card.getTurnOrder();
            rep += ((Integer) card.getTurnOrder()).toString().length() == 1 ?
                    "      " : "     ";

           rep += card.getSteps() + " ║  ";
        }
        rep += "\n";

        for (AssistantCard card : cards)
        {
            rep += CardPiece.MIDDLE_ROW + "  ";
        }
        rep += "\n";

        for (AssistantCard card : cards)
        {
            rep += CardPiece.MIDDLE_ROW + "  ";
        }
        rep += "\n";

        for (AssistantCard card : cards)
        {
            rep += CardPiece.BOTTOM_ROW + "  ";
        }
        rep += "\n";

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

        System.out.println(update);
    }
}

package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.*;

import java.util.stream.IntStream;

public class PlayedAssistantCardUpdate extends ModelUpdate
{
    /**
     * The played assistant card
     */
    private AssistantCard card;

    /**
     * The player who played the card
     */
    private String player;

    /**
     * Constructor
     * 
     * @param card The card played
     * @param player The player who played the card
     */
    public PlayedAssistantCardUpdate(AssistantCard card, String player)
    {
        if (card == null)
            throw new NullPointerException("[PlayedAssistantCardUpdate] Null card");
        if (player == null)
            throw new NullPointerException("[PlayedAssistantCardUpdate] Null player");

        this.card = card;
        this.player = player;
    }

    public AssistantCard getCard()
    {
        return card;
    }

    public String getPlayer()
    {
        return player;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }

    @Override
    public String toString()
    {
        String rep = "";

        rep += player + "\n";

        rep += CardPiece.TOP_ROW + "\n";

        rep += CardPiece.MIDDLE_ROW_WITH_CIRCLE + "\n";

        rep += "║ " + card.getTurnOrder();
        rep += ((Integer) card.getTurnOrder()).toString().length() == 1 ? "      " : "     ";
        rep += card.getSteps() + " ║ \n";

        rep += CardPiece.MIDDLE_ROW + "\n";

        rep += CardPiece.MIDDLE_ROW + "\n";

        rep += CardPiece.BOTTOM_ROW + "\n";

        return rep;
    }

    public static void main(String[] args)
    {
        PlayedAssistantCardUpdate update = new PlayedAssistantCardUpdate(new AssistantCard(Wizard.WIZARD_3, 1, 1), "player");

        System.out.println(update);
    }
}

package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.Visualizer;
import it.polimi.ingsw.model.*;

import java.io.Serial;

public class PlayedAssistantCardUpdate extends ModelUpdate
{
    @Serial
    private static final long serialVersionUID = -4678041619405025552L;

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
    public void handleUpdate(Visualizer handler)
    {
        handler.displayPlayedAssistantCard(this);
    }

    @Override
    public String toString()
    {
        return card.toString();
    }

    public static void main(String[] args)
    {
        PlayedAssistantCardUpdate update = new PlayedAssistantCardUpdate(new AssistantCard(Wizard.WIZARD_3, 1, 1), "player");

        System.out.print(update);
    }
}

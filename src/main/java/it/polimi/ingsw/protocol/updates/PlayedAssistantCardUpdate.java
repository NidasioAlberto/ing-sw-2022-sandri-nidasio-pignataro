package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.AssistantCard;

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
     * @param card The card played
     * @param player The player who played the card
     */
    public PlayedAssistantCardUpdate(AssistantCard card, String player)
    {
        if(card == null)
            throw new NullPointerException("[PlayedAssistantCardUpdate] Null card");
        if(player == null)
            throw new NullPointerException("[PlayedAssistantCardUpdate] Null player");

        this.card   = card;
        this.player = player;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }
}

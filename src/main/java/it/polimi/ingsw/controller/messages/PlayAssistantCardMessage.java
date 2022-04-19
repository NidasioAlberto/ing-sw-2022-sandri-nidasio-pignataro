package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * Message related to the played assistant card.
 */
public class PlayAssistantCardMessage extends ActionMessage
{
    protected PlayAssistantCardMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        handler.playAssistantCard();
    }
}

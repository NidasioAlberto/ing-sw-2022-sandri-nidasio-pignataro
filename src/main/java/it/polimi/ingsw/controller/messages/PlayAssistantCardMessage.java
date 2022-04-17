package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

public class PlayAssistantCardMessage extends ActionMessage
{
    protected PlayAssistantCardMessage(String json)
    {
        super(json);
    }

    public void applyAction(GameActionHandler handler)
    {

    }
}

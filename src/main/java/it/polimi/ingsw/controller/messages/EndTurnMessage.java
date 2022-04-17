package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

public class EndTurnMessage extends ActionMessage
{
    protected EndTurnMessage(String json)
    {
        super(json);
    }

    public void applyAction(GameActionHandler handler)
    {

    }
}

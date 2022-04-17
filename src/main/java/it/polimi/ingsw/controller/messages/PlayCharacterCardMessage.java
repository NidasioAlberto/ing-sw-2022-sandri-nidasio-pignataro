package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

public class PlayCharacterCardMessage extends ActionMessage
{
    protected PlayCharacterCardMessage(String json)
    {
        super(json);
    }

    public void applyAction(GameActionHandler handler)
    {

    }
}

package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

public class CharacterCardActionMessage extends  ActionMessage
{
    protected CharacterCardActionMessage(String json)
    {
        super(json);
    }

    public void applyAction(GameActionHandler handler)
    {

    }
}

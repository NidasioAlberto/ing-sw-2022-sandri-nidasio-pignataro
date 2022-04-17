package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

public class MoveStudentFromEntranceToIslandMessage extends ActionMessage
{
    protected MoveStudentFromEntranceToIslandMessage(String json)
    {
        super(json);
    }

    public void applyAction(GameActionHandler handler)
    {

    }
}

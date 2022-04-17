package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

public class MoveStudentFromEntranceToDiningMessage extends ActionMessage
{
    protected MoveStudentFromEntranceToDiningMessage(String json)
    {
        super(json);
    }

    public void applyAction(GameActionHandler handler)
    {

    }
}

package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * Message related to the movement of a student from entrance to the dining.
 */
public class MoveStudentFromEntranceToDiningMessage extends ActionMessage
{
    protected MoveStudentFromEntranceToDiningMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        handler.moveStudentFromEntranceToDining(this);
    }
}

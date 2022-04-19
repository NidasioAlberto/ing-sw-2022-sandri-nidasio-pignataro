package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * Message related to the movement of a student from entrance to an island.
 */
public class MoveStudentFromEntranceToIslandMessage extends ActionMessage
{
    protected MoveStudentFromEntranceToIslandMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        handler.moveStudentFromEntranceToIsland();
    }
}

package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.SchoolColor;
import java.util.ArrayList;
import java.util.List;

/**
 * Message related to the movement of a student from entrance to an island.
 */
public class MoveStudentFromEntranceToIslandMessage extends ActionMessage
{
    private SchoolColor selectedColor;
    private int selectedIsland;

    public MoveStudentFromEntranceToIslandMessage(SchoolColor selectedColor, int selectedIsland)
    {
        if (selectedColor == null)
            throw new NullPointerException("[ActionMessage] The selected color is null");

        this.selectedColor = selectedColor;

        this.selectedIsland = selectedIsland;
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.moveStudentFromEntranceToIsland(selectedColor, selectedIsland);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND;
    }
}

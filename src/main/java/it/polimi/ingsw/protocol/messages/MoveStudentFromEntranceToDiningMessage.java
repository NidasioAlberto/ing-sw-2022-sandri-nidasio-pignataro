package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.SchoolColor;
import java.util.ArrayList;
import java.util.List;

/**
 * Message related to the movement of a student from entrance to the dining.
 */
public class MoveStudentFromEntranceToDiningMessage extends ActionMessage
{
    private SchoolColor selectedColor;

    public MoveStudentFromEntranceToDiningMessage(SchoolColor selectedColor)
    {
        if (selectedColor == null)
            throw new NullPointerException("[ActionMessage] The selected color is null");

        this.selectedColor = selectedColor;
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.moveStudentFromEntranceToDining(selectedColor);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING;
    }
}

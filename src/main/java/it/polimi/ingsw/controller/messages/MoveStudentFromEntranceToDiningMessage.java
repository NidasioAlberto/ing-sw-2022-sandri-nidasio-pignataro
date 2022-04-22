package it.polimi.ingsw.controller.messages;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.game.BaseGameAction;

/**
 * Message related to the movement of a student from entrance to the dining.
 */
public class MoveStudentFromEntranceToDiningMessage extends ActionMessage
{
    protected MoveStudentFromEntranceToDiningMessage(String json)
    {
        super(json);
        this.actionType = BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING;
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);
        //Check if the action corresponds to the actionType (to avoid cheating)
        if(actionType != BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING)
            throw new InvalidModuleException("[MoveStudentFromEntranceToDiningMessage] Bad action type");

        handler.moveStudentFromEntranceToDining(this);
    }
}

package it.polimi.ingsw.controller.messages;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * Message related to the movement of mother nature.
 */
public class MoveMotherNatureMessage extends ActionMessage
{
    protected MoveMotherNatureMessage(String json)
    {
        super(json);
        this.actionType = BaseGameAction.MOVE_MOTHER_NATURE;
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);
        //Check if the action corresponds to the actionType (to avoid cheating)
        if(actionType != BaseGameAction.MOVE_MOTHER_NATURE)
            throw new InvalidModuleException("[MoveMotherNatureMessage] Bad action type");

        handler.moveMotherNature(this);
    }
}

package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * Message related to the movement of mother nature.
 */
public class MoveMotherNatureMessage extends ActionMessage
{
    private int selectedIsland;

    public MoveMotherNatureMessage(int selectedIsland)
    {
        this.selectedIsland = selectedIsland;
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.moveMotherNature(selectedIsland);
    }
    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.MOVE_MOTHER_NATURE;
    }
}

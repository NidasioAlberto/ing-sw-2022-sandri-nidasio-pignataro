package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

import java.io.Serial;

/**
 * Message related to the movement of mother nature.
 */
public class MoveMotherNatureMessage extends ActionMessage
{
    @Serial
    private static final long serialVersionUID = 2702180496126923853L;

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

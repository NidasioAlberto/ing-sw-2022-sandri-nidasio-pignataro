package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * Message related to the movement of mother nature.
 */
public class MoveMotherNatureMessage extends ActionMessage
{
    protected MoveMotherNatureMessage(String json)
    {
        super(json);
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);

        handler.moveMotherNature(this);
    }
}

package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.controller.messages.ActionMessage;

public class StartOfTurnPhase implements Phase
{
    @Override
    public void onValidAction(GameActionHandler handler)
    {

    }

    @Override
    public void onEndGame(GameActionHandler handler)
    {

    }

    @Override
    public boolean isLegitAction(ActionMessage message, GameActionHandler handler)
    {
        return false;
    }
}

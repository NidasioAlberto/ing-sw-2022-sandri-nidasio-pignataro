package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

public class MoveStudentPhase implements Phase
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
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        return false;
    }
}

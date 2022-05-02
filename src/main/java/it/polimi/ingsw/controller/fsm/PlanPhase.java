package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.exceptions.WrongPlayerException;

import java.util.List;

public class PlanPhase implements Phase
{
    /**
     * We need to maintain some counting about the players that already played an assistant card
     */
    private int count;

    //TODO UNDERSTAND THE PLAYER ORDER
    public PlanPhase()
    {
        this.count = 0;
    }

    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // I take the selected player index. If it is the last one i can
        // change the selected player referring to the sorted list and switch
        // to the startTurnPhase
        int playerIndex = handler.getGame().getSelectedPlayerIndex()
                .orElseThrow(() -> new NoSelectedPlayerException("[PlanPhase]"));

        // If we are not dealing with the last one i switch player and maintain the current state
        if (count < handler.getGame().getPlayersNumber() - 1)
        {
            // In case of a non 0 starting index, i have to use the module
            handler.getGame().selectPlayer((playerIndex + 1) % handler.getGame().getPlayersNumber());
            count++;
        } else
        {
            // If not i select the first player (now about the sorted list)
            handler.getGame().selectPlayer(0);
            handler.setGamePhase(new MoveStudentPhase());
        }
    }

    @Override
    public void onEndGame(GameActionHandler handler)
    {
        // On this event i just switch to the end state
        handler.setGamePhase(new EndGamePhase());
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // I check if it is the allowed player from the initial passed list
        int playerIndex = handler.getGame().getSelectedPlayerIndex()
                .orElseThrow(() -> new NoSelectedPlayerException("[PlanPhase]"));

        // I accept the action if and only if the player is correct and the action is an assistant
        // card play
        if (!handler.getGame().getPlayerTableList().get(playerIndex).getNickname().equals(playerName))
            throw new WrongPlayerException();
        return baseAction == BaseGameAction.PLAY_ASSISTANT_CARD;
    }
}

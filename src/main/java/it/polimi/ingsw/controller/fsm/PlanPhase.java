package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.BaseGameAction;

import java.util.List;

public class PlanPhase implements Phase
{

    /**
     * List that represents the player order that needs to be followed.
     * This is because at the very beginning, the players in table order
     * place an assistant card, whereas for the rest of the match players
     * in play order place an assistant card.
     */
    private List<Player> orderList;

    //TODO UNDERSTAND THE PLAYER ORDER
    public PlanPhase(List<Player> orderList)
    {
        this.orderList = orderList;
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
        if (playerIndex < orderList.size() - 1)
        {
            handler.getGame().selectPlayer(playerIndex + 1);
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
        return orderList.get(playerIndex).getNickname().equals(playerName) &&
                baseAction == BaseGameAction.PLAY_ASSISTANT_CARD;
    }
}

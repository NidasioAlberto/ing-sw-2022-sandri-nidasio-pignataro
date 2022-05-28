package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.WrongPlayerException;

public class EndTurnPhase implements Phase
{
    /**
     * This method returns true only when the game is in the last round
     * @return Boolean that represents the game ending state
     */
    private boolean isGameEnding(GameActionHandler handler)
    {
        // If the student bag is empty i return immediately true
        if(handler.getGame().getStudentBag().size() == 0)
            return true;

        // I can return true if i find a player with no assistant cards available anymore
        for(Player player : handler.getGame().getPlayerTableList())
        {
            if(player.getCards().stream().filter(c -> !c.isUsed()).findFirst().isEmpty())
                return true;
        }

        return false;
    }

    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // If the action is valid i have to switch current player
        // and set the current state to moveStudentPhase unless the
        // current player is the last active one. In that case i have to
        // switch to PlanPhase
        int playerIndex = handler.getGame().getSelectedPlayerIndex().get();

        if(playerIndex < handler.getGame().getPlayerTableList().size() - 1 &&
                handler.getGame().getSortedPlayerList().subList(playerIndex + 1, handler.getGame().getPlayersNumber()).
                stream().filter(player -> player.isActive()).count() > 0)
        {
            // I search the next active player in the sorted list
            for (int i = playerIndex + 1; i < handler.getGame().getPlayersNumber(); i++)
            {
                if (handler.getGame().getSortedPlayerList().get(i).isActive())
                {
                    handler.getGame().selectPlayer(i);
                    handler.getGame().setCurrentPlayerIndexByTable(handler.getGame().getPlayerTableList().
                            indexOf(handler.getGame().getSelectedPlayer().get()));
                    handler.setGamePhase(new MoveStudentPhase());
                    break;
                }
            }
        }
        else
        {
            // If the game is ending i don't have to start again with the round
            if(isGameEnding(handler))
            {
                handler.setGamePhase(new EndGamePhase());
                throw new EndGameException("[EndTurnPhase]");
            }

            // Extract the first active player that played the first move
            Player bestPlayer = handler.getGame().getSortedPlayerList().get(0);
            for (int i = 0; i < handler.getGame().getPlayersNumber(); i++)
            {
                if (handler.getGame().getSortedPlayerList().get(i).isActive())
                {
                    bestPlayer = handler.getGame().getSortedPlayerList().get(i);
                    break;
                }
            }

            // I need to find the index of that best player in table order list
            int index;
            for(index = 0; index < handler.getGame().getPlayerTableList().size() &&
                    !handler.getGame().getPlayerTableList().get(index).equals(bestPlayer); index++);

            // Now that the turn is over i want the plan phase to go with table order
            // but starting with the first player that played this round
            handler.getGame().selectPlayer(index);
            handler.getGame().setCurrentPlayerIndexByTable(index);
            handler.setGamePhase(new PlanPhase());
        }
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName, BaseGameAction baseAction)
    {
        // Check if the player corresponds to the selected one in SORTED LIST
        Player currentPlayer = handler.getGame().getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[EndTurnPhase]"));

        if (!currentPlayer.getNickname().equals(playerName))
            throw new WrongPlayerException();
        return baseAction == BaseGameAction.CHARACTER_CARD_ACTION ||
                baseAction == BaseGameAction.PLAY_CHARACTER_CARD ||
                baseAction == BaseGameAction.END_TURN;
    }
}

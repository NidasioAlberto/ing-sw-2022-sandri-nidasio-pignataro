package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.WrongPlayerException;

public class MoveStudentPhase implements Phase
{
    /**
     * Internal counter that memorize how many students have been moved
     */
    private int counter;

    public MoveStudentPhase()
    {
        this.counter = 1;
    }

    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // The action validity depends also on the number of players
        int playerNumber = handler.getGame().getPlayersNumber();
        int maxMovements = playerNumber == 2 ? 3 : 4;

        // Until we reach the desired moved students number, we maintain this state
        if(counter < maxMovements)
            counter++;
        else
            handler.setGamePhase(new MoveMotherNaturePhase());
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // Check if the player corresponds to the selected one in SORTED LIST
        Player currentPlayer = handler.getGame().getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[MoveStudentPhase]"));

        if (!currentPlayer.getNickname().equals(playerName))
            throw new WrongPlayerException();
        return baseAction == BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND ||
                baseAction == BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING ||
                baseAction == BaseGameAction.CHARACTER_CARD_ACTION ||
                baseAction == BaseGameAction.PLAY_CHARACTER_CARD;
    }
}

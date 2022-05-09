package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.WrongPlayerException;

public class SelectCloudTilePhase implements Phase
{
    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // On valid action i have only to switch to the end of turn
        handler.setGamePhase(new EndTurnPhase());
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // Check if the player corresponds to the selected one in SORTED LIST
        Player currentPlayer = handler.getGame().getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[SelectCloudTilePhase]"));

        if (!currentPlayer.getNickname().equals(playerName))
            throw new WrongPlayerException();
        return baseAction == BaseGameAction.SELECT_CLOUD_TILE ||
                 baseAction == BaseGameAction.CHARACTER_CARD_ACTION ||
                 baseAction == BaseGameAction.PLAY_CHARACTER_CARD;
    }
}

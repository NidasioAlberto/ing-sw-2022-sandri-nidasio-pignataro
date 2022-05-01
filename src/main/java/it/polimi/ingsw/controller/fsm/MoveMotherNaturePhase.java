package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.WrongPlayerException;

public class MoveMotherNaturePhase implements Phase
{
    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // On valid action i only have to switch to cloud tile selection phase
        handler.setGamePhase(new SelectCloudTilePhase());
    }

    @Override
    public void onEndGame(GameActionHandler handler)
    {
        // On game end i switch to the corresponding phase
        handler.setGamePhase(new EndGamePhase());
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // Check if the player corresponds to the selected one in SORTED LIST
        Player currentPlayer = handler.getGame().getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[MoveMotherNaturePhase]"));

        if (!currentPlayer.getNickname().equals(playerName))
            throw new WrongPlayerException();
        return baseAction == BaseGameAction.MOVE_MOTHER_NATURE ||
                 baseAction == BaseGameAction.CHARACTER_CARD_ACTION ||
                 baseAction == BaseGameAction.PLAY_CHARACTER_CARD;
    }
}

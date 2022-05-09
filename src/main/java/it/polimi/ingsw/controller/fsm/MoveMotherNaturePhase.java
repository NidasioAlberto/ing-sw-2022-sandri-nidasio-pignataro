package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.WrongPlayerException;

public class MoveMotherNaturePhase implements Phase
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
        if(!isGameEnding(handler))
        {
            // On valid action i only have to switch to cloud tile selection phase
            handler.setGamePhase(new SelectCloudTilePhase());
        }
        else
        {
            // If it is the last turn i skip the cloud tile selection
            handler.setGamePhase(new EndTurnPhase());
        }
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

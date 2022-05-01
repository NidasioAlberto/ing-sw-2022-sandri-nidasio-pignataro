package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * Message related to the played character card.
 */
public class PlayCharacterCardMessage extends ActionMessage
{
    private int selectedCharacterCard;

    public PlayCharacterCardMessage(int selectedCharacterCard)
    {
        this.selectedCharacterCard = selectedCharacterCard;
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.playCharacterCard(selectedCharacterCard);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.PLAY_CHARACTER_CARD;
    }
}

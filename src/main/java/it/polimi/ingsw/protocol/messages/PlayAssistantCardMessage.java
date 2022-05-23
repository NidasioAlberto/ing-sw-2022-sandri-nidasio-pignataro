package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

import java.io.Serial;

/**
 * Message related to the played assistant card.
 */
public class PlayAssistantCardMessage extends ActionMessage
{
    @Serial
    private static final long serialVersionUID = 7130310886511953916L;

    private int selectedCard;

    public PlayAssistantCardMessage(int selectedCard)
    {
        this.selectedCard = selectedCard;
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.playAssistantCard(selectedCard);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.PLAY_ASSISTANT_CARD;
    }
}

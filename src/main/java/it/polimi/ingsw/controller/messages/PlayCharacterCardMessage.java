package it.polimi.ingsw.controller.messages;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.game.BaseGameAction;

/**
 * Message related to the played character card.
 */
public class PlayCharacterCardMessage extends ActionMessage
{
    protected PlayCharacterCardMessage(String json)
    {
        super(json);
        this.actionType = BaseGameAction.PLAY_CHARACTER_CARD;
    }

    @Override
    public void applyAction(GameActionHandler handler)
    {
        super.applyAction(handler);
        //Check if the action corresponds to the actionType (to avoid cheating)
        if(actionType != BaseGameAction.PLAY_CHARACTER_CARD)
            throw new InvalidModuleException("[PlayCharacterCardMessage] Bad action type");

        handler.playCharacterCard(this);
    }
}

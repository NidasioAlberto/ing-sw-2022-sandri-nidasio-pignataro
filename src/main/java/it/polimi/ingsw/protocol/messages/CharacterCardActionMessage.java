package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.SchoolColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Message related to the activation of the character card effect.
 */
public class CharacterCardActionMessage extends ActionMessage
{
    private ExpertGameAction action;
    private Optional<Integer> selectedIsland;
    private Optional<List<SchoolColor>> selectedColors;

    public CharacterCardActionMessage(ExpertGameAction action, int selectedIsland, List<SchoolColor> selectedColors)
    {
        this.action = action;
        this.selectedIsland = Optional.of(selectedIsland);

        if (selectedColors == null)
            this.selectedColors = Optional.empty();
        else if (selectedColors.contains(null))
            throw new NullPointerException("[ActionMessage] A selected color is null");
        else this.selectedColors = Optional.of(new ArrayList<SchoolColor>(selectedColors));
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        handler.characterCardAction(action, selectedIsland, selectedColors);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.CHARACTER_CARD_ACTION;
    }
}

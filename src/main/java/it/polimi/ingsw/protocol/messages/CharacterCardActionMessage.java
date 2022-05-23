package it.polimi.ingsw.protocol.messages;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.SchoolColor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Message related to the activation of the character card effect.
 */
public class CharacterCardActionMessage extends ActionMessage
{
    @Serial
    private static final long serialVersionUID = 1267639762193407116L;

    private ExpertGameAction action;
    private Integer selectedIsland;
    private List<SchoolColor> selectedColors;

    public CharacterCardActionMessage(ExpertGameAction action, Integer selectedIsland,
            List<SchoolColor> selectedColors)
    {
        if (action == null)
            throw new NullPointerException("[ActionMessage] The action is null");
        this.action = action;

        this.selectedIsland = selectedIsland;

        if (selectedColors == null)
            this.selectedColors = null;
        else if (selectedColors.contains(null))
            throw new NullPointerException("[ActionMessage] A selected color is null");
        else this.selectedColors = new ArrayList<SchoolColor>(selectedColors);
    }

    public void applyAction(GameActionHandler handler)
    {
        checkHandler(handler);
        Optional<Integer> selectedIsland;
        Optional<List<SchoolColor>> selectedColors;

        if (this.selectedIsland == null)
            selectedIsland = Optional.empty();
        else selectedIsland = Optional.of(this.selectedIsland);

        if (this.selectedColors == null)
            selectedColors = Optional.empty();
        else selectedColors = Optional.of(new ArrayList<SchoolColor>(this.selectedColors));

        handler.characterCardAction(action, selectedIsland, selectedColors);
    }

    public BaseGameAction getBaseGameAction()
    {
        return BaseGameAction.CHARACTER_CARD_ACTION;
    }
}

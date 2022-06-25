package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;

import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

/**
 * This class represents the assistant card. Each assistant card has a value, used to determine the turn order, and a number that specifies the
 * maximum moves the player can make mother nature do.
 */
public class AssistantCard implements Serializable
{
    @Serial
    private static final long serialVersionUID = 3254487044517145211L;

    /**
     * Absolute maximum value for the turn order parameter.
     */
    public static final int MAX_TURN_ORDER = 10;

    /**
     * Absolute maximum value for the steps parameter.
     */
    public static final int MAX_STEPS = 5;

    /**
     * The wizard type.
     */
    private Wizard wizard;

    /**
     * Assistant's card value used to determine the turn order.
     */
    private int turnOrder;

    /**
     * Maximum number of steps mother nature can do with this card.
     */
    private int steps;

    /**
     * Tells if the card has already been used
     */
    private boolean used;

    /**
     * Creates an AssistantCard object.
     * 
     * @param wizard The wizard type.
     * @param turnOrder The number of turn.
     * @param steps The max number of steps that mother nature can do.
     * @throws NullPointerException Thrown if the specified color is invalid.
     * @throws IllegalArgumentException Thrown if turnOrder or steps are invalid.
     */
    public AssistantCard(Wizard wizard, int turnOrder, int steps) throws NullPointerException, IllegalArgumentException
    {
        if (wizard == null)
            throw new NullPointerException("[AssistantCard] A null value was given to the wizard parameter");

        if (turnOrder <= 0 || turnOrder > MAX_TURN_ORDER)
            throw new IllegalArgumentException("[AssistantCard] An invalid turn value was provided");

        if (steps <= 0 || steps > MAX_STEPS)
            throw new IllegalArgumentException("[AssistantCard] An invalid steps number was provided");

        if ((2 * steps - turnOrder) != 0 && (2 * steps - turnOrder) != 1)
            throw new IllegalArgumentException("[AssistantCard] Turn value and steps number are not compatible");

        this.wizard = wizard;
        this.turnOrder = turnOrder;
        this.steps = steps;
        this.used = false;
    }

    /**
     * Returns the card's wizard.
     * 
     * @return The card's wizard.
     */
    public Wizard getWizard()
    {
        return wizard;
    }

    /**
     * Returns the card's turn order.
     * 
     * @return The card's turn order.
     */
    public int getTurnOrder()
    {
        return turnOrder;
    }

    /**
     * Returns the card's number of steps.
     * 
     * @return The card's number of steps.
     */
    public int getSteps()
    {
        return steps;
    }

    /**
     * Tells whether the card has been used or not.
     * 
     * @return True if the card has been used.
     */
    public boolean isUsed()
    {
        return used;
    }

    /**
     * Flags the card as used.
     */
    public void use()
    {
        this.used = true;
    }

    /**
     * Draws a 4x6 representation of the card. Uses escape characters to be printed everywhere on the terminal.
     */
    @Override
    public String toString()
    {
        String rep = "";
        //→
        rep += "┌" + turnOrder + (turnOrder < 10 ? "─" : "") + "──┐" + PrintHelper.moveCursorRelative(-1, -6);
        rep += "│ " + wizard.toString() + " │" + PrintHelper.moveCursorRelative(-1, -6);
        rep += "│" + GamePieces.ARROW + steps + " │" + PrintHelper.moveCursorRelative(-1, -6);
        rep += "└────┘";

        return rep;
    }
}

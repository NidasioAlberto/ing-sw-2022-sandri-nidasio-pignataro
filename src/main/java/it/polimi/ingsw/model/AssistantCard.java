package it.polimi.ingsw.model;

import java.io.Serializable;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

/**
 * This class represents the assistant card. Each assistant card has a value, used to determine the turn order, and a number that specifies the
 * maximum moves the player can make mother nature do.
 */
public class AssistantCard implements Serializable
{
    public static final int MAX_TURN_ORDER = 10;

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
     * Constructor.
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

    public Wizard getWizard()
    {
        return wizard;
    }

    public int getTurnOrder()
    {
        return turnOrder;
    }

    public int getSteps()
    {
        return steps;
    }

    public boolean isUsed()
    {
        return used;
    }

    public void toggleUsed()
    {
        this.used = true;
    }

    /**
     * Draws a 4x6 representation of the card.
     */
    @Override
    public String toString()
    {
        String rep = "";

        rep += "┏━━━━┓" + PrintHelper.moveCursorRelative(-1, -6);
        rep += "┃ " + wizard.toString() + " ┃" + PrintHelper.moveCursorRelative(-1, -6);
        rep += "┃" + turnOrder + (turnOrder < 10 ? " " : "") + " " + steps + "┃" + PrintHelper.moveCursorRelative(-1, -6);
        rep += "┗━━━━┛";

        return rep;
    }
}

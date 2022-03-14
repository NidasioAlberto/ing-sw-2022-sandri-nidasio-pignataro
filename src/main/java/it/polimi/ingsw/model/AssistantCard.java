package it.polimi.ingsw.model;

/**
 * This class represents the assistant card. Every player has these cards, they
 * determine the turn order and the steps that mother nature can do.
 */
public class AssistantCard
{
    /**
     * The wizard type
     */
    private Wizard wizard;

    /**
     * The turn value to determine the order
     */
    private int turnOrder;

    /**
     * The steps mother nature can do
     */
    private int steps;

    /**
     * Constructor
     * @param wizard The wizard type
     * @param turn The number of turn
     * @param steps The number of steps mother nature can do
     */
    public AssistantCard(Wizard wizard, int turn, int steps)
    {
        this.wizard     = wizard == null    ? Wizard.WIZARD_1   : wizard;
        this.turnOrder  = turn <= 0         ? 1                 : turn;
        this.steps      = steps <= 0        ? 1                 : steps;
    }

    /**
     * Getters
     */
    public Wizard getWizard() { return wizard; }
    public int getTurnOrder() { return turnOrder; }
    public int getSteps()     { return steps; }
}
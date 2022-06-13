package it.polimi.ingsw.model;

/**
 * Enumeration for the 4 wizards on the assistant cards.
 */
public enum Wizard
{
    /**
     * Wizard 1.
     */
    WIZARD_1("W1"),

    /**
     * Wizard 2.
     */
    WIZARD_2("W2"),

    /**
     * Wizard 3.
     */
    WIZARD_3("W3"),

    /**
     * Wizard 4.
     */
    WIZARD_4("W4");

    String rep;

    /**
     * Creates a Wizard object.
     * 
     * @param rep Name of the Wizard.
     */
    Wizard(String rep)
    {
        this.rep = rep;
    }

    /**
     * Returns the name of the Wizard.
     */
    @Override
    public String toString()
    {
        return rep;
    }
}

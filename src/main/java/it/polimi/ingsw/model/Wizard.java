package it.polimi.ingsw.model;

/**
 * This class enums the 4 main wizard categories.
 */
public enum Wizard
{
    WIZARD_1("W1"), WIZARD_2("W2"), WIZARD_3("W3"), WIZARD_4("W4");

    String rep;

    Wizard(String rep)
    {
        this.rep = rep;
    }

    @Override
    public String toString()
    {
        return rep;
    }
}

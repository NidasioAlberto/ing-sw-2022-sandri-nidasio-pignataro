package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssistantCardTest
{
    /**
     * Test if the constructor works accurately.
     */
    @Test
    public void Constructor()
    {
        AssistantCard card = new AssistantCard(Wizard.WIZARD_1, 10, 5);
        assertAll(() -> assertEquals(Wizard.WIZARD_1, card.getWizard()),
                () -> assertEquals(10, card.getTurnOrder()),
                () -> assertEquals(5, card.getSteps()));

        assertNotEquals(null, card.toString());
    }

    /**
     * For each wizard, create an AssistantCard with that wizard and check it.
     */
    @Test
    public void checkWizardTest()
    {
        for (Wizard wizard : Wizard.values())
        {
            AssistantCard card = new AssistantCard(wizard, 8, 4);
            assertEquals(wizard, card.getWizard());
        }
    }

    /**
     * The assistant card constructor should throw exception when invalid parameter are provided.
     * Test if the constructor works accurately when a wrong card is passed.
     */
    @Test
    public void illegalArgumentTest()
    {
        // Null wizard
        assertThrows(NullPointerException.class, () -> new AssistantCard(null, 10, 5));

        // Invalid turn order value
        assertThrows(IllegalArgumentException.class,
                () -> new AssistantCard(Wizard.WIZARD_1, 0, 3));
        assertThrows(IllegalArgumentException.class,
                () -> new AssistantCard(Wizard.WIZARD_1, 11, 3));

        // Invalid mother nature steps
        assertThrows(IllegalArgumentException.class,
                () -> new AssistantCard(Wizard.WIZARD_1, 3, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new AssistantCard(Wizard.WIZARD_1, 6, 6));

        // Incompatible turn value and steps number
        assertThrows(IllegalArgumentException.class,
                () -> new AssistantCard(Wizard.WIZARD_1, 10, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new AssistantCard(Wizard.WIZARD_1, 2, 10));

    }

    @Test
    public void toggleUsedTest()
    {
        // Create a card
        AssistantCard card = new AssistantCard(Wizard.WIZARD_1, 1, 1);

        // At the beginning the card isn't used
        assertFalse(card.isUsed());

        // When I use the card I toggle it
        card.toggleUsed();

        // The card has been used
        assertTrue(card.isUsed());
    }
}

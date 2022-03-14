package it.polimi.ingsw;

import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.Wizard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the AssistantCard class
 */
public class AssistantCardTest
{
    /**
     * Test if the constructor works accurately
     */
    @Test
    public void Constructor()
    {
        AssistantCard card = new AssistantCard(Wizard.WIZARD_1, 10, 5);
        assertAll(
                () -> assertEquals(card.getWizard(), Wizard.WIZARD_1),
                () -> assertEquals(card.getTurnOrder(), 10),
                () -> assertEquals(card.getSteps(), 5)
        );
    }

    /**
     * Test if the constructor works accurately when a wrong card is passed
     */
    @Test
    public void ConstructorWrongCard()
    {
        AssistantCard card = new AssistantCard(null, 0, -1);
        assertAll(
                () -> assertEquals(card.getWizard(), Wizard.WIZARD_1),
                () -> assertEquals(card.getTurnOrder(), 1),
                () -> assertEquals(card.getSteps(), 1)
        );
    }

}

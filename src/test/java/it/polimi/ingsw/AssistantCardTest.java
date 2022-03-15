package it.polimi.ingsw;

import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.Wizard;
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
        assertAll(() -> assertEquals(card.getWizard(), Wizard.WIZARD_1),
                () -> assertEquals(card.getTurnOrder(), 10),
                () -> assertEquals(card.getSteps(), 5));
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
    }

}

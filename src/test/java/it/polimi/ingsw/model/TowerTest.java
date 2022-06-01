package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TowerTest
{
    /**
     * For each color, create a tower for that color and check it's color.
     */
    @Test
    public void checkTowerColorTest()
    {
        for (TowerColor color : TowerColor.values())
        {
            Tower t = new Tower(color);
            assertEquals(color, t.getColor());
            assertNotEquals(null, t.toString());
        }
    }

    /**
     * The tower constructor should throw NullPointerException if an invalid parameter is passed.
     */
    @Test
    public void nullParameterTest()
    {
        assertThrows(NullPointerException.class, () -> new Tower(null));
    }
}

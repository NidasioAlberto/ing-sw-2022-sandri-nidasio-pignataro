package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoins;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the Monk class
 */
public class MonkTest
{
    CharacterCard monk;
    Game game;
    List<Student> studentsBag;
    Player player1 = new Player("Player1", TowerColor.WHITE);
    Player player2 = new Player("Player2", TowerColor.BLACK);
    @BeforeEach
    public void init()
    {
        game = new Game();
        try
        {
            game.addPlayer(player1);
            game.addPlayer(player2);
        }
        catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        }
        game.setupGame();
        studentsBag = game.getStudentBag();
        monk = CharacterCard.createCharacterCard(CharacterCardType.MONK, game);
    }

    @Test
    public void ConstructorTest()
    {
        // Decorate a null game
        assertThrows(NullPointerException.class, () -> new Monk(null));

        // Check the card type
        assertEquals(CharacterCardType.MONK, monk.getCardType());

        // Check the initial cost
        assertEquals(1, monk.cost);

        // Check that 4 students have been removed from the bag
        assertEquals(studentsBag.size() - 4, game.getStudentBag().size());

        // At the beginning the card is not active
        assertFalse(monk.activated);

        // At the beginning the card isn't used
        assertFalse(monk.firstUsed);
    }

    @Test
    public void isValidActionTest()
    {
        // The card is always playable
        assertTrue(monk.isPlayable());

        game.selectPlayer(0);
        // The card is not active so the isValidAction return the instance validation
        for (GameAction action: GameAction.values())
        {
            assertEquals(game.isValidAction(action), monk.isValidAction(action));
        }

        // If the player doesn't have enough coins an exception is thrown
        assertThrows(NotEnoughCoins.class, () -> monk.activate());

        try
        {
            // Activate the card
            player1.addCoins(1);
            monk.activate();
            for (GameAction action: GameAction.values())
            {
                // When Monk is active the only valid action is MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND
                if (action == GameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND)
                    assertTrue(monk.isValidAction(action));
                else assertFalse(monk.isValidAction(action));
            }
        }
        catch (NotEnoughCoins e)
        {
            e.printStackTrace();
        }

        // The card has been used the first time so its cost increases
        assertTrue(monk.firstUsed);
        assertEquals(2, monk.cost);
        assertTrue(monk.activated);

        // I deactivate the card
        monk.deactivate();
        assertFalse(monk.activated);

        try
        {
            // Activate the card
            player1.addCoins(2);
            monk.activate();
        }
        catch (NotEnoughCoins e)
        {
            e.printStackTrace();
        }

        // The card was already used so the cost doesn't increase
        assertTrue(monk.firstUsed);
        assertEquals(2, monk.cost);
        assertTrue(monk.activated);
    }

    @Test
    public void applyActionTest()
    {
        //assertThrows(NoSuchElementException.class, () -> monk.applyAction());
    }
}

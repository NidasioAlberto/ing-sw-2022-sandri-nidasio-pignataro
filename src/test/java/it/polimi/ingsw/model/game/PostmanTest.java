package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TowerColor;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Class to test the Postman class
 */
public class PostmanTest
{
    // Setup
    CharacterCard postman;
    Game game;
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
        postman = CharacterCard.createCharacterCard(CharacterCardType.POSTMAN, game);
    }

    @Test
    public void ConstructorTest()
    {
        // Decorate a null game
        assertThrows(NullPointerException.class, () -> new Postman(null));

        // Check the card type
        assertEquals(CharacterCardType.POSTMAN, postman.getCardType());

        // Check the initial cost
        assertEquals(1, postman.cost);

        // At the beginning the card is not active
        assertFalse(postman.activated);

        // At the beginning the card isn't used
        assertFalse(postman.firstUsed);
    }

    @Test
    public void isPlayableTest()
    {
        //TODO
    }

    @Test
    public void isValidActionTest()
    {
        // The card doesn't intercept any action
        for (GameAction action: GameAction.values())
        {
            assertEquals(game.isValidAction(action), postman.isValidAction(action));
        }

        // A player must be selected to activate the card
        assertThrows(NoSuchElementException.class, () -> postman.activate());

        // Select a player
        game.selectPlayer(0);

        // If the player doesn't have enough coins an exception is thrown
        assertThrows(NotEnoughCoinsException.class, () -> postman.activate());

        try
        {
            // Activate the card
            player1.addCoins(1);
            postman.activate();
            assertEquals(0, player1.getCoins());
            assertTrue(postman.activated);
            for (GameAction action: GameAction.values())
            {
                // The card doesn't intercept any action
                assertEquals(game.isValidAction(action), postman.isValidAction(action));

                // The card deactivates itself when the action is SELECT_CLOUD_TILE
                if (action == GameAction.SELECT_CLOUD_TILE)
                {
                    assertFalse(postman.activated);
                }
            }
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card has been used the first time so its cost increases
        assertTrue(postman.firstUsed);
        assertEquals(2, postman.cost);

        try
        {
            // Activate the card
            player1.addCoins(2);
            postman.activate();
            assertEquals(0, player1.getCoins());
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card was already used so the cost doesn't increase
        assertTrue(postman.firstUsed);
        assertEquals(2, postman.cost);
        assertTrue(postman.activated);

        // I deactivate the card
        postman.deactivate();
        assertFalse(postman.activated);
    }

    @Test
    public void isValidMotherNatureMovementTest()
    {
        // Select a player
        game.selectPlayer(0);

        try
        {
            // Activate the card
            player1.addCoins(1);
            postman.activate();
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // An exception is thrown if the player hasn't selected a card
        NoSuchElementException e1 = assertThrows(NoSuchElementException.class, () -> postman.isValidMotherNatureMovement(1));
        assertEquals("[Postman] Player didn't select assistant card", e1.getMessage());

        // Select a card
        player1.selectCard(1);

        // Mother natura can't do zero steps
        assertFalse(postman.isValidMotherNatureMovement(0));

        // Correct steps number
        assertTrue(postman.isValidMotherNatureMovement(1));
        assertTrue(postman.isValidMotherNatureMovement(2));
        assertTrue(postman.isValidMotherNatureMovement(3));

        // Wrong steps number
        assertFalse(postman.isValidMotherNatureMovement(4));

        // Deactivate the card
        postman.deactivate();

        // When the card is deactivated the instance's method is called
        for( int i = 0; i < 5; i++)
            assertEquals(game.isValidMotherNatureMovement(i), postman.isValidMotherNatureMovement(i));

    }
}

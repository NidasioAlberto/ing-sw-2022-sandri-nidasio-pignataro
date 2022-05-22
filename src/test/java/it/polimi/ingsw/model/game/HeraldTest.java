package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Class to test the Herald class
 */
public class HeraldTest
{
    // Setup
    CharacterCard herald;
    Game game;
    Player player1 = new Player("Player1", TowerColor.WHITE, GameMode.EXPERT);
    Player player2 = new Player("Player2", TowerColor.BLACK, GameMode.EXPERT);

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
        herald = CharacterCard.createCharacterCard(CharacterCardType.HERALD, game);
    }

    @Test
    public void ConstructorTest()
    {
        // Decorate a null game
        assertThrows(NullPointerException.class, () -> new Herald(null));

        // Check the card type
        assertEquals(CharacterCardType.HERALD, herald.getCardType());

        // Check the initial cost
        assertEquals(3, herald.cost);

        // At the beginning the card is not active
        assertFalse(herald.activated);

        // At the beginning the card isn't used
        assertFalse(herald.firstUsed);
    }

    @Test
    public void isValidActionTest()
    {
        // The card is always playable
        assertTrue(herald.isPlayable());

        // A player must be selected to activate the card
        assertThrows(NoSuchElementException.class, () -> herald.activate());

        // Select a player
        game.selectPlayer(0);

        // If the player doesn't have enough coins an exception is thrown
        assertThrows(NotEnoughCoinsException.class, () -> herald.activate());

        try
        {
            // Activate the card
            player1.getBoard().addCoins(3);
            herald.activate();
            assertEquals(1, player1.getBoard().getCoins());
            for (ExpertGameAction action: ExpertGameAction.values())
            {
                // When Herald is active the only valid action is SELECT_ISLAND
                if (action == ExpertGameAction.SELECT_ISLAND)
                    assertTrue(herald.isValidAction(action));
                else assertFalse(herald.isValidAction(action));
            }
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card has been used the first time so its cost increases
        assertTrue(herald.firstUsed);
        assertEquals(4, herald.cost);
        assertTrue(herald.activated);

        // I deactivate the card
        herald.deactivate();
        assertFalse(herald.activated);

        try
        {
            // Activate the card
            player1.getBoard().addCoins(4);
            herald.activate();
            assertEquals(1, player1.getBoard().getCoins());
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card was already used so the cost doesn't increase
        assertTrue(herald.firstUsed);
        assertEquals(4, herald.cost);
        assertTrue(herald.activated);
    }

    @Test
    public void applyActionTest()
    {
        // An exception is thrown if I call the method without a selected player
        herald.activated = true;
        NoSuchElementException e2 = assertThrows(NoSuchElementException.class, () -> herald.applyAction());
        assertEquals("[Herald] No player selected", e2.getMessage());

        herald.activated = false;

        // Select a player
        game.selectPlayer(0);

        try
        {
            // Activate the card
            player1.getBoard().addCoins(3);
            herald.activate();
            assertEquals(1, player1.getBoard().getCoins());
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // An exception is thrown if no island is selected
        NoSuchElementException e1 = assertThrows(NoSuchElementException.class, () -> herald.applyAction());
        assertEquals("[Herald] No island selected", e1.getMessage());

        // The player selects an island where there is already a student
        int islandIndex = 0;
        if (game.getIslands().get(islandIndex).getStudents().size() == 0)
            islandIndex++;
        assertEquals(1, game.getIslands().get(islandIndex).getStudents().size());
        player1.selectIsland(islandIndex);

        // The player has the professor of the student color on the island
        player1.getBoard().addProfessor(new Professor(game.getIslands().get(islandIndex).getStudents().get(0).getColor()));

        // On the island there isn't a tower
        assertEquals(0, game.getIslands().get(islandIndex).getTowers().size());

        // Apply the action
        herald.applyAction();

        // On the island there is now a tower of the color of player1
        assertEquals(1, game.getIslands().get(islandIndex).getTowers().size());
        assertEquals(TowerColor.WHITE, game.getIslands().get(islandIndex).getTowers().get(0).getColor());

        // The card deactivates by itself
        assertFalse(herald.activated);
    }
}

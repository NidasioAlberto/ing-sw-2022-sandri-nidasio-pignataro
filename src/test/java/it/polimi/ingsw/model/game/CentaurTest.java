package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the Centaur class
 */
public class CentaurTest
{
    // Setup
    CharacterCard centaur;
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
        } catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        }
        game.setupGame();
        centaur = CharacterCard.createCharacterCard(CharacterCardType.CENTAUR, game);
        centaur.init();
    }

    @Test
    public void ConstructorTest()
    {
        // Decorate a null game
        assertThrows(NullPointerException.class, () -> new Centaur(null));

        // Check the card type
        assertEquals(CharacterCardType.CENTAUR, centaur.getCardType());

        // Check the initial cost
        assertEquals(3, centaur.cost);

        // At the beginning the card is not active
        assertFalse(centaur.activated);

        // At the beginning the card isn't used
        assertFalse(centaur.firstUsed);
    }

    @Test
    public void isPlayableTest()
    {
        // This card is playable only before the movement of mother nature
        // At the beginning mother nature hasn't moved
        assertFalse(game.motherNatureMoved);
        // So the card is playable
        assertTrue(centaur.isPlayable());

        // Imagine mother nature moves
        game.motherNatureMoved = true;
        // So the card isn't playable
        assertFalse(centaur.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        // A player must be selected to activate the card
        assertThrows(NoSuchElementException.class, () -> centaur.activate());

        // Select a player
        game.selectPlayer(0);

        // If the player doesn't have enough coins an exception is thrown
        assertThrows(NotEnoughCoinsException.class, () -> centaur.activate());

        try
        {
            // Activate the card
            player1.getBoard().addCoins(3);
            centaur.activate();
            assertEquals(1, player1.getBoard().getCoins());

            // The card accepts only ACTION_BASE
            for (ExpertGameAction action : ExpertGameAction.values())
            {
                if (action == ExpertGameAction.BASE_ACTION)
                    assertTrue(centaur.isValidAction(action));
                else
                    assertFalse(centaur.isValidAction(action));
            }
        } catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card has been used the first time so its cost increases
        assertTrue(centaur.firstUsed);
        assertEquals(4, centaur.cost);
        assertTrue(centaur.activated);

        // Deactivate the card
        centaur.deactivate();
        assertFalse(centaur.activated);

        try
        {
            // Activate the card
            player1.getBoard().addCoins(4);
            centaur.activate();
            assertEquals(1, player1.getBoard().getCoins());
        } catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card was already used so the cost doesn't increase
        assertTrue(centaur.firstUsed);
        assertEquals(4, centaur.cost);
        assertTrue(centaur.activated);
    }

    @Test
    public void applyActionTest()
    {
        try
        {
            // Activate the card
            game.selectPlayer(0);
            player1.getBoard().addCoins(3);
            centaur.activate();
            assertEquals(1, player1.getBoard().getCoins());
            assertTrue(centaur.activated);
        } catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // Mother nature hasn't moved so the card remains active
        game.motherNatureMoved = false;
        centaur.applyAction();
        assertTrue(centaur.activated);

        // Mother nature has moved so the card deactivates itself
        game.motherNatureMoved = true;
        centaur.applyAction();
        assertFalse(centaur.activated);
    }

    @Test
    public void computePlayerInfluenceTest()
    {
        // Select a player
        game.selectPlayer(0);
        player1.getBoard().addCoins(3);

        try
        {
            // Activate the card
            centaur.activate();
            assertEquals(1, player1.getBoard().getCoins());
            assertTrue(centaur.activated);
        } catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // An exception is thrown if the island index is wrong
        IndexOutOfBoundsException e = assertThrows(IndexOutOfBoundsException.class,
                () -> centaur.computePlayerInfluence(player1, -1));
        assertEquals("[Centaur] Island index out of bounds", e.getMessage());
        IndexOutOfBoundsException e1 = assertThrows(IndexOutOfBoundsException.class,
                () -> centaur.computePlayerInfluence(player1, 12));
        assertEquals("[Centaur] Island index out of bounds", e1.getMessage());

        // An exception is thrown if the player is null
        NullPointerException e2 = assertThrows(NullPointerException.class,
                () -> centaur.computePlayerInfluence(null, 1));
        assertEquals("[Centaur] player null", e2.getMessage());

        // The player selects an island where there is already a student
        int islandIndex = 0;
        if (game.getIslands().get(islandIndex).getStudents().size() == 0)
            islandIndex++;
        assertEquals(1, game.getIslands().get(islandIndex).getStudents().size());

        // The player has the professor of the student color on the island
        player1.getBoard().addProfessor(
                new Professor(game.getIslands().get(islandIndex).getStudents().get(0).getColor()));

        // The island has a tower of the color of the player1
        game.getIslands().get(islandIndex).addTower(new Tower(TowerColor.WHITE));

        // The card is active so towers are not counted in the influence
        assertEquals(1, centaur.computePlayerInfluence(player1, islandIndex));
        assertEquals(0, centaur.computePlayerInfluence(player2, islandIndex));

        // Remove the tower
        game.getIslands().get(islandIndex).removeAllTowers();

        // Add a tower of the color of the player2
        game.getIslands().get(islandIndex).addTower(new Tower(TowerColor.BLACK));

        // The card is active so towers are not counted in the influence
        assertEquals(1, centaur.computePlayerInfluence(player1, islandIndex));
        assertEquals(0, centaur.computePlayerInfluence(player2, islandIndex));

        // Deactivate the card
        centaur.deactivate();

        // The card isn't active so towers are counted in the influence
        assertEquals(1, centaur.computePlayerInfluence(player1, islandIndex));
        assertEquals(1, centaur.computePlayerInfluence(player2, islandIndex));
    }
}

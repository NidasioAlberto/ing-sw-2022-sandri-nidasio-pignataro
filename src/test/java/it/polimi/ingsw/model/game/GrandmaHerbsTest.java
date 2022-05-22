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
 * Class to test the GrandmaHerbs class
 */
public class GrandmaHerbsTest
{
    // Setup
    CharacterCard grandmaHerbs;
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
        grandmaHerbs = CharacterCard.createCharacterCard(CharacterCardType.GRANDMA_HERBS, game);
    }

    @Test
    public void ConstructorTest()
    {
        // Decorate a null game
        assertThrows(NullPointerException.class, () -> new Monk(null));

        // Check the card type
        assertEquals(CharacterCardType.GRANDMA_HERBS, grandmaHerbs.getCardType());

        // Check the initial cost
        assertEquals(2, grandmaHerbs.cost);

        // At the beginning the card is not active
        assertFalse(grandmaHerbs.activated);

        // At the beginning the card isn't used
        assertFalse(grandmaHerbs.firstUsed);

        // At the beginning there are 4 noEntryTiles
        assertEquals(4, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());

        assertNotEquals(null, grandmaHerbs.toString());
    }

    @Test
    public void isPlayableTest()
    {
        // Select a player
        player1.getBoard().addCoins(20);
        game.selectPlayer(0);

        // Select an island
        player1.selectIsland(0);

        for (int i = 0; i < 4; i++)
        {
            // The card is playable when there are noEntryTiles
            assertTrue(grandmaHerbs.isPlayable());
            assertEquals(4 - i, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());

            // Activate and apply the card
            try
            {
                grandmaHerbs.activate();
                grandmaHerbs.applyAction();
            } catch (NotEnoughCoinsException e)
            {
                e.printStackTrace();
            }
        }

        // The card isn't playable because there aren't noEntryTiles
        assertFalse(grandmaHerbs.isPlayable());
        assertEquals(0, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());
    }

    @Test
    public void isValidActionTest()
    {
        // A player must be selected to activate the card
        assertThrows(NoSuchElementException.class, () -> grandmaHerbs.activate());

        // Select a player
        game.selectPlayer(0);

        // If the player doesn't have enough coins an exception is thrown
        assertThrows(NotEnoughCoinsException.class, () -> grandmaHerbs.activate());

        try
        {
            // Activate the card
            player1.getBoard().addCoins(2);
            grandmaHerbs.activate();
            assertEquals(1, player1.getBoard().getCoins());
            for (ExpertGameAction action : ExpertGameAction.values())
            {
                // When GrandmaHerbs is active the only valid action is
                // MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND
                if (action == ExpertGameAction.MOVE_NO_ENTRY_FROM_CHARACTER_CARD_TO_ISLAND)
                    assertTrue(grandmaHerbs.isValidAction(action));
                else
                    assertFalse(grandmaHerbs.isValidAction(action));
            }
        } catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card has been used the first time so its cost increases
        assertTrue(grandmaHerbs.firstUsed);
        assertEquals(3, grandmaHerbs.cost);
        assertTrue(grandmaHerbs.activated);

        // I deactivate the card
        grandmaHerbs.deactivate();
        assertFalse(grandmaHerbs.activated);

        try
        {
            // Activate the card
            player1.getBoard().addCoins(3);
            grandmaHerbs.activate();
            assertEquals(1, player1.getBoard().getCoins());
        } catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card was already used so the cost doesn't increase
        assertTrue(grandmaHerbs.firstUsed);
        assertEquals(3, grandmaHerbs.cost);
        assertTrue(grandmaHerbs.activated);
    }

    @Test
    public void applyActionTest()
    {
        // An exception is thrown if I call the method without a selected player
        grandmaHerbs.activated = true;
        NoSuchElementException e4 =
                assertThrows(NoSuchElementException.class, () -> grandmaHerbs.applyAction());
        assertEquals("[GrandmaHerbs] No player selected", e4.getMessage());

        grandmaHerbs.activated = false;

        // Select a player
        player1.getBoard().addCoins(2);
        game.selectPlayer(0);

        // The card isn't active so nothing happens
        grandmaHerbs.applyAction();
        assertEquals(4, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());

        // Activate the card
        try
        {
            grandmaHerbs.activate();
        } catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // If the player hasn't selected an island, an exception is thrown
        assertThrows(NoSuchElementException.class, () -> grandmaHerbs.applyAction());

        // Select an island
        player1.selectIsland(0);

        // At the beginning the selected island has 0 noEntryTiles
        assertEquals(0, game.getIslands().get(0).getNoEntryTiles());

        // Apply the action
        grandmaHerbs.applyAction();

        // The selected island has now 1 noEntryTile
        assertEquals(1, game.getIslands().get(0).getNoEntryTiles());

        // The card has now 1 noEntryTile less
        assertEquals(3, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());

        // At the end the card deactivates by itself
        assertFalse(grandmaHerbs.activated);

        // At this point there are 3 noEntryTiles on the card and
        // 1 noEntryTile is on the selected island

        // Add coins to the player
        player1.getBoard().addCoins(20);

        for (int i = 1; i < 4; i++)
        {
            // Activate and apply the card
            try
            {
                grandmaHerbs.activate();
                grandmaHerbs.applyAction();

                assertEquals(1 + i, game.getIslands().get(0).getNoEntryTiles());
                assertEquals(3 - i, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());
                assertEquals(20 - 3 * i + 1, player1.getBoard().getCoins());
            } catch (NotEnoughCoinsException e1)
            {
                e1.printStackTrace();
            }
        }

        // Activate the card
        try
        {
            grandmaHerbs.activate();
        } catch (NotEnoughCoinsException e2)
        {
            e2.printStackTrace();
        }

        // When there aren't noEntryTiles on the card an exception is thrown
        NoSuchElementException e3 =
                assertThrows(NoSuchElementException.class, () -> grandmaHerbs.applyAction());
        assertEquals("[GrandmaHerbs] No more no entry tiles", e3.getMessage());
    }

    @Test
    public void computeInfluenceTest()
    {
        // An exception is thrown when the island index is wrong
        IndexOutOfBoundsException e = assertThrows(IndexOutOfBoundsException.class,
                () -> grandmaHerbs.computeInfluence(-1));
        assertEquals("[Game] Island index out of bounds", e.getMessage());
        IndexOutOfBoundsException e1 = assertThrows(IndexOutOfBoundsException.class,
                () -> grandmaHerbs.computeInfluence(12));
        assertEquals("[Game] Island index out of bounds", e1.getMessage());

        // Select a player
        player1.getBoard().addCoins(2);
        game.selectPlayer(0);

        // The player selects an island where there is already a student
        int islandIndex = 0;
        if (game.getIslands().get(islandIndex).getStudents().size() == 0)
            islandIndex++;
        player1.selectIsland(islandIndex);

        // Imagine the player1 owns the professor of the only student present on the selected island
        player1.getBoard().addProfessor(
                new Professor(game.getIslands().get(islandIndex).getStudents().get(0).getColor()));

        // Activate the card
        try
        {
            grandmaHerbs.activate();
        } catch (NotEnoughCoinsException e2)
        {
            e2.printStackTrace();
        }

        // Before the action
        assertEquals(0, game.getIslands().get(islandIndex).getNoEntryTiles());
        assertEquals(0, game.getIslands().get(islandIndex).getTowers().size());
        assertEquals(1, game.getIslands().get(islandIndex).getStudents().size());
        assertEquals(4, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());

        // Apply the action
        grandmaHerbs.applyAction();
        assertEquals(1, game.getIslands().get(islandIndex).getNoEntryTiles());
        assertEquals(3, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());

        // Compute the influence on the selected island that has 1 noEntryTile
        grandmaHerbs.computeInfluence(islandIndex);
        // The noEntryTile is removed from the island
        assertEquals(0, game.getIslands().get(islandIndex).getNoEntryTiles());
        // The noEntryTile returns to the card
        grandmaHerbs.isPlayable();  // This method updates the number of entrys
        assertEquals(4, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());
        // The influence isn't calculated
        assertEquals(0, game.getIslands().get(islandIndex).getTowers().size());
        assertEquals(1, game.getIslands().get(islandIndex).getStudents().size());

        // Compute the influence on the selected island that has 0 noEntryTiles,
        // so the influence is computed normally
        grandmaHerbs.computeInfluence(islandIndex);
        assertEquals(0, game.getIslands().get(islandIndex).getNoEntryTiles());
        assertEquals(4, ((GrandmaHerbs) grandmaHerbs).getNoEntryTiles());
        assertEquals(1, game.getIslands().get(islandIndex).getTowers().size());
        assertEquals(player1.getColor(),
                game.getIslands().get(islandIndex).getTowers().get(0).getColor());
        assertEquals(1, game.getIslands().get(islandIndex).getStudents().size());
    }
}

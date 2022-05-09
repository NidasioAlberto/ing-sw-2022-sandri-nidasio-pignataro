package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.NoSuchAssistantCardException;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the Player class
 */
public class PlayerTest
{
    Player player;
    SchoolBoard board;

    @BeforeEach
    public void init()
    {
        board = new SchoolBoard(TowerColor.WHITE, GameMode.CLASSIC);
        board.setPlayersNumber(2);
        for (int i = 0; i < 8; i++)
            board.addTower(new Tower(TowerColor.WHITE));
        player = new Player("Player1", board);
    }

    @Test
    /**
     * Test that Constructor throws NullPointerException when one of the three parameter is null and
     * test the initialization of the object
     */
    public void constructorTest()
    {
        // Nickname can't be null
        assertThrows(NullPointerException.class,
                () -> new Player(null, new SchoolBoard(TowerColor.WHITE, GameMode.CLASSIC)));

        // Color can't be null
        assertThrows(NullPointerException.class, () -> new Player("Player1", (TowerColor) null, GameMode.CLASSIC));

        // SchoolBoard can't be null
        assertThrows(NullPointerException.class, () -> new Player("Player1", (SchoolBoard) null));

        // Check the initialization values
        assertEquals("Player1", player.getNickname());
        assertEquals(board, player.getBoard());
        assertTrue(player.getSelectedCard().isEmpty());
        assertTrue(player.getSelectedIsland().isEmpty());
        assertTrue(player.getSelectedColors().isEmpty());
        assertTrue(player.getSelectedCloudTile().isEmpty());
        assertTrue(player.getCards().isEmpty());
        assertEquals(TowerColor.WHITE, player.getColor());
        assertEquals(0, player.getBoard().getCoins());
    }

    @Test
    /**
     * Test that Constructor that has 2 parameters
     */
    public void constructorTest2()
    {
        // Nickname can't be null
        assertThrows(NullPointerException.class, () -> new Player(null, TowerColor.WHITE, GameMode.CLASSIC));

        // Color can't be null
        assertThrows(NullPointerException.class, () -> new Player("Player1", (TowerColor) null, GameMode.CLASSIC));

        // Check the initialization values
        Player player1 = new Player("Player1", TowerColor.BLACK, GameMode.CLASSIC);
        assertEquals("Player1", player1.getNickname());
        assertTrue(player1.getSelectedCard().isEmpty());
        assertTrue(player1.getSelectedIsland().isEmpty());
        assertTrue(player1.getSelectedColors().isEmpty());
        assertTrue(player1.getSelectedCloudTile().isEmpty());
        assertTrue(player1.getCards().isEmpty());
        assertEquals(TowerColor.BLACK, player1.getColor());
        assertEquals(0, player1.getBoard().getCoins());
    }

    @Test
    /**
     * Test that addCoins throws IllegalArgumentException when the parameter is negative
     */
    public void addCoinsTest()
    {
        // The parameter must be positive
        assertThrows(IllegalArgumentException.class, () -> player.getBoard().addCoins(-1));

        // Add 0 coins
        player.getBoard().addCoins(0);
        assertEquals(0, player.getBoard().getCoins());

        // Add a positive number of coins
        player.getBoard().addCoins(1);
        assertEquals(1, player.getBoard().getCoins());
    }

    @Test
    /**
     * Test that removeCoins throws IllegalArgumentException if the parameter is negative or there
     * aren't enough coins
     */
    public void removeCoinsTest()
    {
        // The parameter must be positive
        assertThrows(IllegalArgumentException.class, () -> player.getBoard().removeCoins(-1));

        // The number of player's coins can't be negative
        assertThrows(NotEnoughCoinsException.class, () -> player.getBoard().removeCoins(1));

        // Add and remove 3 coins
        player.getBoard().addCoins(3);
        assertEquals(3, player.getBoard().getCoins());
        player.getBoard().removeCoins(3);
        assertEquals(0, player.getBoard().getCoins());
    }

    @Test
    /**
     * Test that selectCard throws NullPointerException if the parameter is null and throws
     * IllegalArgumentException if the player doesn't have the selected card
     */
    public void selectCardTest()
    {
        AssistantCard card1 = new AssistantCard(Wizard.WIZARD_1, 1, 1);
        AssistantCard card2 = new AssistantCard(Wizard.WIZARD_1, 2, 1);
        player.addCard(card1);
        player.addCard(card2);

        // Select a card the player doesn't have
        assertThrows(NoSuchAssistantCardException.class, () -> player.selectCard(3));

        // Select a card the player has
        assertTrue(player.getSelectedCard().isEmpty());
        player.selectCard(1);
        assertEquals(card1, player.getSelectedCard().get());

        // Select another card, so the previous one is now used
        assertTrue(card1.isUsed());
        player.selectCard(2);
        assertTrue(card2.isUsed());
        assertEquals(card2, player.getSelectedCard().get());
    }

    @Test
    /**
     * Test that addCard throws NullPointerException if the parameter is null and it doesn't add
     * cards with a different wizard from the cards already present and it doesn't add cards already
     * present
     */
    public void addCardTest()
    {
        // The parameter can't be null
        assertThrows(NullPointerException.class, () -> player.addCard(null));

        // At the beginning player has no cards
        assertTrue(player.getCards().isEmpty());

        // Add a correct card
        AssistantCard card = new AssistantCard(Wizard.WIZARD_1, 1, 1);
        player.addCard(card);
        assertEquals(card, player.getCards().get(0));
        assertEquals(1, player.getCards().size());

        // A card of a different wizard can't be added
        player.addCard(new AssistantCard(Wizard.WIZARD_2, 1, 1));
        assertEquals(1, player.getCards().size());

        // A card already present can't be added
        player.addCard(card);
        assertEquals(1, player.getCards().size());

        // A card with the same turOrder of one already present can't be added
        player.addCard(new AssistantCard(Wizard.WIZARD_1, 1, 1));
        assertEquals(1, player.getCards().size());
    }

    @Test
    /**
     * Test that removeCard throws NullPointerException if the parameter is null and
     * IllegalArgumentException if the player doesn't have the card passed via parameter
     */
    public void removeCardTest()
    {
        // Add a correct card
        AssistantCard card = new AssistantCard(Wizard.WIZARD_1, 1, 1);
        player.addCard(card);
        assertEquals(card, player.getCards().get(0));
        assertEquals(1, player.getCards().size());

        // Remove a card the player doesn't have
        assertThrows(NoSuchAssistantCardException.class, () -> player.removeCard(2));

        // Add a correct card
        AssistantCard card1 = new AssistantCard(Wizard.WIZARD_1, 2, 1);
        player.addCard(card1);
        assertEquals(card1, player.getCards().get(1));
        assertEquals(2, player.getCards().size());

        // Remove a card the player has
        player.removeCard(1);
        assertEquals(1, player.getCards().size());
        assertEquals(card1, player.getCards().get(0));
    }

    @Test
    /**
     * Test that removeSelectedCard throws NoSuchElementException when no card is selected
     */
    public void removeSelectedCardTest()
    {
        // Select a card
        AssistantCard card = new AssistantCard(Wizard.WIZARD_2, 1, 1);
        AssistantCard card1 = new AssistantCard(Wizard.WIZARD_2, 2, 1);
        player.addCard(card);
        player.addCard(card1);
        player.selectCard(1);
        assertEquals(card, player.getSelectedCard().get());
        assertEquals(card, player.getCards().get(0));

        // Remove the selected card
        player.removeSelectedCard();
        assertTrue(player.getSelectedCard().isEmpty());
        assertEquals(1, player.getCards().size());
        assertEquals(card1, player.getCards().get(0));
    }

    @Test
    /**
     * Test the method selectIsland
     */
    public void selectIslandTest()
    {
        // At the beginning no island is selected
        assertTrue(player.getSelectedIsland().isEmpty());

        // Select an island
        player.selectIsland(3);
        assertEquals(3, player.getSelectedIsland().get());
    }

    @Test
    /**
     * Test that selectColor throws a NullPointerException if the parameter is null
     */
    public void selectColorTest()
    {
        // Select a null color
        assertThrows(NullPointerException.class, () -> player.selectColor(null));

        // At the beginning there are no selected colors
        assertTrue(player.getSelectedColors().isEmpty());

        // Select a color
        player.selectColor(SchoolColor.PINK);
        assertEquals(SchoolColor.PINK, player.getSelectedColors().get(0));
        assertEquals(1, player.getSelectedColors().size());

        // Select another color
        player.selectColor(SchoolColor.GREEN);
        assertEquals(SchoolColor.PINK, player.getSelectedColors().get(0));
        assertEquals(SchoolColor.GREEN, player.getSelectedColors().get(1));
        assertEquals(2, player.getSelectedColors().size());
    }

    @Test
    /**
     * Test the method selectCloudTile
     */
    public void selectCloudTileTest()
    {
        // At the beginning no CloudTile is selected
        assertTrue(player.getSelectedCloudTile().isEmpty());

        // Select a CloudTile
        player.selectCloudTile(1);
        assertEquals(1, player.getSelectedCloudTile().get());
    }

    @Test
    /**
     * Test the method selectCharacterCard
     */
    public void selectCharacterCardTest()
    {
        // At the beginning no CharacterCard is selected
        assertTrue(player.getSelectedCharacterCard().isEmpty());

        // Select a CharacterCard
        player.selectCharacterCard(1);
        assertEquals(1, player.getSelectedCharacterCard().get());
    }

    @Test
    /**
     * Test the method clearSelections
     */
    public void clearSelectionsTest()
    {
        // Select a card, island, color and CloudTile
        AssistantCard card = new AssistantCard(Wizard.WIZARD_2, 1, 1);
        player.addCard(card);
        player.selectCard(1);
        player.selectIsland(1);
        player.selectColor(SchoolColor.GREEN);
        player.selectCloudTile(1);
        player.selectCharacterCard(1);

        // Check the selections
        assertEquals(card, player.getSelectedCard().get());
        assertEquals(1, player.getSelectedIsland().get());
        assertEquals(SchoolColor.GREEN, player.getSelectedColors().get(0));
        assertEquals(1, player.getSelectedCloudTile().get());

        // Clear the selections
        player.clearSelections();
        assertFalse(player.getSelectedCard().isEmpty());
        assertTrue(player.getSelectedIsland().isEmpty());
        assertTrue(player.getSelectedColors().isEmpty());

        player.selectIsland(1);
        player.selectColor(SchoolColor.GREEN);

        // Clear the selections at the end of the turn
        player.clearSelectionsEndTurn();
        assertTrue(player.getSelectedIsland().isEmpty());
        assertTrue(player.getSelectedColors().isEmpty());
        assertTrue(player.getSelectedCloudTile().isEmpty());
        assertTrue(player.getSelectedCharacterCard().isEmpty());
    }
}

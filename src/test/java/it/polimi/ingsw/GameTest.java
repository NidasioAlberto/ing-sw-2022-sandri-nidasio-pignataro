package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;

public class GameTest
{
    Game game;

    @BeforeEach
    public void init()
    {
        game = new Game(2);
    }

    @Test
    public void constructorTest()
    {
        assertThrows(NullPointerException.class, () -> game = new Game(null));
        assertThrows(IllegalArgumentException.class, () -> game = new Game(1));
        assertThrows(IllegalArgumentException.class, () -> game = new Game(5));
    }

    @Test
    public void addPlayerTest()
    {
        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK, new SchoolBoard(TowerColor.BLACK));
        Player player2 = new Player("Player2", TowerColor.GREY, new SchoolBoard(TowerColor.GREY));
        Player player3 = new Player("Player3", TowerColor.WHITE, new SchoolBoard(TowerColor.WHITE));
        Player player4 = new Player("Player4", TowerColor.BLACK, new SchoolBoard(TowerColor.BLACK));
        Player player5 = new Player("Player5", TowerColor.GREY, new SchoolBoard(TowerColor.GREY));

        // The game should accept 2 players
        game = new Game(2);
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));
        assertThrows(TooManyPlayersException.class, () -> game.addPlayer(player3));

        // The game should accept 3 players
        game = new Game(3);
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));
        assertDoesNotThrow(() -> game.addPlayer(player3));
        assertThrows(TooManyPlayersException.class, () -> game.addPlayer(player4));

        // The game should accept 4 players
        game = new Game(4);
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));
        assertDoesNotThrow(() -> game.addPlayer(player4));
        assertDoesNotThrow(() -> game.addPlayer(player5));
        assertThrows(TooManyPlayersException.class, () -> game.addPlayer(player3));
    }

    @Test
    /**
     * Covers both selectPlayer and getSelectedPlayer functions.
     */
    public void selectPlayerTest()
    {
        // Create the two players
        Player player1 = new Player("Player1", TowerColor.BLACK, new SchoolBoard(TowerColor.BLACK));
        Player player2 = new Player("Player2", TowerColor.GREY, new SchoolBoard(TowerColor.GREY));

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Now there should not be any selected players
        assertTrue(() -> game.getSelectedPlayer().isEmpty());

        // Select the first player
        assertDoesNotThrow(() -> game.selectPlayer(0));
        assertDoesNotThrow(() -> game.getSelectedPlayer().get());
        assertEquals(game.getSelectedPlayer().get(), player1);

        // Select the second player
        assertDoesNotThrow(() -> game.selectPlayer(1));
        assertDoesNotThrow(() -> game.getSelectedPlayer().get());
        assertEquals(game.getSelectedPlayer().get(), player2);
    }

    @Test
    /**
     * Covers both getSortedPlayerList and getPlayerTableList functions.
     */
    public void getPlayerListTest()
    {
        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK, new SchoolBoard(TowerColor.BLACK));
        Player player2 = new Player("Player2", TowerColor.GREY, new SchoolBoard(TowerColor.GREY));

        // Configure the players board
        player1.getBoard().setPlayersNumber(2);
        player2.getBoard().setPlayersNumber(2);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // The table list order should correspond to the players order
        List<Player> tableList = game.getPlayerTableList();
        assertEquals(player1, tableList.get(0));
        assertEquals(player2, tableList.get(1));

        // Select a card for each player
        player1.selectCard(10);
        player2.selectCard(1);
        List<Player> sortedList = game.getSortedPlayerList();
        assertEquals(player1, sortedList.get(1));
        assertEquals(player2, sortedList.get(0));
    }
}

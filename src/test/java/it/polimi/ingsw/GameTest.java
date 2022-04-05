package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.NoSuchElementException;

import it.polimi.ingsw.model.game.Game;
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
        game = new Game(2, GameMode.CLASSIC);
    }

    @Test
    public void constructorTest()
    {
        assertThrows(NullPointerException.class, () -> game = new Game(null, GameMode.CLASSIC));
        assertThrows(IllegalArgumentException.class, () -> game = new Game(1, GameMode.CLASSIC));
        assertThrows(IllegalArgumentException.class, () -> game = new Game(5, GameMode.CLASSIC));
    }

    @Test
    public void addPlayerTest()
    {
        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);
        Player player3 = new Player("Player3", TowerColor.WHITE);
        Player player4 = new Player("Player4", TowerColor.BLACK);
        Player player5 = new Player("Player5", TowerColor.GREY);

        // The game should accept 2 players
        game = new Game(2, GameMode.CLASSIC);
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));
        assertThrows(TooManyPlayersException.class, () -> game.addPlayer(player3));

        // The game should accept 3 players
        game = new Game(3, GameMode.CLASSIC);
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));
        assertDoesNotThrow(() -> game.addPlayer(player3));
        assertThrows(TooManyPlayersException.class, () -> game.addPlayer(player4));

        // The game should accept 4 players
        game = new Game(4, GameMode.CLASSIC);
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
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

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
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

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

    @Test
    public void putStudentToIslandTest()
    {
        // Now there isn't a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.BLUE)));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));


        // Now there is still not a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.GREEN)));

        // Select the a player
        game.selectPlayer(1);

        // The selected player has not selected an island
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.RED)));

        // Select an island
        player2.selectIsland(3);

        // This should still fail because the game is not setup
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.YELLOW)));

        // Setup the game
        game.setupGame();

        int initialStudents = game.getIslands().get(3).getStudents().size();

        // Now it should work
        assertDoesNotThrow(() -> game.putStudentToIsland(new Student(SchoolColor.YELLOW)));

        // There should be a student in the selected island
        assertEquals(game.getIslands().get(3).getStudents().size(), initialStudents + 1);
    }

    @Test
    public void putStudentToDiningTest()
    {
        // Now there isn't a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.BLUE)));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));


        // Now there is still not a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToDining(new Student(SchoolColor.GREEN)));

        // Select the a player
        game.selectPlayer(1);

        // Now it should work (even if the game is not setup)
        assertDoesNotThrow(() -> game.putStudentToDining(new Student(SchoolColor.YELLOW)));

        // There should be a student in the selected island
        assertEquals(player2.getBoard().getStudentsNumber(SchoolColor.YELLOW), 1);
    }

    @Test
    public void pickStudentFromEntrance()
    {
        // Now there isn't a selected player
        assertThrows(NoSuchElementException.class, () -> game.pickStudentFromEntrance());

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // This should fail because there isn't a selected player
        assertThrows(NoSuchElementException.class, () -> game.pickStudentFromEntrance());

        game.selectPlayer(1);

        // This should still fail because the selected player has not selected a color
        assertThrows(NoSuchElementException.class, () -> game.pickStudentFromEntrance());

        // Setup the game
        game.setupGame();

        // Select the first student's color in the player's entrance
        player2.selectColor(player2.getBoard().getStudentsInEntrance().get(0).getColor());

        // This should work
        assertDoesNotThrow(() -> game.pickStudentFromEntrance());
    }

    @Test
    public void moveMotherNatureTest()
    {
        // The game is not set up, this should throw and error
        assertThrows(NoSuchElementException.class, () -> game.moveMotherNature(3));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // Now it should work
        assertDoesNotThrow(() -> game.moveMotherNature(3));
    }

    @Test
    public void isValidMotherNatureMovementTest()
    {
        // The game is not set up, this should throw
        assertThrows(NoSuchElementException.class, () -> game.isValidMotherNatureMovement(3));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // Select a card and the player
        player1.selectCard(6); // Card number 6 with 3 movements
        game.selectPlayer(0);

        assertFalse(() -> game.isValidMotherNatureMovement(0));
        assertTrue(() -> game.isValidMotherNatureMovement(1));
        assertTrue(() -> game.isValidMotherNatureMovement(2));
        assertTrue(() -> game.isValidMotherNatureMovement(3));
        assertFalse(() -> game.isValidMotherNatureMovement(4));
    }
}

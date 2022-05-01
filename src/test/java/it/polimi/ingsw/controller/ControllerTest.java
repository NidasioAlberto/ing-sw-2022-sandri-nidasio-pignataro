package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TowerColor;
import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.network.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the Controller class
 */
public class ControllerTest
{
    // Set up of a 3 players game in classic mode
    Controller controller;
    Match match;

    @BeforeEach
    public void init()
    {
        Server server = new Server();
        server.createMatch("Game1", 3, GameMode.CLASSIC);
        match = server.getMatchById("Game1");
        controller = new Controller(match, 3, GameMode.CLASSIC);
    }

    @Test
    public void constructorTest()
    {
        // Check the game mode and the number of players
        assertEquals(GameMode.CLASSIC, controller.getGameMode());
        assertEquals(3, controller.getPlayersNumber());

        // Create a controller with a null server
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new Controller(null, 2, GameMode.EXPERT));
        assertEquals("[Controller] The server is null", e1.getMessage());

        // Create a controller with a null mode
        NullPointerException e2 = assertThrows(NullPointerException.class, () -> new Controller(match, 2, null));
        assertEquals("[Controller] Game mode is null", e2.getMessage());

        // Create a controller with an invalid player number
        IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class, () -> new Controller(match, 1, GameMode.EXPERT));
        assertEquals("[Controller] Invalid players number", e3.getMessage());
        IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class, () -> new Controller(match, 4, GameMode.EXPERT));
        assertEquals("[Controller] Invalid players number", e4.getMessage());
    }

    @Test
    public void addPlayerTest()
    {
        // Add a player with a null nickname
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> controller.addPlayer(null));
        assertEquals("[Controller] The nickname is null", e1.getMessage());

        // Add the first player with a correct nickname
        try
        {
            controller.addPlayer("player1");
        } catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        }

        // Add a player with an existing nickname
        try
        {
            controller.addPlayer("player1");
        } catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            assertEquals("[Controller] Already existing a player with such nickname", e.getMessage());
        }

        // Add the second player with a correct nickname
        try
        {
            controller.addPlayer("player2");
        } catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        }

        // There aren't enough players to set up a game
        controller.setupGame();
        assertEquals(null, controller.getGameHandler());

        // Add the third player with a correct nickname
        try
        {
            controller.addPlayer("player3");
        } catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        }

        // Set up a game
        controller.setupGame();
        Game game = controller.getGameHandler().getGame();

        // Check the player are created accurately
        assertEquals("player1", game.getPlayerTableList().get(0).getNickname());
        assertEquals(TowerColor.BLACK, game.getPlayerTableList().get(0).getColor());
        assertEquals("player2", game.getPlayerTableList().get(1).getNickname());
        assertEquals(TowerColor.WHITE, game.getPlayerTableList().get(1).getColor());
        assertEquals("player3", game.getPlayerTableList().get(2).getNickname());
        assertEquals(TowerColor.GREY, game.getPlayerTableList().get(2).getColor());
    }

    @Test
    public void sendMessageTest()
    {
        // Send a null message
        NullPointerException e1 = assertThrows(NullPointerException.class,  () -> controller.sendAllMessage(null));
        assertEquals("[Controller] Message is null", e1.getMessage());
        NullPointerException e2 = assertThrows(NullPointerException.class,  () -> controller.sendMessage("player1", null));
        assertEquals("[Controller] Message is null", e2.getMessage());

        // Send a message to a null player
        NullPointerException e3 = assertThrows(NullPointerException.class,  () -> controller.sendMessage(null, "message"));
        assertEquals("[Controller] Player is null", e3.getMessage());

        // Send a message to a player that doesn't exist
        IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class,  () -> controller.sendMessage("player4", "message"));
        assertEquals( "[Controller] It doesn't exist a player with such nickname", e4.getMessage());

        // Add two players with a correct nickname
        try
        {
            controller.addPlayer("player1");
            controller.addPlayer("player2");
        } catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        }
        // Send a correct message
        controller.sendMessage("player2", "message");
    }

    @Test
    public void endGameTest()
    {
        // Set up a game
        try
        {
            controller.addPlayer("player1");
            controller.addPlayer("player2");
            controller.addPlayer("player3");
        } catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        }
        controller.setupGame();

        // A player has built all the towers
        Player winner = controller.getGameHandler().getGame().getSelectedPlayer().get();
        assertEquals("player1", winner.getNickname());
        for (int i = 0; i < 6; i++)
            winner.getBoard().removeTower(winner.getColor());
        assertThrows(EndGameException.class, () -> winner.getBoard().removeTower(winner.getColor()));



    }

    @Test
    public void performActionTest()
    {

    }
}

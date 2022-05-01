package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NoLegitActionException;
import it.polimi.ingsw.model.game.CharacterCard;
import it.polimi.ingsw.model.game.Monk;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.protocol.messages.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        server.createMatch("Game1", 3, GameMode.EXPERT);
        match = server.getMatchById("Game1");
        controller = new Controller(match, 3, GameMode.EXPERT);
    }

    @Test
    public void constructorTest()
    {
        // Check the game mode and the number of players
        assertEquals(GameMode.EXPERT, controller.getGameMode());
        assertEquals(3, controller.getPlayersNumber());

        // Create a controller with a null server
        NullPointerException e1 = assertThrows(NullPointerException.class,
                () -> new Controller(null, 2, GameMode.EXPERT));
        assertEquals("[Controller] The server is null", e1.getMessage());

        // Create a controller with a null mode
        NullPointerException e2 =
                assertThrows(NullPointerException.class, () -> new Controller(match, 2, null));
        assertEquals("[Controller] Game mode is null", e2.getMessage());

        // Create a controller with an invalid player number
        IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class,
                () -> new Controller(match, 1, GameMode.EXPERT));
        assertEquals("[Controller] Invalid players number", e3.getMessage());
        IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class,
                () -> new Controller(match, 4, GameMode.EXPERT));
        assertEquals("[Controller] Invalid players number", e4.getMessage());
    }

    @Test
    public void addPlayerTest()
    {
        // Add a player with a null nickname
        NullPointerException e1 =
                assertThrows(NullPointerException.class, () -> controller.addPlayer(null));
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
            assertEquals("[Controller] Already existing a player with such nickname",
                    e.getMessage());
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
    public void sendErrorTest()
    {
        // Send a null message
        NullPointerException e1 =
                assertThrows(NullPointerException.class, () -> controller.sendError(null, null));
        assertEquals("[Controller] Message is null", e1.getMessage());
        NullPointerException e2 = assertThrows(NullPointerException.class,
                () -> controller.sendError("player1", null));
        assertEquals("[Controller] Message is null", e2.getMessage());

        // Send a message to a null player
        NullPointerException e3 = assertThrows(NullPointerException.class,
                () -> controller.sendError(null, "message"));
        assertEquals("[Controller] Player is null", e3.getMessage());

        // Send a message to a player that doesn't exist
        IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class,
                () -> controller.sendError("player4", "message"));
        assertEquals("[Controller] It doesn't exist a player with such nickname", e4.getMessage());

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
        controller.sendError("player2", "message");
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
        assertThrows(EndGameException.class,
                () -> winner.getBoard().removeTower(winner.getColor()));



    }

    @Test
    public void performActionTest()
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
        GameActionHandler handler = controller.getGameHandler();
        Game game = handler.getGame();

        // player1 selects an assistant card
        assertDoesNotThrow(() -> controller.performAction(
                new PlayAssistantCardMessage(1), "player1"));
        assertEquals(1, game.getPlayerTableList().get(0).getSelectedCard().get().getTurnOrder());

        // player3 selects an assistant card, so a WrongPlayerException is caught by the controller
        // because player2 should play
        assertDoesNotThrow(() -> controller.performAction(
                new PlayAssistantCardMessage(2), "player3"));
        assertTrue(game.getPlayerTableList().get(2).getSelectedCard().isEmpty());

        // player2 moves mother nature, so a NoLegitActionException is caught by the controller
        // because player2 should play an assistant card
        assertDoesNotThrow(() -> controller.performAction(
                new MoveMotherNatureMessage(2), "player2"));

        // player2 selects an assistant card
        assertDoesNotThrow(() -> controller.performAction(
                new PlayAssistantCardMessage(2), "player2"));
        assertEquals(2, game.getPlayerTableList().get(1).getSelectedCard().get().getTurnOrder());

        // player3 selects an assistant card
        assertDoesNotThrow(() -> controller.performAction(
                new PlayAssistantCardMessage(3), "player3"));
        assertEquals(3, game.getPlayerTableList().get(2).getSelectedCard().get().getTurnOrder());

        // player1 moves a student to an island with a wrong index,
        // so an IslandIndexOutOfBounds is caught by the controller
        SchoolColor color1 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> controller.performAction(
                new MoveStudentFromEntranceToIslandMessage(color1, 13), "player1"));
        assertEquals(9, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());

        for (CharacterCard card : game.getCharacterCards())
        {
            if (card instanceof Monk)
            {
                // player3 activate the monk card but hasn't enough coins,
                // so a NotEnoughCoins is caught by the player
                assertDoesNotThrow(() -> controller.performAction(
                        new PlayCharacterCardMessage(game.getCharacterCards().indexOf(card)), "player1"));
                assertFalse(card.isActivated());

                game.getSelectedPlayer().get().getBoard().addCoins(2);
                // player3 activate the monk card
                assertDoesNotThrow(() -> controller.performAction(
                        new PlayCharacterCardMessage(game.getCharacterCards().indexOf(card)), "player1"));
                assertTrue(card.isActivated());

                // Search a student color not present on the card
                int counter;
                SchoolColor color2 = null;
                for (SchoolColor color : SchoolColor.values()) {
                    counter = 0;
                    for (Student student : ((Monk) card).getStudents()) {
                        if (student.getColor() != color)
                            counter++;
                    }
                    if (counter == ((Monk) card).getStudents().size()) {
                        color2 = color;
                        break;
                    }
                }

                //player3 applies the action of the monk
                List colors = new ArrayList<SchoolColor>();
                colors.add(color2);
                assertDoesNotThrow(() -> controller.performAction(
                        new CharacterCardActionMessage(ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND,
                                5, colors),"player1"));

            }
        }
    }
}

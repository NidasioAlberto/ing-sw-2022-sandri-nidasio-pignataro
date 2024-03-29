package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.fsm.MoveMotherNaturePhase;
import it.polimi.ingsw.controller.fsm.MoveStudentPhase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.controller.fsm.SelectCloudTilePhase;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
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

        // Add the fourth player, an exception is thrown
        assertThrows(TooManyPlayersException.class, () -> controller.addPlayer("player4"));

        // Set up a game
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
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> controller.sendError(null, null));
        assertEquals("[Controller] Message is null", e1.getMessage());
        NullPointerException e2 = assertThrows(NullPointerException.class, () -> controller.sendError("player1", null));
        assertEquals("[Controller] Message is null", e2.getMessage());

        // Send a message to a null player
        NullPointerException e3 = assertThrows(NullPointerException.class, () -> controller.sendError(null, "message"));
        assertEquals("[Controller] Player is null", e3.getMessage());

        // Send a message to a player that doesn't exist
        IllegalArgumentException e4 = assertThrows(IllegalArgumentException.class, () -> controller.sendError("player4", "message"));
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
        Player player1 = controller.getGameHandler().getGame().getPlayerTableList().get(0);
        Player player2 = controller.getGameHandler().getGame().getPlayerTableList().get(1);
        Player player3 = controller.getGameHandler().getGame().getPlayerTableList().get(2);

        // Only one player is active so that player wins
        controller.setPlayerActive("player1", true);
        controller.setPlayerActive("player2", false);
        controller.setPlayerActive("player3", false);
        controller.endGame();

        controller.setPlayerActive("player2", true);
        controller.setPlayerActive("player3", true);

        assertDoesNotThrow(() -> controller.removePlayer("player1"));

        // player1 has built all the towers
        for (int i = 0; i < 5; i++)
            player1.getBoard().removeTower(player1.getColor());
        assertThrows(EndGameException.class, () -> player1.getBoard().removeTower(player1.getColor()));
        // Check the endGame method, player1 is the winner because has built all the towers
        controller.endGame();

        // Add a tower to player1
        player1.getBoard().addTower(new Tower(TowerColor.BLACK));
        assertEquals(1, player1.getBoard().getTowers().size());

        // player1 has finished the assistant cards
        for (int turnOrder = 1; turnOrder <= 10; turnOrder++)
            player1.selectCard(turnOrder);

        // Check the endGame method, the winner is player1 because has built the most
        // towers.
        controller.endGame();

        // Add a card to player1
        player1.removeCard(1);
        player1.addCard(new AssistantCard(Wizard.WIZARD_1, 1, 1));

        // player2 has built the same towers number as player1
        for (int i = 0; i < 5; i++)
            player2.getBoard().removeTower(player2.getColor());
        assertEquals(1, player2.getBoard().getTowers().size());

        // player1 gets a professor
        player1.getBoard().addProfessor(new Professor(SchoolColor.GREEN));

        // player1 plays the last card so the game ends
        player1.selectCard(1);
        // Check the endGame method, the winner is player1 because has built the same towers
        // as player2 but has more professors.
        controller.endGame();

        // Add a card to player1
        player1.removeCard(1);
        player1.addCard(new AssistantCard(Wizard.WIZARD_1, 1, 1));

        // player2 gets a professor
        player2.getBoard().addProfessor(new Professor(SchoolColor.BLUE));

        // player1 plays the last card so the game ends
        player1.selectCard(1);

        // Check the endGame method, it is a tie between player1 and player2
        // because both have built the same towers and have the same number of professors.
        controller.endGame();

        // Add a card to player1
        player1.removeCard(1);
        player1.addCard(new AssistantCard(Wizard.WIZARD_1, 1, 1));

        // player3 has built the same towers number as player1 and player2
        for (int i = 0; i < 5; i++)
            player3.getBoard().removeTower(player3.getColor());
        assertEquals(1, player3.getBoard().getTowers().size());

        // player1 plays the last card so the game ends
        player1.selectCard(1);
        // Check the endGame method, it is a tie between player1 and player2
        // because both have built the same towers and have the same number of professors,
        // and player3 has built the same towers but has fewer professors
        controller.endGame();

        // Add a card to player1
        player1.removeCard(1);
        player1.addCard(new AssistantCard(Wizard.WIZARD_1, 1, 1));

        // player3 gets a professor
        player3.getBoard().addProfessor(new Professor(SchoolColor.PINK));

        // player2 has finished the assistant cards
        for (int turnOrder = 1; turnOrder <= 10; turnOrder++)
            player2.selectCard(turnOrder);

        // Check the endGame method, it is a tie between player1, player2 and player3
        // because all have built the same towers and have the same number of professors.
        controller.endGame();
    }

    @Test
    public void performActionTest()
    {
        // An exception is thrown because it doesn't exist with the given nickname
        assertThrows(IllegalArgumentException.class, () -> controller.performAction(null, "player1"));

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
        GameActionHandler handler = controller.getGameHandler();
        Game game = handler.getGame();
        for (Player player : game.getPlayerTableList())
            player.setActive(true);

        // player1 selects an assistant card
        assertDoesNotThrow(() -> controller.performAction(new PlayAssistantCardMessage(1), "player1"));
        assertEquals(1, game.getPlayerTableList().get(0).getSelectedCard().get().getTurnOrder());

        // player3 selects an assistant card, so a WrongPlayerException is caught by the
        // controller
        // because player2 should play
        assertDoesNotThrow(() -> controller.performAction(new PlayAssistantCardMessage(2), "player3"));
        assertTrue(game.getPlayerTableList().get(2).getSelectedCard().isEmpty());

        // player2 moves mother nature, so a NoLegitActionException is caught by the
        // controller
        // because player2 should play an assistant card
        assertDoesNotThrow(() -> controller.performAction(new MoveMotherNatureMessage(2), "player2"));

        // player2 selects the same assistant card as player1 so controller catches InvalidAssistantCardException
        handler.setGamePhase(new PlanPhase());
        assertDoesNotThrow(() -> controller.performAction(new PlayAssistantCardMessage(1), "player2"));

        // player2 selects an assistant card
        assertDoesNotThrow(() -> controller.performAction(new PlayAssistantCardMessage(2), "player2"));
        assertEquals(2, game.getPlayerTableList().get(1).getSelectedCard().get().getTurnOrder());

        // player3 selects an assistant card
        assertDoesNotThrow(() -> controller.performAction(new PlayAssistantCardMessage(3), "player3"));
        assertEquals(3, game.getPlayerTableList().get(2).getSelectedCard().get().getTurnOrder());

        handler.setGamePhase(new MoveStudentPhase());

        // player1 moves a student to an island with a wrong index,
        // so an IslandIndexOutOfBounds is caught by the controller
        SchoolColor color1 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> controller.performAction(new MoveStudentFromEntranceToIslandMessage(color1, 13), "player1"));
        assertEquals(9, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());

        for (CharacterCard card : game.getCharacterCards())
        {
            if (card instanceof Monk)
            {
                // player1 applies the action of the monk, but hasn't activated the
                // card,
                // so a NoSelectedCharacterCardException is caught by the controller
                int studentsOnIsland = game.getIslands().get(5).getStudents().size();
                List<SchoolColor> colors = new ArrayList<>();
                SchoolColor color4 = ((Monk) card).getStudents().get(0).getColor();
                colors.add(color4);
                assertDoesNotThrow(() -> controller.performAction(
                        new CharacterCardActionMessage(ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND, 5, colors), "player1"));
                assertEquals(studentsOnIsland, game.getIslands().get(5).getStudents().size());
                colors.clear();

                // Player1 has 0 coins
                game.getSelectedPlayer().get().getBoard().removeCoins(1);

                // player1 activates the monk card but hasn't enough coins,
                // so a NotEnoughCoins is caught by the controller
                assertDoesNotThrow(() -> controller.performAction(new PlayCharacterCardMessage(game.getCharacterCards().indexOf(card)), "player1"));
                assertFalse(card.isActivated());

                game.getSelectedPlayer().get().getBoard().addCoins(5);
                // player1 activates the monk card
                assertDoesNotThrow(() -> controller.performAction(new PlayCharacterCardMessage(game.getCharacterCards().indexOf(card)), "player1"));
                assertTrue(card.isActivated());

                // player1 activates the monk card again,
                // so a InvalidCharacterCardException is caught by the controller
                assertDoesNotThrow(() -> controller.performAction(new PlayCharacterCardMessage(game.getCharacterCards().indexOf(card)), "player1"));
                assertTrue(card.isActivated());

                // Search a student color not present on the card
                int counter;
                SchoolColor color2 = null;
                for (SchoolColor color : SchoolColor.values())
                {
                    counter = 0;
                    for (Student student : ((Monk) card).getStudents())
                    {
                        if (student.getColor() != color)
                            counter++;
                    }
                    if (counter == ((Monk) card).getStudents().size())
                    {
                        color2 = color;
                        break;
                    }
                }

                // player1 applies the action of the monk, but has selected a
                // student that is not
                // present in the card, so a NoSuchStudentOnCardException is caught
                // by the
                // controller
                colors.add(color2);
                assertDoesNotThrow(() -> controller.performAction(
                        new CharacterCardActionMessage(ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND, 5, colors), "player1"));
                colors.clear();

                // player1 applies the action of the monk with a student present on
                // the card
                SchoolColor color3 = ((Monk) card).getStudents().get(0).getColor();
                colors.add(color3);
                assertDoesNotThrow(() -> controller.performAction(
                        new CharacterCardActionMessage(ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND, 5, colors), "player1"));
                assertEquals(studentsOnIsland + 1, game.getIslands().get(5).getStudents().size());
                colors.clear();
            }
        }

        // Search a student color not present in the entrance of player1
        int counter;
        SchoolColor color2 = null;
        for (SchoolColor color : SchoolColor.values())
        {
            counter = 0;
            for (Student student : game.getSelectedPlayer().get().getBoard().getStudentsInEntrance())
            {
                if (student.getColor() != color)
                    counter++;
            }
            if (counter == game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size())
            {
                color2 = color;
                break;
            }
        }

        // player1 moves a student to the dining room, but doesn't have the selected color
        // in the
        // entrance,
        // so a NoSuchStudentInEntranceException is caught by the controller
        if (color2 != null)
        {
            final SchoolColor color3 = color2;
            assertDoesNotThrow(() -> controller.performAction(new MoveStudentFromEntranceToDiningMessage(color3), "player1"));
            assertEquals(9, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        }

        // player1 moves mother nature with a wrong number of steps,
        // so a InvalidMovementException is caught by the controller
        handler.setGamePhase(new MoveMotherNaturePhase());
        int motherNatureIndex = game.getMotherNatureIndex().get();
        assertDoesNotThrow(() -> controller.performAction(new MoveMotherNatureMessage(motherNatureIndex + 2), "player1"));
        assertEquals(motherNatureIndex, game.getMotherNatureIndex().get());

        game.clearTurn();
        game.getSelectedPlayer().get().clearSelectionsEndTurn();

        handler.setGamePhase(new PlanPhase());

        // player1 selects an assistant card that has already played,
        // so a NoSuchAssistantCardException is caught by the controller
        assertDoesNotThrow(() -> controller.performAction(new PlayAssistantCardMessage(1), "player1"));

        // player1 selects an invalid cloud tile so the controller catches InvalidCloudTileException
        handler.setGamePhase(new SelectCloudTilePhase());
        assertDoesNotThrow(() -> controller.performAction(new SelectCloudTileMessage(5), "player1"));

        // player1 selects a card that doesn't have so the controller catches NoSuchAssistantCardException
        handler.setGamePhase(new PlanPhase());
        assertDoesNotThrow(() -> controller.performAction(new PlayAssistantCardMessage(11), "player1"));

        handler.setGamePhase(new MoveStudentPhase());
        game.selectPlayer(0);
        for (CharacterCard card : game.getCharacterCards())
        {
            if (card instanceof Minstrel)
            {
                game.getSelectedPlayer().get().getBoard().addCoins(5);

                // player1 activates the minstrel card
                assertDoesNotThrow(() -> controller.performAction(new PlayCharacterCardMessage(game.getCharacterCards().indexOf(card)), "player1"));
                assertTrue(card.isActivated());

                // Search a student color not present on the card
                List<SchoolColor> colors = new ArrayList<>();

                // Add a color present in the entrance to the list
                colors.add(game.getPlayerTableList().get(0).getBoard().getStudentsInEntrance().get(0).getColor());

                // Search a color not present in dining
                SchoolColor color3 = null;
                for (SchoolColor color : SchoolColor.values())
                {
                    if (game.getPlayerTableList().get(0).getBoard().getStudentsNumber(color) == 0)
                    {
                        color3 = color;
                        break;
                    }

                }
                colors.add(color3);

                // player1 applies the action of the minstrel, but has selected a
                // student that is not
                // present in the dining, so a NoSuchStudentInDiningException is caught
                // by the
                // controller
                assertDoesNotThrow(() -> controller
                        .performAction(new CharacterCardActionMessage(ExpertGameAction.SWAP_STUDENT_FROM_ENTRANCE_TO_DINING, 5, colors), "player1"));
                colors.clear();
            }
        }

        handler.setGamePhase(new MoveStudentPhase());
        game.selectPlayer(0);
        for (CharacterCard card : game.getCharacterCards())
        {
            if (card instanceof GrandmaHerbs)
            {
                game.getSelectedPlayer().get().getBoard().addCoins(20);
                game.getSelectedPlayer().get().selectIsland(0);

                // Place all the noEntryTiles
                for (int i = 0; i < 4; i++)
                {
                    card.activate();
                    card.applyAction();
                }

                // player1 activates the grandma card
                game.setCurrentCharacterCard(game.getCharacterCards().indexOf(card));
                card.activate();

                // player1 applies the action of the grandma, but the noEntryTiles are finished
                // so a NoMoreNoEntryTilesException is caught by the controller
                assertDoesNotThrow(() -> controller.performAction(
                        new CharacterCardActionMessage(ExpertGameAction.MOVE_NO_ENTRY_FROM_CHARACTER_CARD_TO_ISLAND, 5, null), "player1"));
            }
        }

        // Move some stuff so that when the influence is calculated the selected player places a tower
        game.moveMotherNature(game.getIslands().size() - game.getMotherNatureIndex().get());
        handler.setGamePhase(new MoveMotherNaturePhase());
        Player selectedPlayer = game.getSelectedPlayer().get();
        selectedPlayer.getBoard().addStudentToDiningRoom(new Student(SchoolColor.BLUE));
        selectedPlayer.getBoard().addProfessor(new Professor(SchoolColor.BLUE));

        // Remove all the towers from the selected player except 1
        game.getIslands().get(1).addStudent(new Student(SchoolColor.BLUE));
        for (int i = 0; i < 5; i++)
            selectedPlayer.getBoard().removeTower(selectedPlayer.getColor());

        // When the computeInfluence is called the last tower of the selected player is removed
        // and GameEndException is caught by the controller
        assertDoesNotThrow(() -> controller.performAction(new MoveMotherNatureMessage(1), selectedPlayer.getNickname()));
    }
}

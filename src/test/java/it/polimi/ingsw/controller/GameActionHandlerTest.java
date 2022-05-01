package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.fsm.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NoLegitActionException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.model.game.CharacterCard;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Monk;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.protocol.messages.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the GameActionHandler class
 */
public class GameActionHandlerTest
{
    // Set up of a 3 players game in expert mode
    Controller controller;
    GameActionHandler handler;
    Match match;
    Game game;

    @BeforeEach
    public void init()
    {
        Server server = new Server();
        server.createMatch("Game1", 3, GameMode.EXPERT);
        match = server.getMatchById("Game1");
        controller = new Controller(match, 3, GameMode.EXPERT);
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
        handler = controller.getGameHandler();
        game = handler.getGame();
    }

    @Test
    public void ConstructorTest()
    {
        // Create a Handler with a null game
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new GameActionHandler(null));
        assertEquals("[GameActionHandler] Null game pointer", e1.getMessage());

        // At the beginning the first player in the table is selected
        assertEquals("player1", game.getSelectedPlayer().get().getNickname());

        // At the beginning the FSM is in the PlanPhase
        assertTrue(handler.getGamePhase() instanceof PlanPhase);
    }

    @Test
    public void handleActionTest()
    {
        // Handle an action with a null action message
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> handler.handleAction(null, "player1"));
        assertEquals("[GameActionHandler] Null action message", e1.getMessage());

        // The action isn't legit because player1 should play and not player2
        assertThrows(NoLegitActionException.class, () -> handler.handleAction(
                new PlayAssistantCardMessage(1),"player2"));

        // The action isn't legit because the action should be an assistant card selection
        assertThrows(NoLegitActionException.class, () -> handler.handleAction(
                new PlayCharacterCardMessage(1), "player1"));

        // Now it is correct
       assertDoesNotThrow(() -> handler.handleAction(
               new PlayAssistantCardMessage(1), "player1"));
    }

    @Test
    public void simulationGameTest()
    {
        // player1 selects an assistant card
        assertDoesNotThrow(() -> handler.handleAction(
                new PlayAssistantCardMessage(1), "player1"));
        assertEquals(1, game.getPlayerTableList().get(0).getSelectedCard().get().getTurnOrder());

        // We are still in the PlanPhase because not all the players have played their assistant card
        assertTrue(handler.getGamePhase() instanceof PlanPhase);

        //TODO controllare che non giochi una carta già giocata

        // player2 selects an assistant card
        assertDoesNotThrow(() -> handler.handleAction(
                new PlayAssistantCardMessage(10), "player2"));
        assertEquals(10, game.getPlayerTableList().get(1).getSelectedCard().get().getTurnOrder());

        // We are still in the PlanPhase because not all the players have played their assistant card
        assertTrue(handler.getGamePhase() instanceof PlanPhase);

        // player3 selects an assistant card
        assertDoesNotThrow(() -> handler.handleAction(
                new PlayAssistantCardMessage(5), "player3"));
        assertEquals(5, game.getPlayerTableList().get(2).getSelectedCard().get().getTurnOrder());

        // We are now in the MoveStudentPhase because all the players have played their assistant card
        assertTrue(handler.getGamePhase() instanceof MoveStudentPhase);

        // player1 performs four moves from the entrance room
        // player1 moves a student to the dining room
        SchoolColor color1 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToDiningMessage(color1), "player1"));
        assertEquals(8, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        assertEquals(1, game.getSelectedPlayer().get().getBoard().getStudentsNumber(color1));

        // We are still in the MoveStudentPhase because player1 hasn't performed 4 moves yet
        assertTrue(handler.getGamePhase() instanceof MoveStudentPhase);

        // player1 moves a student to an island
        SchoolColor color2 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToIslandMessage(color2, 0), "player1"));
        assertEquals(7, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (game.getIslands().get(0).getStudents().size() == 1)
            assertEquals(color2, game.getIslands().get(0).getStudents().get(0).getColor());
        else assertEquals(color2, game.getIslands().get(0).getStudents().get(1).getColor());

        // We are still in the MoveStudentPhase because player1 hasn't performed 4 moves yet
        assertTrue(handler.getGamePhase() instanceof MoveStudentPhase);

        // player1 moves a student to an island
        SchoolColor color3 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToIslandMessage(color3, 1), "player1"));
        assertEquals(6, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (game.getIslands().get(1).getStudents().size() == 1)
            assertEquals(color3, game.getIslands().get(1).getStudents().get(0).getColor());
        else assertEquals(color3, game.getIslands().get(1).getStudents().get(1).getColor());

        // We are still in the MoveStudentPhase because player1 hasn't performed 4 moves yet
        assertTrue(handler.getGamePhase() instanceof MoveStudentPhase);

        // player1 moves a student to an island
        SchoolColor color4 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToIslandMessage(color4, 2), "player1"));
        assertEquals(5, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (game.getIslands().get(2).getStudents().size() == 1)
            assertEquals(color4, game.getIslands().get(2).getStudents().get(0).getColor());
        else assertEquals(color4, game.getIslands().get(2).getStudents().get(1).getColor());

        // We now move to MoveMotherNaturePhase because player1 has performed 4 moves
        assertTrue(handler.getGamePhase() instanceof MoveMotherNaturePhase);

        // player1 moves mother nature
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveMotherNatureMessage((game.getMotherNatureIndex().get() + game.getSelectedPlayer().get().
                        getSelectedCard().get().getSteps()) % game.getIslands().size()), "player1"));

        // We now move to SelectCloudTilePhase
        assertTrue(handler.getGamePhase() instanceof SelectCloudTilePhase);

        // player1 selects a cloud tile
        assertDoesNotThrow(() -> handler.handleAction(
                new SelectCloudTileMessage(0),"player1"));
        assertEquals(9, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        assertEquals(0, game.getCloudTiles().get(0).getStudents().size());

        // We now move to EndTurnPhase
        assertTrue(handler.getGamePhase() instanceof EndTurnPhase);
        assertDoesNotThrow(() -> handler.handleAction(new EndTurnMessage(), "player1"));

        // We now move to MoveStudentPhase of the next player, which is player3
        assertTrue(handler.getGamePhase() instanceof MoveStudentPhase);
        assertEquals("player3", game.getSelectedPlayer().get().getNickname());

        // player3 performs four moves from the entrance room
        // player3 moves a student to the dining room
        SchoolColor color5 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToDiningMessage(color5), "player3"));
        assertEquals(8, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        assertEquals(1, game.getSelectedPlayer().get().getBoard().getStudentsNumber(color5));

        // player3 moves a student to the dining room
        SchoolColor color6 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToDiningMessage(color6), "player3"));
        assertEquals(7, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (color5 == color6)
            assertEquals(2, game.getSelectedPlayer().get().getBoard().getStudentsNumber(color6));
        else assertEquals(1, game.getSelectedPlayer().get().getBoard().getStudentsNumber(color6));

        // player 3 moves a student to an island
        SchoolColor color7 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToIslandMessage(color7, 3), "player3"));
        assertEquals(6, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (game.getIslands().get(3).getStudents().size() == 1)
            assertEquals(color7, game.getIslands().get(3).getStudents().get(0).getColor());
        else assertEquals(color7, game.getIslands().get(3).getStudents().get(1).getColor());

        // player 3 moves a student to an island
        SchoolColor color8 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToIslandMessage(color8, 4), "player3"));
        assertEquals(5, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (game.getIslands().get(4).getStudents().size() == 1)
            assertEquals(color8, game.getIslands().get(4).getStudents().get(0).getColor());
        else assertEquals(color8, game.getIslands().get(4).getStudents().get(1).getColor());
        // TODO giocare una carta carattere, generare degli errori
        // TODO manca da aggiungere le monete quando si mettono tot student in dining in modalità expert

        for (CharacterCard card : game.getCharacterCards())
        {
            if (card instanceof Monk)
            {
                game.getSelectedPlayer().get().getBoard().addCoins(3);
                // player3 activate the monk card
                assertDoesNotThrow(() -> handler.handleAction(
                        new PlayCharacterCardMessage(game.getCharacterCards().indexOf(card)), "player3"));
                assertEquals(card, game.getCurrentCharacterCard().get());
                assertTrue(card.isActivated());

                // player3 applies the action of the monk
                SchoolColor color9 = ((Monk) card).getStudents().get(0).getColor();
                List colors = new ArrayList<SchoolColor>();
                colors.add(color9);
                assertDoesNotThrow(() -> handler.handleAction(
                        new CharacterCardActionMessage(ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND, 5, colors),
                        "player3"));
                if (game.getIslands().get(5).getStudents().size() == 1)
                    assertEquals(color9, game.getIslands().get(5).getStudents().get(0).getColor());
                else assertEquals(color9, game.getIslands().get(5).getStudents().get(1).getColor());

            }
        }
        // player3 moves mother nature
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveMotherNatureMessage((game.getMotherNatureIndex().get() + game.getSelectedPlayer().get().
                        getSelectedCard().get().getSteps()) % game.getIslands().size()), "player3"));

        // player3 selects a cloud tile
        assertDoesNotThrow(() -> handler.handleAction(
                new SelectCloudTileMessage(1),"player3"));
        assertEquals(9, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        assertEquals(0, game.getCloudTiles().get(1).getStudents().size());

        // We now move to EndTurnPhase
        assertTrue(handler.getGamePhase() instanceof EndTurnPhase);
        assertDoesNotThrow(() -> handler.handleAction(new EndTurnMessage(), "player3"));

        // We now move to MoveStudentPhase of the next player, which is player2
        assertTrue(handler.getGamePhase() instanceof MoveStudentPhase);
        assertEquals("player2", game.getSelectedPlayer().get().getNickname());

        // player2 performs four moves from the entrance room
        // player2 moves a student to the dining room
        SchoolColor color10= game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToDiningMessage(color10), "player2"));
        assertEquals(8, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        assertEquals(1, game.getSelectedPlayer().get().getBoard().getStudentsNumber(color10));

        // player2 moves a student to the dining room
        SchoolColor color11 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToDiningMessage(color11), "player2"));
        assertEquals(7, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (color10 == color11)
            assertEquals(2, game.getSelectedPlayer().get().getBoard().getStudentsNumber(color11));
        else assertEquals(1, game.getSelectedPlayer().get().getBoard().getStudentsNumber(color11));

        // player 2 moves a student to an island
        SchoolColor color12 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToIslandMessage(color12, 6), "player2"));
        assertEquals(6, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (game.getIslands().get(6).getStudents().size() == 1)
            assertEquals(color12, game.getIslands().get(6).getStudents().get(0).getColor());
        else assertEquals(color12, game.getIslands().get(6).getStudents().get(1).getColor());

        // player 2 moves a student to an island
        SchoolColor color13 = game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().get(0).getColor();
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveStudentFromEntranceToIslandMessage(color13, 7), "player2"));
        assertEquals(5, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        if (game.getIslands().get(7).getStudents().size() == 1)
            assertEquals(color13, game.getIslands().get(7).getStudents().get(0).getColor());
        else assertEquals(color13, game.getIslands().get(7).getStudents().get(1).getColor());

        // player2 moves mother nature
        assertDoesNotThrow(() -> handler.handleAction(
                new MoveMotherNatureMessage((game.getMotherNatureIndex().get() + game.getSelectedPlayer().get().
                        getSelectedCard().get().getSteps()) % game.getIslands().size()), "player2"));

        // player3 selects a cloud tile
        assertDoesNotThrow(() -> handler.handleAction(
                new SelectCloudTileMessage(2),"player2"));
        assertEquals(9, game.getSelectedPlayer().get().getBoard().getStudentsInEntrance().size());
        assertEquals(0, game.getCloudTiles().get(2).getStudents().size());

        // We now move to EndTurnPhase
        assertTrue(handler.getGamePhase() instanceof EndTurnPhase);
        assertDoesNotThrow(() -> handler.handleAction(new EndTurnMessage(), "player2"));

        // We now move to PlanPhase because all the players have done their turn
        assertTrue(handler.getGamePhase() instanceof PlanPhase);
        // Now player1 has to play an assistant card because has played the card with the lowest turnOrder the previous round
        assertEquals("player1", game.getSelectedPlayer().get().getNickname());
        assertEquals(4, game.getCloudTiles().get(2).getStudents().size());
    }
}

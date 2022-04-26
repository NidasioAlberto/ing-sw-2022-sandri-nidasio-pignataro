package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.fsm.MoveStudentPhase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.controller.messages.PlayAssistantCardMessage;
import it.polimi.ingsw.controller.messages.PlayCharacterCardMessage;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.exceptions.NoLegitActionException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.network.Server;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertThrows(NoLegitActionException.class, () -> handler.handleAction(ActionMessage.buildActionMessage(
                new JSONObject().put("actionId", PlayAssistantCardMessage.class.getName()).put("selectedCard", 1)),
                "player2"));

        // The action isn't legit because the action should be
        assertThrows(NoLegitActionException.class, () -> handler.handleAction(ActionMessage.buildActionMessage(
                        new JSONObject().put("actionId", PlayCharacterCardMessage.class.getName()).put("selectedCharacterCard", 1)),
                "player1"));

        // Now it is correct
       assertDoesNotThrow(() -> handler.handleAction(ActionMessage.buildActionMessage(
                       new JSONObject().put("actionId", PlayAssistantCardMessage.class.getName()).put("selectedCard", 1)),
               "player1"));
    }

    @Test
    public void simulationGameTest()
    {
        // player1 selects an assistant card
        assertDoesNotThrow(() -> handler.handleAction(ActionMessage.buildActionMessage(
                        new JSONObject().put("actionId", PlayAssistantCardMessage.class.getName()).put("selectedCard", 1)),
                "player1"));
        assertEquals(1, game.getPlayerTableList().get(0).getSelectedCard().get().getTurnOrder());

        // We are still in the PlanPhase because not all the players have played their assistant card
        assertTrue(handler.getGamePhase() instanceof PlanPhase);

        //TODO controllare che non giochi una carta già giocata

        // player2 selects an assistant card
        assertDoesNotThrow(() -> handler.handleAction(ActionMessage.buildActionMessage(
                        new JSONObject().put("actionId", PlayAssistantCardMessage.class.getName()).put("selectedCard", 10)),
                "player2"));
        assertEquals(10, game.getPlayerTableList().get(1).getSelectedCard().get().getTurnOrder());

        // We are still in the PlanPhase because not all the players have played their assistant card
        assertTrue(handler.getGamePhase() instanceof PlanPhase);

        // player3 selects an assistant card
        assertDoesNotThrow(() -> handler.handleAction(ActionMessage.buildActionMessage(
                        new JSONObject().put("actionId", PlayAssistantCardMessage.class.getName()).put("selectedCard", 5)),
                "player3"));
        assertEquals(5, game.getPlayerTableList().get(2).getSelectedCard().get().getTurnOrder());

        // We are now in the MoveStudentPhase because all the players have played their assistant card
        assertTrue(handler.getGamePhase() instanceof MoveStudentPhase);
    }
}

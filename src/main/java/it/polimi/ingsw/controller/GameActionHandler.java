package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.fsm.Phase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.model.game.Game;

/**
 * This class represents the collection of methods and game finite state machine
 * that allow the actual controller in MVC pattern to execute methods and
 * requests by the player and verify that these requests are actually correct.
 * In this class, all possible user actions are translated and verified
 */
public class GameActionHandler
{
    /**
     * The model instance that needs to be controlled
     */
    private Game game;

    /**
     * The FSM that checks whether the action is legit.
     */
    private Phase gamePhase;

    /**
     * Constructor
     * @param game The game instance to be controlled
     * @throws NullPointerException When the parameter is null
     */
    public GameActionHandler(Game game)
    {
        if(game == null)
            throw new NullPointerException("[GameActionHandler] Null game pointer");

        this.game = game;

        //Instantiate the FSM
        gamePhase = new PlanPhase();
    }

    /**
     * This method is called by the Controller object to handle an action
     * message incoming from a player
     * @param message The command pattern message that needs to be executed
     * @throws NullPointerException When the passed message is null
     */
    public void handleAction(ActionMessage message)
    {
        if(message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        // Check if the fsm situation validates the action

        message.applyAction(this);
    }

    public void playAssistantCard()
    {

    }

    public void moveStudentFromEntranceToIsland()
    {

    }

    public void moveMotherNature()
    {

    }

    public void selectCloudTile()
    {

    }

    public void playCharacterCard()
    {

    }

    public void characterCardAction()
    {

    }

    public void endTurn()
    {

    }

    /**
     * Getters and setters
     */
    public Phase getGamePhase() { return gamePhase; }
    public void setGamePhase(Phase phase)
    {
        if(phase == null)
            throw new NullPointerException("[GameActionHandler] Null phase");
        this.gamePhase = phase;
    }
}

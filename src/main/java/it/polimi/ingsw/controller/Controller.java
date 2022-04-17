package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.network.Match;

/**
 * This class allows creating, starting and ending a game.
 * The controller links the model package with the network one
 * in order to organize the match phases and actions.
 */
public class Controller
{
    /**
     * The game instance that needs to be controlled
     */
    private Game game;

    /**
     * The server of the game
     */
    private Match match;

    /**
     * The actionHandler to execute actions
     */
    private GameActionHandler actionHandler;

    /**
     * Constructor
     * @param match The match server
     * @param playersNumber The number of players of this game
     * @param mode The modality of the game
     */
    public Controller(Match match, int playersNumber, GameMode mode)
    {
        this.match = match;

        game = new Game(playersNumber, mode);

        actionHandler = new GameActionHandler(game);
    }

    private void endGame()
    {

    }

    public void setupGame()
    {

    }

    public void performAction(ActionMessage message)
    {

    }

    public void sendAllMessage(String message)
    {

    }

    public void sendMessage(String player, String message)
    {

    }

    public void addPlayer(String nickname)
    {

    }

    public GameActionHandler getGameHandler()
    {
        return actionHandler;
    }

    public GameMode getGameMode()
    {
        return game.getGameMode();
    }

    public int getPlayersNumber()
    {
        return game.getPlayersNumber();
    }
}

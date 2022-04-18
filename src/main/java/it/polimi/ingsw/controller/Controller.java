package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TowerColor;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
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
     * The game instance that needs to be controlled.
     */
    private Game game;

    /**
     * The server of the game.
     */
    private Match match;

    /**
     * The actionHandler to execute actions.
     */
    private GameActionHandler actionHandler;

    /**
     * Constructor.
     * @param match The match server.
     * @param playersNumber The number of players of this game.
     * @param mode The modality of the game.
     * @throws NullPointerException If the server or game mode are null.
     * @throws IllegalArgumentException If the players number is invalid.
     */
    public Controller(Match match, int playersNumber, GameMode mode)
            throws NullPointerException, IllegalArgumentException
    {
        if (match == null)
            throw new NullPointerException("[Controller] The server is null");

        this.match = match;

        if (mode == null)
            throw new NullPointerException("[Controller] Game mode is null");

        //TODO se non facciamo la modalità a 4 giocatori bisogna cambiare questo e altro
        if (playersNumber < 2 || playersNumber > 4)
            throw new IllegalArgumentException("[Controller] Invalid players number");

        game = new Game(playersNumber, mode);

        actionHandler = new GameActionHandler(game);
    }

    private void endGame()
    {

    }

    /**
     * Sets up the game.
     */
    public void setupGame()
    {
        game.setupGame();
    }

    public void performAction(ActionMessage message)
    {

    }

    /**
     * Sends a message to all the players.
     * @param message The message to send.
     * @throws NullPointerException If the message is null.
     */
    public void sendAllMessage(String message) throws NullPointerException
    {
        if (message == null)
            throw new NullPointerException("[Controller] Message is null");

        match.sendAllMessage(message);
    }

    /**
     * Sends a message to a specified player.
     * @param player The message receiver.
     * @param message The message to send.
     * @throws NullPointerException If the player or message are null.
     * @throws IllegalArgumentException If the player doesn't exist.
     */
    public void sendMessage(String player, String message) throws NullPointerException, IllegalArgumentException
    {
        if (message == null)
            throw new NullPointerException("[Controller] Message is null");

        if (player == null)
            throw new NullPointerException("[Controller] Player is null");

        // Check if there is a player with such nickname
        for (Player existingPlayer: game.getPlayerTableList())
        {
            // If the player exists, the message is sent
            if (existingPlayer.getNickname().equals(player)){
                match.sendMessage(player, message);
                return;
            }
        }

        throw new IllegalArgumentException("[Controller] It doesn't exist a player with such nickname");
    }

    /**
     * Adds a player to the game.
     * @param nickname The player's nickname.
     * @throws NullPointerException If the nickname is null.
     * @throws IllegalArgumentException If already exists a player with such nickname.
     * @throws TooManyPlayersException If there are too many players.
     */
    public void addPlayer(String nickname)
            throws NullPointerException, IllegalArgumentException, TooManyPlayersException
    {
        if (nickname == null)
            throw new NullPointerException("[Controller] The nickname is null");

        for (Player player: game.getPlayerTableList())
        {
            if (player.getNickname().equals(nickname))
                throw new IllegalArgumentException("[Controller] Already existing a player with such nickname");
        }

        //TODO il server dovrebbe controllare che non venga lanciata la TooManyPLayersException

        // Add the player to the game with the correct color of the towers
        switch (game.getPlayersNumber())
        {
            case 0:
                game.addPlayer(new Player(nickname, TowerColor.BLACK));
            case 1:
                game.addPlayer(new Player(nickname, TowerColor.WHITE));
            case 2:
                game.addPlayer(new Player(nickname, TowerColor.GREY));
            default:
                game.addPlayer(new Player(nickname, TowerColor.BLACK));
        }
    }

    /**
     * Getters
     */
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

package it.polimi.ingsw.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.controller.fsm.EndTurnPhase;
import it.polimi.ingsw.controller.fsm.Phase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.controller.fsm.SuspendedPhase;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.protocol.answers.*;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.protocol.messages.EndTurnMessage;
import it.polimi.ingsw.protocol.updates.ModelUpdate;

/**
 * Class used to manges a match. All players in the match are saved and the game controller used to play the game.
 */
public class Match implements Subscriber<ModelUpdate>
{
    private Server server;

    // Connected players
    private List<PlayerConnection> players;

    // Disconnected players
    private List<String> missingPlayers;

    private Controller gameController;

    private String matchId;

    /**
     * Creates a new Match object.
     * 
     * @param server Server instance which creates the new Match.
     * @param matchId Match identification name.
     * @param playersNumber Number of players in this match.
     * @param mode Game mode for this match.
     */
    public Match(Server server, String matchId, int playersNumber, GameMode mode)
    {
        this.server = server;
        this.matchId = matchId;
        players = new ArrayList<>();
        missingPlayers = new ArrayList<>();
        gameController = new Controller(this, playersNumber, mode);
        gameController.getGame().subscribe(this);
    }

    /**
     * Adds a player to the match. If all the players are connected the game is setup.
     *
     * @throws NullPointerException If the nickname is null.
     * @throws IllegalArgumentException If already exists a player with such nickname.
     * @throws TooManyPlayersException If there are too many players.
     * @return true if the player is added, else false.
     */
    public boolean addPlayer(PlayerConnection player) throws NullPointerException, IllegalArgumentException, TooManyPlayersException
    {
        try
        {
            players.add(player);

            System.out.print("[Match] Current players list: ");
            for (PlayerConnection p : players)
                System.out.print(p.getPlayerName().get() + " ");
            System.out.println();

            if (!missingPlayers.contains(player.getPlayerName().get()))
            {
                gameController.addPlayer(player.getPlayerName().get());
                gameController.setPlayerActive(player.getPlayerName().get(), true);
                gameController.getGame().getPlayerTableList().stream().filter((p) -> p.getNickname().equals(player.getPlayerName().get())).findFirst()
                        .ifPresent((p) -> p.subscribe(this));

                System.out.println("[Match] New player added to the match");
            } else
            {
                missingPlayers.remove(player.getPlayerName().get());
                gameController.setPlayerActive(player.getPlayerName().get(), true);

                // Interrupt the timeout
                Phase currentPhase = gameController.getGameHandler().getGamePhase();
                if (currentPhase instanceof SuspendedPhase)
                    ((SuspendedPhase) currentPhase).getTimeout().cancel(true);

                player.sendAnswer(new JoinedMatchAnswer(matchId));

                Map<String, Integer> players = new HashMap<>();
                for (Player gamePlayer : gameController.getGame().getPlayerTableList())
                    players.put(gamePlayer.getNickname(), gameController.getGame().getPlayerTableList().indexOf(gamePlayer));

                // Notify the match's players
                player.sendAnswer(new StartMatchAnswer(players));

                // Send the current status of the game to the player
                gameController.getGame().notifyPlayers();
                System.out.println("[Match] Previously disconnected player added to the match " + player.getPlayerName().get());
            }

            return true;

        } catch (Exception e)
        {
            players.remove(player);
            System.out.print(e);
            player.sendAnswer(new ErrorAnswer(e.getMessage()));
            return false;
        }
    }

    /**
     * Remove the player from the match. Method used only to remove a player who quits or disconnects.
     *
     * @param player to remove.
     */
    public void removePlayer(PlayerConnection player)
    {
        if (player == null)
        {
            return;
        }

        // Check if the game is started
        if (getPlayersNumber() == gameController.getPlayersNumber())
        {
            missingPlayers.add(player.getPlayerName().get());
            players.remove(player);
            gameController.setPlayerActive(player.getPlayerName().get(), false);
            for (PlayerConnection activePlayer : players)
                activePlayer.sendAnswer(new ErrorAnswer(player.getPlayerName().get() + " has just disconnected"));

            Phase currentPhase = gameController.getGameHandler().getGamePhase();

            // If remains only one active player the GameActionHandler moves to SuspendedPhase
            if (players.size() == 1)
            {
                GameActionHandler handler = gameController.getGameHandler();
                players.get(0).sendAnswer(
                        new ErrorAnswer("You are the only active player," + " if no other player reconnects before 1 minute you will win"));
                handler.setGamePhase(new SuspendedPhase(handler.getGamePhase(), gameController));
            }
            // If the disconnected player was the current player, its turn ends
            else if (currentPhase instanceof PlanPhase)
            {
                // Check if the player disconnected was the current one, we are in PlanPhase so the order
                // is based on table order
                if (gameController.getGame().getPlayerTableList().get(gameController.getGame().getSelectedPlayerIndex().get()).getNickname()
                        .equals(player.getPlayerName().get()))
                {
                    // The game is in plan phase so it moves on
                    currentPhase.onValidAction(gameController.getGameHandler());
                }
            } else
            {
                // Check if the player disconnected was the current one, we are not in PlanPhase so the order
                // is based on sorted order
                if (gameController.getGame().getSortedPlayerList().get(gameController.getGame().getSelectedPlayerIndex().get()).getNickname()
                        .equals(player.getPlayerName().get()))
                {
                    // If the game isn't in plan phase, the player's turn ends
                    gameController.getGameHandler().setGamePhase(new EndTurnPhase());
                    gameController.performAction(new EndTurnMessage(), player.getPlayerName().get());
                }
            }
        }
        // The game has not started yet
        else
        {
            try
            {
                players.remove(player);
                gameController.removePlayer(player.getPlayerName().get());
                for (PlayerConnection activePlayer : players)
                    activePlayer.sendAnswer(new ErrorAnswer(player.getPlayerName().get() + " has just disconnected"));
            } catch (NoSelectedPlayerException e)
            {
                System.out.println("[Match] The player to remove wasn't in a match.");
            }
        }
    }

    /**
     * Sends the given answer to every connected player.
     * 
     * @param answer Answer to send.
     */
    public void sendAllAnswer(Answer answer)
    {
        for (PlayerConnection player : players)
            player.sendAnswer(answer);
    }

    /**
     * Sends the given error to the specified player.
     * 
     * @param playerName Target player's name.
     * @param message Error message to send.
     */
    public void sendError(String playerName, String message)
    {
        // Find the player with the given name
        for (PlayerConnection player : players)
            if (player.getPlayerName().isPresent() && player.getPlayerName().get().equals(playerName))
                player.sendAnswer(new ErrorAnswer(message));
    }

    /**
     * Ends the match by removing it from the server.
     * 
     * @param message Message to send to the players.
     */
    public void endMatch(String message)
    {
        server.removeMatch(this, message);
        for (PlayerConnection player : players)
            players.remove(player);
    }

    /**
     * Applies and action by executing it on the game controller.
     * 
     * @param action Action to perform.
     * @param player Player performing the action.
     */
    public void applyAction(ActionMessage action, PlayerConnection player)
    {
        gameController.performAction(action, player.getPlayerName().get());
    }

    /**
     * Returns the total number of players registered in the match.
     */
    public int getPlayersNumber()
    {
        return players.size() + missingPlayers.size();
    }

    public Controller getController()
    {
        return gameController;
    }

    /**
     * Returns the currently connected players.
     */
    public List<PlayerConnection> getPlayers()
    {
        return players;
    }

    public List<String> getMissingPlayers()
    {
        return missingPlayers;
    }

    /**
     * This method is invoked when the model has been changed.
     * 
     * @param update The item that has been changed.
     */
    @Override
    public void onNext(ModelUpdate update)
    {
        if (update.getPlayerDestination().isPresent())
        {
            players.stream().filter((player) -> player.getPlayerName().equals(update.getPlayerDestination())).findFirst()
                    .ifPresent((player) -> player.sendModelUpdate(update));
        } else
        {
            for (PlayerConnection player : players)
                player.sendModelUpdate(update);
        }
    }

    /**
     * This method is called immediately when the subscription is done.
     */
    @Override
    public void onSubscribe(Subscription subscription)
    {}

    /**
     * This method is called by the model when an error occurs.
     */
    @Override
    public void onError(Throwable throwable)
    {}

    /**
     * This method is called when the model knows that nothing will change in the future anymore.
     */
    @Override
    public void onComplete()
    {}
}

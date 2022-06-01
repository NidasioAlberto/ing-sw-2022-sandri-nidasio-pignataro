package it.polimi.ingsw.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import javax.swing.plaf.synth.SynthStyle;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.controller.fsm.EndTurnPhase;
import it.polimi.ingsw.controller.fsm.Phase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.controller.fsm.SuspendedPhase;
import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Wizard;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.protocol.answers.*;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.protocol.messages.EndTurnMessage;
import it.polimi.ingsw.protocol.updates.ModelUpdate;
import it.polimi.ingsw.protocol.updates.PlayedAssistantCardUpdate;

public class Match implements Subscriber<ModelUpdate>
{
    private Server server;

    private List<PlayerConnection> players;

    // Disconnected players
    private List<String> missingPlayers;

    private Controller gameController;

    private String matchId;

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

                sendAllAnswer(new ErrorAnswer(player.getPlayerName().get() + " has just joined"));

                System.out.println("[Match] New player added to the match");
            } else
            {
                missingPlayers.remove(player.getPlayerName().get());
                gameController.setPlayerActive(player.getPlayerName().get(), true);

                // Interrupt the timeout
                Phase currentPhase = gameController.getGameHandler().getGamePhase();
                if (currentPhase instanceof SuspendedPhase)
                    ((SuspendedPhase) currentPhase).getTimeout().cancel(true);

                sendAllAnswer(new ErrorAnswer(player.getPlayerName().get() + " has just reconnected"));

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
            System.out.print(e.toString());
            player.sendAnswer(new ErrorAnswer(e.getMessage()));
            return false;
        }
    }

    /**
     * Remove the player from the match.
     * Method used only to remove a player who quits or disconnects.
     *
     * @param player to remove.
     */
    public void removePlayer(PlayerConnection player)
    {
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
                players.get(0).sendAnswer(new ErrorAnswer("You are the only active player," +
                        " if no other player reconnects before 1 minute you will win"));
                handler.setGamePhase(new SuspendedPhase(handler.getGamePhase(), gameController));
            }
            // If the disconnected player was the current player, its turn ends
            else if (currentPhase instanceof PlanPhase)
            {
                // Check if the player disconnected was the current one, we are in PlanPhase so the order
                // is based on table order
                if (gameController.getGame().getPlayerTableList().get(gameController.getGame().getSelectedPlayerIndex().
                        get()).getNickname().equals(player.getPlayerName().get()))
                {
                    // The game is in plan phase so it moves on
                    currentPhase.onValidAction(gameController.getGameHandler());
                }
            }
            else
            {
                // Check if the player disconnected was the current one, we are not in PlanPhase so the order
                // is based on sorted order
                if (gameController.getGame().getSortedPlayerList().get(gameController.getGame().getSelectedPlayerIndex().
                        get()).getNickname().equals(player.getPlayerName().get()))
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
            }
            catch (NoSelectedPlayerException e)
            {
                System.out.println("[Match] The player to remove wasn't in a match.");
            }
        }
    }

    public void sendAllAnswer(Answer answer)
    {
        for (PlayerConnection player : players)
            player.sendAnswer(answer);
    }

    public void sendError(String playerName, String message)
    {
        System.out.print("[Match] Sending error, players: ");
        for (PlayerConnection p : players)
            System.out.print(p.getPlayerName().get() + " ");
        System.out.println();

        // Find the player with the given name
        for (PlayerConnection player : players)
            if (player.getPlayerName().isPresent() && player.getPlayerName().get().equals(playerName))
                    player.sendAnswer(new ErrorAnswer(message));
    }

    public void endMatch(String message)
    {
        server.removeMatch(this, message);
        for (PlayerConnection player : players)
            players.remove(player);
    }

    public void applyAction(ActionMessage action, PlayerConnection player)
    {
        gameController.performAction(action, player.getPlayerName().get());
    }

    public int getPlayersNumber()
    {
        return players.size() + missingPlayers.size();
    }

    public Controller getController()
    {
        return gameController;
    }

    public List<PlayerConnection> getPlayers()
    {
        return players;
    }

    public List<String> getMissingPlayers()
    {
        return missingPlayers;
    }

    /**
     * This method is invoked when the model has been changed. Observer pattern
     * 
     * @param update The item that has been changed
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
     * 
     * TODO: Maybe useful, if so add it to UML.
     */
    @Override
    public void onSubscribe(Subscription subscription)
    {}

    /**
     * This method is called by the model when an error occurs.
     * 
     * TODO: Maybe useful, if so add it to UML.
     */
    @Override
    public void onError(Throwable throwable)
    {}

    /**
     * This method is called when the model knows that nothing will change in the future anymore.
     * 
     * TODO: Maybe useful, if so add it to UML.
     */
    @Override
    public void onComplete()
    {}
}

package it.polimi.ingsw.network;

import java.util.*;
import java.util.concurrent.Executors;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.protocol.answers.Answer;
import it.polimi.ingsw.protocol.answers.EndMatchAnswer;
import it.polimi.ingsw.protocol.answers.JoinedMatchAnswer;
import it.polimi.ingsw.protocol.answers.MatchesListAnswer;
import it.polimi.ingsw.protocol.messages.ActionMessage;


/**
 * This class is used handle the server connection and all commands coming from the clients.
 */
public class Server
{
    private ServerConnection serverConnection;

    private List<PlayerConnection> lobby;

    private Map<String, Match> matches;

    private Map<PlayerConnection, Match> playersMapMatch;

    /**
     * Creates a new server instance.
     * 
     * Starts the thread that interacts with the console.
     */
    public Server()
    {
        serverConnection = new ServerConnection(this);
        lobby = new ArrayList<>();
        matches = new HashMap<>();
        playersMapMatch = new HashMap<>();

        // Start a thread that controls when to close the server
        Thread quiThread = new Thread(this::waitToQuit);
        quiThread.start();
    }

    /**
     * Creates a new server instance with the given port.
     * 
     * @param port Port to use for the socket.
     */
    public Server(int port)
    {
        this();
        serverConnection = new ServerConnection(this, port);
    }

    /**
     * Whats for the terminal to receive a "quit" command, it then stops the server and exits.
     */
    public void waitToQuit()
    {
        System.out.println("[Server] Type \"quit\" to exit");

        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            if (scanner.nextLine().equals("quit"))
            {
                scanner.close();
                serverConnection.setActive(false);
                System.exit(0);
                break;
            }
        }
    }

    public synchronized ServerConnection getServerConnection()
    {
        return serverConnection;
    }

    public Map<String, Match> getAllMatches()
    {
        return matches;
    }

    public Set<String> getMatchesIds()
    {
        return matches.keySet();
    }

    public Match getMatchById(String id) throws NullPointerException
    {
        return matches.get(id);
    }

    public List<PlayerConnection> getLobby()
    {
        return lobby;
    }

    /**
     * Creates a new match with the given information.
     * 
     * @param matchId Match identification string.
     * @param playersNumber Number of players in the match.
     * @param mode Game mode used for the match.
     * @throws IllegalArgumentException Thrown if the match id is already used for another match.
     */
    public void createMatch(String matchId, int playersNumber, GameMode mode) throws IllegalArgumentException
    {
        // Check if a match with the same id already exists
        if (matches.containsKey(matchId))
            throw new IllegalArgumentException("[Server] A match with id " + matchId + " already exists");

        // Create the match
        matches.put(matchId, new Match(this, matchId, playersNumber, mode));
    }

    /**
     * Creates a new match and adds the player to it
     * 
     * @param matchId New match id.
     * @param playersNumber Number of players for the new match.
     * @param mode Game mode for the new match.
     * @param player Player who created the match and needs to be included.
     * @throws NullPointerException If the player is null.
     * @throws IllegalArgumentException If the player lacks a nickname, if it is participating in another match, if there is already a player with
     *         such nickname of if there is no match with the given id.
     * @throws TooManyPlayersException If there are too many players.
     */
    public void createMatch(String matchId, int playersNumber, GameMode mode, PlayerConnection player)
            throws NullPointerException, IllegalArgumentException, TooManyPlayersException
    {
        createMatch(matchId, playersNumber, mode);

        // Add the player to the match
        addPlayerToMatch(matchId, player);

        // Update all the players in the lobby with the new list of matches
        sendToLobby(new MatchesListAnswer(matches));
    }

    /**
     * Sends EndMatchAnswers to every connected players in the match and removes the match from the server.
     * 
     * @param match Match to remove.
     * @param message Message to send to the players.
     * @throws NullPointerException Thrown if the given match is null.
     */
    public void removeMatch(Match match, String message) throws NullPointerException
    {
        if (match == null)
            throw new NullPointerException("[Server] Attempting to remove a null Match");

        // Notify all the players
        match.sendAllAnswer(new EndMatchAnswer(message));

        // Move all the players to the lobby
        for (PlayerConnection player : match.getPlayers())
            addPlayerToLobby(player);

        // Delete the match
        matches.entrySet().removeIf(entry -> entry.getValue() == match);
        playersMapMatch.entrySet().removeIf(entry -> entry.getValue() == match);
    }

    /**
     * Adds a player to the match identified with the given match id.
     *
     * @throws NullPointerException If the player is null.
     * @throws IllegalArgumentException If the player lacks a nickname, if it is participating in another match, if there is already a player with
     *         such nickname of if there is no match with the given id.
     * @throws TooManyPlayersException If there are too many players.
     */
    public void addPlayerToMatch(String matchId, PlayerConnection player)
            throws NullPointerException, IllegalArgumentException, TooManyPlayersException
    {
        // Check if the player is in the lobby
        if (!lobby.contains(player))
            throw new IllegalArgumentException("[Server] The player must be in the lobby to be included in a game");

        // Check if the player has a name
        if (player.getPlayerName().isEmpty())
            throw new IllegalArgumentException("[Server] The player must have a name to participate in a match");

        // Check if the player isn't in another match
        if (playersMapMatch.containsKey(player))
            throw new IllegalArgumentException("[Server] The player is already participating in a match");

        // Find the match with the given id
        Match match = matches.get(matchId);

        // Check if the match isn't null
        if (match == null)
            throw new IllegalArgumentException("[Server] There is no match with id " + matchId);

        // Add the player to the match
        if (match.addPlayer(player))
        {
            playersMapMatch.put(player, match);

            // Remove the player from the lobby
            lobby.remove(player);

            // Notify the player
            player.sendAnswer(new JoinedMatchAnswer(matchId));
        }
    }

    /**
     * Adds a player to the lobby.
     * 
     * This is used when a new user connects to the server. Users in the lobby receives automatic updates when a new match is creates.
     * 
     * @param player New player connection.
     * @throws NullPointerException Thrown if the given player connection is null.
     */
    public void addPlayerToLobby(PlayerConnection player) throws NullPointerException
    {
        if (player == null)
            throw new NullPointerException("[Server] Attempting to add a null PlayerConnection to the lobby");

        lobby.add(player);
        System.out.println("[Server] Added new player to lobby");
    }

    /**
     * Removes a player form the match he is in.
     * 
     * If the player isn't in any match nothing is done.
     * 
     * @param player Player to remove.
     */
    public void removePlayerFromMatch(PlayerConnection player)
    {
        // If the player is part of a match remove it
        if (playersMapMatch.containsKey(player))
        {
            Match match = playersMapMatch.get(player);
            match.removePlayer(player);
            playersMapMatch.remove(player);

            System.out.println("[Server] Removed player from match " + player.getPlayerName().get());

            // And add him to the lobby
            lobby.add(player);

            if (match.getPlayers().size() == 0)
            {
                removeMatch(match, "The match no longer exists");

                // Update all the players in the lobby with the new list of matches
                sendToLobby(new MatchesListAnswer(matches));
            }
        }
    }

    /**
     * Removes the player form the entire server.
     * 
     * It removes the player first form the match (if he is in one) and then removes him form the lobby.
     * 
     * @param player Player to remove.
     */
    public void removePlayerFromServer(PlayerConnection player)
    {
        removePlayerFromMatch(player);

        // If the player is in the lobby remove it
        lobby.remove(player);
    }

    /**
     * Applies the given action on the match the player is in.
     * 
     * @param action Action to apply.
     * @param player Player performing the action.
     * @throws IllegalArgumentException Thrown if the player isn't in any match.
     */
    public void applyAction(ActionMessage action, PlayerConnection player) throws IllegalArgumentException
    {
        // Check if the player is part of a match
        if (!playersMapMatch.containsKey(player))
            throw new IllegalArgumentException(
                    "[Server] The player is not part of a match, can't perform the action " + action.getBaseGameAction().name());

        // Retrieve the match
        Match match = playersMapMatch.get(player);

        // Perform the action
        match.applyAction(action, player);
    }

    /**
     * Send the given answer to all the players in the lobby.
     * 
     * @param answer Answer to send to all the players in the lobby.
     */
    public void sendToLobby(Answer answer)
    {
        System.out.println("[Server] Sending answer " + answer.getClass().getSimpleName() + " to the whole lobby");

        for (PlayerConnection player : lobby)
            player.sendAnswer(answer);
    }

    public boolean isPlayerInAMatch(PlayerConnection player)
    {
        return playersMapMatch.get(player) != null;
    }

    /**
     * Starts the server by instantiating the server class and running its server connection.
     */
    public static void main(String[] args)
    {
        Server server;

        if (args.length == 1)
            try
            {
                server = new Server(Integer.parseInt(args[0]));
            } catch (NumberFormatException e)
            {
                System.out.println("[Server] The specified port is not a valid number, falling back to the default");
                server = new Server();
            }
        else
            server = new Server();

        Executors.newCachedThreadPool().submit(server.serverConnection);
    }
}

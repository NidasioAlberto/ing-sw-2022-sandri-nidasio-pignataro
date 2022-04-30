package it.polimi.ingsw.network;

import java.util.*;
import java.util.concurrent.Executors;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.model.GameMode;

public class Server
{
    private ServerConnection serverConnection;

    private List<PlayerConnection> lobby;

    private Map<String, Match> matches;

    private Map<PlayerConnection, Match> playersMapMatch;

    public Server()
    {
        serverConnection = new ServerConnection(this);
        lobby = new ArrayList<>();
        matches = new HashMap<>();

        // Start a thread that controls when to close the server
        Thread quiThread = new Thread(this::waitToQuit);
        quiThread.start();
    }

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

    public void createMatch(String id, int playersNumber, GameMode mode)
            throws IllegalArgumentException
    {
        // Check if a match with the same id already exists
        if (matches.containsKey(id))
            throw new IllegalArgumentException(
                    "[Server] A match with id " + id + " already exists");

        // Create the match
        matches.put(id, new Match(playersNumber, mode));
    }

    public void addPlayerToMatch(String id, PlayerConnection player) throws IllegalArgumentException
    {
        // Check if the player has a name
        if (player.getPlayerName().isEmpty())
            throw new IllegalArgumentException(
                    "[Server] The player must have a name to participate in a match");

        // Check if the player isn't in another match
        if (playersMapMatch.containsKey(player))
            throw new IllegalArgumentException(
                    "[Server] The player is already participating in a match");

        // Find the match with the given id
        Match match = matches.get(id);

        // Check if the match isn't null
        if (match == null)
            throw new IllegalArgumentException("[Server] There is no match with id " + id);

        // Add the player to the match
        match.addPlayer(player);
        playersMapMatch.put(player, match);
    }

    public void removePlayer(PlayerConnection player)
    {
        // If the player is part of a match remove it
        if (playersMapMatch.containsKey(player))
        {
            Match match = playersMapMatch.get(player);
            match.removePlayer(player);
            playersMapMatch.remove(player);
        }

        // If the player is in the lobby remove it
        lobby.remove(player);
    }

    public void actionCall(ActionMessage action, PlayerConnection player)
            throws IllegalArgumentException
    {
        // Check if the player is part of a match
        if (!playersMapMatch.containsKey(player))
            throw new IllegalArgumentException(
                    "[Server] The player is not part of a match, can't perform the action "
                            + action.getBaseGameAction().name());

        // Retrieve the match
        Match match = playersMapMatch.get(player);

        // Perform the action
        match.actionCall(action, player);
    }

    public static void main(String[] args)
    {
        Server server = new Server();
        Executors.newCachedThreadPool().submit(server.serverConnection);
    }
}

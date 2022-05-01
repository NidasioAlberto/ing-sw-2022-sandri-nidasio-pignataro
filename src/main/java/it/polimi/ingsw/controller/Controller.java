package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TowerColor;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allows creating, starting and ending a game. The controller links the model package
 * with the network one in order to organize the match phases and actions.
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
     * 
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

        if (mode == null)
            throw new NullPointerException("[Controller] Game mode is null");

        this.match = match;

        // TODO: Se non facciamo la modalità a 4 giocatori bisogna cambiare questo e altro
        if (playersNumber < 2 || playersNumber > 3)
            throw new IllegalArgumentException("[Controller] Invalid players number");

        game = new Game(playersNumber, mode);
    }

    /**
     * The method is called when the game is ended. It determines the winner.
     */
    private void endGame()
    {
        // Check if there is a player that has built all the towers
        for (Player player : game.getPlayerTableList())
        {
            if (player.getBoard().getTowers().size() == 0)
            {
                match.endMatch("The game is ended. The winner is " + player.getNickname());
                return;
            }
        }

        // Check if there is a player who has run out of cards
        boolean runOutOfCards = false;

        for (Player player : game.getPlayerTableList())
        {
            if (player.getCards().size() == 0)
            {
                runOutOfCards = true;
            }
        }

        // Check if there are only 3 groups of islands or there aren't student in the bag
        if (runOutOfCards || game.getIslands().size() <= 3 || game.getStudentBag().size() == 0)
        {
            List<Player> rank = new ArrayList<>(game.getPlayerTableList());

            // The winner is the player who has built the most towers.
            // In case of a tie, the winner is the player who controls the most professors
            rank.stream().sorted(
                    (a, b) -> a.getBoard().getTowers().size() == b.getBoard().getTowers().size()
                            ? a.getBoard().getProfessors().size()
                                    - b.getBoard().getProfessors().size()
                            : a.getBoard().getTowers().size() - b.getBoard().getTowers().size());

            // The first player in the rank has the most tower or has the same tower as the second,
            // but the first has more professors, so the first wins
            if (rank.get(0).getBoard().getTowers().size() > rank.get(1).getBoard().getTowers()
                    .size()
                    || (rank.get(0).getBoard().getTowers().size() == rank.get(1).getBoard()
                            .getTowers().size()
                            && rank.get(0).getBoard().getProfessors().size() > rank.get(1)
                                    .getBoard().getProfessors().size()))
            {
                match.endMatch("The game is ended. The winner is " + rank.get(0).getNickname());
                return;
            }
            // Case of a three players game and a tie between the first two
            else if (rank.size() == 3 && (rank.get(0).getBoard().getTowers().size() > rank.get(2)
                    .getBoard().getTowers().size()
                    || (rank.get(0).getBoard().getTowers().size() == rank.get(2).getBoard()
                            .getTowers().size()
                            && rank.get(0).getBoard().getProfessors().size() > rank.get(2)
                                    .getBoard().getProfessors().size())))
            {
                match.endMatch("The game is ended. It is a tie between " + rank.get(0).getNickname()
                        + " and " + rank.get(1).getNickname());
                return;
            }
            // Otherwise, it is a tie between all the players
            else
            {
                match.endMatch("The game is ended. It is a tie between all the players.");
                return;
            }
        }
    }

    /**
     * Sets up the game and instantiates the GameActionHandler.
     */
    public void setupGame()
    {
        try
        {
            game.setupGame();
            actionHandler = new GameActionHandler(game);
        } catch (NotEnoughPlayersException e)
        {
            // TODO: Review how this should happen
            match.endMatch(e.getMessage());
        }
    }

    /**
     * The method passes the message to the handler. It catches and handles all the possible
     * exceptions that are thrown if something goes wrong.
     * 
     * @param message It represents the player's action.
     */
    public void performAction(ActionMessage message, String playerName)
    {
        try
        {
            actionHandler.handleAction(message, playerName);
        } catch (EndGameException e)
        {
            endGame();
            // se il gioco non è veramente terminato?
            // forse conviene separare in isGameEnded and computeWinner o mi fido che il gioco sia
            // terminato
        } catch (NoLegitActionException e)
        {
            sendError(getCurrentPlayer(), "You can't do this action right now.");
        } catch (WrongPlayerException e)
        {
            sendError(e.getPlayer(), "This is not your turn, you have to wait.");
        } catch (NoSelectedIslandException e)
        {
            sendError(getCurrentPlayer(), "You have to select an island to perform the action.");
        } catch (NoSelectedColorException e)
        {
            sendError(getCurrentPlayer(), "You have to select a color to perform the action.");
        } catch (NoSelectedAssistantCardException e)
        {
            sendError(getCurrentPlayer(),
                    "You have to select an assistant card to perform the action.");
        } catch (NoSelectedStudentsException e)
        {
            sendError(getCurrentPlayer(), "You have to select the students to perform the action.");
        } catch (NoSelectedCloudTileException e)
        {
            sendError(getCurrentPlayer(), "You have to select a cloud tile to perform the action.");
        } catch (NoSelectedCharacterCardException e)
        {
            sendError(getCurrentPlayer(),
                    "You have to select a character card to perform the action.");
        } catch (IslandIndexOutOfBoundsException e)
        {
            sendError(getCurrentPlayer(), "You can't select that island.");
        } catch (NotEnoughCoinsException e)
        {
            sendError(getCurrentPlayer(), "You don't have enough coins to play this card.");
        } catch (NoSuchStudentOnCardException e)
        {
            sendError(getCurrentPlayer(), "You have to select a student present on the card.");
        } catch (NoSuchStudentInEntranceException e)
        {
            sendError(getCurrentPlayer(),
                    "You have to select a student present in your entrance room.");
        } catch (NoSuchStudentInDiningException e)
        {
            sendError(getCurrentPlayer(),
                    "You have to select a student present in your dining room.");
        } catch (NoSuchAssistantCardException e)
        {
            sendError(getCurrentPlayer(), "You don't have the assistant card you selected.");
        } catch (InvalidMovementException e)
        {
            sendError(getCurrentPlayer(), e.getMessage());
        } catch (InvalidCharacterCardException e)
        {
            sendError(getCurrentPlayer(), e.getMessage());
        } catch (NoMoreNoEntryTilesException e)
        {
            sendError(getCurrentPlayer(),
                    "You can't play GrandmaHerbs now, because the No Entry tiles are finished.");
        } catch (Exception e)
        {
            match.endMatch(
                    "Oh no, we are sorry but an internal error occurred, we will fix it as soon as possible");
            // TODO va chiusa la partita, servirebbe un metodo del tipo closeGame in match
        }
    }

    /**
     * Sends a message to a specified player.
     * 
     * @param player The message receiver.
     * @param message The message to send.
     * @throws NullPointerException If the player or message are null.
     * @throws IllegalArgumentException If the player doesn't exist.
     */
    public void sendError(String player, String message)
            throws NullPointerException, IllegalArgumentException
    {
        if (message == null)
            throw new NullPointerException("[Controller] Message is null");

        if (player == null)
            throw new NullPointerException("[Controller] Player is null");

        // Check if there is a player with such nickname
        for (Player existingPlayer : game.getPlayerTableList())
        {
            // If the player exists, the message is sent
            if (existingPlayer.getNickname().equals(player))
            {
                match.sendError(player, message);
                return;
            }
        }

        throw new IllegalArgumentException(
                "[Controller] It doesn't exist a player with such nickname");
    }

    /**
     * Adds a player to the game.
     * 
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

        for (Player player : game.getPlayerTableList())
        {
            if (player.getNickname().equals(nickname))
                throw new IllegalArgumentException(
                        "[Controller] Already existing a player with such nickname");
        }

        // TODO il server dovrebbe controllare che non venga lanciata la TooManyPLayersException

        // Add the player to the game with the correct color of the towers
        switch (game.getPlayerTableList().size())
        {
            case 0:
                game.addPlayer(new Player(nickname, TowerColor.BLACK));
                break;
            case 1:
                game.addPlayer(new Player(nickname, TowerColor.WHITE));
                break;
            case 2:
                game.addPlayer(new Player(nickname, TowerColor.GREY));
                break;
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

    public String getCurrentPlayer()
    {
        return game.getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[Controller]")).getNickname();
    }
}

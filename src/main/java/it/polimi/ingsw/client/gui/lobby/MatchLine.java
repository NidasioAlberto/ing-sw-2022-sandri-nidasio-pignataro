package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.model.GameMode;

/**
 * Class used to display a match in the tableView in CreateMatchScene scene.
 */
public class MatchLine
{
    private String name;
    private GameMode mode;
    private String playersNumber;

    public MatchLine(String name, GameMode mode, String playersNumber)
    {
        this.name = name;
        this.playersNumber = playersNumber;
        this.mode = mode;
    }

    public String getName()
    {
        return name;
    }

    public GameMode getMode()
    {
        return mode;
    }

    public String getPlayersNumber()
    {
        return playersNumber;
    }
}

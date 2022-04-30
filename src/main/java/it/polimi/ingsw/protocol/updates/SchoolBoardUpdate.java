package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.SchoolBoard;

public class SchoolBoardUpdate extends ModelUpdate
{
    /**
     * The modified school board
     */
    private SchoolBoard board;

    /**
     * The player string name to which the board is assigned
     */
    private String player;

    /**
     * Constructor
     * @param board The modified board
     * @param player The player that owns the board
     */
    public SchoolBoardUpdate(SchoolBoard board, String player)
    {
        if(board == null)
            throw new NullPointerException("[SchoolBoardUpdate] Null board");
        if(player == null)
            throw new NullPointerException("[SchoolBoardUpdate] Null player name");

        this.board  = board;
        this.player = player;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }
}

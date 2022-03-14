package it.polimi.ingsw.model;
public enum CloudTileType
{
    TILE_2_4(3), TILE_3(4);

    /**
     * Internal num based on the number of players
     */
    private int playerNum;

    /**
     * Constructor
     * @param num The number of players
     */
    private CloudTileType(int num) { this.playerNum = num; }

    /**
     * Getter
     */
    public int getPlayerNum() { return playerNum; }
}

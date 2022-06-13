package it.polimi.ingsw.model;

/**
 * All cloud tiles share a specific students capacity determined by the player count. When playing with 2 or 4 players the cloud tiles hold 3 students
 * whereas when playing with 3 players they hold 4 students.
 */
public enum CloudTileType
{
    /**
     * Cloud tile type used when 2 or 4 players are in the game. The tile will hold 3 students.
     */
    TILE_2_4_PLAYERS(3),

    /**
     * Cloud tile type used when 3 players are in the game. The tile will hold 4 students.
     */
    TILE_3_PLAYERS(4);

    private int studentCapacity;

    /**
     * Creates a CloudTileType object.
     * 
     * @param capacity The students capacity.
     */
    private CloudTileType(int capacity)
    {
        this.studentCapacity = capacity;
    }

    /**
     * Returns the students capacity of the tile.
     * 
     * @return The students capacity of the tile.
     */
    public int getStudentCapacity()
    {
        return studentCapacity;
    }
}

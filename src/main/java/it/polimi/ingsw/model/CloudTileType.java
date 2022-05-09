package it.polimi.ingsw.model;

/**
 * All cloud tiles share a specific students capacity determined by the player count. When playing
 * with 2 or 4 players the cloud tiles hold 3 students whereas when playing with 3 players they hold
 * 4 students.
 */
public enum CloudTileType
{
    TILE_2(3), TILE_3(4);

    private int studentCapacity;

    private CloudTileType(int capacity)
    {
        this.studentCapacity = capacity;
    }

    public int getStudentCapacity()
    {
        return studentCapacity;
    }
}

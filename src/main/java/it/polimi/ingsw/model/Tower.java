package it.polimi.ingsw.model;

/**
 * This class represents the tower. Each player/team has 6 towers in total
 * and the amount of the "island positioned" ones determines the winner of the match.
 */
public class Tower
{
    /**
     * The tower color (enum)
     */
    private TowerColor color;

    /**
     * Constructor
     * @param color The tower color
     */
    public Tower(TowerColor color) { this.color = color; }

    /**
     * Tower color getter
     * @return The TowerColor
     */
    public TowerColor getColor() { return color; }
}

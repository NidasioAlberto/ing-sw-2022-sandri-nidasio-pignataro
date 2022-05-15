package it.polimi.ingsw.client.gui.objects;

import javafx.scene.paint.Color;

public enum TowerType
{
    BLACK(Color.BLACK),
    WHITE(Color.WHITE),
    GRAY(Color.GRAY);

    /**
     * The tower color
     */
    private Color color;

    /**
     * Constructor
     * @param color The tower color
     */
    private TowerType(Color color) { this.color = color; }

    /**
     * Color getter
     */
    public Color getColor() { return color; }
}

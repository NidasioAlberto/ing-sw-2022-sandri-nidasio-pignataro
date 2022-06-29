package it.polimi.ingsw.client.gui.objects.types;

import it.polimi.ingsw.client.gui.ObjectModelParser;
import javafx.scene.paint.Color;

public enum TowerType
{
    BLACK(Color.rgb(65, 68, 68)), WHITE(Color.WHITE), GREY(Color.rgb(113, 115, 116));

    /**
     * Object file parser
     */
    private ObjectModelParser parser;

    /**
     * The tower color
     */
    private Color color;

    /**
     * Constructor
     * 
     * @param color The tower color
     */
    private TowerType(Color color)
    {
        this.parser = new ObjectModelParser("Models/tower.obj", 1);
        this.color = color;
    }

    /**
     * Getters
     */
    public ObjectModelParser getParser()
    {
        return parser;
    }

    public Color getColor()
    {
        return color;
    }
}

package it.polimi.ingsw.client.gui.objects.types;

import it.polimi.ingsw.client.gui.ObjectModelParser;
import javafx.scene.paint.Color;

/**
 * Enumerates all the students and correlates them with the corresponding texture
 */
public enum StudentType
{
    RED("Models/redStudent.obj", Color.rgb(238, 50, 46)),
    BLUE("Models/blueStudent.obj", Color.rgb(46, 196, 243)),
    GREEN("Models/greenStudent.obj", Color.rgb(35, 180, 128)),
    PINK("Models/pinkStudent.obj", Color.rgb(218, 97, 163)),
    YELLOW("Models/yellowStudent.obj", Color.rgb(252, 180, 24));

    /**
     * Object file parser
     */
    private ObjectModelParser parser;

    /**
     * Texture color
     */
    private Color color;

    /**
     * Constructor
     */
    private StudentType(String filename, Color color)
    {
        this.parser = new ObjectModelParser(filename, 5);
        this.color = color;
    }

    /**
     * Parser getter
     */
    public ObjectModelParser getParser() { return parser; }

    public Color getColor() { return color; }
}

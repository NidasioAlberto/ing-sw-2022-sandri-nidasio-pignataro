package it.polimi.ingsw.client.gui.objects.types;

import it.polimi.ingsw.client.gui.ObjectModelParser;
import javafx.scene.paint.Color;

public enum ProfessorType
{
    RED("Models/redProfessor.obj", Color.rgb(238, 50, 46)), BLUE("Models/blueProfessor.obj", Color.rgb(46, 196, 243)), GREEN(
            "Models/greenProfessor.obj", Color.rgb(35, 180, 128)), PINK("Models/pinkProfessor.obj",
                    Color.rgb(218, 97, 163)), YELLOW("Models/yellowProfessor.obj", Color.rgb(252, 180, 24));

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
    private ProfessorType(String filename, Color color)
    {
        this.parser = new ObjectModelParser(filename, 3);
        this.color = color;
    }

    /**
     * Parser getter
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

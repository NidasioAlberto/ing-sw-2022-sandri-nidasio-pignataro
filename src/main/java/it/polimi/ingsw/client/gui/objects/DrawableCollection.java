package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;

public abstract class DrawableCollection
{
    /**
     * The game point light
     */
    protected PointLight pointLight;

    /**
     * The game ambient light
     */
    protected AmbientLight ambientLight;

    /**
     * The game group
     */
    protected Group group;

    /**
     * The game animation updater
     */
    protected AnimationHandler updater;

    /**
     * The collection position
     */
    protected Point3D position;

    /**
     * Constructor
     */
    protected DrawableCollection(PointLight pointLight, AmbientLight ambientLight, Group group, AnimationHandler updater)
    {
        // Check the validity of all the attributes
        if (pointLight == null)
            throw new NullPointerException("[DrawableCollection] Null point light");
        if (ambientLight == null)
            throw new NullPointerException("[DrawableCollection] Null ambient light");
        if (group == null)
            throw new NullPointerException("[DrawableCollection] Null group");
        if (updater == null)
            throw new NullPointerException("[DrawableCollection] Null animation handler");

        // Assign all the attributes
        this.pointLight = pointLight;
        this.ambientLight = ambientLight;
        this.group = group;
        this.updater = updater;

        // Init the position to 0
        position = new Point3D(0, 0, 0);
    }

    /**
     * Method to clear, unsubscribe all the collection objects
     */
    abstract void clearAll();

    /**
     * Method to add all the collection elements to the group
     */
    abstract void addToGroup();

    /**
     * Method to remove all the collection elements to the group
     */
    abstract void removeFromGroup();

    /**
     * Method to translate the whole collection
     * 
     * @param point The 3D point representing the translation
     */
    abstract void translate(Point3D point);

    /**
     * Position getter
     */
    public Point3D getPosition()
    {
        return position;
    }
}

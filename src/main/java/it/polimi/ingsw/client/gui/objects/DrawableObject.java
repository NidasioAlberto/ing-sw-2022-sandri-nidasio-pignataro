package it.polimi.ingsw.client.gui.objects;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PointLight;

public abstract class DrawableObject
{
    /**
     * Method to register the object to a group
     * @param group Group where to add the object
     */
    public abstract void addToGroup(Group group);

    /**
     * Method to remove the object from a group
     * @param group Group where to remove the object
     */
    public abstract void removeFromGroup(Group group);

    /**
     * Method to translate the object in X, Y and Z axes
     * @param point The 3D point where translate the object
     */
    public abstract void translate(Point3D point);

    /**
     * Method to return the current position of the object
     * @return The point where the object currently is
     */
    public abstract Point3D getPosition();

    /**
     * Method to subscribe to an eventual point light if necessary
     * @param light The light that the object has to subscribe to
     */
    public abstract void subscribeToLight(PointLight light);

    /**
     * Method called to update every object animation if there is ibe
     */
    public abstract void updateAnimation();
}

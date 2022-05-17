package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.PointLight;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class DrawableObject
{
    /**
     * Eventual animation updater to be used
     */
    protected AnimationHandler updater;

    /**
     * Collection of ending positions to go with the animation process.
     * The pair represents the point where the object should go
     * and the step that should do every cycle
     */
    protected List<Pair<Point3D, Double>> positions;

    /**
     * Constructor
     * @param updater The animation updater to which subscribe the object
     */
    protected  DrawableObject(AnimationHandler updater)
    {
        if(updater != null)
            this.updater = updater;

        positions = new ArrayList<>();
    }

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
     * Adds a rotation transformation to the object
     * @param rotation The transformation to be added
     */
    public abstract void addRotation(Rotate rotation);

    /**
     * Method to return the current position of the object
     * @return The point where the object currently is
     */
    public abstract Point3D getPosition();

    /**
     * Method to subscribe to an eventual point light if necessary
     * @param light The light that the object has to subscribe to
     */
    public abstract void subscribeToPointLight(PointLight light);

    /**
     * Method to subscribe to an eventual ambient light if necessary
     * @param light The light that the object has to subscribe to
     */
    public abstract void subscribeToAmbientLight(AmbientLight light);

    /**
     * This method adds a step of animation to the queue
     * @param point The point to reach
     * @param speed The speed needed
     */
    public void addAnimationPosition(Point3D point, double speed)
    {
        if(point == null)
            throw new NullPointerException("[DrawableObject] Null new point position");
        if(speed <= 0)
            throw new IllegalArgumentException("[DrawableObject] Speed less or equal to 0");

        // I add the new position with its speed
        positions.add(new Pair<>(point, speed));
    }

    /**
     * Method called to update every object animation if there is one.
     * If not overrided the update animation makes a step forward to the next
     * position inside the array of positions
     */
    public void updateAnimation()
    {
        // Check if there actually is some position to reach
        if(positions.isEmpty())
            return;

        // Calculate the distance between the actual position of the object
        // and the point to reach. If it is less than the step i just go there
        if(getPosition().distance(positions.get(0).getKey()) < positions.get(0).getValue())
        {
            // Translate there
            translate(positions.get(0).getKey());
            // Delete the position and return
            positions.remove(0);
            return;
        }

        // If i'm still far, i move in that direction
        Point3D flag = positions.get(0).getKey();
        Point3D current = getPosition();
        double D = positions.get(0).getValue();
        double t = D / current.distance(flag); // This is the multiplication factor

        // Calculate one of the new positions on the calculated rect
        Point3D newPosition = new Point3D(current.getX() - flag.getX(), current.getY() - flag.getY(), current.getZ() - flag.getZ());
        newPosition = newPosition.multiply(t).add(current);

        // I need to understand the direction (if -t or t due to second grade equation)
        if(newPosition.distance(flag) < current.distance(flag))
        {
            translate(newPosition);
            return;
        }

        // If it is not the correct one i choose the other one
        newPosition = new Point3D(current.getX() - flag.getX(), current.getY() - flag.getY(), current.getZ() - flag.getZ());
        newPosition = newPosition.multiply(-t).add(current);
        translate(newPosition);
    }
}

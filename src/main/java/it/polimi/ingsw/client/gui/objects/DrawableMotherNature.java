package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.ActionTranslator;
import it.polimi.ingsw.client.gui.AnimationHandler;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.effect.Light;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import javax.swing.text.html.HTMLDocument;

/**
 * This class represents the drawable object of mother nature
 * aka 3 spheres one on top of another
 */
public class DrawableMotherNature extends DrawableObject
{

    /**
     * Number of the spheres that compose mother nature
     */
    private final int NUMBER_OF_SPHERES;

    /**
     * Initial radius
     */
    private final float STARTING_RADIUS;

    /**
     * Delta of radius between a sphere and the next one
     */
    private final float DELTA_RADIUS;

    /**
     * Set of spheres that compose mother nature
     */
    private Sphere spheres[];

    /**
     * Drag and drop movement variables
     */
    private volatile double offsetPosX;
    private volatile double offsetPosZ;

    private volatile double posX;
    private volatile double posZ;

    private boolean dragging;

    /**
     * Constructor
     */
    public DrawableMotherNature(int number_of_spheres, float starting_radius, float delta_radius, AnimationHandler updater)
    {
        super(updater);

        if(number_of_spheres < 0 || starting_radius < 0 || delta_radius < 0)
            throw new IllegalArgumentException("[DrawableMotherNature] Invalid mother nature dimensions");

        if(starting_radius < (number_of_spheres - 1) * delta_radius)
            throw new IllegalArgumentException("[DrawableMotherNature] The spheres disappear before the end");

        // Create the material which will color the spheres
        PhongMaterial material = new PhongMaterial();
        material.setSpecularColor(Color.WHITE);
        material.setDiffuseColor(Color.valueOf("f0722a"));

        // Set Mother Nature appearance
        NUMBER_OF_SPHERES = number_of_spheres;
        STARTING_RADIUS = starting_radius;
        DELTA_RADIUS = delta_radius;

        // Create the array of spheres
        spheres = new Sphere[NUMBER_OF_SPHERES];

        // Actually create the spheres and add them
        for(int i = 0; i < spheres.length; i++)
        {
            spheres[i] = new Sphere();
            spheres[i].setRadius(STARTING_RADIUS - i * DELTA_RADIUS);
            spheres[i].setMaterial(material);
            spheres[i].setTranslateY(-(i * STARTING_RADIUS - i * DELTA_RADIUS));
        }

        // Dragging is false at the beginning
        dragging = false;

        // Set the drag and drop features
        spheres[0].setOnDragDetected((event) -> {
            offsetPosX = event.getX();
            offsetPosZ = event.getZ();
            posX = getPosition().getX();
            posZ = getPosition().getZ();
            for(Sphere sphere1 : spheres)
                sphere1.setMouseTransparent(true);
            spheres[0].setCursor(Cursor.MOVE);
            spheres[0].startFullDrag();

            // Set the dragging to true so that the islands
            // cannot set mother nature position while dragging
            dragging = true;

            // Set the dragging element in action translator
            ActionTranslator.getInstance().setDraggedItem("MotherNature");
        });
        spheres[0].setOnMouseReleased((event) -> {
            for(Sphere sphere1 : spheres)
                sphere1.setMouseTransparent(false);

            // Set also the cursor back to normal
            spheres[0].setCursor(Cursor.DEFAULT);

            // Set the dragging back to false
            dragging = false;
        });

        // I set the drag relative to spheres[0] (The base one)
        spheres[0].setOnMouseDragged((event) -> {
            posX = event.getX() - offsetPosX;
            posZ = event.getZ() - offsetPosZ;
            this.translate(new Point3D(getPosition().getX() + posX, 0, getPosition().getZ() + posZ));
        });

        // At the end if the updater != null i add the box to it
        if(this.updater != null)
            this.updater.subscribeObject(this);
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableMotherNature] Null group scene");

        // Add all the spheres to the root
        for(Sphere sphere : spheres)
            group.getChildren().add(sphere);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableMotherNature] Null group scene");

        // Remove all the spheres
        for(Sphere sphere : spheres)
            group.getChildren().remove(sphere);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableMotherNature] Null point light");

        // Add the light for all spheres
        for(Sphere sphere : spheres)
            light.getScope().add(sphere);
    }

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableMotherNature] Null ambient light");

        // Add the light for all spheres
        for(Sphere sphere : spheres)
            light.getScope().add(sphere);
    }

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableMotherNature] Null point light");

        // Add the light for all spheres
        for(Sphere sphere : spheres)
            light.getScope().remove(sphere);
    }

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableMotherNature] Null ambient light");

        // Add the light for all spheres
        for(Sphere sphere : spheres)
            light.getScope().remove(sphere);
    }

    @Override
    public void enableVisibility()
    {

    }

    @Override
    public void disableVisibility()
    {

    }

    /**
     * Position setters
     */
    @Override
    public void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableMotherNature] Null point");

        setX(point.getX());
        setY(point.getY());
        setZ(point.getZ());
    }

    // This method does nothing, mother nature is a sphere
    @Override
    public void addRotation(Rotate rotation) {}

    private void setX(double x)
    {
        for(Sphere sphere : spheres)
            sphere.translateXProperty().set(x);
    }

    private void setY(double y)
    {
        for(int i = 0; i < spheres.length; i++)
            spheres[i].setTranslateY(y - (i * STARTING_RADIUS - i * DELTA_RADIUS) - STARTING_RADIUS / 2);
    }

    private void setZ(double z)
    {
        for(Sphere sphere : spheres)
            sphere.translateZProperty().set(z);
    }

    @Override
    public Point3D getPosition() { return new Point3D(spheres[0].getTranslateX(), spheres[0].getTranslateY(), spheres[0].getTranslateZ()); }
    public boolean isDragging() { return dragging; }
}

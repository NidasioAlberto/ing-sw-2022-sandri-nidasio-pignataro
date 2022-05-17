package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import java.util.Objects;
import java.util.Random;

public class DrawableIsland extends DrawableObject
{
    /**
     * Island square dimensions
     */
    private final int DIMENSION;

    /**
     * Island draw type
     */
    private final IslandType TYPE;

    /**
     * Box containing the island texture
     */
    private Box box;

    /**
     * Animation angle (to which apply the Math.sin and float the island)
     */
    private float floatingAngle;

    /**
     * Constructor
     * @param dimension The square dimensions
     * @param type The texture island type
     */
    public DrawableIsland(int dimension, IslandType type, AnimationHandler updater)
    {
        super(updater);

        if(dimension < 0)
            throw new IllegalArgumentException("[DrawableIsland] Negative island dimensions");
        if(type == null)
            throw new NullPointerException("[DrawableIsland] Null island type pointer");

        // Set the island constants
        DIMENSION = dimension;
        TYPE = type;

        // Random set the first angle
        floatingAngle = new Random().nextFloat(360);

        // Setup the box as square
        box = new Box(dimension, dimension, 0);

        // Create and setup the material with the texture
        PhongMaterial material = new PhongMaterial();

        // Depending on the island type i can open the file
        material.setDiffuseMap(new Image(
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        type.getFilename()))));

        // Set the material with the texture
        box.setMaterial(material);

        // Rotate the island by 90 degrees in the x axis and 180 in the y axis
        box.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        box.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));

        // Set the node to mouse transparent
        box.setMouseTransparent(true);

        // At the end if the updater != null i add the box to it
        if(this.updater != null)
            this.updater.subscribeObject(this);
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableIsland] Null group scene");

        // Add the box to the group
        group.getChildren().add(box);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableIsland] Null group scene");

        // Remove the box
        group.getChildren().remove(box);
    }

    // Does nothing because i don't want reflections
    @Override
    public void subscribeToPointLight(PointLight light) {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableIsland] Null ambient light");

        // Subscribe the island to the light
        light.getScope().add(box);
    }

    @Override
    public void updateAnimation()
    {
        // I update the floating angle and them sum the Y shift with the sin
        floatingAngle += 5;
        floatingAngle = floatingAngle % 360;
        setY((float)box.getTranslateY() + (float) (Math.sin(Math.toRadians(floatingAngle)) / 2));
    }


    /**
     * Position setters. Needs to be synchronized for animations handling
     */
    @Override
    public synchronized void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableIsland] Null point");

        // Set the box position
        box.translateXProperty().set(point.getX());
        box.translateYProperty().set(point.getY());
        box.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if(rotation == null)
            throw new NullPointerException("[DrawableIsland] Null rotation");

        // Rotate the box
        box.getTransforms().add(rotation);
    }

    public synchronized void setX(float x) { box.translateXProperty().set(x); }

    public synchronized void setY(float y) { box.translateYProperty().set(y); }
    public synchronized void setZ(float z) { box.translateZProperty().set(z); }
    @Override
    public Point3D getPosition() { return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ()); }
}

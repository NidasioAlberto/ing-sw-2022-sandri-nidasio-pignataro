package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.IslandType;
import it.polimi.ingsw.client.gui.objects.types.TowerType;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DrawableIsland extends DrawableObject
{
    // Constants
    public static final float FLOATING_AMPLITUDE = 5;
    public static final float FLOATING_ANGULAR_VELOCITY = 1.5f;

    /**
     * Island square dimensions
     */
    private final int DIMENSION;

    /**
     * Positioning constants
     */
    private final double X_TOWER = -0.17;
    private final double Y_TOWER = 0.25;

    /**
     * Island draw type
     */
    private final IslandType TYPE;

    /**
     * Box containing the island texture
     */
    private Box box;

    /**
     * Island payloads
     */
    private DrawableTower tower;
    private List<DrawableStudent> students;

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

        // Create the collection of students
        students = new ArrayList<>();

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
        box.setMouseTransparent(false);

        // At the end if the updater != null i add the box to it
        if(this.updater != null)
            this.updater.subscribeObject(this);

        box.setOnDragOver((event) -> {
            System.out.println(event.getGestureSource());
        });

        box.setOnMouseDragReleased((event) -> {
            System.out.println(event.getEventType());
        });
    }

    public void addTower(TowerType type, Group group, PointLight light)
    {
        // I add the tower only if there isn't already one
        if(tower != null)
            return;

        // Create the drawable tower
        tower = new DrawableTower(type, updater);

        // Translate the tower
        tower.translate(new Point3D(X_TOWER * DIMENSION + getPosition().getX(), 0, Y_TOWER * DIMENSION + getPosition().getY()));

        // Add the tower to group and point light
        tower.addToGroup(group);
        tower.subscribeToPointLight(light);
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
    public void enableVisibility()
    {

    }

    @Override
    public void disableVisibility()
    {

    }

    @Override
    public void updateAnimation()
    {
        // I update the floating angle and them sum the Y shift with the sin
        floatingAngle += FLOATING_ANGULAR_VELOCITY;
        floatingAngle = floatingAngle % 360;
        translate(new Point3D(getPosition().getX(), Math.sin(Math.toRadians(floatingAngle)) * FLOATING_AMPLITUDE, getPosition().getZ()));
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

        // Update all the components
        if(tower != null)
            tower.translate(new Point3D(X_TOWER * DIMENSION + point.getX(), point.getY(), Y_TOWER * DIMENSION + point.getZ()));
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if(rotation == null)
            throw new NullPointerException("[DrawableIsland] Null rotation");

        // Rotate the box
        box.getTransforms().add(rotation);
    }

    public synchronized void setX(double x) { box.translateXProperty().set(x); }

    public synchronized void setY(double y) { box.translateYProperty().set(y); }
    public synchronized void setZ(double z) { box.translateZProperty().set(z); }
    @Override
    public Point3D getPosition() { return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ()); }
}

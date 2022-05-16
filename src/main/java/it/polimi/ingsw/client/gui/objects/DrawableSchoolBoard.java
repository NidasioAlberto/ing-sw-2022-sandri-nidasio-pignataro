package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.TowerColor;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.lang.reflect.Array;
import java.util.*;

public class DrawableSchoolBoard extends DrawableObject
{
    // Scaling factor between x and y dimensions to maintain the proportions
    public static final float SCALE_FACTOR = 2.30f;

    /**
     * Positioning constants
     */
    private static final double DINING_X_STEP   = 0.04763;
    private static final double DINING_Y_STEP   = 0.07143;
    private static final double ENTRANCE_X_STEP = 0.05857;
    private static final double TOWER_X_STEP    = 0.07714;
    private static final double TOWER_Y_STEP    = 0.07143;
    private static final double FIRST_X_DINING    = -0.29286;
    private static final double FIRST_X_PROFESSOR = 0.22857;
    private static final double FIRST_X_ENTRANCE  = -0.44429;
    private static final double FIRST_Y_ENTRANCE  = 0.14286;
    private static final double FIRST_X_TOWER     = 0.33857;
    private static final double FIRST_Y_TOWER     = 0.10714;
    private static final Map<SchoolColor, Double> HEIGHTS = new HashMap<>();

    /**
     * Reference Schoolboard arrays
     */
    private List<DrawableStudent> entrance;
    private List<DrawableTower> towers;
    private List<DrawableProfessor> professors;
    private Map<SchoolColor, List<DrawableStudent>> dining;

    /**
     * X Dimension constant
     */
    private final float X_DIMENSION;

    /**
     * Y Dimension constant
     */
    private final float Y_DIMENSION;

    /**
     * Box containing the SchoolBoard texture
     */
    private final Box box;

    /**
     * Collection of rotations to perform on the drawable objects
     */
    private List<Rotate> rotations;

    /**
     * Constructor
     * @param x_dimension The x dimension of the board
     */
    public DrawableSchoolBoard(float x_dimension)
    {
        if(x_dimension < 0)
            throw new IllegalArgumentException("[DrawableSchoolBoard] X dimension less than 0");

        // Assign the parameters
        X_DIMENSION = x_dimension;
        Y_DIMENSION = x_dimension / SCALE_FACTOR;

        // Initialize all the reference model
        entrance = new ArrayList<>();
        professors = new ArrayList<>();
        towers = new ArrayList<>();
        dining = new HashMap<>();

        for(SchoolColor color : SchoolColor.values())
            dining.put(color, new ArrayList<>());

        // Create the rotation collection
        rotations = new ArrayList<>();

        // Create the map with reference heights
        HEIGHTS.put(SchoolColor.GREEN, 0.14286);
        HEIGHTS.put(SchoolColor.RED, 0.07143);
        HEIGHTS.put(SchoolColor.YELLOW, 0.0);
        HEIGHTS.put(SchoolColor.PINK, -0.07143);
        HEIGHTS.put(SchoolColor.BLUE, -0.14286);

        // Create the box
        this.box = new Box(X_DIMENSION, Y_DIMENSION, 0);

        // Create the correct texture
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("schoolboard.png"))));

        // Assign the texture
        box.setMaterial(material);

        // Rotate the box correctly
        box.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        box.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null group");

        // Add the box to the group
        group.getChildren().add(box);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null group");

        // Remove the box from the group
        group.getChildren().remove(box);
    }

    // This method does nothing because i don't want reflections
    @Override
    public void subscribeToPointLight(PointLight light) {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null ambient light");

        // Add the box to the light
        light.getScope().add(box);
    }

    /**
     * Adds a student to the dining room
     * @param color The student color to be added
     * @param group The group to which add the student
     * @param light The point light to which subscribe the new student
     */
    public void addStudentToDining(SchoolColor color, Group group, PointLight light)
    {
        // Check that the student can be there
        if(dining.get(color).size() >= 10)
            return;

        // Create the new student of the same color
        DrawableStudent student = new DrawableStudent(StudentType.valueOf(color.name()));

        // The coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_DINING * X_DIMENSION + dining.get(color).size() * DINING_X_STEP * X_DIMENSION, 0,
                HEIGHTS.get(color) * X_DIMENSION);

        // For all the present rotations i rotate the student
        for(Rotate rotation : rotations)
        {
            coordinates = rotation.transform(coordinates);

            // Rotate also the component with a little correction in axis
            student.addRotation(new Rotate(rotation.getAngle(), new Point3D(-rotation.getAxis().getX(), rotation.getAxis().getY(), -rotation.getAxis().getZ())));
        }

        // Translate the student in the correct position
        student.translate(new Point3D(coordinates.getX() + box.getTranslateX(),
                coordinates.getY() + box.getTranslateY(),
                coordinates.getZ() + box.getTranslateZ()));

        // Add the student to the model
        dining.get(color).add(student);

        // Add the student to the group and to the lights
        student.addToGroup(group);
        student.subscribeToPointLight(light);
    }

    /**
     * Adds a professor to the schoolboard
     * @param color The professor color
     * @param group The group to which add the professor
     * @param light The point light to which add the professor
     */
    public void addProfessor(SchoolColor color, Group group, PointLight light)
    {
        // Check if that professor is already inside the schoolboard
        for(DrawableProfessor professor : professors)
        {
            // If there is a copy i don't add the professor
            if(professor.getType().name().equals(color.name()))
                return;
        }

        // Create the new professor
        DrawableProfessor professor = new DrawableProfessor(ProfessorType.valueOf(color.name()));

        // The coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_PROFESSOR * X_DIMENSION, 0, HEIGHTS.get(color) * X_DIMENSION );

        // For all the present rotations i rotate the professor
        for(Rotate rotation : rotations)
        {
            coordinates = rotation.transform(coordinates);

            // Rotate also the component with a little correction
            professor.addRotation(new Rotate(rotation.getAngle(), new Point3D(rotation.getAxis().getZ(), rotation.getAxis().getY(), -rotation.getAxis().getX())));
        }

        // Translate the professor where it should be
        professor.translate(new Point3D(coordinates.getX() + box.getTranslateX(),
                coordinates.getY() + box.getTranslateY(),
                coordinates.getZ() + box.getTranslateZ()));

        // Add the professor to the model
        professors.add(professor);

        // Add the Professor to the group and to the light
        professor.addToGroup(group);
        professor.subscribeToPointLight(light);
    }

    /**
     * Adds a student to the entrance
     * @param color The student color
     * @param group The group to which add the student
     * @param light The point light to which add the student
     */
    public void addStudentToEntrance(SchoolColor color, Group group, PointLight light)
    {
        // Check if it is actually possible
        if(entrance.size() >= 9)
            return;

        // Create the new student
        DrawableStudent student = new DrawableStudent(StudentType.valueOf(color.name()));

        // The coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_ENTRANCE * X_DIMENSION + ((entrance.size() + 1) % 2) * ENTRANCE_X_STEP * X_DIMENSION, 0,
                FIRST_Y_ENTRANCE * X_DIMENSION - (int)((entrance.size() + 1) / 2) * DINING_Y_STEP * X_DIMENSION);

        // For all the present rotations i rotate the student
        for(Rotate rotation : rotations)
        {
            coordinates = rotation.transform(coordinates);

            // Rotate also the component with a little correction in axis
            student.addRotation(new Rotate(rotation.getAngle(), new Point3D(-rotation.getAxis().getX(), rotation.getAxis().getY(), -rotation.getAxis().getZ())));
        }

        // Translate the student to the correct position
        student.translate(new Point3D(coordinates.getX() + box.getTranslateX(),
                coordinates.getY() + box.getTranslateY(),
                coordinates.getZ() + box.getTranslateZ()));

        // Add the student to the model
        entrance.add(student);

        // Add the student to the group and the light
        student.addToGroup(group);
        student.subscribeToPointLight(light);
    }

    /**
     * Adds the tower to the schoolboard
     * @param color The color of the tower
     * @param group The group to which add the tower
     * @param light The point light to which add the tower
     */
    public void addTower(TowerColor color, Group group, PointLight light)
    {
        // Verify that it could actually be done
        if(towers.size() >= 8)
            return;

        // Create the new tower
        DrawableTower tower = new DrawableTower(TowerType.valueOf(color.name()));

        // The coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_TOWER * X_DIMENSION + (towers.size() % 2) * TOWER_X_STEP * X_DIMENSION, 0,
                FIRST_Y_TOWER * X_DIMENSION - (int)(towers.size() / 2) * TOWER_Y_STEP * X_DIMENSION);

        // For all the present rotations i rotate the tower
        for(Rotate rotation : rotations)
        {
            coordinates = rotation.transform(coordinates);

            // Rotate also the component
            tower.addRotation(rotation);
        }

        // Translate the tower to the correct position
        tower.translate(new Point3D(coordinates.getX() + box.getTranslateX(),
                coordinates.getY() + box.getTranslateY(),
                coordinates.getZ() + box.getTranslateZ()));

        // Add the tower to the model
        towers.add(tower);

        // Add the tower to the group and to the light
        tower.addToGroup(group);
        tower.subscribeToPointLight(light);
    }

    /**
     * Position setters need to be synchronized
     */
    @Override
    public synchronized void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null point");

        // Set the box position
        box.translateXProperty().set(point.getX());
        box.translateYProperty().set(point.getY());
        box.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        // The idea is to add the rotation to the box and memorize it
        // so that students, towers and professors added after are affected
        // TODO consider an update for the present objects

        if(rotation == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null rotation");

        // Add the transformation to the box (little axis swap because of box as plane)
        box.getTransforms().add(new Rotate(-rotation.getAngle(), new Point3D(rotation.getAxis().getX(), rotation.getAxis().getZ(), rotation.getAxis().getY())));

        // Also i add the rotation to the list
        rotations.add(rotation);
    }

    @Override
    public Point3D getPosition() { return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ()); }
}

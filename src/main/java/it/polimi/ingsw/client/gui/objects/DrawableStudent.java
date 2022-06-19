package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.ActionTranslator;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.StudentType;
import it.polimi.ingsw.model.SchoolColor;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

public class DrawableStudent extends DrawableObject
{
    /**
     * Student type (color)
     */
    private final StudentType TYPE;

    /**
     * Determines if the student is on a character card or not
     */
    private boolean characterStudent;

    /**
     * Object mesh
     */
    private final TriangleMesh triangleMesh;
    private final MeshView studentMesh;

    /**
     * Drag and drop movement variables
     */
    private volatile double offsetPosX;
    private volatile double offsetPosZ;

    private volatile double posX;
    private volatile double posZ;

    /**
     * Boolean that represents if the student is drag and droppable. DIFFERENT from mouse transparent because it can be addressed for a drop.
     */
    private boolean draggable;

    /**
     * Constructor
     */
    public DrawableStudent(StudentType type, AnimationHandler updater)
    {
        super(updater);

        if (type == null)
            throw new NullPointerException("[DrawableStudent] Null student type");

        // Assign the constant parameters
        TYPE = type;

        // I set the character student to false
        characterStudent = false;

        // Create the mesh referring to the Object file
        triangleMesh = new TriangleMesh();

        // Set all the vertices, textures, faces and normals
        triangleMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getNormals().addAll(type.getParser().getNormals());
        triangleMesh.getFaces().addAll(type.getParser().getFaces());
        triangleMesh.getPoints().addAll(type.getParser().getVertices());
        triangleMesh.getTexCoords().addAll(type.getParser().getTextures());

        // Now i create the actual node with the mesh
        studentMesh = new MeshView(triangleMesh);

        // I create the material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(type.getColor());
        material.setSpecularColor(Color.WHITE);

        // Apply the material
        studentMesh.setMaterial(material);

        // Rotate the student of 180 degrees on the y axis
        Rotate rotation = new Rotate(180, new Point3D(0, 1, 0));
        studentMesh.getTransforms().add(rotation);

        // Set the node to mouse transparent
        studentMesh.setMouseTransparent(false);
        draggable = true;

        // Set the drag and drop features
        studentMesh.setOnMouseDragEntered((event) -> {
            material.setDiffuseColor(type.getColor().interpolate(new Color(1, 1, 1, 0.9), 0.5));
        });

        studentMesh.setOnMouseDragExited((event) -> {
            material.setDiffuseColor(type.getColor());
        });

        studentMesh.setOnMouseClicked((event) -> {
            // Ensure that it is a double click
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
            {
                // Set the dragged item
                ActionTranslator.getInstance().setDraggedItem("Student");

                // Set the selected color
                ActionTranslator.getInstance().selectColor(SchoolColor.valueOf(TYPE.name()));

                // Execute the action
                ActionTranslator.getInstance().execute();
            }
        });

        studentMesh.setOnDragDetected((event) -> {
            if (draggable)
            {
                offsetPosX = event.getX();
                offsetPosZ = event.getZ();
                posX = studentMesh.getTranslateX();
                posZ = studentMesh.getTranslateZ();
                studentMesh.setMouseTransparent(true);
                studentMesh.setCursor(Cursor.MOVE);
                studentMesh.startFullDrag();

                // Set the dragged element on the action translator
                if (!characterStudent)
                    ActionTranslator.getInstance().setDraggedItem("Student");
                else
                    ActionTranslator.getInstance().setDraggedItem("CharacterStudent");

                // Set also the selected color
                ActionTranslator.getInstance().selectColor(SchoolColor.valueOf(TYPE.name()));
            }
        });

        studentMesh.setOnMouseDragged((event) -> {
            if (draggable)
            {
                posX = rotation.transform(new Point3D(event.getX(), 0, 0)).getX() - offsetPosX;
                posZ = rotation.transform(new Point3D(0, 0, event.getZ())).getZ() - offsetPosZ;
                this.translate(new Point3D(studentMesh.getTranslateX() + posX, 0, studentMesh.getTranslateZ() + posZ));
            }
        });

        studentMesh.setOnMouseReleased((event) -> {
            studentMesh.setCursor(Cursor.DEFAULT);
            studentMesh.setMouseTransparent(false);
        });

        studentMesh.setOnMouseDragReleased((event) -> {
            // Set the original color
            material.setDiffuseColor(type.getColor());

            // Set the dropped on element
            if (!characterStudent)
                ActionTranslator.getInstance().setDroppedOnItem("Student");
            else
                ActionTranslator.getInstance().setDroppedOnItem("CharacterStudent");

            // Select the color
            ActionTranslator.getInstance().selectColor(SchoolColor.valueOf(TYPE.name()));

            // Act the action translator
            ActionTranslator.getInstance().execute();
        });
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableStudent] Null group");

        // Add the cylinder to the group
        group.getChildren().add(studentMesh);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableStudent] Null group");

        // Remove the cylinder from the group
        group.getChildren().remove(studentMesh);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableStudent] Null point light");

        // The students should be under the light so i subscribe it
        light.getScope().add(studentMesh);
    }

    // This method does nothing because i don't want light from everywhere

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {}

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableStudent] Null point light");

        // The students should be under the light so i unsubscribe it
        light.getScope().remove(studentMesh);
    }

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {}

    @Override
    public void enableVisibility()
    {
        studentMesh.setMouseTransparent(false);
    }

    @Override
    public void disableVisibility()
    {
        studentMesh.setMouseTransparent(true);
    }

    /**
     * Sets the draggable property of the student
     */
    public void setDraggable(boolean drag)
    {
        this.draggable = drag;
    }

    /**
     * Sets the student to character
     */
    public void setCharacter(boolean character)
    {
        this.characterStudent = character;
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableStudent] Null point");

        // Set all the translation
        studentMesh.translateXProperty().set(point.getX());
        studentMesh.translateYProperty().set(point.getY());
        studentMesh.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableStudent]");

        // Add the transformation
        studentMesh.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(studentMesh.getTranslateX(), studentMesh.getTranslateY(), studentMesh.getTranslateZ());
    }

    public StudentType getType()
    {
        return TYPE;
    }
}

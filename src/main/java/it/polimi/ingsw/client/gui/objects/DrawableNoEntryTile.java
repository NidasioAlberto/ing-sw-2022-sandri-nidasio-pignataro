package it.polimi.ingsw.client.gui.objects;

import java.util.Objects;
import it.polimi.ingsw.client.gui.ActionTranslator;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.ObjectModelParser;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class DrawableNoEntryTile extends DrawableObject
{
    // The object parser
    private static final ObjectModelParser parser = new ObjectModelParser("Models/noEntry.obj", 5);

    /**
     * Object mesh and texture
     */
    private final TriangleMesh triangleMesh;
    private final MeshView noEntryMesh;

    /**
     * Drag and drop movement variables
     */
    private volatile double offsetPosX;
    private volatile double offsetPosZ;

    /**
     * Constructor
     * 
     * @param updater The animation updater
     */
    public DrawableNoEntryTile(AnimationHandler updater)
    {
        super(updater);

        // Create the mesh referring to the object file
        triangleMesh = new TriangleMesh();

        // Set all the vertices, textures, faces and normals
        triangleMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getNormals().addAll(parser.getNormals());
        triangleMesh.getFaces().addAll(parser.getFaces());
        triangleMesh.getPoints().addAll(parser.getVertices());
        triangleMesh.getTexCoords().addAll(parser.getTextures());

        // Create the actual node with the mesh
        noEntryMesh = new MeshView(triangleMesh);

        // Create the texture material
        PhongMaterial material = new PhongMaterial();

        // Pick the texture
        material.setDiffuseMap(new Image(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("noEntry.png"))));

        // Set the material
        noEntryMesh.setMaterial(material);

        // Add the initial rotation
        addRotation(new Rotate(180, new Point3D(0, 1, 0)));

        // Set drag and drop features
        noEntryMesh.setOnDragDetected((event) -> {
            offsetPosX = event.getX();
            offsetPosZ = event.getZ();
            noEntryMesh.setMouseTransparent(true);
            noEntryMesh.setCursor(Cursor.MOVE);
            noEntryMesh.startFullDrag();

            // Set the dragged element on the action translator
            ActionTranslator.getInstance().setDraggedItem("NoEntryTile");
        });

        noEntryMesh.setOnMouseDragged((event) -> {
            Point3D translation = new Point3D(event.getX(), 0, event.getZ());

            // For every transform inside the mesh i adapt the translation
            for (Transform transform : noEntryMesh.getTransforms())
                translation = transform.transform(translation);

            // Delete the offset
            translation.add(new Point3D(-offsetPosX, 0, -offsetPosZ));

            // Translate the object
            translate(translation.add(getPosition()));
        });

        noEntryMesh.setOnMouseReleased((event) -> {
            // Reset the default mouse settings
            noEntryMesh.setCursor(Cursor.DEFAULT);
            noEntryMesh.setMouseTransparent(false);
        });

        // For reset purposes
        noEntryMesh.setOnMouseDragReleased((event) -> {
            // Set the dragged on element
            ActionTranslator.getInstance().setDroppedOnItem("NoEntryTile");

            // Act the action translator
            ActionTranslator.getInstance().execute();
        });
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null group");

        group.getChildren().add(noEntryMesh);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null group");

        group.getChildren().remove(noEntryMesh);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null point light");

        light.getScope().add(noEntryMesh);
    }

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {}

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null point light");

        light.getScope().remove(noEntryMesh);
    }

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {}

    @Override
    public void enableVisibility()
    {
        noEntryMesh.setMouseTransparent(false);
    }

    @Override
    public void disableVisibility()
    {
        noEntryMesh.setMouseTransparent(true);
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null 3D point");

        noEntryMesh.translateXProperty().set(point.getX());
        noEntryMesh.translateYProperty().set(point.getY());
        noEntryMesh.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null rotation");

        noEntryMesh.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(noEntryMesh.getTranslateX(), noEntryMesh.getTranslateY(), noEntryMesh.getTranslateZ());
    }
}

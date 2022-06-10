package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.ProfessorType;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;

public class DrawableProfessor extends DrawableObject
{
    /**
     * Professor type
     */
    private final ProfessorType TYPE;

    /**
     * Object meshes
     */
    private TriangleMesh triangleMesh;
    private MeshView professorMesh;

    /**
     * Constructor
     * 
     * @param type The type of the professor (Color and therefore it's shape)
     */
    public DrawableProfessor(ProfessorType type, AnimationHandler updater)
    {
        super(updater);

        if (type == null)
            throw new NullPointerException("[DrawableProfessor] Null professor type");

        // Assign the constants
        TYPE = type;

        // Create the mesh referring to the object file
        triangleMesh = new TriangleMesh();

        // Set all the vertices, textures, faces and normals
        triangleMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getNormals().addAll(type.getParser().getNormals());
        triangleMesh.getFaces().addAll(type.getParser().getFaces());
        triangleMesh.getPoints().addAll(type.getParser().getVertices());
        triangleMesh.getTexCoords().addAll(type.getParser().getTextures());

        // Now i create the actual node with the mesh
        professorMesh = new MeshView(triangleMesh);

        // Set the texture color
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(TYPE.getColor());
        material.setSpecularColor(Color.WHITE);

        // Rotate around the y axis of -90 degrees
        professorMesh.getTransforms().add(new Rotate(-90, new Point3D(0, 1, 0)));

        // Apply the material
        professorMesh.setMaterial(material);

        // Set the node to mouse transparent
        professorMesh.setMouseTransparent(true);
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableProfessor] Null group");

        // Add the mesh to the group
        group.getChildren().add(professorMesh);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableProfessor] Null group");

        // Remove the mesh from the group
        group.getChildren().remove(professorMesh);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableProfessor] Null point light");

        // Subscribe to the point light
        light.getScope().add(professorMesh);
    }

    // This method does nothing because the professor needs to be subscribed only to point lights
    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {}

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableProfessor] Null point light");

        // unsubscribe from the point light
        light.getScope().remove(professorMesh);
    }

    // This method does nothing because the professor needs to be subscribed only to point lights
    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {}

    @Override
    public void enableVisibility()
    {

    }

    @Override
    public void disableVisibility()
    {

    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableProfessor] Null point");

        // Translate all the coordinates
        professorMesh.translateXProperty().set(point.getX());
        professorMesh.translateYProperty().set(point.getY());
        professorMesh.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableProfessor] Null rotation");

        // Add the rotation to the object
        professorMesh.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(professorMesh.getTranslateX(), professorMesh.getTranslateY(), professorMesh.getTranslateZ());
    }

    public ProfessorType getType()
    {
        return TYPE;
    }
}

package org.openjfx.isosurface.suface;

import javafx.scene.shape.TriangleMesh;
import org.openjfx.isosurface.VoxelGrid;
import org.openjfx.isosurface.util.Float3;

import java.util.HashMap;

/**
 * The base class for objects that build triangle meshes that represent the surface of 3D scalar fields.
 */
public abstract class SdfMeshBuilder {
    /**
     * Adds a triangle to a triangle mesh, reusing existing points if possible to allow for smooth normals.
     *
     * @param p0          the first point of the triangle
     * @param p1          the second point of the triangle
     * @param p2          the third point of the triangle
     * @param smoothGroup the smoothing group of the triangle
     * @param deDupMap    a hashmap used to deduplicate points
     * @param mesh        the mesh to add the triangle to
     */
    protected static void addTriFace(Float3 p0, Float3 p1, Float3 p2, int smoothGroup, HashMap<Float3, Integer> deDupMap, TriangleMesh mesh) {
        Integer p0Index = deDupMap.get(p0);
        Integer p1Index = deDupMap.get(p1);
        Integer p2Index = deDupMap.get(p2);

        if (p0Index == null) {
            p0Index = mesh.getPoints().size() / 3;
            deDupMap.put(p0, p0Index);
            mesh.getPoints().addAll(p0.x(), p0.y(), p0.z());
        }

        if (p1Index == null) {
            p1Index = mesh.getPoints().size() / 3;
            deDupMap.put(p1, p1Index);
            mesh.getPoints().addAll(p1.x(), p1.y(), p1.z());
        }

        if (p2Index == null) {
            p2Index = mesh.getPoints().size() / 3;
            deDupMap.put(p2, p2Index);
            mesh.getPoints().addAll(p2.x(), p2.y(), p2.z());
        }

        mesh.getFaces().addAll(p0Index, 0, p1Index, 0, p2Index, 0);
        mesh.getFaceSmoothingGroups().addAll(smoothGroup);
    }

    /**
     * Adds a quad to a triangle mesh, reusing existing points if possible to allow for smooth normals.
     * The quad is automatically triangulated into two separate triangles.
     *
     * @param p0          the first point of the quad
     * @param p1          the second point of the quad
     * @param p2          the third point of the quad
     * @param p3          the fourth point of the quad
     * @param smoothGroup the smoothing group of the triangle
     * @param deDupMap    a hashmap used to deduplicate points
     * @param mesh        the mesh to add the triangle to
     */
    protected static void addQuadFace(Float3 p0, Float3 p1, Float3 p2, Float3 p3, int smoothGroup, HashMap<Float3, Integer> deDupMap, TriangleMesh mesh) {
        Integer p0Index = deDupMap.get(p0);
        Integer p1Index = deDupMap.get(p1);
        Integer p2Index = deDupMap.get(p2);
        Integer p3Index = deDupMap.get(p3);

        if (p0Index == null) {
            p0Index = mesh.getPoints().size() / 3;
            deDupMap.put(p0, p0Index);
            mesh.getPoints().addAll(p0.x(), p0.y(), p0.z());
        }

        if (p1Index == null) {
            p1Index = mesh.getPoints().size() / 3;
            deDupMap.put(p1, p1Index);
            mesh.getPoints().addAll(p1.x(), p1.y(), p1.z());
        }

        if (p2Index == null) {
            p2Index = mesh.getPoints().size() / 3;
            deDupMap.put(p2, p2Index);
            mesh.getPoints().addAll(p2.x(), p2.y(), p2.z());
        }

        if (p3Index == null) {
            p3Index = mesh.getPoints().size() / 3;
            deDupMap.put(p3, p3Index);
            mesh.getPoints().addAll(p3.x(), p3.y(), p3.z());
        }

        mesh.getFaces().addAll(p0Index, 0, p1Index, 0, p2Index, 0, p0Index, 0, p2Index, 0, p3Index, 0);
        mesh.getFaceSmoothingGroups().addAll(smoothGroup, smoothGroup);
    }

    /**
     * Creates a triangle mesh from a scalar field.
     * The new mesh data will be added to the triangle mesh provided, overwriting any existing mesh data.
     *
     * @param voxelGrid     the sdf data from which to extract a mesh
     * @param isoLevel      the offset of the surface of the sdf
     * @param smoothShading whether the mesh should have smooth shading
     * @param mesh          the destination for the mesh data
     */
    public abstract void buildMesh(VoxelGrid voxelGrid, float isoLevel, boolean smoothShading, TriangleMesh mesh);

    @Override
    public abstract String toString();
}

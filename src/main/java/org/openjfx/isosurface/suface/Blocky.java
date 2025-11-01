package org.openjfx.isosurface.suface;

import javafx.scene.shape.TriangleMesh;
import org.openjfx.isosurface.VoxelGrid;
import org.openjfx.isosurface.util.Float3;

import java.util.HashMap;

/**
 * A class used to construct triangle meshes that represent the
 * surface of 3D scalar fields using a simple blocky algorithm.
 */
public final class Blocky extends SdfMeshBuilder {
    @Override
    public void buildMesh(VoxelGrid voxelGrid, float isoLevel, boolean smoothShading, TriangleMesh mesh) {
        final HashMap<Float3, Integer> deDupMap = new HashMap<>();
        final int smoothGroup = smoothShading ? 1 : 0;

        mesh.getFaces().clear();
        mesh.getPoints().clear();
        mesh.getFaceSmoothingGroups().clear();

        for (int z = 0; z < voxelGrid.getDepth(); z++) {
            for (int y = 0; y < voxelGrid.getHeight(); y++) {
                for (int x = 0; x < voxelGrid.getWidth(); x++) {

                    // skip if the current voxel is outside the shape
                    if (voxelGrid.getVoxel(x, y, z) > isoLevel) {
                        continue;
                    }

                    // get the coordinates of the corners of this voxel
                    // NOTE: doing the same calculation for both the min and max vertex positions ensures
                    // the positions of identical cells match and can be deduplicated, which may not happen
                    // if `max_pos` == `min_pos + voxelSize` due to floating point precision limitations
                    final Float3 minVoxelCornerPos = voxelGrid.getVoxelCornerPos(x, y, z);
                    final Float3 maxVoxelCornerPos = voxelGrid.getVoxelCornerPos(x + 1, y + 1, z + 1);

                    final float minX = minVoxelCornerPos.x();
                    final float minY = minVoxelCornerPos.y();
                    final float minZ = minVoxelCornerPos.z();

                    final float maxX = maxVoxelCornerPos.x();
                    final float maxY = maxVoxelCornerPos.y();
                    final float maxZ = maxVoxelCornerPos.z();

                    final Float3 p0 = new Float3(minX, minY, minZ);
                    final Float3 p1 = new Float3(maxX, minY, minZ);
                    final Float3 p2 = new Float3(minX, maxY, minZ);
                    final Float3 p3 = new Float3(maxX, maxY, minZ);
                    final Float3 p4 = new Float3(minX, minY, maxZ);
                    final Float3 p5 = new Float3(maxX, minY, maxZ);
                    final Float3 p6 = new Float3(minX, maxY, maxZ);
                    final Float3 p7 = new Float3(maxX, maxY, maxZ);

                    // get the distances of all adjacent voxels
                    float d0 = voxelGrid.getVoxel(x + 1, y, z);
                    float d1 = voxelGrid.getVoxel(x - 1, y, z);
                    float d2 = voxelGrid.getVoxel(x, y + 1, z);
                    float d3 = voxelGrid.getVoxel(x, y - 1, z);
                    float d4 = voxelGrid.getVoxel(x, y, z + 1);
                    float d5 = voxelGrid.getVoxel(x, y, z - 1);

                    // if an adjacent voxel is outside the shape then add a face between both voxels
                    if (d0 >= isoLevel) addQuadFace(p5, p1, p3, p7, smoothGroup, deDupMap, mesh); // left face
                    if (d1 >= isoLevel) addQuadFace(p4, p6, p2, p0, smoothGroup, deDupMap, mesh); // right face
                    if (d2 >= isoLevel) addQuadFace(p2, p6, p7, p3, smoothGroup, deDupMap, mesh); // top face
                    if (d3 >= isoLevel) addQuadFace(p0, p1, p5, p4, smoothGroup, deDupMap, mesh); // bottom face
                    if (d4 >= isoLevel) addQuadFace(p5, p7, p6, p4, smoothGroup, deDupMap, mesh); // front face
                    if (d5 >= isoLevel) addQuadFace(p0, p2, p3, p1, smoothGroup, deDupMap, mesh); // back face
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Blocky";
    }
}

package org.openjfx.isosurface.suface;

import javafx.scene.shape.TriangleMesh;
import org.openjfx.isosurface.VoxelGrid;
import org.openjfx.isosurface.util.Axis;
import org.openjfx.isosurface.util.Float3;
import org.openjfx.isosurface.util.Int3;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class used to construct triangle meshes that represent the
 * surface of 3D scalar fields using the Surface Nets algorithm.
 */
public class SurfaceNets extends SdfMeshBuilder {
    private static final Int3[] edgeEndOffsets = {
            new Int3(1, 0, 0),
            new Int3(0, 1, 0),
            new Int3(0, 0, 1)
    };
    private static final Int3[][] edgeNeighbourOffsets = {
            {
                    new Int3(+0, -1, -1),
                    new Int3(+0, +0, -1),
                    new Int3(+0, +0, +0),
                    new Int3(+0, -1, +0)
            },
            {
                    new Int3(-1, +0, -1),
                    new Int3(-1, +0, +0),
                    new Int3(+0, +0, +0),
                    new Int3(+0, +0, -1)
            },
            {
                    new Int3(-1, -1, +0),
                    new Int3(+0, -1, +0),
                    new Int3(+0, +0, +0),
                    new Int3(-1, +0, +0)
            }
    };

    @Override
    public void buildMesh(VoxelGrid voxelGrid, float isoLevel, boolean smoothShading, TriangleMesh mesh) {
        final HashMap<Int3, Float3> voxelPoints = new HashMap<>();
        final HashMap<Int3, Integer> voxelNumActiveEdges = new HashMap<>();
        final ArrayList<Int3> edgeStartCoords = new ArrayList<>();
        final ArrayList<Axis> edgeAxes = new ArrayList<>();
        final ArrayList<Boolean> edgesPositiveDir = new ArrayList<>();
        final HashMap<Float3, Integer> deDupMap = new HashMap<>();
        final int smoothGroup = smoothShading ? 1 : 0;

        mesh.getFaces().clear();
        mesh.getPoints().clear();
        mesh.getFaceSmoothingGroups().clear();

        for (int z = -1; z < voxelGrid.getDepth(); z++) {
            for (int y = -1; y < voxelGrid.getHeight(); y++) {
                for (int x = -1; x < voxelGrid.getWidth(); x++) {
                    final Int3 startCoord = new Int3(x, y, z);
                    final float dStart = voxelGrid.getVoxel(startCoord);
                    final Float3 pStart = voxelGrid.getVoxelCenterPos(startCoord);
                    final boolean pStartExterior = dStart < isoLevel;

                    // loop over each edge that starts from the current voxel and
                    // ends at a neighbouring voxel in the positive X, Y, and Z axes
                    for (int i = 0; i < 3; i++) {
                        final Axis axis = Axis.values()[i];
                        final Int3 endCoord = startCoord.add(edgeEndOffsets[i]);
                        final float dEnd = voxelGrid.getVoxel(endCoord);
                        final boolean pEndExterior = dEnd < isoLevel;

                        // check that the edge intersects the surface, i.e. one
                        // end is inside the shape and the other end is outside
                        if (pEndExterior != pStartExterior) {
                            final Float3 pEnd = voxelGrid.getVoxelCenterPos(endCoord);
                            final Float3 vertex = edgeIntersection(isoLevel, pStart, pEnd, dStart, dEnd);

                            // add the position of the edge intersection to the
                            // points inside the four voxels that share the edge
                            for (final Int3 neighbourOffset : edgeNeighbourOffsets[i]) {
                                final Int3 voxelCoord = startCoord.add(neighbourOffset);

                                if (!voxelPoints.containsKey(voxelCoord)) {
                                    voxelPoints.put(voxelCoord, vertex);
                                    voxelNumActiveEdges.put(voxelCoord, 1);
                                } else {
                                    voxelPoints.put(voxelCoord, voxelPoints.get(voxelCoord).add(vertex));
                                    voxelNumActiveEdges.put(voxelCoord, voxelNumActiveEdges.get(voxelCoord) + 1);
                                }
                            }

                            edgeStartCoords.add(startCoord);
                            edgeAxes.add(axis);
                            edgesPositiveDir.add(pStartExterior);
                        }
                    }
                }
            }
        }

        // average all voxel point positions
        for (final Int3 key : voxelPoints.keySet()) {
            final float numEdgesReciprocal = 1.0f / voxelNumActiveEdges.get(key);
            voxelPoints.put(key, voxelPoints.get(key).mult(numEdgesReciprocal));
        }

        // for each edge that intersects the surface, construct a quad from the points inside its neighbouring voxels
        for (int i = 0; i < edgeStartCoords.size(); i++) {
            final Int3 voxelCoord = edgeStartCoords.get(i);

            final int maxCoordX = voxelCoord.x();
            final int maxCoordY = voxelCoord.y();
            final int maxCoordZ = voxelCoord.z();

            final int minCoordX = maxCoordX - 1;
            final int minCoordY = maxCoordY - 1;
            final int minCoordZ = maxCoordZ - 1;

            final Int3 vertexCoord0;
            final Int3 vertexCoord1;
            final Int3 vertexCoord2;
            final Int3 vertexCoord3;

            switch (edgeAxes.get(i)) {
                case X:
                    vertexCoord0 = new Int3(maxCoordX, minCoordY, minCoordZ);
                    vertexCoord1 = new Int3(maxCoordX, maxCoordY, minCoordZ);
                    vertexCoord2 = new Int3(maxCoordX, maxCoordY, maxCoordZ);
                    vertexCoord3 = new Int3(maxCoordX, minCoordY, maxCoordZ);
                    break;
                case Y:
                    vertexCoord0 = new Int3(minCoordX, maxCoordY, minCoordZ);
                    vertexCoord1 = new Int3(minCoordX, maxCoordY, maxCoordZ);
                    vertexCoord2 = new Int3(maxCoordX, maxCoordY, maxCoordZ);
                    vertexCoord3 = new Int3(maxCoordX, maxCoordY, minCoordZ);
                    break;
                default:
                    vertexCoord0 = new Int3(minCoordX, minCoordY, maxCoordZ);
                    vertexCoord1 = new Int3(maxCoordX, minCoordY, maxCoordZ);
                    vertexCoord2 = new Int3(maxCoordX, maxCoordY, maxCoordZ);
                    vertexCoord3 = new Int3(minCoordX, maxCoordY, maxCoordZ);
                    break;
            }

            final Float3 vertex0 = voxelPoints.get(vertexCoord0);
            final Float3 vertex1 = voxelPoints.get(vertexCoord1);
            final Float3 vertex2 = voxelPoints.get(vertexCoord2);
            final Float3 vertex3 = voxelPoints.get(vertexCoord3);

            // make sure the quad has the right orientation
            if (edgesPositiveDir.get(i)) {
                addQuadFace(vertex0, vertex1, vertex2, vertex3, smoothGroup, deDupMap, mesh);
            } else {
                addQuadFace(vertex0, vertex3, vertex2, vertex1, smoothGroup, deDupMap, mesh);
            }
        }
    }

    @Override
    public String toString() {
        return "Surface Nets";
    }
}

package crystalspider.justverticalslabs.model.utils;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Utilities to handle Vertices.
 */
public class VertexUtils {
  /**
   * X vertex element offset.
   */
  public static final short X_OFFSET = 0;
  /**
   * Y vertex element offset.
   */
  public static final short Y_OFFSET = 1;
  /**
   * Z vertex element offset.
   */
  public static final short Z_OFFSET = 2;
  /**
   * U vertex element offset.
   */
  public static final short U_OFFSET = 4;
  /**
   * V vertex element offset.
   */
  public static final short V_OFFSET = 5;

  /**
   * Number of vertices for a single face.
   */
  public static final int VERTEX_COUNT = DefaultVertexFormat.BLOCK.getVertexSize();
  /**
   * Number of elements in a single vertex.
   */
  public static final int VERTEX_SIZE = DefaultVertexFormat.BLOCK.getIntegerSize();

  /**
   * 
   * 
   * @param vertices
   * @param referringBlock
   * @return
   */
  public static final boolean isInternalFace(int[] vertices, boolean referringBlock) {
    boolean flag = true;
    for (int vertexIndex = 0; vertexIndex < VERTEX_COUNT && flag; vertexIndex += VERTEX_SIZE) {
      float x = Float.intBitsToFloat(vertices[vertexIndex + X_OFFSET]);
      float y = Float.intBitsToFloat(vertices[vertexIndex + Y_OFFSET]);
      float z = Float.intBitsToFloat(vertices[vertexIndex + Z_OFFSET]);
      flag = x > 0 && x < 1 && x != 0.5 && y > 0 && y < (referringBlock ? 1 : 0.5) && y != 0.5 && z > 0 && z < 1 && z != 0.5;
    }
    return flag;
  }

  /**
   * Returns the updated vertices.
   * Updates UV vertex elements to use the new sprite instead of the old one.
   * Updates the Y Position vertex element to make the new sprite adhere to the correct shape.
   * 
   * @param vertices
   * @param referredVertices
   * @param oldSprite
   * @param newSprite
   * @param faceUp
   * @return updated vertices.
   */
  public static final int[] updateVertices(int[] vertices, int[] referredVertices, TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, boolean faceUp) {
    int[] updatedVertices = vertices.clone();
    for (int vertexIndex = 0; vertexIndex < VERTEX_COUNT; vertexIndex += VERTEX_SIZE) {
      float y = Float.intBitsToFloat(referredVertices[vertexIndex + Y_OFFSET]);
      // Lower only top face since RenderType CutoutMipped will remove extra transparent texture bits that go out of the shape.
      if (faceUp && y > 0 && y < 1 && y != 0.5) {
        updatedVertices[vertexIndex + Y_OFFSET] = Float.floatToRawIntBits(y < 0.5 ? y + 0.5F : y);
      }
      updatedVertices[vertexIndex + U_OFFSET] = changeUVertexElementSprite(oldSprite, newSprite, updatedVertices[vertexIndex + U_OFFSET]);
      updatedVertices[vertexIndex + V_OFFSET] = changeVVertexElementSprite(oldSprite, newSprite, updatedVertices[vertexIndex + V_OFFSET]);
    }
    return updatedVertices;
  }

  /**
   * Changes the U Vertex value to reference the new sprite.
   * Uses the same formula as {@link net.minecraft.client.renderer.block.model.FaceBakery#fillVertex(int[], int, Vector3f, TextureAtlasSprite, BlockFaceUV) FaceBakery.fillVertex(int[], int, Vector3f, TextureAtlasSprite, BlockFaceUV)} (index = i + 4),
   * but gets the double parameter from {@link #getUV(float, float, float)}.
   * 
   * @param oldSprite
   * @param newSprite
   * @param vertex
   * @return updated U Vertex.
   */
  private static final int changeUVertexElementSprite(TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, int vertex) {
    return Float.floatToRawIntBits(newSprite.getU(getUV(Float.intBitsToFloat(vertex), oldSprite.getU0(), oldSprite.getU1())));
  }

  /**
   * Changes the V Vertex value to reference the new sprite.
   * Uses the same formula as {@link net.minecraft.client.renderer.block.model.FaceBakery#fillVertex(int[], int, Vector3f, TextureAtlasSprite, BlockFaceUV) FaceBakery.fillVertex(int[], int, Vector3f, TextureAtlasSprite, BlockFaceUV)} (index = i + 5),
   * but gets the double parameter from {@link #getUV(float, float, float)}.
   * 
   * @param oldSprite
   * @param newSprite
   * @param vertex
   * @return updated V Vertex.
   */
  private static final int changeVVertexElementSprite(TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, int vertex) {
    return Float.floatToRawIntBits(newSprite.getV(getUV(Float.intBitsToFloat(vertex), oldSprite.getV0(), oldSprite.getV1())));
  }

  /**
   * Inverse formula for {@link TextureAtlasSprite#getU(double)} and {@link TextureAtlasSprite#getV(double)}.
   * Given the internal values of the sprite, returns the parameter needed to get the given result for either of the above functions.
   * 
   * @param uv - {@link TextureAtlasSprite#getU(double)} or {@link TextureAtlasSprite#getV(double)} result.
   * @param uv0 - {@link TextureAtlasSprite#u0} or {@link TextureAtlasSprite#v0}.
   * @param uv1 - {@link TextureAtlasSprite#u1} or {@link TextureAtlasSprite#v1}.
   * @return parameter to get the given result with either {@link TextureAtlasSprite#getU(double)} or {@link TextureAtlasSprite#getV(double)}.
   */
  private static final double getUV(float uv, float uv0, float uv1) {
    return (double) (uv - uv0) * 16.0F / (uv1 - uv0);
  }
}

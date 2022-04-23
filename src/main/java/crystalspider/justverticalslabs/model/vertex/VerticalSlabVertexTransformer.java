package crystalspider.justverticalslabs.model.vertex;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.VertexTransformer;

/**
 * TODO:
 * Crop textures rather than stretching them.
 * Emit light if referred block emits light.
 */
public class VerticalSlabVertexTransformer extends VertexTransformer {
  /**
   * 
   */
  private final BakedQuad referringBakedQuad;
  /**
   * 
   */
  private int vertices = 0;
  /**
   * 
   */
  private int elements = 0;

  public VerticalSlabVertexTransformer(BakedQuadBuilder quadBuilder, BakedQuad referringBakedQuad) {
    super(quadBuilder);
    this.referringBakedQuad = referringBakedQuad;
  }

  /**
   * 
   * 
   * @param
   * @param
   */
  @Override
  public void put(int element, float... vertexData) {
    VertexFormat vertexFormat = getVertexFormat();
    if (vertexFormat.getElements().get(element).getUsage() == VertexFormatElement.Usage.UV) {
      LightUtil.unpack(referringBakedQuad.getVertices(), vertexData, vertexFormat, vertices, element);
    }
    elements++;
    if (elements == vertexFormat.getElements().size()) {
      vertices++;
      elements = 0;
    }
    super.put(element, vertexData);
  }
}

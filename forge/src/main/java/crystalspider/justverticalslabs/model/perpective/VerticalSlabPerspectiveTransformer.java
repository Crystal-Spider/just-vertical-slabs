package crystalspider.justverticalslabs.model.perpective;

import static java.util.Map.entry;

import java.util.Map;

import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;

/**
 * Holder of {@link Transformation perspective transformations} for Vertical Slab Items.
 */
public class VerticalSlabPerspectiveTransformer {
  /**
   * Map of all the Vertical Slab Item {@link Transformation transformations} associated with their respective {@link TransformType}.
   */
  private static final Map<TransformType, ItemTransform> TRANSFORMATIONS = Map.ofEntries(
    entry(TransformType.GUI, getItemTransform(new Vector3f(0.1F, -0.05F, 0), new Vector3f(30, 45, 0), new Vector3f(0.625F, 0.625F, 0.625F))),
    entry(TransformType.GROUND, getItemTransform(new Vector3f(0, 0.015F, 0.075F), Vector3f.ZERO, new Vector3f(0.25F, 0.25F, 0.25F))),
    entry(TransformType.FIXED, getItemTransform(Vector3f.ZERO, Vector3f.ZERO, new Vector3f(0.5F, 0.5F, 0.5F))),
    entry(TransformType.FIRST_PERSON_LEFT_HAND, getItemTransform(Vector3f.ZERO, new Vector3f(0, 315, 0), new Vector3f(0.4F, 0.4F, 0.4F))),
    entry(TransformType.FIRST_PERSON_RIGHT_HAND, getItemTransform(Vector3f.ZERO, new Vector3f(0, 315, 0), new Vector3f(0.4F, 0.4F, 0.4F))),
    entry(TransformType.THIRD_PERSON_LEFT_HAND, getItemTransform(new Vector3f(-0.125F, 0.006F, 0), new Vector3f(75, 315, 0), new Vector3f(0.375F, 0.375F, 0.375F))),
    entry(TransformType.THIRD_PERSON_RIGHT_HAND, getItemTransform(new Vector3f(0, 0.16F, 0), new Vector3f(75, 135, 0), new Vector3f(0.375F, 0.375F, 0.375F)))
  );

  /**
   * Returns the correct Vertical Slab Item {@link Transformation} given the {@link TransformType}.
   * 
   * @param transformType - {@link TransformType}.
   * @return one of {@link #TRANSFORMATIONS Vertical Slab Item Transformations}.
   */
  public static final ItemTransform getTransform(TransformType transformType) {
    return TRANSFORMATIONS.getOrDefault(transformType, ItemTransform.NO_TRANSFORM);
  }

  public static final ItemTransform getItemTransform(Vector3f translation, Vector3f rotation, Vector3f scale) {
    return new ItemTransform(rotation, translation, scale);
  }
}

package crystalspider.justverticalslabs.model.perpective;

import static java.util.Map.entry;

import java.util.Map;

import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraftforge.common.util.TransformationHelper;

/**
 * Holder of {@link Transformation perspective transformations} for Vertical Slab Items.
 */
public class VerticalSlabPerspectiveTransformer {
  /**
   * Map of all the Vertical Slab Item {@link Transformation transformations} associated with their respective {@link TransformType}.
   */
  private static final Map<TransformType, Transformation> TRANSFORMATIONS = Map.ofEntries(
    entry(TransformType.GUI, getTransformation(new Vector3f(0.1F, -0.05F, 0), new Vector3f(30, 45, 0), new Vector3f(0.625F, 0.625F, 0.625F))),
    entry(TransformType.GROUND, getTransformation(new Vector3f(0, 0.015F, 0.075F), Vector3f.ZERO, new Vector3f(0.25F, 0.25F, 0.25F))),
    entry(TransformType.FIXED, getTransformation(Vector3f.ZERO, Vector3f.ZERO, new Vector3f(0.5F, 0.5F, 0.5F))),
    entry(TransformType.FIRST_PERSON_LEFT_HAND, getTransformation(Vector3f.ZERO, new Vector3f(0, 315, 0), new Vector3f(0.4F, 0.4F, 0.4F))),
    entry(TransformType.FIRST_PERSON_RIGHT_HAND, getTransformation(Vector3f.ZERO, new Vector3f(0, 135, 0), new Vector3f(0.4F, 0.4F, 0.4F))),
    entry(TransformType.THIRD_PERSON_LEFT_HAND, getTransformation(new Vector3f(0F, 0.175F, 0), new Vector3f(75, 315, 0), new Vector3f(0.375F, 0.375F, 0.375F))),
    entry(TransformType.THIRD_PERSON_RIGHT_HAND, getTransformation(new Vector3f(0F, 0.175F, 0), new Vector3f(75, 135, 0), new Vector3f(0.375F, 0.375F, 0.375F)))
  );

  /**
   * Returns a new {@link Transformation} given the translation, rotation and scale {@link Vector3f vectors}.
   * 
   * @param translation - translation {@link Vector3f}.
   * @param rotation - rotation {@link Vector3f}, in degrees.
   * @param scale - scale {@link Vector3f}.
   * @return a new {@link Transformation}.
   */
  private static final Transformation getTransformation(Vector3f translation, Vector3f rotation, Vector3f scale) {
    return new Transformation(translation, TransformationHelper.quatFromXYZ(rotation, true), scale, null);
  }

  /**
   * Returns the correct Vertical Slab Item {@link Transformation} given the {@link TransformType}.
   * 
   * @param transformType - {@link TransformType}.
   * @return one of {@link #TRANSFORMATIONS Vertical Slab Item Transformations}.
   */
  public static final Transformation getTransformation(TransformType transformType) {
    return TRANSFORMATIONS.getOrDefault(transformType, Transformation.identity());
  }
}

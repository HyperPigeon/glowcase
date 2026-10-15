package dev.hephaestus.glowcase.mixinsupport;

import dev.hephaestus.glowcase.util.PartPoseInfo;
import org.jspecify.annotations.Nullable;
import java.util.Map;

public interface PartPosedRenderState {
	void glowcase$setPartPoses(@Nullable Map<String, PartPoseInfo> partPoses);
	@Nullable Map<String, PartPoseInfo> glowcase$getPartPoses();
}

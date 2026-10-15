package dev.hephaestus.glowcase.mixin.client;

import dev.hephaestus.glowcase.mixinsupport.PartPosedRenderState;
import dev.hephaestus.glowcase.util.PartPoseInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements PartPosedRenderState {
	@Unique private @Nullable Map<String, PartPoseInfo> glowcase$partPoses;

	@Override
	public void glowcase$setPartPoses(@Nullable Map<String, PartPoseInfo> partPoses) {
		this.glowcase$partPoses = partPoses;
	}

	@Override
	public @Nullable Map<String, PartPoseInfo> glowcase$getPartPoses() {
		return glowcase$partPoses;
	}
}

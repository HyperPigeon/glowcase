package dev.hephaestus.glowcase.client.util;

import dev.hephaestus.glowcase.mixinsupport.PartPosedRenderState;
import dev.hephaestus.glowcase.util.PartPoseInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.Map;

public class ModelPartUtil {

	@Nullable
	public static ModelPart getModelRoot(Entity entity) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) return null;
		if (entity == null) return null;

		EntityRenderer<?, ?> renderer = minecraft.getEntityRenderDispatcher().getRenderer(entity);
		if (renderer instanceof LivingEntityRenderer<?, ?, ?> living) {
			return living.getModel().root();
		}
		return null;
	}

	public static void applyPartPoses(Model<?> model, Object state) {
		if (!(state instanceof PartPosedRenderState posed)) {
			return;
		}
		Map<String, PartPoseInfo> partPoses = posed.glowcase$getPartPoses();
		if (partPoses == null || partPoses.isEmpty()) {
			return;
		}

		walk("root", model.root(), partPoses);
	}

	private static void walk(String path, ModelPart parent, Map<String, PartPoseInfo> partPoses) {
		Map<String, ModelPart> children = parent.children;
		for (Map.Entry<String, ModelPart> entry : children.entrySet()) {
			ModelPart part = entry.getValue();
			String name = entry.getKey();
			String partPath = path + "." + name;
			if (part == null || name == null) {
				continue;
			}
			if(partPoses.containsKey(partPath)) {
				PartPoseInfo partPoseInfo = partPoses.get(partPath);

				Vector3fc offset = partPoseInfo.offset();
				Vector3fc rotation = partPoseInfo.rotation();

				part.x += offset.x();
				part.y += offset.y();
				part.z += offset.z();
				part.xRot += rotation.x() * Mth.DEG_TO_RAD;
				part.yRot += rotation.y() * Mth.DEG_TO_RAD;
				part.zRot += rotation.z() * Mth.DEG_TO_RAD;
			}
			walk(partPath,part, partPoses);
		}
	}

}

package dev.hephaestus.glowcase.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.hephaestus.glowcase.client.util.ModelPartUtil;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelFeatureRenderer.class)
public class ModelFeatureRendererMixin {
	@WrapOperation(method = "prepareModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;setupAnim(Ljava/lang/Object;)V"))
	private void applyPartPoses(Model<?> model, Object state, Operation<Void> original) {
		original.call(model, state);
		ModelPartUtil.applyPartPoses(model, state);
	}
}

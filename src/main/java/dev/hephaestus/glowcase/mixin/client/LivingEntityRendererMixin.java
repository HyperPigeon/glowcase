package dev.hephaestus.glowcase.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.hephaestus.glowcase.client.util.ModelPartUtil;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@WrapOperation(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;)V"))
	private void applyPartPoses(EntityModel<?> model, Object state, Operation<Void> original) {
		original.call(model, state);
		ModelPartUtil.applyPartPoses(model, state);
	}
}

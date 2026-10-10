package dev.hephaestus.glowcase.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record PartPoseInfo(String name, Vector3fc offset, Vector3fc rotation) {
	public static final Codec<PartPoseInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(PartPoseInfo::name),
		ExtraCodecs.VECTOR3F.lenientOptionalFieldOf("offset", new Vector3f()).forGetter(PartPoseInfo::offset),
		ExtraCodecs.VECTOR3F.lenientOptionalFieldOf("rotation", new Vector3f()).forGetter(PartPoseInfo::rotation)
	).apply(instance, PartPoseInfo::new));

	public static final StreamCodec<ByteBuf, PartPoseInfo> PACKET_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, PartPoseInfo::name,
		ByteBufCodecs.VECTOR3F, PartPoseInfo::offset,
		ByteBufCodecs.VECTOR3F, PartPoseInfo::rotation,
		PartPoseInfo::new
	);

	public float getRotationAtAxis(int axis){
		if(axis == 0) {
			return rotation().x();
		}
		else if (axis == 1) {
			return rotation().y();
		}
		else {
			return rotation().z();
		}
	}

	public float getOffsetAtAxis(int axis){
		if(axis == 0) {
			return offset().x();
		}
		else if (axis == 1) {
			return offset().y();
		}
		else {
			return offset().z();
		}
	}

	public PartPoseInfo updateRotationAtAxis(int axis, float newRotation) {
		Vector3f newRotationVec;
		if(axis == 0) {
			newRotationVec = new Vector3f(newRotation, rotation.y(), rotation.z());
			return new PartPoseInfo(name(), offset(), newRotationVec);
		}
		else if (axis == 1) {
			newRotationVec = new Vector3f(rotation.x(), newRotation, rotation.z());
			return new PartPoseInfo(name(), offset(), newRotationVec);
		}
		else {
			newRotationVec = new Vector3f(rotation.x(), rotation().y(), newRotation);
			return new PartPoseInfo(name(), offset(), newRotationVec);
		}
	}

	public PartPoseInfo updateOffsetAtAxis(int axis, float newOffset) {
		Vector3f newOffsetVec;
		if(axis == 0) {
			newOffsetVec = new Vector3f(newOffset, offset().y(), offset().z());
			return new PartPoseInfo(name(), newOffsetVec, rotation());
		}
		else if (axis == 1) {
			newOffsetVec = new Vector3f(offset().x(), newOffset, offset().z());
			return new PartPoseInfo(name(), newOffsetVec, rotation());
		}
		else {
			newOffsetVec = new Vector3f(offset().x(), offset().y(), newOffset);
			return new PartPoseInfo(name(), newOffsetVec, rotation());
		}
	}

	public PartPoseInfo updateRotation(Vector3f newRotation) {
		return new PartPoseInfo(name(), offset(), newRotation);
	}

	public PartPoseInfo updateOffset(Vector3f newOffset) {
		return new PartPoseInfo(name(), newOffset, rotation());
	}

}

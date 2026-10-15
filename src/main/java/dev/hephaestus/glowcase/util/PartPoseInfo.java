package dev.hephaestus.glowcase.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
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

	public float getRotationAtAxis(Direction.Axis axis) {
		return switch (axis) {
			case X -> rotation().x();
			case Y -> rotation().y();
			case Z -> rotation().z();
		};
	}

	public float getOffsetAtAxis(Direction.Axis axis) {
		return switch (axis) {
			case X -> offset().x();
			case Y -> offset().y();
			case Z -> offset().z();
		};
	}

	public PartPoseInfo updateRotationAtAxis(Direction.Axis axis, float newRotation) {
		final Vector3f newRotationVec = switch (axis) {
			case X -> new Vector3f(newRotation, rotation.y(), rotation.z());
			case Y -> new Vector3f(rotation.x(), newRotation, rotation.z());
			case Z -> new Vector3f(rotation.x(), rotation().y(), newRotation);
		};
		return new PartPoseInfo(name(), offset(), newRotationVec);
	}

	public PartPoseInfo updateOffsetAtAxis(Direction.Axis axis, float newOffset) {
		Vector3f newOffsetVec = switch (axis) {
			case X -> new Vector3f(newOffset, offset().y(), offset().z());
			case Y -> new Vector3f(offset().x(), newOffset, offset().z());
			case Z -> new Vector3f(offset().x(), offset().y(), newOffset);
		};
		return new PartPoseInfo(name(), newOffsetVec, rotation());
	}

	public PartPoseInfo updateRotation(Vector3f newRotation) {
		return new PartPoseInfo(name(), offset(), newRotation);
	}

	public PartPoseInfo updateOffset(Vector3f newOffset) {
		return new PartPoseInfo(name(), newOffset, rotation());
	}

}

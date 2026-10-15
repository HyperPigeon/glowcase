package dev.hephaestus.glowcase.client.gui.screen.ingame;

import dev.hephaestus.glowcase.block.entity.DisplayBlockEntity;
import dev.hephaestus.glowcase.block.entity.EntityDisplayBlockEntity;
import dev.hephaestus.glowcase.client.gui.widget.ingame.number.Vec3FieldsWidget;
import dev.hephaestus.glowcase.client.util.ModelPartUtil;
import dev.hephaestus.glowcase.packet.C2SEditEntityDisplayBlock;
import dev.hephaestus.glowcase.util.PartPoseInfo;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityDisplayEditScreen extends DisplayBlockEditScreen {

	private final EntityDisplayBlockEntity entityDisplay;
	private final Set<String> expandedParts = new HashSet<>();
	private boolean expanded = false;
	private final int STARTING_X = 10;
	private final int STARTING_Y = 200;
	private int scrollOffset = 0;
	private int maxScroll = 0;


	public EntityDisplayEditScreen(DisplayBlockEntity displayBlock) {
		super(displayBlock);
		this.entityDisplay = (EntityDisplayBlockEntity) displayBlock;
	}

	@Override
	public void init() {
		super.init();

		int bottom = STARTING_Y + 22;
		Entity displayEntity = this.entityDisplay.getDisplayEntity();
		if(displayEntity != null) {
			this.addRenderableWidget(Button.builder(
				Component.literal(this.expanded ? "▼ " : "▶ ").append(Component.translatable("gui.glowcase.model_parts")),
				_ -> {
					this.expanded = !this.expanded;
					this.rebuildWidgets();
				}
			).bounds(STARTING_X, STARTING_Y, 200, 20).build());

			ModelPart root = ModelPartUtil.getModelRoot(displayEntity);
			if (this.expanded && root != null) {
				bottom = this.addPartRows("root", root, bottom, 0);
			}
		}

		this.maxScroll = Math.max(0, bottom + 10 - this.height);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0, this.maxScroll);
		this.offsetWidgets(-this.scrollOffset);


	}

	private int addPartRows(String path, ModelPart parent, int y, int depth) {
		int indent = Math.min(depth, 6) * 10;
		for (Map.Entry<String, ModelPart> entry : parent.children.entrySet()) {
			String name = entry.getKey();
			String partPath = path + "." + name;
			boolean partExpanded = this.expandedParts.contains(partPath);
			String displayName = name.replace("_", " ");
			displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);

			this.addRenderableWidget(Button.builder(Component.literal((partExpanded ? "▼ " : "▶ ") + displayName), _ -> {
				if (!this.expandedParts.remove(partPath)) this.expandedParts.add(partPath);
				this.rebuildWidgets();
			}).bounds(STARTING_X + 10 + indent, y, 190 - indent, 20).build());

			y += 22;
			if (!partExpanded) continue;

			for (int i = 0; i <= 1; i++) {
				boolean isRotation = i == 0;
				Component label = Component.translatable(isRotation ? "gui.glowcase.rotation" : "gui.glowcase.offset");
				this.addRenderableWidget(new StringWidget(STARTING_X + 20, y + 5, this.font.width(label), 9, label, this.font));

				PartPoseInfo partPoseInfo = this.entityDisplay.getPartPostInfo(partPath);
				this.addRenderableWidget(Vec3FieldsWidget.builder(this.font, new Vec3(isRotation ? partPoseInfo.rotation() : partPoseInfo.offset()))
					.setPos(STARTING_X + 70, y).
					setWidth(246).setHeight(18)
					.setRotation(isRotation)
					.setOnValueChange(vec3 -> {
					PartPoseInfo current = this.entityDisplay.getPartPostInfo(partPath);
						this.entityDisplay.setPartPoseInfo(
							partPath,
							isRotation ? current.withRotation(vec3.toVector3f()) : current.withOffset(vec3.toVector3f())
						);
					editDisplayBlock();
				}).build());
				y += 22;
			}
			y = this.addPartRows(partPath, entry.getValue(), y, depth + 1);
		}
		return y;

	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		int newOffset = Mth.clamp(this.scrollOffset - (int) (scrollY * 20), 0, this.maxScroll);
		if (newOffset != this.scrollOffset) {
			this.offsetWidgets(this.scrollOffset - newOffset);
			this.scrollOffset = newOffset;
			return true;
		}
		return super.mouseScrolled(x, y, scrollX, scrollY);
	}

	private void offsetWidgets(int scrollOffset) {
		for (GuiEventListener child : this.children()) {
			if (child instanceof AbstractWidget widget) widget.setY(widget.getY() + scrollOffset);
		}
	}

	@Override
	public CustomPacketPayload getUpdatePayload() {
		return C2SEditEntityDisplayBlock.of(blockEntity);
	}

}

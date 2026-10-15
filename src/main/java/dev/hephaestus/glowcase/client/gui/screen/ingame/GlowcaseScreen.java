package dev.hephaestus.glowcase.client.gui.screen.ingame;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public abstract class GlowcaseScreen extends Screen {
	// Scroll handler
	private long lastScroll = Util.getMillis();
	private int scrollOffsetX = 0;
	private int scrollOffsetY = 0;
	private int maxScrollX = 0;
	private int maxScrollY = 0;

	protected GlowcaseScreen() {
		super(Component.empty());
	}

	protected GlowcaseScreen(Component title) {
		super(title);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		this.extractTransparentBackground(graphics);
		this.extractBlurredBackground(graphics);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}


	/**
	 * Sets the max scroll of the UI, adjusting every element when necessary.
	 *
	 * @param maxScrollX     The maximum the pane can be scrolled on X
	 * @param maxScrollY     The maximum the pane can be scrolled on Y
	 * @param useDeltaOffset Whether to use the offset to adjust elements.
	 *                       If this is called from init, pass {@code false}.
	 */
	protected final void setMaxScroll(int maxScrollX, int maxScrollY, boolean useDeltaOffset) {
		this.maxScrollX = Math.max(maxScrollX, 0);
		this.maxScrollY = Math.max(maxScrollY, 0);

		final int newOffsetX = Mth.clamp(this.scrollOffsetX, 0, this.maxScrollX);
		final int newOffsetY = Mth.clamp(this.scrollOffsetY, 0, this.maxScrollY);

		if (useDeltaOffset) {
			this.offsetWidgets(
				newOffsetX - this.scrollOffsetX,
				newOffsetY - this.scrollOffsetY
			);
		} else {
			this.offsetWidgets(
				-newOffsetX,
				-newOffsetY
			);
		}

		this.scrollOffsetX = newOffsetX;
		this.scrollOffsetY = newOffsetY;
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		return (!this.scrollPaneFirst() && super.mouseScrolled(x, y, scrollX, scrollY)) || scrollElements(
			scrollX,
			scrollY
		);
	}

	private boolean scrollPaneFirst() {
		return Util.getMillis() - this.lastScroll < 500L;
	}

	protected boolean scrollElements(double scrollX, double scrollY) {
		int newOffsetX = Mth.clamp(this.scrollOffsetX - (int) (scrollX * 20), 0, this.maxScrollX);
		int newOffsetY = Mth.clamp(this.scrollOffsetY - (int) (scrollY * 20), 0, this.maxScrollY);
		if (newOffsetX != this.scrollOffsetX || newOffsetY != this.scrollOffsetY) {
			// TODO: consider using absolutes rather than relative?
			this.offsetWidgets(this.scrollOffsetX - newOffsetX, this.scrollOffsetY - newOffsetY);
			this.scrollOffsetX = newOffsetX;
			this.scrollOffsetY = newOffsetY;
			this.lastScroll = Util.getMillis();
			return true;
		}
		return false;
	}

	/**
	 * Adjusts every element to be scrolled. By default, this adjusts every abstract widget.
	 */
	protected void offsetWidgets(int deltaX, int deltaY) {
		for (GuiEventListener child : this.children()) {
			if (child instanceof AbstractWidget widget) {
				widget.setPosition(
					widget.getX() + deltaX,
					widget.getY() + deltaY
				);
			}
		}
	}
}

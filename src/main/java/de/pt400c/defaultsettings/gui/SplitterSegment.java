package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SplitterSegment extends Segment {
	
	private final LeftMenu menu;
	private final float origX;
	
	public SplitterSegment(GuiScreen gui, float posX, float posY, int height, LeftMenu menu) {
		super(gui, posX, posY, 1, height, false);
		this.menu = menu;
		this.origX = posX;

	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		this.posX = origX - this.menu.offs;
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
		glShadeModel(GL_SMOOTH);

		Segment.drawGradientCircle((float) this.getPosX(), (float) this.getPosY() + 4, 6, 270, 75, 0xffaaaaaa, 0x00ffffff);

		Segment.drawGradientCircle((float) this.getPosX(), (float) this.getPosY() + this.getHeight() - 4, 6, 0, 75, 0xffaaaaaa, 0x00ffffff);

		Segment.drawGradient(this.getPosX(), this.getPosY() + 4, this.getPosX() + 6, this.getPosY() + this.getHeight() - 4, 0xffaaaaaa, 0x00ffffff, 0);

		glShadeModel(GL_FLAT);
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xffbebebe, true, null, false);
	}
}
package de.pt400c.defaultsettings.gui;

import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;

@SideOnly(Side.CLIENT)
public class TextSegment extends Segment {

	public final int color;
	public final String text;
	private final int offset;
	
	public TextSegment(GuiScreen gui, float posX, float posY, int width, int height, String text, int color, boolean popup) {
		this(gui, posX, posY, width, height, text, color, 9, popup);
	}
	
	public TextSegment(GuiScreen gui, float posX, float posY, int width, int height, String text, int color, int offset, boolean popup) {
		super(gui, posX, posY, width, height, popup);
		
		this.color = color;
		this.text = text;
		this.offset = offset;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		
		GL11.glPushMatrix();
     	GL11.glEnable(GL11.GL_BLEND);
     	GL11.glDisable(GL11.GL_ALPHA_TEST);
     	OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
     	int offsetY = 0;
     	
     	for(String line : this.text.split("\n")) {
     		this.drawString(line, (float) this.getPosX(), (float) this.getPosY() + offsetY, this.color, false);
     		offsetY += this.offset;
     	}
     	
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
		
	}
}

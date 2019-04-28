package de.pt400c.defaultsettings.gui;

import java.awt.Color;
import java.util.function.Function;
import org.lwjgl.opengl.GL11;
import de.pt400c.defaultsettings.GuiConfig;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;

public class QuitButtonSegment extends ButtonSegment {

	public QuitButtonSegment(GuiScreen gui, float posX, float posY, int width, int height, Function<ButtonSegment, Boolean> function, boolean popup) {
		super(gui, posX, posY, "X", function, width, height, 0, popup);
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		
		float alpha = !this.isPopupSegment ? 0 : ((GuiConfig) this.gui).popupField == null ? 1 : ((GuiConfig) this.gui).popupField.getWindow().alphaRate;
		
		Segment.drawRect2(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xffbe2e2c : 0xffd85755, alpha);
		GL11.glPushMatrix();
     	GL11.glEnable(GL11.GL_BLEND);
     	OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		MC.fontRenderer.drawString(this.title, (float) (posX + this.getWidth() / 2 - 2), (float) (posY + this.getHeight() / 2 - 4), calcAlpha(0xffffffff, alpha).getRGB(), false);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		
	}
	
	public static Color calcAlpha(int color, float alpha) {
		return new Color(getRed(color), getGreen(color), getBlue(color), GuiConfig.clamp((int) ((1 - alpha) * 255F), 4, 255));
	}
	
	public static int getRed(int value) {
        return (value >> 16) & 0xFF;
    }
	
	public static int getGreen(int value) {
        return (value >> 8) & 0xFF;
    }
	
	public static int getBlue(int value) {
        return value & 0xFF;
    }
	
	public static int getAlpha(int value) {
        return (value >> 24) & 0xff;
    }


}
package de.pt400c.defaultsettings.gui;

import java.util.function.Function;

import com.mojang.blaze3d.platform.GlStateManager;

import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL11.*;
import de.pt400c.defaultsettings.gui.MathUtil.Vec2f;

@OnlyIn(Dist.CLIENT)
public class QuitButtonSegment extends ButtonSegment {

	private final float offs;
	private final Function<GuiConfig, Integer> posXF;
	
	public QuitButtonSegment(Screen gui, Function<GuiConfig, Integer> posX, float posY, int width, int height, Function<ButtonSegment, Boolean> function, float offs, boolean popup) {
		super(gui, posX.apply((GuiConfig) gui), posY, null, function, width, height, 0, popup);
		this.offs = offs;
		this.posXF = posX;
	}
	
	public QuitButtonSegment(Screen gui, float posX, float posY, int width, int height, Function<ButtonSegment, Boolean> function, float offs, boolean popup) {
		super(gui, posX, posY, null, function, width, height, 0, popup);
		this.offs = offs;
		posXF = null;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		if(resized != this.resized_mark && posXF != null) {
			posX = posXF.apply((GuiConfig) this.gui);
			this.resized_mark = resized;
		}
		
		glPushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		drawRectRoundedCorners(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xffbe2e2c : 0xffd85755, this.isPopupSegment ? 3.0F : 5.0F);
		
		final int scaleFactor = (int) scaledFactor;
     	drawLine2D_2(1, 1, 1, 1, scaleFactor, this.isPopupSegment ? 3.0F : 5.0F, new Vec2f((float) posX + width / 2 - this.offs, (float) posY + height / 2 - this.offs), new Vec2f((float) posX + width / 2 + this.offs, (float) posY + height / 2 + this.offs));
     	drawLine2D_2(1, 1, 1, 1, scaleFactor, this.isPopupSegment ? 3.0F : 5.0F, new Vec2f((float) posX + width / 2 + this.offs, (float) posY + height / 2 - this.offs), new Vec2f((float) posX + width / 2 - this.offs, (float) posY + height / 2 + this.offs));
		GlStateManager.disableBlend();
		GlStateManager.enableTexture();
		glPopMatrix();
	}
}
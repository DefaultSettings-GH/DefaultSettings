package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeftMenu extends Segment {
	
	private List<Segment> children = new ArrayList<>();
	public float offsetTick = 2 * MathUtil.PI;
	private boolean selected;
	private float extend = 0;
	public float offs = 0;
	public final float maxOffTick = 2 * MathUtil.PI;
	private final Function<GuiConfig, Integer> heightF;

	public LeftMenu(Screen gui, float posX, float posY, float width, Function<GuiConfig, Integer> height) {
		super(gui, posX, posY, width, height.apply((GuiConfig) gui), false);
		this.heightF = height;
	}
	
	@Override
    public void render(int mouseX, int mouseY, float partialTicks) {
		
		if(resized != this.resized_mark) {
			height = heightF.apply((GuiConfig) this.gui);
			this.resized_mark = resized;
		}
		
		this.selected = !this.isSelected(mouseX, mouseY);
		if (!this.selected)
			this.extend = 25;
		else
			this.extend = 0;
		
		final float triple = (float) Math.sin(0.25 * offsetTick);
		final float func = triple * triple * triple * 6;
		
		if(this.selected && offsetTick < 2 * MathUtil.PI)
			offsetTick += 0.4;
	
		else if(offsetTick > 0 && !this.selected){
			offsetTick -= 0.35;
		}
	
		offs = (float) func * 5;
        synchronized (this.children) {
            this.children.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));

            if(((GuiConfig) this.gui).popupField == null)
            	this.children.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
        }

	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= this.getPosX() && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() + this.extend && mouseY < this.getPosY() + this.getHeight();
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		synchronized (this.children) {
			for (Segment segment : children) {
				if (segment.mouseClicked(mouseX, mouseY, mouseButton))
					return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean mouseDragged(int p_mouseDragged_1_, int p_mouseDragged_3_, int p_mouseDragged_5_) {
		synchronized (this.children) {
			for (Segment segment : this.children) {
				if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_))
					break;
				
			}
		}
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
	}
	
	@Override
	public boolean mouseScrolled(float p_mouseScrolled_1_) {
		synchronized (this.children) {
			for (Segment segment : this.children) 
				if (segment.mouseScrolled(p_mouseScrolled_1_)) 
					return true;
				
		}
		return false;
	}
	
	@Override
	public boolean mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_3_, int p_mouseReleased_5_) {
		synchronized (this.children) {
			for (Segment segment : this.children) {
				if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_))
					return true;

			}
		}
		return false;
	}

	public LeftMenu addChild(Segment segment) {
		synchronized (this.children) {
			this.children.add(segment.setPos(this.posX + segment.posX, this.posY + segment.posY));
			segment.init();
		}
		return this;
	}
	
	public List<Segment> getChildren() {
		return this.children;
	}
}
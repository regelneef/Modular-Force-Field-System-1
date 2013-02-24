package mffs.client.gui;

import mffs.common.ModularForceFieldSystem;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiMFFS extends GuiContainer
{
	public enum SlotType
	{
		NONE, BATTERY, LIQUID
	}

	public static final int METER_HEIGHT = 49;
	public static final int METER_WIDTH = 14;

	protected GuiTextField textFieldFrequency;

	protected int containerWidth;
	protected int containerHeight;

	public GuiMFFS(Container par1Container)
	{
		super(par1Container);
		this.ySize = 217;
	}

	@Override
	public void initGui()
	{
		super.initGui();

	}

	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);

		/**
		 * Everytime a key is typed, try to reset the frequency.
		 */
		this.textFieldFrequency.textboxKeyTyped(par1, par2);

		try
		{
			short newFrequency = (short) Math.max(0, Short.parseShort(this.textFieldFrequency.getText()));
			this.textFieldFrequency.setText(newFrequency + "");

			/*
			 * if (((IItemFrequency) this.itemStack.getItem()).getFrequency(this.itemStack) !=
			 * newFrequency) { ((IItemFrequency)
			 * this.itemStack.getItem()).setFrequency(newFrequency, this.itemStack);
			 * PacketDispatcher
			 * .sendPacketToServer(PacketManager.getPacketWithID(ZhuYaoWanYi.CHANNEL,
			 * WanYiPacketType.HUO_LUAN.ordinal(), newFrequency)); }
			 */
		}
		catch (NumberFormatException e)
		{
		}
	}

	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		this.textFieldFrequency.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
	{
		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;

		int hua = this.mc.renderEngine.getTexture(ModularForceFieldSystem.GUI_DIRECTORY + "gui_base.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(hua);
		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize, this.ySize);
	}

	protected void drawSlot(int x, int y, ItemStack itemStack)
	{
		int hua = this.mc.renderEngine.getTexture(ModularForceFieldSystem.GUI_DIRECTORY + "gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(hua);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);
		// TODO: DRAW itemStack
	}

	protected void drawSlot(int x, int y, SlotType type)
	{
		int hua = this.mc.renderEngine.getTexture(ModularForceFieldSystem.GUI_DIRECTORY + "gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(hua);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

		switch (type)
		{
			default:
				break;
			case BATTERY:
			{
				this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 18, 18, 18);
				break;
			}
			case LIQUID:
			{
				this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 18 * 2, 18, 18);
				break;
			}
		}
	}

	protected void drawSlot(int x, int y)
	{
		this.drawSlot(x, y, SlotType.NONE);
	}

	protected void drawBar(int x, int y, float scale)
	{
		int hua = this.mc.renderEngine.getTexture(ModularForceFieldSystem.GUI_DIRECTORY + "gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(hua);
		/**
		 * Draw background progress bar/
		 */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 0, 22, 15);

		if (scale > 0)
		{
			/**
			 * Draw white color actual progress.
			 */
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 15, 22 - (int) (scale * 22), 15);
		}
	}

	protected void drawEnergy(int x, int y, float scale)
	{
		int hua = this.mc.renderEngine.getTexture(ModularForceFieldSystem.GUI_DIRECTORY + "gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(hua);
		/**
		 * Draw background progress bar/
		 */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);

		if (scale > 0)
		{
			/**
			 * Draw white color actual progress.
			 */
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 11, 107 - (int) (scale * 107), 11);
		}
	}

	protected void drawMeter(int x, int y, float scale, float r, float g, float b)
	{
		int hua = this.mc.renderEngine.getTexture(ModularForceFieldSystem.GUI_DIRECTORY + "gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		/**
		 * Draw the background meter.
		 */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 0, METER_WIDTH, METER_HEIGHT);

		/**
		 * Draw liquid/gas inside
		 */
		GL11.glColor4f(r, g, b, 1.0F);
		int actualScale = (int) ((METER_HEIGHT - 1) * scale);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y + (METER_HEIGHT - 1 - actualScale), 40, 49, METER_WIDTH - 1, actualScale);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		/**
		 * Draw measurement lines
		 */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 49 * 2, METER_WIDTH, METER_HEIGHT);
	}

	public void drawTooltip(int x, int y, String... toolTips)
	{
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		if (toolTips != null)
		{
			int var5 = 0;
			int var6;
			int var7;

			for (var6 = 0; var6 < toolTips.length; ++var6)
			{
				var7 = this.fontRenderer.getStringWidth((String) toolTips[var6]);

				if (var7 > var5)
				{
					var5 = var7;
				}
			}

			var6 = x + 12;
			var7 = y - 12;
			int var9 = 8;

			if (toolTips.length > 1)
			{
				var9 += 2 + (toolTips.length - 1) * 10;
			}

			if (this.guiTop + var7 + var9 + 6 > this.containerHeight)
			{
				var7 = this.containerHeight - var9 - this.guiTop - 6;
			}

			this.zLevel = 300.0F;
			int var10 = -267386864;
			this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
			this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
			this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
			this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
			this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
			int var11 = 1347420415;
			int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
			this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
			this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
			this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
			this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

			for (int var13 = 0; var13 < toolTips.length; ++var13)
			{
				String var14 = toolTips[var13];

				this.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);

				if (var13 == 0)
				{
					var7 += 2;
				}

				var7 += 10;
			}

			this.zLevel = 0.0F;

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_LIGHTING);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}
}

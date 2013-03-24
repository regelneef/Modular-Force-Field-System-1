package mffs.common.container;

import mffs.common.SlotCard;
import mffs.common.SlotHelper;
import mffs.common.tileentity.TChouQi;
import net.minecraft.entity.player.EntityPlayer;

public class CChouQi extends ContainerMFFS
{
	public CChouQi(EntityPlayer player, TChouQi tileEntity)
	{
		super(tileEntity);

		/**
		 * Focillium Input
		 */
		this.addSlotToContainer(new SlotHelper(tileEntity, 0, 9, 83));

		/**
		 * Frequency Card
		 */
		this.addSlotToContainer(new SlotCard(tileEntity, 1, 9, 41));

		/**
		 * Upgrades
		 */
		this.addSlotToContainer(new SlotHelper(tileEntity, 2, 154, 67));
		this.addSlotToContainer(new SlotHelper(tileEntity, 3, 154, 87));
		this.addSlotToContainer(new SlotHelper(tileEntity, 4, 154, 47));

		this.addPlayerInventory(player);
	}
}
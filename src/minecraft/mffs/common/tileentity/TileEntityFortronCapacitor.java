package mffs.common.tileentity;

import icbm.api.RadarRegistry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mffs.api.FortronGrid;
import mffs.api.IForceEnergyItems;
import mffs.api.IFortronCapacitor;
import mffs.api.IFortronFrequency;
import mffs.api.IFortronStorage;
import mffs.api.IPowerLinkItem;
import mffs.common.card.ItemCardSecurityLink;
import mffs.common.container.ContainerCapacitor;
import mffs.common.upgrade.ItemUpgradeCapacity;
import mffs.common.upgrade.ItemUpgradeRange;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityFortronCapacitor extends TileEntityFortron implements IFortronStorage, IFortronCapacitor
{
	public enum TransferMode
	{
		EQUALIZE, DISTRIBUTE, DRAIN, FILL
	}

	private int distributionMode = 0;
	private int transmissionRange = 20;
	private TransferMode transferMode = TransferMode.EQUALIZE;

	public TileEntityFortronCapacitor()
	{
		this.fortronTank.setCapacity(500 * LiquidContainerRegistry.BUCKET_VOLUME);
	}

	@Override
	public void initiate()
	{
		super.initiate();
		RadarRegistry.register(this);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			if (!this.isDisabled())
			{
				/**
				 * Transmit Fortrons in frequency network, evenly distributing them.
				 */
				if (this.isPoweredByRedstone() && this.ticks % 20 == 0)
				{
					Set<IFortronFrequency> machines = this.getLinkedDevices();

					if (machines.size() > 1)
					{
						/**
						 * Check spread mode. Equal, Give All, Take All
						 */
						int totalFortron = 0;
						int totalCapacity = 0;

						HashMap<IFortronFrequency, Double> distributionMap = new HashMap<IFortronFrequency, Double>();

						for (IFortronFrequency machine : machines)
						{
							if (machine != null)
							{
								totalFortron += machine.getFortronEnergy();
								totalCapacity += machine.getFortronCapacity();
							}
						}

						if (totalFortron > 0 && totalCapacity > 0)
						{
							switch (this.transferMode)
							{
								case EQUALIZE:
								{
									for (IFortronFrequency machine : machines)
									{
										if (machine != null)
										{
											double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
											int amountToSet = (int) (totalFortron * capacityPercentage);
											machine.setFortronEnergy(amountToSet);
										}
									}

									break;
								}
								case DISTRIBUTE:
								{

									for (IFortronFrequency machine : machines)
									{
										if (machine != null)
										{
											int amountToSet = (int) (totalFortron / machines.size());
											machine.setFortronEnergy(amountToSet);
										}
									}
									break;
								}
								case DRAIN:
								{
									int remainingFortron = totalFortron;

									for (IFortronFrequency machine : machines)
									{
										if (machine != null)
										{
											double capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
											int amountToSet = (int) (totalFortron * capacityPercentage);
											machine.setFortronEnergy(amountToSet);
											remainingFortron -= amountToSet;
										}
									}
									break;
								}
								case FILL:
									break;
							}
						}
					}

					this.setActive(true);
				}
				else
				{
					this.setActive(false);
				}

				/**
				 * Packet Update for Client only when GUI is open.
				 */
				if (this.ticks % 4 == 0 && this.playersUsing > 0)
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 15);
				}
			}
		}
	}

	/**
	 * Packet Methods
	 */
	@Override
	public List getPacketUpdate()
	{
		List objects = new LinkedList();
		objects.addAll(super.getPacketUpdate());
		objects.add(this.distributionMode);
		objects.add(this.transmissionRange);
		return objects;
	}

	@Override
	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream)
	{
		super.onReceivePacket(packetID, dataStream);

		if (packetID == 1)
		{
			this.distributionMode = dataStream.readInt();
			this.transmissionRange = dataStream.readInt();
		}
	}

	@Override
	public void invalidate()
	{
		RadarRegistry.unregister(this);
		super.invalidate();
	}

	public void setTransmitRange(int transmitRange)
	{
		this.transmissionRange = transmitRange;
		PacketManager.sendPacketToClients(getDescriptionPacket(), this.worldObj, new Vector3(this), 12);
	}

	public int getTransmitRange()
	{
		return this.transmissionRange;
	}

	public int getPowerLinkMode()
	{
		return this.distributionMode;
	}

	@Override
	public Container getContainer(InventoryPlayer inventoryplayer)
	{
		return new ContainerCapacitor(inventoryplayer.player, this);
	}

	@Override
	public int getSizeInventory()
	{
		return 4;
	}

	public int getSecStation_ID()
	{
		TileEntitySecurityStation sec = getLinkedSecurityStation();
		if (sec != null)
		{
			return sec.getDeviceID();
		}
		return 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.distributionMode = nbt.getInteger("distributionMode");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("distributionMode", this.distributionMode);
	}

	@Override
	public Set<IFortronFrequency> getLinkedDevices()
	{
		return FortronGrid.INSTANCE.get(this.worldObj, new Vector3(this), this.transmissionRange, this.getFrequency());
	}

	@Override
	public boolean isItemValid(int slotID, ItemStack itemStack)
	{
		switch (slotID)
		{
			case 0:
				if ((itemStack.getItem() instanceof ItemUpgradeCapacity))
				{
					return true;
				}
				break;
			case 1:
				if ((itemStack.getItem() instanceof ItemUpgradeRange))
				{
					return true;
				}
				break;
			case 2:
				if (((itemStack.getItem() instanceof IForceEnergyItems)) || ((itemStack.getItem() instanceof IPowerLinkItem)))
				{
					return true;
				}
				break;
			case 4:
				if ((itemStack.getItem() instanceof ItemCardSecurityLink))
				{
					return true;
				}
				break;
			case 3:
		}
		return false;
	}

}
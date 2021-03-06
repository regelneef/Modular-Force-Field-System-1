package mffs.machine.tile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import mffs.Fortron;
import mffs.ModularForceFieldSystem;
import mffs.api.modules.IModule;
import mffs.item.ItemForcillium;
import mffs.item.card.ItemCardFrequency;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

import com.google.common.io.ByteArrayDataInput;

/**
 * A TileEntity that extract forcillium into fortrons.
 * 
 * @author Calclavia
 * 
 */
public class TileExtractor extends TileElectric
{
	/**
	 * The amount of watts this machine uses.
	 */
	public static final int WATTAGE = 1000;
	public static final int REQUIRED_TIME = 20 * 15;
	public int processTime = 0;

	public TileExtractor()
	{
		this.fortronTank.setCapacity(10 * LiquidContainerRegistry.BUCKET_VOLUME);
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
				 * Spread Fortron to nearby Fortron Capacitors
				 */
				for (int i = 0; i < 6; i++)
				{
					TileEntity tileEntity = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), ForgeDirection.getOrientation(i));

					if (tileEntity instanceof TileCapacitor)
					{
						// TODO:Finish this.
						// ((TileEntityFortronCapacitor)tileEntity)
					}
				}

				if (this.canUse())
				{
					if (this.wattsReceived >= TileExtractor.WATTAGE)
					{
						if (this.processTime == 0)
						{
							int runTime = REQUIRED_TIME;

							/**
							 * Speed upgrade will reduce the time required. TODO: Add more upgrade
							 * ability.
							 */
							// TODO: Get upgrades to work better.
							if (getStackInSlot(3) != null)
							{
								if (this.getStackInSlot(3).itemID == ModularForceFieldSystem.itMSuDu.itemID)
								{
									runTime /= 2;
								}
							}

							this.processTime = runTime;
						}

						if (this.processTime > 0)
						{
							this.processTime--;

							if (this.processTime < 1)
							{
								this.use();
								this.processTime = 0;
							}
						}
						else
						{
							this.processTime = 0;
						}

						this.wattsReceived -= WATTAGE;
					}
				}
				else
				{
					this.processTime = 0;
				}
			}
		}
	}

	@Override
	public int getSizeInventory()
	{
		return 5;
	}

	@Override
	public ElectricityPack getRequest()
	{
		if (this.canUse() && !this.isPoweredByRedstone())
		{
			return new ElectricityPack(WATTAGE / this.getVoltage(), this.getVoltage());
		}

		return super.getRequest();
	}

	@Override
	public boolean isActive()
	{
		return !this.isPoweredByRedstone();
	}

	public boolean canUse()
	{
		if (!this.isDisabled())
		{
			if (this.isStackValidForSlot(0, this.getStackInSlot(0)))
			{
				return Fortron.getAmount(this.fortronTank) < this.fortronTank.getCapacity();
			}
		}

		return false;
	}

	private void use()
	{
		if (this.canUse())
		{
			this.fortronTank.fill(Fortron.getFortron(1250 + this.worldObj.rand.nextInt(1000)), true);
			this.decrStackSize(0, 1);
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
		objects.add(this.processTime);
		return objects;
	}

	@Override
	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException
	{
		super.onReceivePacket(packetID, dataStream);
		if (packetID == 1)
		{
			this.processTime = dataStream.readInt();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		this.processTime = nbt.getInteger("processTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setInteger("processTime", this.processTime);
	}

	@Override
	public boolean isStackValidForSlot(int slotID, ItemStack itemStack)
	{
		if (itemStack != null)
		{
			if (slotID >= 2 && slotID <= 4)
			{
				return itemStack.getItem() instanceof IModule;
			}

			switch (slotID)
			{
				case 0:
					return itemStack.getItem() instanceof ItemForcillium;
				case 1:
					return itemStack.getItem() instanceof ItemCardFrequency;
			}
		}

		return false;
	}
}
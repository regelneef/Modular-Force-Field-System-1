package mffs.common.block;

import mffs.common.MFFSConfiguration;
import mffs.common.MFFSCreativeTab;
import mffs.common.ModularForceFieldSystem;
import mffs.common.SecurityHelper;
import mffs.common.SecurityRight;
import mffs.common.multitool.ItemMultitool;
import mffs.common.tileentity.TileEntityControlSystem;
import mffs.common.tileentity.TileEntityMFFS;
import mffs.common.tileentity.TileEntitySecurityStation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import buildcraft.api.tools.IToolWrench;

public abstract class BlockMFFSMachine extends BlockMachine
{
	public BlockMFFSMachine(int id, String name)
	{
		super(MFFSConfiguration.CONFIGURATION.getBlock(name, id).getInt(id), UniversalElectricity.machine);
		this.setBlockName(name);
		this.setBlockUnbreakable();
		this.setRequiresSelfNotify();
		this.setResistance(100.0F);
		this.setStepSound(soundMetalFootstep);
		this.setRequiresSelfNotify();
		this.setCreativeTab(MFFSCreativeTab.INSTANCE);
		this.setTextureFile(ModularForceFieldSystem.BLOCK_TEXTURE_FILE);
	}

	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			TileEntityMFFS tileEntity = (TileEntityMFFS) world.getBlockTileEntity(x, y, z);
			ItemStack equippedItem = entityPlayer.getCurrentEquippedItem();

			if (equippedItem != null && (equippedItem.getItem() instanceof ItemMultitool || equippedItem.getItem() instanceof IToolWrench))
			{
				return this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
			}

			if ((tileEntity instanceof TileEntitySecurityStation) && (tileEntity.isActive()))
			{
				if (!SecurityHelper.isAccessGranted(tileEntity, entityPlayer, world, SecurityRight.CSR))
				{
					return true;
				}
			}

			if (tileEntity instanceof TileEntityControlSystem)
			{
				if (!SecurityHelper.isAccessGranted(tileEntity, entityPlayer, world, SecurityRight.UCS))
				{
					return true;
				}
			}

			if (!SecurityHelper.isAccessGranted(tileEntity, entityPlayer, world, SecurityRight.EB))
			{
				return true;
			}

			if (!world.isRemote)
			{
				entityPlayer.openGui(ModularForceFieldSystem.instance, 0, world, x, y, z);
			}
		}

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving)
	{
		TileEntity tile = world.getBlockTileEntity(i, j, k);

		if (tile instanceof TileEntityMFFS)
		{
			TileEntityMFFS tileEntity = (TileEntityMFFS) tile;
			int side = MathHelper.floor_double(entityliving.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
			int height = Math.round(entityliving.rotationPitch);

			if (height >= 65)
			{
				tileEntity.setDirection(ForgeDirection.getOrientation(1));
			}
			else if (height <= -65)
			{
				tileEntity.setDirection(ForgeDirection.getOrientation(0));
			}
			else if (side == 0)
			{
				tileEntity.setDirection(ForgeDirection.getOrientation(2));
			}
			else if (side == 1)
			{
				tileEntity.setDirection(ForgeDirection.getOrientation(5));
			}
			else if (side == 2)
			{
				tileEntity.setDirection(ForgeDirection.getOrientation(3));
			}
			else if (side == 3)
			{
				tileEntity.setDirection(ForgeDirection.getOrientation(4));
			}
		}
	}

	@Override
	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		// Reorient the block
		switch (world.getBlockMetadata(x, y, z))
		{
			case 0:
				world.setBlockMetadata(x, y, z, 1);
				break;
			case 1:
				world.setBlockMetadata(x, y, z, 2);
				break;
			case 2:
				world.setBlockMetadata(x, y, z, 5);
				break;
			case 5:
				world.setBlockMetadata(x, y, z, 3);
				break;

			case 3:
				world.setBlockMetadata(x, y, z, 4);
				break;

			case 4:
				world.setBlockMetadata(x, y, z, 0);
				break;
		}

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityAdvanced)
		{
			((TileEntityAdvanced) tileEntity).initiate();
			world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
			return true;
		}

		return false;
	}

	@Override
	public int getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z, int side)
	{
		TileEntity t = iBlockAccess.getBlockTileEntity(x, y, z);

		if (t instanceof TileEntityMFFS)
		{
			TileEntityMFFS tileEntity = (TileEntityMFFS) t;

			ForgeDirection blockfacing = ForgeDirection.getOrientation(side);
			ForgeDirection facingDirection = tileEntity.getDirection();

			if (tileEntity.isActive())
			{
				if (blockfacing.equals(facingDirection))
				{
					return this.blockIndexInTexture + 3 + 1;
				}
				if (blockfacing.equals(facingDirection.getOpposite()))
				{
					return this.blockIndexInTexture + 3 + 2;
				}
				return this.blockIndexInTexture + 3;
			}

			if (blockfacing.equals(facingDirection))
			{
				return this.blockIndexInTexture + 1;
			}
			if (blockfacing.equals(facingDirection.getOpposite()))
			{
				return this.blockIndexInTexture + 2;
			}
		}

		return this.blockIndexInTexture;
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int i, int j, int k, double d, double d1, double d2)
	{
		if ((world.getBlockTileEntity(i, j, k) instanceof TileEntityMFFS))
		{
			TileEntity tileentity = world.getBlockTileEntity(i, j, k);
			if (((TileEntityMFFS) tileentity).isActive())
			{
				return 999.0F;
			}
			return 100.0F;
		}

		return 100.0F;
	}
}
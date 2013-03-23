package mffs.common.block;

import java.util.List;
import java.util.Random;

import mffs.api.ForceFieldType;
import mffs.api.IForceFieldBlock;
import mffs.common.MFFSConfiguration;
import mffs.common.ZhuYao;
import mffs.common.tileentity.TLiChang;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BLiQiang extends BBase implements IForceFieldBlock
{
	public BLiQiang(int id)
	{
		super(id, "forceField", Material.glass);
		this.setBlockUnbreakable();
		this.setResistance(999.0F);
		this.setCreativeTab(null);
		this.setTickRandomly(true);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockid)
	{
		if (blockid != ZhuYao.blockForceField.blockID)
		{
			for (int x1 = -1; x1 <= 1; x1++)
			{
				for (int y1 = -1; y1 <= 1; y1++)
				{
					for (int z1 = -1; z1 <= 1; z1++)
					{
						if (world.getBlockId(x + x1, y + y1, z + z1) != ZhuYao.blockForceField.blockID)
						{
							if (world.getBlockId(x + x1, y + y1, z + z1) == 0)
							{
								breakBlock(world, x + x1, y + y1, z + z1, 0, 0);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int a, int b)
	{
		/**
		 * TODO: Checks the Projector to see if breaking this is legit.
		 */
		super.breakBlock(world, x, y, z, a, b);
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityPlayer)
	{
		/**
		 * TODO: Check if shock mode is on, if so, hurt entity
		 */
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		/**
		 * Allow creative players who are holding shift to go through the force field. TODO: Allow
		 * security bypassing.
		 */
		List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.9, z + 1));

		for (EntityPlayer entityPlayer : entities)
		{
			if (entityPlayer != null)
			{
				if (entityPlayer.capabilities.isCreativeMode && entityPlayer.isSneaking())
				{
					return null;
				}
			}
		}

		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox(x + f, y + f, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(x, y, z, x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity)
	{
		/**
		 * TODO: Check if shock mode is on, if so, hurt entity
		 * entity.attackEntityFrom(ModularForceFieldSystem.fieldShock, 10);
		 */
		if (entity instanceof EntityLiving)
		{
			((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 20, 3));
			((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 3));
		}
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
		return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double d, double d1, double d2)
	{
		// TODO: DO SOMETHING

		return Integer.MAX_VALUE;
	}

	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random)
	{
		if ((MFFSConfiguration.advancedParticles) && (world.getBlockMetadata(i, j, k) == ForceFieldType.Zapper.ordinal()))
		{
			double d = i + Math.random() + 0.2D;
			double d1 = j + Math.random() + 0.2D;
			double d2 = k + Math.random() + 0.2D;

			world.spawnParticle("townaura", d, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int meta)
	{
		if (meta == ForceFieldType.Camouflage.ordinal())
		{
			return new TLiChang();
		}

		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return createTileEntity(world, 0);
	}

	@Override
	public void weakenForceField(World world, int x, int y, int z)
	{
		if (MFFSConfiguration.influencedbyothermods)
		{
			world.setBlock(x, y, z, 0, 0, 2);
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}
}
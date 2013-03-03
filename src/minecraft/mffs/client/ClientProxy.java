package mffs.client;

import mffs.client.renderer.RenderForceField;
import mffs.client.renderer.RenderForcilliumExtractor;
import mffs.client.renderer.RenderFortronCapacitor;
import mffs.common.CommonProxy;
import mffs.common.ModularForceFieldSystem;
import mffs.common.tileentity.TileEntityForcilliumExtractor;
import mffs.common.tileentity.TileEntityFortronCapacitor;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		MinecraftForgeClient.preloadTexture(ModularForceFieldSystem.BLOCK_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ModularForceFieldSystem.ITEM_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ModularForceFieldSystem.TEXTURE_DIRECTORY + "projector.png");
		RenderingRegistry.registerBlockHandler(new RenderForceField());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFortronCapacitor.class, new RenderFortronCapacitor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityForcilliumExtractor.class, new RenderForcilliumExtractor());
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

	@Override
	public boolean isClient()
	{
		return true;
	}
}
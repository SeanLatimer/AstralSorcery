/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.cmd.CommandAstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.event.ClientInitializedEvent;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerEntity;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralSorcery
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:20
 */
@Mod(modid = AstralSorcery.MODID, name = AstralSorcery.NAME, version = AstralSorcery.VERSION,
        dependencies = "required-after:forge@[14.23.2.2611,);required-after:baubles;after:crafttweaker",
        certificateFingerprint = "certificate-placeholder :^)",
        acceptedMinecraftVersions = "[1.12.2]")
public class AstralSorcery {

    public static final String MODID = "astralsorcery";
    public static final String NAME = "Astral Sorcery";
    public static final String VERSION = "1.9.0";
    public static final String CLIENT_PROXY = "hellfirepvp.astralsorcery.client.ClientProxy";
    public static final String COMMON_PROXY = "hellfirepvp.astralsorcery.common.CommonProxy";

    private static boolean devEnvChache = false;

    @Mod.Instance(MODID)
    public static AstralSorcery instance;

    public static Logger log = LogManager.getLogger(NAME);

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = VERSION;
        devEnvChache = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        proxy.preLoadConfigEntries();
        Config.load(event.getSuggestedConfigurationFile());

        proxy.registerConfigDataRegistries();
        Config.loadDataRegistries(event.getModConfigurationDirectory());
        Config.loadConfigRegistries(ConfigDataAdapter.LoadPhase.PRE_INIT);

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Config.loadConfigRegistries(ConfigDataAdapter.LoadPhase.INIT);
        MinecraftForge.EVENT_BUS.register(this);

        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Config.loadConfigRegistries(ConfigDataAdapter.LoadPhase.POST_INIT);
        proxy.postInit();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAstralSorcery());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        CelestialGatewaySystem.instance.onServerStart();
    }

    @SubscribeEvent
    public void onClientFinish(ClientInitializedEvent event) {
        proxy.clientFinishedLoading();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        ResearchManager.saveAndClearServerCache();
        StarlightTransmissionHandler.getInstance().serverCleanHandlers();
        EventHandlerServer.perkCooldowns.clear();
        EventHandlerEntity.invulnerabilityCooldown.clear();
        EventHandlerEntity.ritualFlight.clear();
        EventHandlerEntity.attackStack.clear();
        EventHandlerEntity.spawnDenyRegions.clear();
    }

    @Mod.EventHandler
    public void onServerStop(FMLServerStoppedEvent event) {
        WorldCacheManager.wipeCache();
        //SpellCastingManager.INSTANCE.clearEffects();
    }

    public static boolean isRunningInDevEnvironment() {
        return devEnvChache;
    }

    static {
        FluidRegistry.enableUniversalBucket();
    }

}

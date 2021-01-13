package cat.nyaa.nyaautils;

import cat.nyaa.nyaacore.component.ComponentNotAvailableException;
import cat.nyaa.nyaacore.component.IMessageQueue;
import cat.nyaa.nyaacore.component.ISystemBalance;
import cat.nyaa.nyaacore.component.NyaaComponent;
import cat.nyaa.nyaautils.commandwarpper.EsschatCmdWarpper;
//import cat.nyaa.nyaautils.commandwarpper.TeleportCmdWarpper;
import cat.nyaa.nyaautils.commandwarpper.TpsPingCmdWarpper;
import cat.nyaa.nyaautils.dropprotect.DropProtectListener;
import cat.nyaa.nyaautils.elytra.ElytraEnhanceListener;
import cat.nyaa.nyaautils.elytra.FuelManager;
import cat.nyaa.nyaautils.exhibition.ExhibitionListener;
import cat.nyaa.nyaautils.expcapsule.ExpCapListener;
import cat.nyaa.nyaautils.extrabackpack.ExtraBackpackGUI;
import cat.nyaa.nyaautils.extrabackpack.ExtraBackpackListener;
import cat.nyaa.nyaautils.lootprotect.LootProtectListener;
import cat.nyaa.nyaautils.mailbox.MailboxListener;
import cat.nyaa.nyaautils.mention.MentionListener;
import cat.nyaa.nyaautils.messagequeue.MessageQueue;
import cat.nyaa.nyaautils.particle.ParticleListener;
import cat.nyaa.nyaautils.particle.ParticleTask;
import cat.nyaa.nyaautils.realm.RealmListener;
import cat.nyaa.nyaautils.redstonecontrol.RedstoneControlListener;
import cat.nyaa.nyaautils.signedit.SignEditListener;
import cat.nyaa.nyaautils.sit.SitListener;
import cat.nyaa.nyaautils.timer.TimerListener;
import cat.nyaa.nyaautils.timer.TimerManager;
import cat.nyaa.nyaautils.tpsping.TpsPingTask;
import cat.nyaa.nyaautils.vote.VoteTask;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class NyaaUtils extends JavaPlugin {
    public static NyaaUtils instance;
    public ISystemBalance systemBalance = null;
    public I18n i18n;
    public CommandHandler commandHandler;
    public Configuration cfg;
    public LootProtectListener lpListener;
    public DropProtectListener dpListener;
    public DamageStatListener dsListener;
    public ExhibitionListener exhibitionListener;
    public ExpCapListener expCapListener;
    public MailboxListener mailboxListener;
    public ElytraEnhanceListener elytraEnhanceListener;
    //public TeleportCmdWarpper teleportCmdWarpper;
    public FuelManager fuelManager;
    public TimerManager timerManager;
    public TimerListener timerListener;
    public WorldEditPlugin worldEditPlugin;
    public RealmListener realmListener;
    public ParticleListener particleListener;
    public ParticleTask particleTask;
    public SignEditListener signEditListener;
    public MentionListener mentionListener;
    public EsschatCmdWarpper esschatCmdWarpper;
    public VoteTask voteTask;
    public MessageQueue messageQueueListener;
    public RedstoneControlListener redstoneControlListener;
    public TpsPingTask tpsPingTask;
    public TpsPingCmdWarpper tpsPingCmdWarpper;
    public SitListener sitListener;
    public ExtraBackpackListener extraBackpackListener;

    @Override
    public void onEnable() {
        instance = this;
        cfg = new Configuration(this);
        cfg.load();
        i18n = new I18n(this, cfg.language);
        commandHandler = new CommandHandler(this, i18n);
        getCommand("nyaautils").setExecutor(commandHandler);
        getCommand("nyaautils").setTabCompleter((TabCompleter) commandHandler);
        lpListener = new LootProtectListener(this);
        dpListener = new DropProtectListener(this);
        dsListener = new DamageStatListener(this);
        elytraEnhanceListener = new ElytraEnhanceListener(this);
        //teleportCmdWarpper = new TeleportCmdWarpper(this);
        exhibitionListener = new ExhibitionListener(this);
        expCapListener = new ExpCapListener(this);
        mailboxListener = new MailboxListener(this);
        fuelManager = new FuelManager(this);
        timerManager = new TimerManager(this);
        timerListener = new TimerListener(this);
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        }
        realmListener = new RealmListener(this);
        try {
            systemBalance = NyaaComponent.get(ISystemBalance.class);
        } catch (ComponentNotAvailableException e) {
            systemBalance = null;
        }
        particleTask = new ParticleTask(this);
        particleListener = new ParticleListener(this);
        signEditListener = new SignEditListener(this);
        mentionListener = new MentionListener(this);
        messageQueueListener = new MessageQueue(this);
        NyaaComponent.register(IMessageQueue.class, messageQueueListener);
        redstoneControlListener = new RedstoneControlListener(this);
        sitListener = new SitListener(this);
        extraBackpackListener = new ExtraBackpackListener(this);
        voteTask = null;
        if (cfg.ping_enable || cfg.tps_enable) {
            tpsPingTask = new TpsPingTask(this);
            tpsPingTask.runTaskTimer(this, 0, 0);
            tpsPingCmdWarpper = new TpsPingCmdWarpper(this);
        }
    }

    @Override
    public void onDisable() {
        ExtraBackpackGUI.closeAll();
        getServer().getScheduler().cancelTasks(this);
        getCommand("nyaautils").setExecutor(null);
        getCommand("nyaautils").setTabCompleter(null);
        HandlerList.unregisterAll(this);
        cfg.save();
    }
}

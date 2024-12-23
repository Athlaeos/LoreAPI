package me.athlaeos.lapi;

import me.athlaeos.lapi.commands.CommandManager;
import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.configuration.ConfigUpdater;
import me.athlaeos.lapi.format.DisplayFormatRegistry;
import me.athlaeos.lapi.gui.MenuListener;
import me.athlaeos.lapi.hooks.PluginHook;
import me.athlaeos.lapi.listeners.ArmorSwitchListener;
import me.athlaeos.lapi.listeners.GamemodeListener;
import me.athlaeos.lapi.listeners.HandSwitchListener;
import me.athlaeos.lapi.listeners.StatChangeListener;
import me.athlaeos.lapi.localization.Translator;
import me.athlaeos.lapi.network.NetworkHandlerImpl;
import me.athlaeos.lapi.network.PacketListener;
import me.athlaeos.lapi.nms.NMS;
import me.athlaeos.lapi.placeholder.PlaceholderRegistry;
import me.athlaeos.lapi.utils.MinecraftVersion;
import me.athlaeos.lapi.utils.Utils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoreAPIPlugin extends JavaPlugin {

    private static LoreAPIPlugin instance;
    private static NMS nms;
    private static YamlConfiguration pluginConfig;
    private static boolean enabled = true;
    private static final Map<Class<? extends PluginHook>, PluginHook> activeHooks = new HashMap<>();
    private static boolean miniMessageSupported = false;
    {
        instance = this;
    }

    public static LoreAPIPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad(){
        pluginConfig = saveAndUpdateConfig("config.yml");
        saveConfig("formats.yml");

        String lang = pluginConfig.getString("language", "en-us");
        save("languages/en-us.json");
        save("languages/en-us_materials.json");
        saveConfig("formats.yml");
        saveConfig("wrappers.yml");
        saveConfig("placeholder_configs/item_attack_speed.yml");
        saveConfig("placeholder_configs/item_attributes.yml");
        saveConfig("placeholder_configs/item_damage.yml");
        saveConfig("placeholder_configs/item_durability.yml");
        saveConfig("placeholder_configs/item_enchantments.yml");
        saveConfig("placeholder_configs/item_lore.yml");

        if (!setupNMS()){
            enabled = false;
            return;
        }
        miniMessageSupported = setupMiniMessage();
        Translator.load(lang);
    }

    @Override
    public void onEnable() {
        if (!enabled){
            logSevere("This version of Minecraft is not compatible with LoreAPI. Sorry!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else nms.onEnable();
        registerCommand(new CommandManager(), "loreapi");

        // listener registration
        registerListener(new MenuListener());
        registerListener(new ArmorSwitchListener());
        registerListener(new HandSwitchListener());
        registerListener(new StatChangeListener());
        registerListener(new GamemodeListener());
        PacketListener listener = new PacketListener(new NetworkHandlerImpl());
        listener.addAll();
        registerListener(listener);

        DisplayFormatRegistry.load();
        PlaceholderRegistry.load();

        if (pluginConfig.getBoolean("check_updates")) UpdateChecker.checkUpdate(); // ignore for the time being

        for (PluginHook hook : activeHooks.values()) hook.whenPresent();
    }

    private boolean setupNMS() {
        try {
            String nmsVersion = MinecraftVersion.getServerVersion().getNmsVersion();
            if (nmsVersion == null) return false;
            Class<?> clazz = Class.forName("me.athlaeos.nms.NMS_" + nmsVersion);

            if (NMS.class.isAssignableFrom(clazz)) {
                nms = (NMS) clazz.getDeclaredConstructor().newInstance();
            }

            return nms != null;
        } catch (Exception | Error ex) {
            Utils.logStackTrace(ex, true);
            return false;
        }
    }

    private boolean setupMiniMessage(){
        try {
            Class.forName("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer");
            logInfo("MiniMessage support initialized");
            return true;
        } catch (ClassNotFoundException ignored){
            logInfo("MiniMessage support could not be initialized");
            return false;
        }
    }

    public static NMS getNms() {
        return nms;
    }

    private void registerListener(Listener listener){
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void registerListener(Listener listener, String configKey){
        if (pluginConfig.getBoolean(configKey)) registerListener(listener);
    }

    private void registerCommand(CommandExecutor command, String cmd){
        PluginCommand c = LoreAPIPlugin.getInstance().getCommand(cmd);
        if (c == null) return;
        c.setExecutor(command);
    }

    public YamlConfiguration saveConfig(String name){
        save(name);
        return ConfigManager.saveConfig(name).get();
    }

    public void save(String name){
        File file = new File(this.getDataFolder(), name);
        if (!file.exists()) this.saveResource(name, false);
    }

    private void updateConfig(String name){
        File configFile = new File(getDataFolder(), name);
        try {
            ConfigUpdater.update(instance, name, configFile, new ArrayList<>());
        } catch (IOException e) {
            Utils.logStackTrace(e, true);
        }
    }

    private void updateConfig(String name, List<String> excludedSections){
        File configFile = new File(getDataFolder(), name);
        try {
            ConfigUpdater.update(instance, name, configFile, excludedSections);
        } catch (IOException e) {
            Utils.logStackTrace(e, true);
        }
    }

    private YamlConfiguration saveAndUpdateConfig(String config){
        save(config);
        updateConfig(config);
        return saveConfig(config);
    }

    private YamlConfiguration saveAndUpdateConfig(String config, List<String> excludedSections){
        save(config);
        updateConfig(config, excludedSections);
        return saveConfig(config);
    }

    public static void logInfo(String message){
        instance.getServer().getLogger().info("[LoreAPI] " + message);
    }

    public static void logWarning(String warning){
        instance.getServer().getLogger().warning("[Plugin] " + warning);
    }
    public static void logFine(String warning){
        instance.getServer().getLogger().fine("[Plugin] " + warning);
        Utils.sendMessage(instance.getServer().getConsoleSender(), "&a[Plugin] " + warning);
    }

    public static void logSevere(String help){
        instance.getServer().getLogger().severe("[Plugin] " + help);
    }

    public static YamlConfiguration getPluginConfig() {
        return pluginConfig;
    }

    private static void registerHook(PluginHook hook){
        if (hook.isPresent()) {
            activeHooks.put(hook.getClass(), hook);
            logInfo("Initialized plugin hook with " + hook.getPlugin());
        }
    }

    public static boolean isHookFunctional(Class<? extends PluginHook> hook){
        return activeHooks.containsKey(hook);
    }

    @SuppressWarnings("unchecked")
    public static <T extends PluginHook> T getHook(Class<T> hook){
        return (T) activeHooks.get(hook);
    }

    public static boolean isMiniMessageSupported() {
        return miniMessageSupported;
    }
}

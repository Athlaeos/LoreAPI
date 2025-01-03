package me.athlaeos.lapi.configuration;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Config {
    private final String name;
    private File file;
    private YamlConfiguration config;
    private final LoreAPIPlugin plugin = LoreAPIPlugin.getInstance();

    public Config(String name) {
        this.name = name;
        save();
    }

    public Config save() {
        if ((this.config == null) || (this.file == null))
            return this;
        try {
            ConfigurationSection section = config.getConfigurationSection("");
            if (section != null && !section.getKeys(true).isEmpty())
                config.save(this.file);
        } catch (IOException ex) {
            Utils.logStackTrace(ex, true);
        }
        return this;
    }

    public YamlConfiguration get() {
        if (this.config == null)
            reload();

        return this.config;
    }

    public Config saveDefaultConfig() {
        file = new File(plugin.getDataFolder(), this.name);

        plugin.saveResource(this.name, false);

        return this;
    }

    public Config reload() {
        if (file == null)
            this.file = new File(plugin.getDataFolder(), this.name);

        this.config = YamlConfiguration.loadConfiguration(file);

        Reader defConfigStream;
        try {
            InputStream resource = plugin.getResource(this.name);
            if (resource != null) {
                defConfigStream = new InputStreamReader(resource, StandardCharsets.UTF_8);
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                this.config.setDefaults(defConfig);
            }
        } catch (NullPointerException ignored) {
        }
        return this;
    }

    public Config copyDefaults(boolean force) {
        get().options().copyDefaults(force);
        return this;
    }

    public Config set(String key, Object value) {
        get().set(key, value);
        return this;
    }

    public Object get(String key) {
        return get().get(key);
    }
}
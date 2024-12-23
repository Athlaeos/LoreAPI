package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.localization.Translator;
import me.athlaeos.lapi.placeholder.ListPlaceholder;
import me.athlaeos.lapi.utils.ItemBuilder;
import me.athlaeos.lapi.utils.StringUtils;
import me.athlaeos.lapi.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;
import java.util.regex.Pattern;

public class EnchantmentPlaceholder extends ListPlaceholder {
    private final Map<Enchantment, String> enchantmentFormat = new LinkedHashMap<>();

    private final List<String> format;
    private final boolean compress;
    private final int compressionMaxLength;
    private final String compressionSeparator;
    private final boolean hideFlag;

    public EnchantmentPlaceholder(){
        LoreAPIPlugin.getInstance().saveConfig("placeholder_configs/item_enchantments.yml");
        YamlConfiguration config = ConfigManager.getConfig("placeholder_configs/item_enchantments.yml").get();
        compress = config.getBoolean("compression");
        compressionMaxLength = config.getInt("compression_max_length");
        compressionSeparator = config.getString("compression_max_length");
        format = config.getStringList("format");
        hideFlag = config.getBoolean("hide_flag");

        ConfigurationSection enchantmentSection = config.getConfigurationSection("enchantments");
        if (enchantmentSection == null) return;
        for (String enchantmentString : enchantmentSection.getKeys(false)){
            Enchantment enchantment = Arrays.stream(enchantmentString.split(Pattern.quote("|"))).map(LoreAPIPlugin.getNms()::getEnchantmentFromString).filter(Objects::nonNull).findFirst().orElse(null);
            if (enchantment == null){
                LoreAPIPlugin.logWarning("Invalid enchantment given in placeholder_configs." + enchantmentString);
                continue;
            }
            String translation = config.getString("enchantments." + enchantmentString);
            enchantmentFormat.put(enchantment, Translator.translatePlaceholders(translation));
        }
    }

    @Override
    public List<String> parse(Player player, ItemBuilder item) {
        if (hideFlag) item.flag(ItemFlag.HIDE_ENCHANTS);
        Map<Enchantment, Integer> enchantments = item.getMeta() instanceof EnchantmentStorageMeta e ? e.getStoredEnchants() : item.getMeta().getEnchants();

        return new ArrayList<>(StringUtils.setListPlaceholder(format, "%enchantments%", getFormattedEnchantments(enchantments)));
    }

    private List<String> getFormattedEnchantments(Map<Enchantment, Integer> enchantments){
        List<String> formattedEnchantments = new ArrayList<>();
        for (Enchantment e : enchantmentFormat.keySet()){
            if (!enchantments.containsKey(e)) continue;
            String translation = enchantmentFormat.get(e);
            if (translation == null) continue;
            int level = enchantments.get(e);
            formattedEnchantments.add(translation
                    .replace("%lv_roman%", StringUtils.toRoman(level))
                    .replace("%lv_numeric%", String.valueOf(level))
            );
        }
        if (compress){
            List<String> compressedLines = new ArrayList<>();
            int currentLength = 0;
            StringBuilder stringBuilder = new StringBuilder();
            for (String enchant : formattedEnchantments){
                String rawString = ChatColor.stripColor(Utils.chat(enchant));
                String rawSeparator = ChatColor.stripColor(Utils.chat(compressionSeparator));
                if (!stringBuilder.isEmpty()) {
                    // StringBuilder already has stuff, so we're adding a separator and enchant
                    if (currentLength + rawString.length() + rawSeparator.length() > compressionMaxLength){
                        // Adding a separator and enchant would exceed the limit, so it's added to a new line
                        currentLength = rawString.length();
                        compressedLines.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    } else {
                        // Adding a separator and enchant is within the limit, so they're added
                        stringBuilder.append(compressionSeparator).append(enchant);
                        currentLength += rawString.length() + rawSeparator.length();
                    }
                } else {
                    // The StringBuilder is empty, so we're adding an enchantment
                    stringBuilder.append(enchant);
                    currentLength += rawString.length();
                }
            }
            return compressedLines;
        }
        return formattedEnchantments;
    }

    @Override
    public String getIdentifier() {
        return "lapi";
    }

    @Override
    public String getPlaceholder() {
        return "item_enchantments";
    }
}

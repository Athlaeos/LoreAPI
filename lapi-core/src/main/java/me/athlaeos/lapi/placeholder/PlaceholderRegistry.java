package me.athlaeos.lapi.placeholder;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.format.DisplayFormat;
import me.athlaeos.lapi.format.DisplayFormatRegistry;
import me.athlaeos.lapi.hooks.PAPIHook;
import me.athlaeos.lapi.placeholder.placeholders.*;
import me.athlaeos.lapi.placeholder.placeholders.attribute.AttributePlaceholder;
import me.athlaeos.lapi.placeholder.placeholders.attribute.ItemAttackSpeedPlaceholder;
import me.athlaeos.lapi.placeholder.placeholders.attribute.ItemDamagePlaceholder;
import me.athlaeos.lapi.placeholder.placeholders.WrapperPlaceholder;
import me.athlaeos.lapi.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlaceholderRegistry implements Reloadable {
    private static final Map<String, StaticPlaceholder> staticPlaceholders = new HashMap<>();
    private static final Map<String, List<StaticPlaceholder>> placeholderCache = new HashMap<>();
    private static final Map<String, ParameterizedPlaceholder> parameterizedPlaceholders = new HashMap<>();
    private static final List<String> exceptions = new ArrayList<>();
    private static boolean skipNoMetaItems = false;

    static {
        Reloader.register(new PlaceholderRegistry());
    }

    public static void registerPlaceholder(StaticPlaceholder placeholder){
        if (!(placeholder instanceof StringPlaceholder || placeholder instanceof ListPlaceholder)) throw new UnsupportedOperationException("The Placeholder interface is not legal for own implementation! Must be a StringPlaceholder or ListPlaceholder");
        staticPlaceholders.put(getPlaceholderName(placeholder), placeholder);
    }

    public static void registerPlaceholder(ParameterizedPlaceholder placeholder){
        parameterizedPlaceholders.put(placeholder.getIdentifier(), placeholder);
    }

    public static StaticPlaceholder getPlaceholder(String identifier){
        if (identifier == null) return null;
        return staticPlaceholders.get(identifier);
    }

    public static ParameterizedPlaceholder getParameterizedPlaceholder(String identifier){
        if (identifier == null) return null;
        return parameterizedPlaceholders.get(identifier);
    }

    /**
     * Parses an ItemStack's display name and lore for placeholders.
     * <br>
     * List placeholders may only be used once per item as to prevent a recursive loop
     * @param item The item to parse
     * @param player The player observer to parse the item for
     * @return The parsed ItemStack
     */
    public static ItemStack parseItemStack(ItemStack item, Player player){
        if (ItemUtils.isEmpty(item)) return item;
        ItemBuilder builder = new ItemBuilder(item);
        if (skipNoMetaItems && !item.hasItemMeta() && !exceptions.contains(item.getType().toString())) return item;
        DisplayFormat format = DisplayFormatRegistry.getFormat(builder.getMeta());
        if (format == null) return item;
        format.convert(builder, player);
        return builder.get();
    }

    public static String parseString(Player player, ItemBuilder item, String string){
        List<StaticPlaceholder> placeholders = getPlaceholders(string);
        for (StaticPlaceholder placeholder : placeholders){
            if (!(placeholder instanceof StringPlaceholder s)) continue;
            string = string.replace(String.format("$%s$", getPlaceholderName(s)), s.parse(player, item));
        }

        Collection<Pair<ParameterizedPlaceholder, String>> dynamicPlaceholders = getUndefinedPlaceholders(string);
        for (Pair<ParameterizedPlaceholder, String> placeholder : dynamicPlaceholders){
            String replacement = placeholder.getObj1().parseString(player, item, placeholder.getObj2());
            if (replacement == null) continue;
            string = string.replace(String.format("$%s_%s$", placeholder.getObj1().getIdentifier(), placeholder.getObj2()), replacement);
        }

        if (LoreAPIPlugin.isHookFunctional(PAPIHook.class)) string = PAPIHook.parse(player, string);
        return string;
    }

    public static List<String> parseList(Player player, ItemBuilder item, List<String> originalList){
        return parseList(player, item, originalList, new ArrayList<>());
    }

    private static List<String> parseList(Player player, ItemBuilder item, List<String> originalList, List<String> blacklistedPlaceholders){
        List<String> newListAfterPlaceholders = new ArrayList<>();
        for (String s : originalList){
            s = parseString(player, item, s);

            List<ListPlaceholder> placeholders = getPlaceholders(s).stream().filter(p -> p instanceof ListPlaceholder).map(p -> (ListPlaceholder) p).toList();
            if (placeholders.isEmpty()) newListAfterPlaceholders.add(s);
            else {
                ListPlaceholder first = placeholders.getFirst();
                String placeholder = String.format("$%s$", getPlaceholderName(first));
                if (blacklistedPlaceholders.contains(placeholder)) continue;

                List<String> insert = me.athlaeos.lapi.utils.StringUtils.setListPlaceholder(new ArrayList<>(List.of(s)), placeholder, first.parse(player, item));

                // we keep recursively adding parsed lists until no more list placeholders are present
                blacklistedPlaceholders.add(placeholder);
                newListAfterPlaceholders.addAll(parseList(player, item, insert, blacklistedPlaceholders));
            }
        }

        List<String> newListAfterDynamicPlaceholders = new ArrayList<>();

        for (String s : newListAfterPlaceholders){
            List<Pair<ParameterizedPlaceholder, String>> placeholders = getUndefinedPlaceholders(s);
            if (placeholders.isEmpty()) newListAfterDynamicPlaceholders.add(s);
            else {
                Pair<ParameterizedPlaceholder, String> first = placeholders.getFirst();
                String placeholder = String.format("$%s_%s$", first.getObj1().getIdentifier(), first.getObj2());
                if (blacklistedPlaceholders.contains(placeholder)) continue;
                String[] split = s.split(Pattern.quote(placeholder));
                String prefix = split.length > 0 ? split[0] : "";
                String suffix = split.length > 1 ? split[1] : "";
                List<String> parsed = first.getObj1().parseList(player, item, first.getObj2());
                if (parsed == null){
                    newListAfterDynamicPlaceholders.add(s);
                    continue;
                }
                List<String> insert = me.athlaeos.lapi.utils.StringUtils.setListPlaceholder(new ArrayList<>(List.of(s)), placeholder, parsed);

                // we keep recursively adding parsed lists until no more list placeholders are present
                blacklistedPlaceholders.add(placeholder);
                newListAfterDynamicPlaceholders.addAll(parseList(player, item, insert, blacklistedPlaceholders));
            }
        }

        return newListAfterDynamicPlaceholders;
    }

    private static List<StaticPlaceholder> getPlaceholders(String string){
        if (placeholderCache.containsKey(string)) return placeholderCache.get(string);
        String[] matches = Utils.defIfNull(StringUtils.substringsBetween(string, "$", "$"), new String[0]);
        List<StaticPlaceholder> placeholders = new ArrayList<>();
        for (String match : matches){
            StaticPlaceholder placeholder = staticPlaceholders.get(match);
            if (placeholder == null) continue;
            placeholders.add(placeholder);
        }
        placeholderCache.put(string, placeholders);
        return placeholders;
    }

    private static List<Pair<ParameterizedPlaceholder, String>> getUndefinedPlaceholders(String string){
        String[] matches = Utils.defIfNull(StringUtils.substringsBetween(string, "$", "$"), new String[0]);
        List<Pair<ParameterizedPlaceholder, String>> placeholders = new ArrayList<>();
        for (String match : matches){
            ParameterizedPlaceholder placeholder = getParameterizedPlaceholder(parameterizedPlaceholders.keySet().stream().filter(match::startsWith).findFirst().orElse(null));
            if (placeholder == null) continue;
            String params = match.replaceFirst(String.format("%s_", placeholder.getIdentifier()), "");
            placeholders.add(new Pair<>(placeholder, params));
        }
        return placeholders;
    }

    private static String getPlaceholderName(StaticPlaceholder placeholder){
        return String.format("%s_%s", placeholder.getIdentifier(), placeholder.getPlaceholder());
    }

    public static Map<String, StaticPlaceholder> getStaticPlaceholders() {
        return Collections.unmodifiableMap(staticPlaceholders);
    }

    public static Map<String, StaticPlaceholder> getRegisteredPlaceholdersByIdentifier(String identifier) {
        return new HashMap<>(staticPlaceholders).entrySet().stream().filter(e -> e.getKey().equals(identifier)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, ParameterizedPlaceholder> getParameterizedPlaceholders() {
        return Collections.unmodifiableMap(parameterizedPlaceholders);
    }

    public static void load(){
        YamlConfiguration config = LoreAPIPlugin.getPluginConfig();
        skipNoMetaItems = config.getBoolean("skip_no_meta_items");
        exceptions.clear();
        exceptions.addAll(config.getStringList("exceptions"));

        YamlConfiguration wrapperConfig = ConfigManager.getConfig("wrappers.yml").get();
        ConfigurationSection wrapperSection = wrapperConfig.getConfigurationSection("");
        if (wrapperSection != null){
            for (String w : wrapperSection.getKeys(false)){
                List<String> format = wrapperConfig.getStringList(w + ".format");
                new WrapperPlaceholder(w, format).register();
            }
        }

        YamlConfiguration attributeConfig = ConfigManager.getConfig("placeholder_configs/item_attributes.yml").get();
        ConfigurationSection attributeSection = attributeConfig.getConfigurationSection("attributes");
        if (attributeSection != null){
            for (String a : attributeSection.getKeys(false)){
                String attributeString = attributeConfig.getString("attributes." + a + ".attribute");
                if (attributeString == null) continue;

                Attribute attribute = Arrays.stream(attributeString.split(Pattern.quote("|"))).map(s -> Catch.catchOrElse(() -> Attribute.valueOf(s), null)).filter(Objects::nonNull).findFirst().orElse(null);
                if (attribute == null){
                    LoreAPIPlugin.logWarning("Invalid attribute " + attributeString + " given in placeholder_configs/item_attributes.yml, could not parse");
                    continue;
                }
                String format = attributeConfig.getString("attributes." + a + ".attribute");
                int precision = attributeConfig.getInt("attributes." + a + ".decimal_precision", 0);
                new AttributePlaceholder(format, a, precision, attribute).register(true);
            }
        }
        new ItemDamagePlaceholder().register(true);
        new ItemAttackSpeedPlaceholder().register(true);

        new DurabilityListPlaceholder(new DurabilityBarPlaceholder()).register(true);
        new DurabilityListPlaceholder(new DurabilityPercentilePlaceholder()).register(true);
        new DurabilityListPlaceholder(new DurabilitySymbolPlaceholder()).register(true);
        new DurabilityListPlaceholder(new DurabilityNumericPlaceholder()).register(true);

        new LorePlaceholder().register();
        new DisplayNamePlaceholder().register();
        new EnchantmentPlaceholder().register();

        new CustomModelDataInjector().register();
        new EnchantmentGlintInjector().register();
        new ModelInjector().register();
        new ToolTipInjector().register();
    }

    @Override
    public void reload() {
        ConfigManager.reloadConfig("placeholder_configs/item_attributes.yml");
        load();
    }

    @Override
    public boolean canReloadAsync() {
        return true;
    }
}

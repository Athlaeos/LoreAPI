package me.athlaeos.lapi.placeholder.placeholders.attribute;

import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.placeholder.PlaceholderRegistry;
import me.athlaeos.lapi.placeholder.StringPlaceholder;
import me.athlaeos.lapi.utils.DefaultItemProperties;
import me.athlaeos.lapi.utils.ItemBuilder;
import me.athlaeos.lapi.utils.ItemUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AttributePlaceholder extends StringPlaceholder {
    private final Map<String, AttributeModifierFunction> additiveAttributeSources = new HashMap<>();
    private final Map<String, AttributeModifierFunction> scalarAttributeSources = new HashMap<>();
    private final Map<String, AttributeModifierFunction> multiplicativeAttributeSources = new HashMap<>();

    protected static final boolean applyFlag = ConfigManager.getConfig("placeholder_configs/item_attributes.yml").get().getBoolean("hide_flag", false);
    private final int decimalPrecision;
    private final String format;
    private final Attribute attribute;
    private final String placeholder;
    private String identifier = "lapi";

    public AttributePlaceholder(String format, String placeholder, int decimalPrecision, Attribute attribute){
        this.attribute = attribute;
        this.placeholder = placeholder;
        this.format = format;
        this.decimalPrecision = decimalPrecision;
    }
    public AttributePlaceholder(String format, String identifier, String placeholder, int decimalPrecision, Attribute attribute){
        this.identifier = identifier;
        this.attribute = attribute;
        this.placeholder = placeholder;
        this.format = format;
        this.decimalPrecision = decimalPrecision;
    }

    @Override
    public String parse(Player player, ItemBuilder item) {
        double totalStat = getStatValue(player, item, true);
        double baseStat = getStatValue(player, item, false);

        String format = "%." + decimalPrecision + "f";

        if (applyFlag) {
            item.flag(ItemFlag.HIDE_ATTRIBUTES);
            item.getMeta().addAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE, new AttributeModifier("dummy", 0.01, AttributeModifier.Operation.ADD_SCALAR));
        }
        return this.format
                .replace("%stat_total%", String.format(format, totalStat))
                .replace("%stat_base%", String.format(format, baseStat));
    }

    public double getStatValue(Player player, ItemBuilder item, boolean includeEffects){
        double value = 0;
        // player base damage
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) value = attributeInstance.getBaseValue();

        if (includeEffects) for (AttributeModifierFunction modifier : additiveAttributeSources.values()) value += modifier.apply(player, item);

        // attributes from worn items (unlikely, but just in case)
        ItemMeta meta = item.getMeta();
        Collection<AttributeModifier> allAttributeModifiers = ItemUtils.getAllWornModifiers(player, attribute, item.getItem(), false, null); // all modifiers from all equipped items excluding the main hand
        Collection<AttributeModifier> metaModifiers = meta.getAttributeModifiers(attribute); // only the modifiers belonging to the observed item
        if (metaModifiers != null && !metaModifiers.isEmpty()) {
            allAttributeModifiers.addAll(metaModifiers);
        } else {
            AttributeModifier modifier = DefaultItemProperties.getDefaultModifier(attribute, item.getItem().getType());
            if (modifier != null) allAttributeModifiers.add(modifier);
        }

        double baseScalar = 0;
        if (includeEffects) for (AttributeModifierFunction modifier : scalarAttributeSources.values()) baseScalar += modifier.apply(player, item);

        double baseMultiplier = 1;
        if (includeEffects) for (AttributeModifierFunction modifier : multiplicativeAttributeSources.values()) baseMultiplier *= (1 + modifier.apply(player, item));

        return ItemUtils.getTotalAttributeValue(allAttributeModifiers, value, baseScalar, baseMultiplier, m -> true);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * The sum of all returned function values is added before the scalar and multiplier. <br>
     * As example, having 3 functions each returning 1, 2, and 3 will result in +6 damage being
     * added before the scalar and multiplier. (assuming we're using a damage source) <br><br>
     * This map may also have entries removed or replaced.
     * @return a mutable map with all the additive attribute sources.
     */
    public Map<String, AttributeModifierFunction> getAdditiveAttributeSources() {
        return additiveAttributeSources;
    }

    /**
     * The sum of all returned function values is multiplied by the attribute value after
     * additive attribute sources, but before multiplicative ones. <br>
     * As example, having 3 scalar functions return 0.5, 0.3, and 0.2 each will result in a
     * (1 + 0.5 + 0.3 + 0.2) = 2x multiplier on damage. (assuming we're using a damage source) <br>
     * Assuming the additive attribute sources returned 6 total, this results in an attribute value of
     * 12 before the multiplier functions.<br><br>
     * This map may also have entries removed or replaced.
     * @return a mutable map with all the scalar damage sources
     */
    public Map<String, AttributeModifierFunction> getMultiplicativeAttributeSources() {
        return multiplicativeAttributeSources;
    }

    /**
     * Each returned function value is multiplied with one another after adding 1, and is
     * again multiplied by the damage value after additive and scalar functions are executed.<br>
     * As example, having 3 multiplier functions return 1 each will result in a
     * (1 + 1) * (1 + 1) * (1 + 1) = 8x multiplier on damage. <br>
     * Assuming the additive and scalar damage sources returned 12 total,
     * this results in a damage value of 96.<br>
     * I hope I don't have to elaborate further on how this mode is the most impactful
     * of all the damage buff modes. You probably shouldn't use these a lot<br><br>
     * This map may also have entries removed or replaced.
     * @return a mutable map with all the multiplier damage sources
     */
    public Map<String, AttributeModifierFunction> getScalarAttributeSources() {
        return scalarAttributeSources;
    }

    public interface AttributeModifierFunction extends BiFunction<Player, ItemBuilder, Double> {
        String getIdentifier();

        Double apply(Player player, ItemBuilder itemBuilder);
    }

    public void register(boolean includingListWrapper){
        if (includingListWrapper) PlaceholderRegistry.registerPlaceholder(new AttributeListPlaceholder(this, attribute));
        super.register();
    }
}

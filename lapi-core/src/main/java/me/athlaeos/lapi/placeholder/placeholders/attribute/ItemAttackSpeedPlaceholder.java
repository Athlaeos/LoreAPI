package me.athlaeos.lapi.placeholder.placeholders.attribute;

import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemAttackSpeedPlaceholder extends AttributePlaceholder {

    private static final AttributeModifierFunction hasteAmplifierEffect = new AttributeModifierFunction() {
        @Override
        public String getIdentifier() { return "modifier_haste"; }
        @Override
        public Double apply(Player player, ItemBuilder itemBuilder) {
            PotionEffect hasteEffect = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
            int hasteLevel = hasteEffect == null ? -1 : hasteEffect.getAmplifier();
            return 0.1 * (hasteLevel + 1);
        }
    };
    private static final AttributeModifierFunction fatigueAmplifierEffect = new AttributeModifierFunction() {
        @Override
        public String getIdentifier() { return "modifier_fatigue"; }
        @Override
        public Double apply(Player player, ItemBuilder itemBuilder) {
            PotionEffect fatigueEffect = player.getPotionEffect(PotionEffectType.SLOW_DIGGING);
            int fatigueLevel = fatigueEffect == null ? -1 : fatigueEffect.getAmplifier();
            return -0.1 * (fatigueLevel + 1);
        }
    };

    private final int precision;
    private final String format;

    public ItemAttackSpeedPlaceholder(){
        super("", "item_attack_speed_total", 0, Attribute.GENERIC_ATTACK_SPEED);
        getScalarAttributeSources().put(hasteAmplifierEffect.getIdentifier(), hasteAmplifierEffect);
        getScalarAttributeSources().put(fatigueAmplifierEffect.getIdentifier(), fatigueAmplifierEffect);

        YamlConfiguration config = ConfigManager.getConfig("placeholder_configs/item_attack_speed.yml").get();
        precision = config.getInt("decimal_precision");
        format = config.getString("format");
    }

    @Override
    public String parse(Player player, ItemBuilder item) {
        if (applyFlag) item.flag(ItemFlag.HIDE_ATTRIBUTES);
        double totalStat = getStatValue(player, item, true);
        double baseStat = getStatValue(player, item, false);

        String format = "%." + precision + "f";
        return this.format
                .replace("%stat_total%", String.format(format, totalStat))
                .replace("%stat_base%", String.format(format, baseStat));
    }
}

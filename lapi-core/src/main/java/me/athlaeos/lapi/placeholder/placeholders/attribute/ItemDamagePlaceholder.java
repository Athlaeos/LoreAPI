package me.athlaeos.lapi.placeholder.placeholders.attribute;

import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemDamagePlaceholder extends AttributePlaceholder {

    private static final AttributeModifierFunction strengthAmplifierEffect = new AttributeModifierFunction() {
        @Override
        public String getIdentifier() { return "modifier_strength"; }
        @Override
        public Double apply(Player player, ItemBuilder itemBuilder) {
            PotionEffect strengthEffect = player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
            int strengthLevel = strengthEffect == null ? -1 : strengthEffect.getAmplifier();
            return 3D * (strengthLevel + 1);
        }
    };
    private static final AttributeModifierFunction weaknessAmplifierEffect = new AttributeModifierFunction() {
        @Override
        public String getIdentifier() { return "modifier_weakness"; }
        @Override
        public Double apply(Player player, ItemBuilder itemBuilder) {
            PotionEffect strengthEffect = player.getPotionEffect(PotionEffectType.WEAKNESS);
            int strengthLevel = strengthEffect == null ? -1 : strengthEffect.getAmplifier();
            return -4D * (strengthLevel + 1);
        }
    };
    private static final AttributeModifierFunction sharpnessAmplifierEffect = new AttributeModifierFunction() {
        @Override
        public String getIdentifier() {
            return "modifier_sharpness";
        }

        @Override
        public Double apply(Player player, ItemBuilder itemBuilder) {
            int sharpnessLevel = itemBuilder.getMeta().getEnchantLevel(Enchantment.DAMAGE_ALL);
            return sharpnessLevel > 0 ? (0.5 * sharpnessLevel + 0.5) : 0;
        }
    };
    private static final AttributeModifierFunction smiteAmplifierEffect = new AttributeModifierFunction() {
        @Override
        public String getIdentifier() {
            return "modifier_smite";
        }

        @Override
        public Double apply(Player player, ItemBuilder itemBuilder) {
            int smiteLevel = itemBuilder.getMeta().getEnchantLevel(Enchantment.DAMAGE_UNDEAD);
            return smiteLevel * 2.5;
        }
    };
    private static final AttributeModifierFunction baneOfArthropodsAmplifierEffect = new AttributeModifierFunction() {
        @Override
        public String getIdentifier() {
            return "modifier_bane_of_arthropods";
        }

        @Override
        public Double apply(Player player, ItemBuilder itemBuilder) {
            int boaLevel = itemBuilder.getMeta().getEnchantLevel(Enchantment.DAMAGE_ARTHROPODS);
            return boaLevel * 2.5;
        }
    };
    private static final AttributeModifierFunction impalingAmplifierEffect = new AttributeModifierFunction() {
        @Override
        public String getIdentifier() {
            return "modifier_impaling";
        }

        @Override
        public Double apply(Player player, ItemBuilder itemBuilder) {
            int impalingLevel = itemBuilder.getMeta().getEnchantLevel(Enchantment.IMPALING);
            return impalingLevel * 2.5;
        }
    };

    private final int decimalPrecision;
    private final String smiteDamage;
    private final String boaDamage;
    private final String impalingDamage;
    private final String genericDamage;
    private final String format;

    public ItemDamagePlaceholder(){
        super("", "item_damage_total", 0, Attribute.GENERIC_ATTACK_DAMAGE);
        getAdditiveAttributeSources().put(strengthAmplifierEffect.getIdentifier(), strengthAmplifierEffect);
        getAdditiveAttributeSources().put(weaknessAmplifierEffect.getIdentifier(), weaknessAmplifierEffect);
        getAdditiveAttributeSources().put(sharpnessAmplifierEffect.getIdentifier(), sharpnessAmplifierEffect);

        YamlConfiguration config = ConfigManager.getConfig("placeholder_configs/item_damage.yml").get();
        decimalPrecision = config.getInt("decimal_precision");
        smiteDamage = config.getString("smite");
        impalingDamage = config.getString("impaling");
        boaDamage = config.getString("bane_of_arthropods");
        genericDamage = config.getString("damage");
        format = config.getString("format");
    }

    @Override
    public String parse(Player player, ItemBuilder item) {
        String format = "%." + decimalPrecision + "f";

        double totalDamage = getStatValue(player, item, true);
        double baseDamage = getStatValue(player, item, false);

        double smiteDamage = smiteAmplifierEffect.apply(player, item);
        double boaDamage = baneOfArthropodsAmplifierEffect.apply(player, item);
        double impalingDamage = impalingAmplifierEffect.apply(player, item);
        String smite = smiteDamage > 0 ? this.smiteDamage
                .replace("%damage_total%", String.format(format, totalDamage + smiteDamage))
                .replace("%damage_extra%", String.format(format, smiteDamage)) :
                "";
        String baneOfArthropods = boaDamage > 0 ? this.boaDamage
                .replace("%damage_total%", String.format(format, totalDamage + boaDamage))
                .replace("%damage_extra%", String.format(format, boaDamage)) :
                "";
        String impaling = impalingDamage > 0 ? this.impalingDamage
                .replace("%damage_total%", String.format(format, totalDamage + impalingDamage))
                .replace("%damage_extra%", String.format(format, impalingDamage)) :
                "";
        String standard = this.genericDamage
                .replace("%damage_total%", String.format(format, totalDamage))
                .replace("%damage_extra%", String.format(format, baseDamage));

        if (applyFlag) {
            item.flag(ItemFlag.HIDE_ATTRIBUTES);
            item.getMeta().addAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE, new AttributeModifier("dummy", 0.01, AttributeModifier.Operation.ADD_SCALAR));
        }
        return this.format
                .replace("%damage%", standard)
                .replace("%smite%", smite)
                .replace("%impaling%", impaling)
                .replace("%bane_of_arthropods%", baneOfArthropods);
    }
}

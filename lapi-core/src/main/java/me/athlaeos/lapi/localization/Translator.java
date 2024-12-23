package me.athlaeos.lapi.localization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.utils.ItemUtils;
import me.athlaeos.lapi.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Translator {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static Language language;
    private static Language defaultLanguage;
    private static Materials materials;
    private static Materials defaultMaterials;
    private static String languageCode;

    /**
     * Returns the value mapped to the given key. Sends console a warning if no value is mapped and defaults to the key.
     */
    public static String getTranslation(String key){
        if (language == null) return "";
        if (!key.contains("<lang.") && !language.getStrings().containsKey(key)) LoreAPIPlugin.logWarning("No translated value mapped for " + key);
        return translatePlaceholders(language.getStrings().getOrDefault(key, key));
    }

    public static String getMaterialTranslation(String material){
        return materials.getMaterialTranslations().getOrDefault(material,
                defaultMaterials.getMaterialTranslations().getOrDefault(material, "&cNo material mapping for " + material));
    }

    /**
     * Returns the raw value mapped to the given key. Can be null if no value is mapped
     */
    public static String getRawTranslation(String key){
        if (language == null) return "";
        if (!language.getStrings().containsKey(key)) return null;
        return translatePlaceholders(language.getStrings().get(key));
    }

    public static Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public static Materials getDefaultMaterials() {
        return defaultMaterials;
    }

    public static Materials getMaterials() {
        return materials;
    }

    public static List<String> getListTranslation(String key) {
        if (language == null) return new ArrayList<>();
        return language.getLists().getOrDefault(key, new ArrayList<>());
    }

    public static String translatePlaceholders(String originalString){
        if (originalString == null) return null;
        String[] langMatches = StringUtils.substringsBetween(originalString, "<lang.", ">");
        if (langMatches != null) {
            for (String s : langMatches)
                originalString = originalString.replace("<lang." + s + ">", getTranslation(s));
        }

        String[] materialMatches = StringUtils.substringsBetween(originalString, "<material.", ">");
        if (materialMatches != null) {
            for (String s : materialMatches)
                originalString = originalString.replace("<material." + s + ">", getMaterialTranslation(s));
        }
        return originalString;
    }

    public static List<String> translateListPlaceholders(List<String> originalList){
        List<String> newList = new ArrayList<>();
        if (originalList == null) return newList;

        for (String l : originalList) {
            String subString = StringUtils.substringBetween(l, "<lang.", ">");
            if (subString == null) {
                // list does not contain placeholder match, string is added normally
                newList.add(l);
            } else {
                // list has a line matching the placeholder format, placeholder is replaced with associated value
                List<String> placeholderList = getListTranslation(subString);
                if (placeholderList.isEmpty()){
                    newList.add(translatePlaceholders(l));
                } else {
                    // each line in the associated list is once again passed through the translation method
                    placeholderList.forEach(s -> newList.add(translatePlaceholders(s)));
                }
            }
        }
        return newList;
    }

    public static void load(String l){
        languageCode = l;
        LoreAPIPlugin.logInfo("Loading LoreAPI config with language " + l + " selected");
        try (BufferedReader langReader = new BufferedReader(new FileReader(new File(LoreAPIPlugin.getInstance().getDataFolder(), "/languages/" + l + ".json"), StandardCharsets.UTF_8))) {
            language = gson.fromJson(langReader, Language.class);
        } catch (IOException exception){
            Utils.logStackTrace(exception, true);
            language = new Language();
        }

        try (BufferedReader materialReader = new BufferedReader(new FileReader(new File(LoreAPIPlugin.getInstance().getDataFolder(), "/languages/" + l + "_materials.json"), StandardCharsets.UTF_8))) {
            materials = gson.fromJson(materialReader, Materials.class);
        } catch (IOException exception){
            Utils.logStackTrace(exception, true);
            materials = new Materials();
        }

        int entriesAdded = 0;
        InputStream defaultStream = LoreAPIPlugin.getInstance().getClass().getResourceAsStream("/languages/en-us.json");
        if (defaultStream != null){
            try (InputStreamReader defaultReader = new InputStreamReader(defaultStream, StandardCharsets.UTF_8)){
                defaultLanguage = gson.fromJson(defaultReader, Language.class);

                for (String key : defaultLanguage.getStrings().keySet()){
                    if (!language.getStrings().containsKey(key)){
                        language.getStrings().put(key, defaultLanguage.getStrings().get(key));
                        if (entriesAdded == 0 && !l.equalsIgnoreCase("en-us")) LoreAPIPlugin.logWarning("Language file was outdated! New english entries added to /languages/" + l + ".json. Sorry for the spam, but if you don't use the default (en-us) be sure to keep track of and translate the following entries to your locale");
                        if (!l.equalsIgnoreCase("en-us")) LoreAPIPlugin.logWarning("string > " + key);
                        entriesAdded++;
                    }
                }
                for (String key : defaultLanguage.getLists().keySet()){
                    if (!language.getLists().containsKey(key)){
                        language.getLists().put(key, defaultLanguage.getLists().get(key));
                        if (entriesAdded == 0 && !l.equalsIgnoreCase("en-us")) LoreAPIPlugin.logWarning("Language file was outdated! New english entries added to /languages/" + l + ".json. Sorry for the spam, but if you don't use the default (en-us) be sure to keep track of and translate the following entries to your locale");
                        if (!l.equalsIgnoreCase("en-us")) LoreAPIPlugin.logWarning("list > " + key);
                        entriesAdded++;
                    }
                }
            } catch (IOException exception){
                Utils.logStackTrace(exception, true);
            }

            if (entriesAdded > 0) {
                try (FileWriter writer = new FileWriter(new File(LoreAPIPlugin.getInstance().getDataFolder(), "languages/" + l + ".json"), StandardCharsets.UTF_8)){
                    gson.toJson(language, writer);
                } catch (IOException exception){
                    Utils.logStackTrace(exception, true);
                }
            }
        }

        int materialEntriesAdded = 0;
        InputStream defaultMaterialStream = LoreAPIPlugin.getInstance().getClass().getResourceAsStream("/languages/en-us_materials.json");
        if (defaultMaterialStream != null){
            try (InputStreamReader defaultReader = new InputStreamReader(defaultMaterialStream, StandardCharsets.UTF_8)){
                defaultMaterials = gson.fromJson(defaultReader, Materials.class);

                for (String key : defaultMaterials.getMaterialTranslations().keySet()){
                    if (!materials.getMaterialTranslations().containsKey(key)){
                        materials.getMaterialTranslations().put(key, defaultMaterials.getMaterialTranslations().get(key));
                        if (materialEntriesAdded == 0 && !l.equalsIgnoreCase("en-us")) LoreAPIPlugin.logWarning("Materials file was outdated! New english entries added to /languages/" + l + "_materials.json. Sorry for the spam, but if you don't use the default (en-us) be sure to keep track of and translate the following entries to your locale");
                        if (!l.equalsIgnoreCase("en-us")) LoreAPIPlugin.logWarning(" > " + key);
                        materialEntriesAdded++;
                    }
                }
            } catch (IOException exception){
                Utils.logStackTrace(exception, true);
            }

            if (materialEntriesAdded > 0) {
                try (FileWriter writer = new FileWriter(new File(LoreAPIPlugin.getInstance().getDataFolder(), "languages/" + l + "_materials.json"), StandardCharsets.UTF_8)){
                    gson.toJson(materials, writer);
                } catch (IOException exception){
                    Utils.logStackTrace(exception, true);
                }
            }
        }
    }

    /**
     * Replaces any language placeholders in the display name and lore to their translated versions
     * @param iMeta the item to translate
     */
    public static void translateItemMeta(ItemMeta iMeta){
        boolean translated = false;
        if (iMeta == null) return;
        if (iMeta.hasDisplayName()){
            if (iMeta.getDisplayName().contains("<lang.")){
                iMeta.setDisplayName(Utils.chat(translatePlaceholders(iMeta.getDisplayName())));
                translated = true;
            }
        }
        if (iMeta.hasLore() && iMeta.getLore() != null){
            List<String> newLore = new ArrayList<>();
            if (iMeta.getLore().stream().anyMatch(s -> s.contains("<lang."))){
                for (String s : translateListPlaceholders(iMeta.getLore())){
                    newLore.add(Utils.chat(s));
                }
                translated = true;
            } else newLore = iMeta.getLore();

            iMeta.setLore(newLore);
        }
        if (!translated) return;
        ItemUtils.normalizeItemText(iMeta);
    }

    public static String getLanguageCode() {
        return languageCode;
    }
}

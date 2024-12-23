package me.athlaeos.lapi.localization;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Language {
    private final Map<String, String> strings = new LinkedHashMap<>();
    private final Map<String, List<String>> lists = new LinkedHashMap<>();

    public Map<String, String> getStrings() {
        return strings;
    }

    public Map<String, List<String>> getLists() {
        return lists;
    }
}

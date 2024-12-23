package me.athlaeos.lapi.utils;

import me.athlaeos.lapi.LoreAPIPlugin;

public class Catch {
    public static <T> T catchOrElse(Returns<T> c, T def){
        return catchOrElse(c, def, null);
    }

    public static <T> T catchOrElse(Returns<T> c, T def, String log){
        try {
            return c.get();
        } catch (Exception e){
            if (log != null) LoreAPIPlugin.logWarning(log);
            return def;
        }
    }
    public static void catchOrLog(Runnable c){
        catchOrLog(c, null);
    }

    public static void catchOrLog(Runnable c, String log){
        try {
            c.run();
        } catch (Exception e){
            if (log != null) LoreAPIPlugin.logWarning(log);
        }
    }
}

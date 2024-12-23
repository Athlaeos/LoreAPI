package me.athlaeos.lapi.placeholder;

/**
 * Not legal for external implementation. LoreAPI will not account for it. <br>
 * <br>
 * Static placeholders are defined with an identifier + placeholder, and are directly
 * registered as such. This type does not have interchangeable parameters, and so the user is
 * expected to always use the same text to access this placeholder.<br>
 * For example, if you make a placeholder that just inserts a number where the number is defined
 * in the placeholder text,
 * static placeholders do not allow you to make something like $lapi_number_#$ where # represents
 * said number. For that you would need to use a {@link ParameterizedPlaceholder}.<br><br>
 *
 * Do note, however, that StaticPlaceholders due to their deterministic nature are faster and
 * can be cached. ParameterizedPlaceholders are not like this, so generally speaking if you can avoid
 * using a ParameterizedPlaceholder, avoid it.
 */
public interface StaticPlaceholder {
    /**
     * The identifier name of the expansion. Will determine the placeholder's prefix. <br>
     * Example final placeholder with "lore" as getPlaceholder() and "lapi" as getIdentifier():
     * <code>$lapi_lore$</code><br><br>
     * <b>The resulting getFinalPlaceholder() should be unique unless you want to overwrite
     * previous placeholders</b>
     * @return The identifier of the plugin
     */
    String getIdentifier();

    /**
     * The placeholder's name. Will determine the placeholder's suffix. <br>
     * Example final placeholder with "lore" as getPlaceholder() and "lapi" as getIdentifier():
     * <code>$lapi_lore$</code><br><br>
     * <b>The resulting getFinalPlaceholder() should be unique unless you want to overwrite
     * previous placeholders</b>
     * @return The placeholder's name
     */
    String getPlaceholder();

    default void register(){
        PlaceholderRegistry.registerPlaceholder(this);
    }
}

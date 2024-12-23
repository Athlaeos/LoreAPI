package me.athlaeos.lapi.utils;

import me.athlaeos.lapi.LoreAPIPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    static final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static List<String> chat(List<String> messages){
        if (messages == null) return new ArrayList<>();
        return messages.stream().map(Utils::chat).toList();
    }

    /**
     * Converts all color codes to ChatColor. Works with hex codes.
     * Hex code format is triggered with &#123456
     * @param message the message to convert
     * @return the converted message
     */
    public static String chat(String message) {
        if (message == null) return null;
        if (message.isEmpty()) return "";
        char COLOR_CHAR = ChatColor.COLOR_CHAR;
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static void sendMessage(CommandSender whomst, String message){
        if (!StringUtils.isEmpty(message)) {
            if (message.startsWith("ACTIONBAR") && whomst instanceof Player p) {
                sendActionBar(p, message.replaceFirst("ACTIONBAR", ""));
            } else if (message.startsWith("TITLE(") && message.endsWith(")") && !message.equalsIgnoreCase("title()") && whomst instanceof Player p){
                String title = message.replaceFirst("TITLE", "");
                String subtitle = "";
                int titleDuration = 40;
                int fadeDuration = 5;
                String subString = message.substring(6, message.length() - 1);
                String[] args = subString.split(";");
                if (args.length > 0) title = args[0];
                if (args.length > 1) subtitle = args[1];
                if (args.length > 2) titleDuration = Catch.catchOrElse(() -> Integer.parseInt(args[2]), 100);
                if (args.length > 3) fadeDuration = Catch.catchOrElse(() -> Integer.parseInt(args[2]), 10);

                sendTitle(p, title, subtitle, titleDuration, fadeDuration);
            } else {
                whomst.sendMessage(chat(message));
            }
        }
    }

    public static void sendActionBar(Player whomst, String message){
        if (!StringUtils.isEmpty(ChatColor.stripColor(chat(message)))) whomst.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chat(message)));
    }

    public static void sendTitle(Player whomst, String title, String subtitle, int duration, int fade){
        if (!StringUtils.isEmpty(title)) whomst.sendTitle(chat(title), chat(subtitle), fade, duration, fade);
    }

    public static void logStackTrace(Throwable t, boolean severe){
        if (severe) LoreAPIPlugin.logSevere(t.toString());
        else LoreAPIPlugin.logWarning(t.toString());
        for (StackTraceElement element : t.getStackTrace()){
            if (severe) LoreAPIPlugin.logSevere(element.toString());
            else LoreAPIPlugin.logWarning(element.toString());
        }
    }

    public static Map<String, OfflinePlayer> getPlayersFromUUIDs(Collection<UUID> uuids){
        Map<String, OfflinePlayer> players = new HashMap<>();
        for (UUID uuid : uuids){
            OfflinePlayer player = LoreAPIPlugin.getInstance().getServer().getOfflinePlayer(uuid);
            players.put(player.getName(), player);
        }
        return players;
    }

    public static Map<String, Player> getOnlinePlayersFromUUIDs(Collection<UUID> uuids){
        Map<String, Player> players = new HashMap<>();
        for (UUID uuid : uuids){
            Player player = LoreAPIPlugin.getInstance().getServer().getPlayer(uuid);
            if (player != null) players.put(player.getName(), player);
        }
        return players;
    }

    public static Color hexToRgb(String colorStr) {
        return Color.fromRGB(Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ));
    }

    public static String rgbToHex(int r, int g, int b){
        return String.format("#%02x%02x%02x", r, g, b);
    }


    public static Collection<Player> selectPlayers(CommandSender source, String selector) throws IllegalArgumentException{
        Collection<Player> targets = new HashSet<>();
        if (selector.startsWith("@")){
            for (Entity part : Bukkit.selectEntities(source, selector)){
                if (part instanceof Player)
                    targets.add((Player) part);
            }
        } else {
            Player target = LoreAPIPlugin.getInstance().getServer().getPlayer(selector);
            if (target != null) targets.add(target);
        }
        return targets;
    }

    public static <T> Map<Integer, List<T>> paginate(int pageSize, List<T> allEntries) {
        Map<Integer, List<T>> pages = new HashMap<>();

        int maxPages = (int) Math.ceil((double) allEntries.size() / (double) pageSize);
        for (int pageNumber = 0; pageNumber < maxPages; pageNumber++) {
            pages.put(pageNumber, allEntries.subList( // sublist from start of page to start of next page
                    Math.min(pageNumber * pageSize, allEntries.size()),
                    Math.min((pageNumber + 1) * pageSize, allEntries.size())
            ));
        }

        return pages;
    }

    public static boolean isChunkLoaded(Location l){
        if (l.getWorld() == null) throw new IllegalArgumentException("Chunk load check can't be performed on locations where its world is null!");
        return l.getWorld().isChunkLoaded(l.getBlockX()/16, l.getBlockZ()/16);
    }

    private static final Collection<ClickType> legalClickTypes = Set.of(ClickType.DROP, ClickType.CONTROL_DROP,
            ClickType.MIDDLE, ClickType.WINDOW_BORDER_LEFT, ClickType.WINDOW_BORDER_RIGHT, ClickType.UNKNOWN, ClickType.CREATIVE, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
    private static final Collection<ClickType> illegalClickTypes = Set.of(ClickType.SWAP_OFFHAND, ClickType.NUMBER_KEY, ClickType.DOUBLE_CLICK);

    public static void calculateClickEvent(InventoryClickEvent e, int maxAmount, int... slotsToCover){
        Player p = (Player) e.getWhoClicked();
        ItemStack cursor = p.getItemOnCursor();
        ItemStack clickedItem = e.getCurrentItem();
        if (e.getClickedInventory() == null) return;
        Inventory openInventory = e.getView().getTopInventory();
        e.setCancelled(true);
        if (e.getClickedInventory() instanceof PlayerInventory){
            // player inventory item clicked
            if (e.isShiftClick() && !ItemUtils.isEmpty(clickedItem)){
                // shift click, check if slotsToCalculate are available for new items, otherwise do nothing more as there's no slot to transfer to
                for (Integer i : slotsToCover){
                    ItemStack slotItem = openInventory.getItem(i);
                    if (ItemUtils.isEmpty(slotItem)) {
                        if (clickedItem.getAmount() <= maxAmount) {
                            openInventory.setItem(i, clickedItem);
                            e.setCurrentItem(null);
                        }
                        else {
                            ItemStack itemToPut = clickedItem.clone();
                            itemToPut.setAmount(maxAmount);
                            if (clickedItem.getAmount() - maxAmount <= 0) e.setCurrentItem(null);
                            else clickedItem.setAmount(clickedItem.getAmount() - maxAmount);
                            openInventory.setItem(i, itemToPut);
                        }
                        return;
                    } else if (slotItem.isSimilar(clickedItem)) {
                        // similar slot item, add as much as possible
                        if (slotItem.getAmount() < maxAmount) {
                            int amountToTransfer = Math.min(clickedItem.getAmount(), maxAmount - slotItem.getAmount());
                            if (clickedItem.getAmount() == amountToTransfer) {
                                e.setCurrentItem(null);
                            } else {
                                if (clickedItem.getAmount() - amountToTransfer <= 0) e.setCurrentItem(null);
                                else clickedItem.setAmount(clickedItem.getAmount() - amountToTransfer);
                            }
                            slotItem.setAmount(slotItem.getAmount() + amountToTransfer);
                            return;
                        }
                    }
                }
                // no available slot found, do nothing more
            } else e.setCancelled(false); // regular inventory click, do nothing special
        } else if (e.getClickedInventory().equals(e.getView().getTopInventory())){
            // opened inventory clicked
            if (legalClickTypes.contains(e.getClick())) { // inconsequential action used, allow event and do nothing more
                e.setCancelled(false);
                return;
            }
            if (illegalClickTypes.contains(e.getClick())) return; // incalculable action used, event is cancelled and do nothing more
            // other actions have to be calculated
            if (e.isLeftClick() || e.isRightClick()) {
                // transfer or swap all if not similar
                if (ItemUtils.isEmpty(cursor)){
                    // pick up clicked item, should be fine
                    e.setCancelled(false);
                } else {
                    if (ItemUtils.isEmpty(clickedItem)){
                        int amountToTransfer = (e.isRightClick() ? 1 : maxAmount);
                        if (cursor.getAmount() > amountToTransfer){
                            ItemStack itemToTransfer = cursor.clone();
                            itemToTransfer.setAmount(amountToTransfer);
                            e.setCurrentItem(itemToTransfer);
                            cursor.setAmount(cursor.getAmount() - amountToTransfer);
                            p.setItemOnCursor(cursor);
                        } else {
                            e.setCurrentItem(cursor);
                            p.setItemOnCursor(null);
                        }
                    } else {
                        // swap or transfer items
                        if (cursor.isSimilar(clickedItem)){
                            // are similar, transfer as much as possible
                            int clickedMax = Math.min(clickedItem.getType().getMaxStackSize(), maxAmount);
                            if (clickedItem.getAmount() < clickedMax){
                                int amountToTransfer = e.isRightClick() ? 1 : Math.min(cursor.getAmount(), clickedMax - clickedItem.getAmount());
                                if (cursor.getAmount() == amountToTransfer) {
                                    p.setItemOnCursor(null);
                                } else {
                                    cursor.setAmount(cursor.getAmount() - amountToTransfer);
                                    p.setItemOnCursor(cursor);
                                }
                                clickedItem.setAmount(clickedItem.getAmount() + amountToTransfer);
                            } // clicked item already equals or exceeds max amount, do nothing more
                        } else {
                            // not similar, swap items if cursor has valid amount
                            if (cursor.getAmount() <= maxAmount){
                                // valid amount, swap items
                                ItemStack temp = cursor.clone();
                                p.setItemOnCursor(clickedItem);
                                e.setCurrentItem(temp);
                            } // invalid amount, do nothing more
                        }
                    }
                }
            }
        }
    }

    public static void calculateDragEvent(InventoryDragEvent e, int maxAmount, Integer... affectedSlots){
        ItemStack cursor = e.getCursor();
        int newAmount = ItemUtils.isEmpty(cursor) ? 0 : cursor.getAmount();
        Collection<Integer> slots = Set.of(affectedSlots);
        e.setCancelled(true);
        for (Integer slot : e.getNewItems().keySet()){
            ItemStack newItem = e.getNewItems().get(slot);
            if (slots.contains(slot) && newItem.getAmount() > maxAmount) { // dragged slot would be capped afterwards, subtract excess
                int excess = newItem.getAmount() - maxAmount;
                newAmount += excess;
                e.getNewItems().get(slot).setAmount(maxAmount);
            }
        }
        for (Integer slot : e.getNewItems().keySet()){
            e.getView().setItem(slot, e.getNewItems().get(slot));
        }
        ItemStack oldCursor = e.getOldCursor().clone();
        oldCursor.setAmount(newAmount);

        LoreAPIPlugin.getInstance().getServer().getScheduler().runTaskLater(LoreAPIPlugin.getInstance(), () -> {
            if (oldCursor.getAmount() > 0) e.getWhoClicked().setItemOnCursor(oldCursor);
            else e.getWhoClicked().setItemOnCursor(null);
        }, 1L);
    }

    /**
     * Combines all the given collections into the first collection, which should typically be empty
     * @param collection the collection in which to add all the following collections
     * @param collections the collections which to add in order to the original collection
     * @return the collection as given in the first argument
     */
    @SafeVarargs
    public static <T> Collection<T> concat(Collection<T> collection, Collection<T>... collections){
        for (Collection<T> c : collections) collection.addAll(c);
        return collection;
    }

    /**
     * Combines all the given lists into the first list, which should typically be empty
     * @param collection the collection in which to add all the following collections
     * @param collections the collections which to add in order to the original collection
     * @return the collection as given in the first argument
     */
    @SafeVarargs
    public static <T> List<T> concat(List<T> collection, List<T>... collections){
        for (List<T> c : collections) collection.addAll(c);
        return collection;
    }

    public static <T> T defIfNull(T t, T def){
        return t == null ? def : t;
    }
}



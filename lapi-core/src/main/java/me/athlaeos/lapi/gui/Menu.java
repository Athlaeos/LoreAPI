package me.athlaeos.lapi.gui;

import me.athlaeos.lapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public abstract class Menu {
    protected Inventory inventory;
    protected PlayerMenuUtility playerMenuUtility;

    public Menu(PlayerMenuUtility playerMenuUtility){
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void handleMenu(InventoryDragEvent e);

    public abstract void setMenuItems();

    public void open(){
        inventory = Bukkit.createInventory(null, getSlots(), Utils.chat(getMenuName()));

        this.setMenuItems();
        MenuListener.setActiveMenu(playerMenuUtility.getOwner(), this);

        playerMenuUtility.getOwner().openInventory(inventory);
    }

    public Inventory getInventory(){
        return inventory;
    }

    public void onClose(){
        // do nothing by default
    }
}

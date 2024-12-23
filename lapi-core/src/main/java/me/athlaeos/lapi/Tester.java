package me.athlaeos.lapi;

import org.bukkit.enchantments.Enchantment;

public class Tester {
    public static void main(String[] args){
        for (Enchantment e : Enchantment.values()){
            System.out.println(e.getKey());
        }
    }
}

/*
 * DeluxeCoinflip Plugin
 * Copyright (c) 2021 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.deluxecoinflip.utility;

import net.zithium.deluxecoinflip.utility.universal.XMaterial;
import net.zithium.library.items.Base64Util;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemStackBuilder {

    private final ItemStack ITEM_STACK;

    public ItemStackBuilder(ItemStack item) {
        this.ITEM_STACK = item;
    }

    public static ItemStackBuilder getItemStack(ConfigurationSection section) {
        final Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(section.getString("material", "null").toUpperCase());

        ItemStack item;
        if(xMaterial.isPresent()) {
            item = xMaterial.get().parseItem();
        }else{
            return new ItemStackBuilder(XMaterial.BARRIER.parseItem()).withName("&cInvalid material");
        }

        if (item.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && section.contains("base64")) {
            item = Base64Util.getBaseHead(section.getString("base64")).clone();
        }

        ItemStackBuilder builder = new ItemStackBuilder(item);

        if (section.contains("amount")) {
            builder.withAmount(section.getInt("amount"));
        }

        if (section.contains("display_name")) {
            builder.withName(section.getString("display_name"));
        }

        if (section.contains("lore")) {
            builder.withLore(section.getStringList("lore"));
        }

        if (section.contains("custom_model_data")) {
            builder.withCustomModelData(section.getInt("custom_model_data"));
        }

        if (section.contains("glow") && section.getBoolean("glow")) {
            builder.withGlow();
        }

        if (section.contains("item_flags")) {
            List<ItemFlag> flags = new ArrayList<>();
            section.getStringList("item_flags").forEach(text -> {
                try {
                    ItemFlag flag = ItemFlag.valueOf(text);
                    flags.add(flag);
                } catch (IllegalArgumentException ignored) {
                }
            });
            builder.withFlags(flags.toArray(new ItemFlag[0]));
        }

        return builder;
    }

    public ItemStackBuilder setType(Material material) {
        ITEM_STACK.setType(material);
        return this;
    }

    public ItemStackBuilder withAmount(int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    public ItemStackBuilder withFlags(ItemFlag... flags) {
        ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(flags);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withName(String name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setDisplayName(TextUtil.color(name));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withCustomModelData(int data) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setCustomModelData(data);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder setSkullOwner(OfflinePlayer owner) {

        SkullMeta im = (SkullMeta) ITEM_STACK.getItemMeta();
        try {
            im.setOwningPlayer(owner);
        } catch (ClassCastException | NoSuchMethodError ex) {
            im.setOwner(owner.getName());
        }

        ITEM_STACK.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder withLore(List<String> lore) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<String> coloredLore = new ArrayList<String>();
        for (String s : lore) {
            coloredLore.add(TextUtil.color(s));
        }
        meta.setLore(coloredLore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder withDurability(int durability) {
        ITEM_STACK.setDurability((short) durability);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder withData(int data) {
        ITEM_STACK.setDurability((short) data);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, final int level) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemStackBuilder withGlow() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ITEM_STACK.setItemMeta(meta);
        ITEM_STACK.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        return this;
    }

    public ItemStackBuilder withType(Material material) {
        ITEM_STACK.setType(material);
        return this;
    }

    public ItemStackBuilder clearLore() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setLore(new ArrayList<String>());
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder clearEnchantments() {
        for (Enchantment enchantment : ITEM_STACK.getEnchantments().keySet()) {
            ITEM_STACK.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStackBuilder withColor(Color color) {
        Material type = ITEM_STACK.getType();
        if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();
            meta.setColor(color);
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withColor is only applicable for leather armor!");
        }
    }

    public ItemStack build() {
        return ITEM_STACK;
    }
}

package com.industrialworld.manager.recipe;

import org.bukkit.inventory.ItemStack;

public interface GrindRecipe extends RecipeBase {
    boolean matches(ItemStack itemStack);
    double getPowerNeeded();
}

package com.industrialworld.task;

import com.industrialworld.ConstItems;
import com.industrialworld.IndustrialWorld;
import com.industrialworld.manager.ConfigManager;
import com.industrialworld.manager.MainManager;
import com.industrialworld.utils.DebuggingLogger;
import org.bukkit.Bukkit;

public class AfterLoadTask implements Runnable {
    @Override
    public void run() {
        ConfigManager.init(IndustrialWorld.instance);


        DebuggingLogger.debug("register blocks");
        RegisterTask.registerBlock();
        ConfigManager.loadBlocks();

        // Register recipes, blocks
        DebuggingLogger.debug("register items");
        RegisterTask.registerItem();
        ConstItems.initConst();
        DebuggingLogger.debug("register recipes");
        RegisterTask.registerRecipes();

        // Register tick
        Bukkit.getScheduler().runTaskTimer(IndustrialWorld.instance, MainManager::update, 0, 0);

        /*DebuggingLogger.debug("Register Industrial crafting table.");
        Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(IndustrialWorld.instance, "crafting_table_craft"),
                                          ConstItems.IW_CRAFTING_TABLE).shape("AAA", "ABA", "AAA")
                                 .setIngredient('A', Material.IRON_INGOT)
                                 .setIngredient('B', Material.CRAFTING_TABLE));*/
    }
}
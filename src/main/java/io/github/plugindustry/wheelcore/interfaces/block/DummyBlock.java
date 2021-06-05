package io.github.plugindustry.wheelcore.interfaces.block;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.interfaces.Tickable;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class DummyBlock implements BlockBase, Tickable, Placeable, Destroyable, Interactive {
    @Override
    public void onTick() {
        // Do nothing.
    }

    @Override
    public boolean onInteract(Player player, Action action, ItemStack tool, Block block) {
        return true;
    }

    public boolean onBlockPlace(ItemStack item, Block block) {
        MainManager.addBlock(block.getLocation(), this, null);
        if (getMaterial() != item.getType())
            block.setType(getMaterial());
        return true;
    }

    public boolean onBlockDestroy(Block block, ItemStack tool, DestroyMethod method) {
        // We do nothing by default so you should do this job in your implementation too.
        MainManager.removeBlock(block.getLocation());
        block.getWorld().dropItem(block.getLocation(), getItemStack());
        return true;
    }

    @Nonnull
    public abstract ItemStack getItemStack();

    @Nonnull
    public abstract Material getMaterial();
}

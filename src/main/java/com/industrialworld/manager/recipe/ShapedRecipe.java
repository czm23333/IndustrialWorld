package com.industrialworld.manager.recipe;

import com.industrialworld.item.ItemType;
import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShapedRecipe implements CraftingRecipe {
    private final List<List<Object>> matrix;
    private final Object result;
    private final Map<ItemStack, Integer> damages = new HashMap<>();

    protected ShapedRecipe(List<List<Object>> matrix, Object result) {
        if (matrix.size() <= 0 || matrix.size() > 3 || matrix.get(0).size() <= 0 || matrix.get(0).size() > 3) {
            throw new IllegalArgumentException("Incorrect size of recipe");
        }
        this.matrix = matrix;

        this.result = result;
    }

    static void checkItemDamage(List<List<ItemStack>> matrix, Map<Integer, ItemStack> damage, Map<ItemStack, Integer> damages) {
        for (int i = 0; i < matrix.size(); ++i) {
            List<ItemStack> row = matrix.get(i);
            for (int j = 0; j < row.size(); ++j) {
                ItemStack is = row.get(j);
                final int finalI = i;
                final int finalJ = j;
                damages.forEach((items, dmg) -> {
                    ItemStack temp;
                    if (is == null)
                        temp = null;
                    else {
                        temp = is.clone();
                        temp.setDurability((short) 0);
                    }

                    if (ItemStackUtil.isSimilar(items, temp)) {
                        ItemStack newIs = is.clone();
                        newIs.setDurability((short) (newIs.getDurability() + dmg));
                        if (newIs.getDurability() > newIs.getType().getMaxDurability())
                            newIs = new ItemStack(Material.AIR);
                        if (damage != null) {
                            damage.put(finalI * 3 + finalJ + 1, newIs);
                        }
                    }
                });
            }
        }
    }

    @Override
    public MatchInfo matches(List<List<ItemStack>> matrix, @Nullable Map<Integer, ItemStack> damage) {
        if (matrix.size() != 3) {
            return new MatchInfo(false, false, null);
        }

        IWMaterial material = null;

        for (int i = 0; i < matrix.size(); i++) {
            List<ItemStack> row = matrix.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (this.matrix.get(i).get(j) instanceof ItemStack) {
                    ItemStack is = row.get(j);
                    ItemStack temp;
                    if (is == null)
                        temp = null;
                    else {
                        temp = is.clone();
                        if (temp.getType().getMaxDurability() != 0)
                            temp.setDurability((short) 0);
                    }

                    if (!ItemStackUtil.isSimilar((ItemStack) this.matrix.get(i).get(j), temp)) {
                        return new MatchInfo(false, false, null);
                    }
                } else if (this.matrix.get(i).get(j) instanceof ItemType) {
                    IWMaterial currentMaterial = ItemStackUtil.getItemMaterial(row.get(j));
                    if (material == null || material.equals(currentMaterial)) {
                        material = currentMaterial;
                    } else {
                        return new MatchInfo(false, false, null);
                    }
                    if (!ItemStackUtil.getItemType(row.get(j)).equals(this.matrix.get(i).get(j))) {
                        return new MatchInfo(false, false, null);
                    }
                } else if (this.matrix.get(i).get(j) == null) {
                    if (row.get(j) != null)
                        return new MatchInfo(false, false, null);
                } else {
                    throw new IllegalArgumentException("The object in matrix is neither ItemStack nor ItemType, it is" +
                                                       " " +
                                                       this.matrix.get(i).get(j).getClass().getName());
                }
            }
        }

        // check for damage to items.
        if (damage != null)
            checkItemDamage(matrix, damage, this.damages);

        return new MatchInfo(true, material != null, material);
    }

    @Override
    public CraftingRecipe addItemCost(ItemStack is, int durability) {
        this.damages.put(is, durability);
        return this;
    }

    public RecipeChoice.MaterialChoice getChoiceAt(int slot) {
        Object ing = matrix.get(slot / 3).get(slot % 3);
        if (ing instanceof ItemStack)
            return new RecipeChoice.MaterialChoice(((ItemStack) ing).getType());
        else if (ing instanceof ItemType)
            return new RecipeChoice.MaterialChoice(((ItemType) ing).getTemplate()
                                                           .getAllItems()
                                                           .stream()
                                                           .map(ItemStack::getType)
                                                           .collect(Collectors.toList()));
        else
            return null;
    }

    @Override
    public ItemStack getResult(IWMaterial iwMaterial) {
        if (result instanceof ItemStack) {
            return ((ItemStack) result).clone();
        } else if (result instanceof ItemType) {
            return ((ItemType) result).getTemplate().getItemStack(iwMaterial);
        } else {
            return new ItemStack(Material.AIR);
        }
    }
}

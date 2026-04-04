package me.ghoul.qoh.neoforge.datagen;

import me.ghoul.qoh.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(arg, completableFuture);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.OWNER_TAG.get(), 1)
                .requires(Items.NAME_TAG)
                .requires(Items.FEATHER)
                .unlockedBy("has_name_tag", has(Items.NAME_TAG))
                .save(recipeOutput);
    }
}

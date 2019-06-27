package com.mrntlu.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mrntlu.foodrecipes.models.Recipe;
import com.mrntlu.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private String mRecipeId;
    private boolean mDidRetrieveRecipe;

    public RecipeViewModel(){
        mRecipeRepository=RecipeRepository.getInstance();
        mDidRetrieveRecipe=false;
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipeRepository.getRecipe();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeRepository.isRecipeRequestTimedOut();
    }

    public void searchRecipeById(String recipeId){
        mRecipeId=recipeId;
        mRecipeRepository.searchRecipeById(recipeId);
    }

    public String getRecipeId() {
        return mRecipeId;
    }

    public boolean didRetrieveRecipe() {
        return mDidRetrieveRecipe;
    }

    public void setDidRetrieveRecipe(boolean mDidRetrieveRecipe) {
        this.mDidRetrieveRecipe = mDidRetrieveRecipe;
    }
}

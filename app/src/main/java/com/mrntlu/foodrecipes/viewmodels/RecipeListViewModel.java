package com.mrntlu.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mrntlu.foodrecipes.models.Recipe;
import com.mrntlu.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private boolean mIsViewingRecipes;
    private boolean mIsPerformingQuery;

    public RecipeListViewModel(){
        mRecipeRepository=RecipeRepository.getInstance();
        mIsPerformingQuery=false;
    }

    public LiveData<Boolean> isQueryExhausted(){
        return mRecipeRepository.isQueryExhausted();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipeRepository.getRecipes();
    }

    public void searchRecipesApi(String query,int pageNumber){
        mIsViewingRecipes=true;
        mIsPerformingQuery=true;
        mRecipeRepository.searchRecipesApi(query,pageNumber);
    }

    public void searchNextPage(){
        if (!isPerformingQuery() && mIsViewingRecipes && !isQueryExhausted().getValue()){
            mRecipeRepository.searchNextPage();
        }
    }

    public boolean isViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean mIsViewingRecipes) {
        this.mIsViewingRecipes = mIsViewingRecipes;
    }

    public boolean isPerformingQuery() {
        return mIsPerformingQuery;
    }

    public void setIsPerformingQuery(boolean mIsPerformingQuery) {
        this.mIsPerformingQuery = mIsPerformingQuery;
    }

    public boolean onBackPressed(){
        if (mIsPerformingQuery){
            mRecipeRepository.cancelRequest();
            mIsPerformingQuery=false;
        }
        if (mIsViewingRecipes){
            mIsViewingRecipes=false;
            return false;
        }
        return true;
    }
}

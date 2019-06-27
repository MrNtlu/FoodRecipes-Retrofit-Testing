package com.mrntlu.foodrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mrntlu.foodrecipes.adapters.OnRecipeListener;
import com.mrntlu.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.mrntlu.foodrecipes.models.Recipe;
import com.mrntlu.foodrecipes.util.Testing;
import com.mrntlu.foodrecipes.util.VerticalSpacingItemDecorator;
import com.mrntlu.foodrecipes.viewmodels.RecipeListViewModel;
import java.util.List;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {
    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView=findViewById(R.id.recipe_list);
        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        searchView=findViewById(R.id.search_view);

        initRecyclerView();
        subscribeObservers();
        initSearchView();
        if (!mRecipeListViewModel.isViewingRecipes()){
            displaySearchCategories();
        }
        setSupportActionBar(((Toolbar)findViewById(R.id.toolbar)));
    }

    private void subscribeObservers(){
        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes!=null) {
                    if (mRecipeListViewModel.isViewingRecipes()) {
                        Testing.printRecipes(recipes, TAG);
                        mRecipeListViewModel.setIsPerformingQuery(false);
                        mAdapter.setRecipes(recipes);
                    }
                }
            }
        });

        mRecipeListViewModel.isQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    mAdapter.setQueryExhausted();
                }
            }
        });
    }

    private void initRecyclerView(){
        mAdapter=new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator=new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)){
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });
    }


    private void initSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.displayLoading();
                mRecipeListViewModel.searchRecipesApi(s,1);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent=new Intent(this,RecipeActivity.class);
        intent.putExtra("recipe",mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        mAdapter.displayLoading();
        mRecipeListViewModel.searchRecipesApi(category,1);
        searchView.clearFocus();
    }

    private void displaySearchCategories(){
        mRecipeListViewModel.setIsViewingRecipes(false);
        mAdapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.onBackPressed()){
            super.onBackPressed();
        }else{
            displaySearchCategories();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.action_categories){
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}

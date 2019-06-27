package com.mrntlu.foodrecipes.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mrntlu.foodrecipes.R;
import com.mrntlu.foodrecipes.models.Recipe;
import com.mrntlu.foodrecipes.util.Constants;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecipeRecyclerAdapter";

    private static final int RECIPE_TYPE=1,LOADING_TYPE=2,CATEGORY_TYPE=3,EXHAUSTED_TYPE=4;

    private List<Recipe> mRecipes;
    private OnRecipeListener onRecipeListener;

    public RecipeRecyclerAdapter(OnRecipeListener onRecipeListener) {
        this.onRecipeListener = onRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=null;
        switch (viewType){
            case RECIPE_TYPE:{
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item,parent,false);
                return new RecipeViewHolder(view,onRecipeListener);
            }case LOADING_TYPE:{
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item,parent,false);
                return new LoadingViewHolder(view);
            }case CATEGORY_TYPE:{
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item,parent,false);
                return new CategoryViewHolder(view,onRecipeListener);
            }case EXHAUSTED_TYPE:{
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_exhausted,parent,false);
                return new SearchExhaustedViewHolder(view);
            }default:{
                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item,parent,false);
                return new RecipeViewHolder(view,onRecipeListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecipeViewHolder) {
            RequestOptions requestOptions=new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);
            RecipeViewHolder viewHolder=((RecipeViewHolder) holder);

            Glide.with(viewHolder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(mRecipes.get(position).getImage_url())
                    .into(viewHolder.image);

            viewHolder.title.setText(mRecipes.get(position).getTitle());
            viewHolder.publisher.setText(mRecipes.get(position).getPublisher());
            viewHolder.socialScore.setText(String.valueOf(Math.round(mRecipes.get(position).getSocial_rank())));

        }else if (holder instanceof CategoryViewHolder){
            RequestOptions requestOptions=new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background);
            CategoryViewHolder viewHolder=((CategoryViewHolder) holder);

            Uri path=Uri.parse("android.resource://com.mrntlu.foodrecipes/drawable/"+mRecipes.get(position).getImage_url());
            Glide.with(viewHolder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(path)
                    .into(viewHolder.categoryImage);

            viewHolder.categoryTitle.setText(mRecipes.get(position).getTitle());
        }
    }

    public void setQueryExhausted(){
        hideLoading();
        Recipe exhaustedRecipe=new Recipe();
        exhaustedRecipe.setTitle("EXHAUSTED...");
        mRecipes.add(exhaustedRecipe);
        notifyDataSetChanged();
    }

    private void hideLoading(){
        if (isLoading()){
            for (Recipe recipe:mRecipes){
                if (recipe.getTitle().equals("LOADING:::")){
                    mRecipes.remove(recipe);
                }
            }
            notifyDataSetChanged();
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (mRecipes.get(position).getSocial_rank()==-1){
            return CATEGORY_TYPE;
        }else if (mRecipes.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }else if (mRecipes.get(position).getTitle().equals("EXHAUSTED...")){
            return EXHAUSTED_TYPE;
        }else if (position==mRecipes.size()-1 && position!=0 && !mRecipes.get(position).getTitle().equals("EXHAUSTED...")){
            return LOADING_TYPE;
        }else{
            return RECIPE_TYPE;
        }
    }

    public void displayLoading(){
        if (!isLoading()){
            Recipe recipe=new Recipe();
            recipe.setTitle("LOADING...");
            List<Recipe> loadingList=new ArrayList<>();
            loadingList.add(recipe);
            mRecipes=loadingList;
            notifyDataSetChanged();
        }
    }

    public void displaySearchCategories(){
        List<Recipe> categories=new ArrayList<>();
        for (int i=0;i< Constants.DEFAULT_SEARCH_CATEGORIES.length;i++){
            Recipe recipe=new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        mRecipes=categories;
        notifyDataSetChanged();
    }

    private boolean isLoading(){
        if (mRecipes!=null && mRecipes.size()>0){
            if (mRecipes.get(mRecipes.size()-1).getTitle().equals("LOADING...")){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mRecipes!=null?mRecipes.size():0;
    }

    public void setRecipes(List<Recipe> recipes){
        mRecipes=recipes;
        notifyDataSetChanged();
    }

    public Recipe getSelectedRecipe(int position){
        if (mRecipes!=null){
            if (mRecipes.size()>0){
                return mRecipes.get(position);
            }
        }
        return null;
    }

    private class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title,publisher,socialScore;
        AppCompatImageView image;
        OnRecipeListener onRecipeListener;

        RecipeViewHolder(@NonNull View itemView,OnRecipeListener onRecipeListener) {
            super(itemView);
            this.onRecipeListener=onRecipeListener;
            title=itemView.findViewById(R.id.recipe_title);
            publisher=itemView.findViewById(R.id.recipe_publisher);
            socialScore=itemView.findViewById(R.id.recipe_social_score);
            image=itemView.findViewById(R.id.recipe_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRecipeListener.onRecipeClick(getAdapterPosition());
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class SearchExhaustedViewHolder extends RecyclerView.ViewHolder{
        SearchExhaustedViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnRecipeListener listener;
        CircleImageView categoryImage;
        TextView categoryTitle;

        CategoryViewHolder(@NonNull View itemView,OnRecipeListener listener) {
            super(itemView);
            categoryImage=itemView.findViewById(R.id.category_image);
            categoryTitle=itemView.findViewById(R.id.category_title);
            this.listener=listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onCategoryClick(categoryTitle.getText().toString());
        }
    }
}

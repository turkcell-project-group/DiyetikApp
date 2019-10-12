package com.project.diyetikapp.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.diyetikapp.Common.Common;
import com.project.diyetikapp.Database.Database;
import com.project.diyetikapp.Favorites;
import com.project.diyetikapp.FoodDetail;
import com.project.diyetikapp.FoodList;
import com.project.diyetikapp.Interface.ItemClickListener;
import com.project.diyetikapp.Model.Food;
import com.project.diyetikapp.Model.Order;
import com.project.diyetikapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, viewGroup, false);
        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder viewHolder, final int i) {
        viewHolder.food_name.setText(favoritesList.get(i).getFoodName());
        viewHolder.food_price.setText(String.format("$ %s", favoritesList.get(i).getFoodPrice().toString()));
        Picasso.with(context).load(favoritesList.get(i).getFoodImage()).
        into(viewHolder.food_image);


        viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExists = new Database(context).checkFoodExists(favoritesList.get(i).getFoodId(), Common.currentUser.getHomeAdress());

                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favoritesList.get(i).getFoodId(),
                            favoritesList.get(i).getFoodName(),
                            "1",
                            favoritesList.get(i).getFoodPrice(),
                            favoritesList.get(i).getFoodDiscount(),
                            favoritesList.get(i).getFoodDescription()
                    ));
                } else {

                    new Database(context).increaseCart(Common.currentUser.getPhone(),
                            favoritesList.get(i).getFoodId());

                }
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();

            }
        });


        final Favorites local = favoritesList.get(i);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                // start new activity
                Intent foodDetail = new Intent(context, FoodDetail.class);
                // Because category ıd is key ,so we just get key of this item
                foodDetail.putExtra("FoodId", favoritesList.get(i).getFoodId());// send foodId to new Activity
                context.startActivity(foodDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void removeItem(int position){
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Favorites item,int position){
        favoritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favorites getItem(int position){
        return favoritesList.get(position);
    }
}

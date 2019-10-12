package com.project.diyetikapp;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.project.diyetikapp.Common.Common;
import com.project.diyetikapp.Database.Database;
import com.project.diyetikapp.Helper.RecyclerItemTouchHelper;
import com.project.diyetikapp.Interface.RecyclerItemTouchListener;
import com.project.diyetikapp.Model.Order;
import com.project.diyetikapp.ViewHolder.FavoritesAdapter;
import com.project.diyetikapp.ViewHolder.FavoritesViewHolder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FavoritesAdapter adapter;
    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rootLayout = findViewById(R.id.root_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_favorites);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_from_left);
        recyclerView.setLayoutAnimation(controller);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
loadFavorites();
    }

    private void loadFavorites() {
        adapter = new FavoritesAdapter(this,new Database(this).getAllFavorites(Common.currentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int directions, int position) {
       if(viewHolder instanceof FavoritesViewHolder){
           String name = ((FavoritesAdapter)recyclerView.getAdapter()).getItem(position).getFoodName();
           final Favorites deleteItem = ((FavoritesAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
           final int deleteIndex = viewHolder.getAdapterPosition();
           new Database(getBaseContext()).removeFromFavorites(deleteItem.getFoodId(), Common.currentUser.getPhone());
           Snackbar snackbar = Snackbar.make(rootLayout, name + "removed from favorites!", Snackbar.LENGTH_LONG);
           snackbar.setAction("UNDO", new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   adapter.restoreItem(deleteItem, deleteIndex);
                   new Database(getApplication()).addToFavorites(deleteItem) ;
                   /*int total = 0;
                   List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                   for (Order item : orders)
                       total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                   Locale locale = new Locale("en", "US");
                   NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                   txtTotalPrice.setText(fmt.format(total));*/
               }
           });
           snackbar.setActionTextColor(Color.YELLOW);
           snackbar.show();

       }
    }
}

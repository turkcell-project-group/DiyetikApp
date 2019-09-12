package com.project.diyetikapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.project.diyetikapp.Common.Common;
import com.project.diyetikapp.Database.Database;
import com.project.diyetikapp.Interface.ItemClickListener;
import com.project.diyetikapp.Model.Category;
import com.project.diyetikapp.Model.Food;
import com.project.diyetikapp.ViewHolder.FoodViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference foodList;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search functionalty
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favorites
    Database localDB;

    //facebook share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SwipeRefreshLayout swipeRefreshLayout;

//create target from picasso
    Target target=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            //create photo from bitmap
            SharePhoto photo=new SharePhoto.Builder().setBitmap(bitmap).build();

            if (ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content=new SharePhotoContent.Builder().addPhoto(photo).build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_food_list);

        //init facebook
        callbackManager=CallbackManager.Factory.create();
        shareDialog=new ShareDialog(this);

        // Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        //Local DB
        localDB = new Database(this);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnectedToInterner(getBaseContext())) {
                        loadListFood(categoryId);
                    }
                    else{
                        Toast.makeText(FoodList.this,"Lütfen bağlantınızı kontrol ediniz!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnectedToInterner(getBaseContext())) {
                        loadListFood(categoryId);
                    }
                    else{
                        Toast.makeText(FoodList.this,"Lütfen bağlantınızı kontrol ediniz!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });


        //Load food list
        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        //materialSearchBar.setSpeechMode(false); No need, because we already define it at XML
        loadSuggest(); // write function to load Suggest from Firebase
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //when user type their text, we will change suggest list

                List<String> suggest = new ArrayList<String>();
                for (String search :suggestList){ // loop in suggest List.
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))suggest.add(search);

                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                 // when search bar is close
                //restore original suggest adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }
            @Override
            public void onSearchConfirmed(CharSequence text) {
                // when search finish
                //show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
        
    }

    private void startSearch(CharSequence text)  {
        //create query by name
        Query searchByName = foodList.orderByChild("Name").equalTo(text.toString());
        //create options with query
        FirebaseRecyclerOptions<Food> foodOptions =new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).
                        into(viewHolder.food_image);
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // start new activity
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        // Because category ıd is key ,so we just get key of this item
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());// send foodId to new Activity
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter); // set adapter for recyler view search result
    }

    private void loadSuggest() {
        foodList.orderByChild("MenuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName()); // add name of food to suggest list

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {



            }
        });
    }

    private void loadListFood(String categoryId) {

        //create query by category Id
        Query searchByName = foodList.orderByChild("menuId").equalTo(categoryId);
        //create options with query
        FirebaseRecyclerOptions<Food> foodOptions =new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("$ %s",model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage()).
                        into(viewHolder.food_image);

                //add favorites
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to share
                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(getApplicationContext()).load(model.getImage()).into(target);
                    }
                });

                //Click to change state of favorites
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())){
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+"was added to Favorites", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+"was removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // start new activity
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        // Because category ıd is key ,so we just get key of this item
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());// send foodId to new Activity
                        startActivity(foodDetail);
                    }
                });
            }
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);

            }
        };
        adapter.startListening();
        Log.d("Tag",""+ adapter.getItemCount());


        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        searchAdapter.stopListening();
    }
}

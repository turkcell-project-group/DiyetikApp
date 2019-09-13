package com.project.diyetikapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.diyetikapp.Common.Common;
import com.project.diyetikapp.Database.Database;
import com.project.diyetikapp.Model.MyResponse;
import com.project.diyetikapp.Model.Notification;
import com.project.diyetikapp.Model.Order;
import com.project.diyetikapp.Model.Request;
import com.project.diyetikapp.Model.Sender;
import com.project.diyetikapp.Model.Token;
import com.project.diyetikapp.Remote.APIService;
import com.project.diyetikapp.ViewHolder.CartAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;
    APIService mService;

    Place shippingAddress;

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
        setContentView(R.layout.activity_cart);



        //Init Service
        mService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (FButton) findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Kartınız boş!", Toast.LENGTH_SHORT).show();

            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your adress:");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_adress_comment = inflater.inflate(R.layout.order_adress_comment, null);

        PlaceAutocompleteFragment edtAdress=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        edtAdress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText)edtAdress.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Adresinizi giriniz");
        ((EditText)edtAdress.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);

        edtAdress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                shippingAddress=place;
            }

            @Override
            public void onError(Status status) {

                Log.e("ERROR",status.getStatusMessage());
            }
        });

       // final MaterialEditText edtAdress = order_adress_comment.findViewById(R.id.edtAdress);
        final MaterialEditText edtComment = order_adress_comment.findViewById(R.id.edtComment);
        alertDialog.setView(order_adress_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        shippingAddress.getAddress().toString(),
                        txtTotalPrice.getText().toString(),
                        "1",
                        cart,
                        String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress),
                        edtComment.getText().toString()
                );
                // submit to firebase
                // we will using System.Currentmilli to key
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number).setValue(request);
                // delete cart
                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);
                /*Toast.makeText(Cart.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                finish();*/
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });
        alertDialog.show();


    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapshot.getValue(Token.class);
                    Notification notification = new Notification("Diyet Sepeti", "Tou have new order" + order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                   if(response.code() == 200) {
                                       if (response.body().success == 1) {
                                           Toast.makeText(Cart.this, "Thank you,Order Place", Toast.LENGTH_SHORT).show();
                                           finish();
                                       } else {
                                           Toast.makeText(Cart.this, "Failed!!", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR",t.getMessage());

                                }
                            });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood() {
        cart = new Database(this).getCarts();

        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate total price
        int total = 0;
        for (Order order : cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {

        // we will remove item at List<Order> by positoion
        cart.remove(position);

        // we will delete all old data from all old data sqlite
        new Database(this).cleanCart();

        //we will update new data from list<order> to sqlite
        for (Order item : cart)
            new Database(this).addToCart(item);

        //refresh
        loadListFood();
    }
}

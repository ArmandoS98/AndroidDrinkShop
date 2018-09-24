package aesc.dev.androiddrinkshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import aesc.dev.androiddrinkshop.Adapter.CartAdapter;
import aesc.dev.androiddrinkshop.Adapter.FavoriteAdapter;
import aesc.dev.androiddrinkshop.Database.ModelDB.Cart;
import aesc.dev.androiddrinkshop.Database.ModelDB.Favorite;
import aesc.dev.androiddrinkshop.Retrofit.IDrinkShopAPI;
import aesc.dev.androiddrinkshop.Utils.Common;
import aesc.dev.androiddrinkshop.Utils.RecyclerItemTouchHelper;
import aesc.dev.androiddrinkshop.Utils.RecyclerItemTouchHelperListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    private static final String TAG = "CartActivity";
    RecyclerView recycler_cart;
    Button btn_place_order;
    CartAdapter adapter;

    List<Cart> cartList = new ArrayList<>();

    CompositeDisposable compositeDisposable;

    RelativeLayout rootLayout;

    IDrinkShopAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        compositeDisposable = new CompositeDisposable();
        mService = Common.getAPI();
        recycler_cart = findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_cart);

        btn_place_order = findViewById(R.id.btn_place_order);
        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

        rootLayout = findViewById(R.id.rootLayout);

        loadCartItems();

    }

    private void placeOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Submit Order");

        View submit_order_layout = LayoutInflater.from(this).inflate(R.layout.submit_order_layout, null);

        final EditText edt_comment = submit_order_layout.findViewById(R.id.edt_comment);
        final EditText edt_other_address = submit_order_layout.findViewById(R.id.edt_other_address);

        final RadioButton rdi_user_address = submit_order_layout.findViewById(R.id.rdi_user_address);
        final RadioButton rdi_other_address = submit_order_layout.findViewById(R.id.rdi_other_address);

        //Events
        rdi_user_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    edt_other_address.setEnabled(false);
            }
        });

        rdi_other_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    edt_other_address.setEnabled(true);
            }
        });

        builder.setView(submit_order_layout);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String orderComment = edt_comment.getText().toString();
                final String orderAddress;
                if (rdi_user_address.isChecked())
                    orderAddress = Common.currentUser.getAddress();
                else if (rdi_other_address.isChecked())
                    orderAddress = edt_other_address.getText().toString();
                else
                    orderAddress = "";
                //submit order
                compositeDisposable.add(
                        Common.cartRepository.getCartItems()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Cart>>() {
                            @Override
                            public void accept(List<Cart> carts) throws Exception {
                                if (!TextUtils.isEmpty(orderAddress))
                                    sendOrderToServer(Common.cartRepository.sumPrice(),
                                            carts,
                                            orderComment,orderAddress);
                                else
                                    Toast.makeText(CartActivity.this, "Order address can't null", Toast.LENGTH_SHORT).show();
                            }
                        })
                );
            }
        });


        builder.show();
    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment, String orderAddress) {
        if (carts.size() > 0){
            String orderDetail = new Gson().toJson(carts);

            mService.submitOrder(sumPrice,orderDetail,orderComment,orderAddress,Common.currentUser.getPhone())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Toast.makeText(CartActivity.this, "Order Submit", Toast.LENGTH_SHORT).show();

                            //Clerar cart
                            Common.cartRepository.emptyCart();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, "ERROR: " + t.getMessage());
                        }
                    });
        }
    }


    private void loadCartItems() {
        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {
                        displayCartItems(carts);
                    }
                })
        );
    }

    private void displayCartItems(List<Cart> carts) {
        cartList = carts;
        adapter = new CartAdapter(this,carts);
        recycler_cart.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    //Exit application when click BACK button
    /*boolean isBackButtonClicked = false;

    @Override
    public void onBackPressed() {
        if (isBackButtonClicked){
            super.onBackPressed();
            return;
        }
        this.isBackButtonClicked = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    protected void onResume() {
        //isBackButtonClicked = false;
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartAdapter.CartViewHolder){
            String name = cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            //Delete item from adapter
            adapter.removeItem(deletedIndex);
            //Delete item from room database
            Common.cartRepository.deleteCart(deletedItem);

            Snackbar snackbar = Snackbar.make(rootLayout, new StringBuilder(name).append(" removed from Favorites List").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deletedItem,deletedIndex);
                    Common.cartRepository.insertToCart(deletedItem);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}

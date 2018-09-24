package aesc.dev.androiddrinkshop.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import aesc.dev.androiddrinkshop.Interface.ItemClickListener;
import aesc.dev.androiddrinkshop.R;

public class DrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView img_product;
    TextView txt_drink_name;
    TextView txt_drink_price;

    ItemClickListener itemClickListener;

    ImageView btn_add_to_cart;
    ImageView btn_favorite;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DrinkViewHolder(View itemView) {
        super(itemView);

        img_product = itemView.findViewById(R.id.image_product);
        txt_drink_name = itemView.findViewById(R.id.txt_drink_name);
        txt_drink_price = itemView.findViewById(R.id.txt_drink_price);
        btn_add_to_cart = itemView.findViewById(R.id.btn_add_cart);
        btn_favorite = itemView.findViewById(R.id.btn_favorite);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v);
    }
}

package com.fci_zu_eng_gemy_95.foodsorders.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Order;
import com.fci_zu_eng_gemy_95.foodsorders.R;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
    import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private Context context;
    private List<Order> orders ;

    public CartAdapter(Context context, List<Order> orders) {
        this.orders = new ArrayList<>();
        this.context = context;
        this.orders = orders;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {

        TextDrawable drawable = TextDrawable.builder()
                .buildRoundRect(""+orders.get(position).getQuantity(), Color.RED,10);
        holder.img_cart_count.setImageDrawable(drawable);

        holder.txt_cart_item_name.setText(orders.get(position).getProductName());

        Locale locale = new Locale("en","US");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(orders.get(position).getPrice())) * (Integer.parseInt(orders.get(position).getQuantity()));
        holder.txt_cart_price.setText(numberFormat.format(price));

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    TextView txt_cart_item_name, txt_cart_price;
    ImageView img_cart_count;

    public CartViewHolder(View itemView) {
        super(itemView);

        txt_cart_item_name = itemView.findViewById(R.id.cart_item_name);
        txt_cart_price = itemView.findViewById(R.id.cart_item_price);
        img_cart_count = itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Your action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}

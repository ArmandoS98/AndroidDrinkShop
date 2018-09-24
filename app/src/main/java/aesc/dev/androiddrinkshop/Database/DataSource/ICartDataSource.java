package aesc.dev.androiddrinkshop.Database.DataSource;

import java.util.List;

import aesc.dev.androiddrinkshop.Database.ModelDB.Cart;
import io.reactivex.Flowable;

public interface ICartDataSource {
    Flowable<List<Cart>> getCartItems();
    Flowable<List<Cart>> getCartItemById(int cartItemId);
    int countCartItems();
    float sumPrice();
    void emptyCart();
    void insertToCart(Cart...carts);
    void updateCart(Cart...carts);
    void deleteCart(Cart...carts);
}

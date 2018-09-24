package aesc.dev.androiddrinkshop.Utils;

import java.util.ArrayList;
import java.util.List;

import aesc.dev.androiddrinkshop.Database.DataSource.CartRepository;
import aesc.dev.androiddrinkshop.Database.DataSource.FavoriteRepository;
import aesc.dev.androiddrinkshop.Database.Local.AESCRoomDatabase;
import aesc.dev.androiddrinkshop.Model.Category;
import aesc.dev.androiddrinkshop.Model.Drink;
import aesc.dev.androiddrinkshop.Model.User;
import aesc.dev.androiddrinkshop.Retrofit.IDrinkShopAPI;
import aesc.dev.androiddrinkshop.Retrofit.RetrofitClient;

public class Common {
    public static final String BASE_URL = "http://192.168.1.8/drinkshop/";

    public static final String TOPPING_MENU_ID = "7";

    public static User currentUser = null;
    public static Category currentCategory = null;

    public static List<Drink> toppingList = new ArrayList<>();

    public static double toppingPrice = 0.0;
    public static List<String> toppingAdded = new ArrayList<>();

    //Holde field
    public static int sizeOfCup = -1; // -1 : No chose (error) , 0 : M , 1 : L
    public static int sugar = -1; // -1 : no chose (error)
    public static int ice = -1;

    //Database
    public static AESCRoomDatabase aescRoomDatabase;
    public static CartRepository cartRepository;
    public static FavoriteRepository favoriteRepository;

    public static IDrinkShopAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }

}

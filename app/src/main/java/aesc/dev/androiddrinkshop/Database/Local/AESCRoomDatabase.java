package aesc.dev.androiddrinkshop.Database.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import aesc.dev.androiddrinkshop.Database.ModelDB.Cart;
import aesc.dev.androiddrinkshop.Database.ModelDB.Favorite;

@Database(entities = {Cart.class, Favorite.class}, version = 1)
public abstract class AESCRoomDatabase extends RoomDatabase{

    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoriteDAO();

    private static AESCRoomDatabase instance;

    public static AESCRoomDatabase getInstance(Context context){
        if (instance == null)
            instance = Room.databaseBuilder(context, AESCRoomDatabase.class,"AESC_DrinkShopDB")
                    .allowMainThreadQueries()
                    .build();

        return instance;
    }
}

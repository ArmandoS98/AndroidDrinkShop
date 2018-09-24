package aesc.dev.androiddrinkshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import aesc.dev.androiddrinkshop.Adapter.DrinkAdapter;
import aesc.dev.androiddrinkshop.Model.Drink;
import aesc.dev.androiddrinkshop.Retrofit.IDrinkShopAPI;
import aesc.dev.androiddrinkshop.Utils.Common;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    List<String> suggestList = new ArrayList<>();
    List<Drink> localDataSource = new ArrayList<>();

    MaterialSearchBar searchBar;
    RecyclerView recycler_search;

    IDrinkShopAPI mService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    DrinkAdapter searchAdapter, adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Init Service
        mService = Common.getAPI();

        recycler_search = findViewById(R.id.recycler_search);
        recycler_search.setLayoutManager(new GridLayoutManager(this,2));
        recycler_search.setHasFixedSize(true);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Enter your drink");

        loadAllDrinks();

        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search:suggestList){
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recycler_search.setAdapter(adapter);  //Restore full list of drink
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        List<Drink> result = new ArrayList<>();
        for (Drink drink: localDataSource)
            if (drink.Name.contains(text))
                result.add(drink);
        searchAdapter = new DrinkAdapter(this,result);
        recycler_search.setAdapter(searchAdapter);
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void loadAllDrinks() {
        compositeDisposable.add(mService.getAllDrinks().observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<Drink>>() {
            @Override
            public void accept(List<Drink> drinks) throws Exception {
                displayListDrink(drinks);
                buildSuggestList(drinks);
            }
        }));
    }

    private void buildSuggestList(List<Drink> drinks) {
        for (Drink drink:drinks)
            suggestList.add(drink.Name);
        searchBar.setLastSuggestions(suggestList);
    }

    private void displayListDrink(List<Drink> drinks) {
        localDataSource = drinks;
        adapter = new DrinkAdapter(this,drinks);
        recycler_search.setAdapter(adapter);
    }
}

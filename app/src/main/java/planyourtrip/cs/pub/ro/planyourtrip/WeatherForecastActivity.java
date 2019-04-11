package planyourtrip.cs.pub.ro.planyourtrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Objects;

import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class WeatherForecastActivity extends AppCompatActivity {

    private TextView selectCityTextView;
    private LottieAnimationView lottieAnimationView;

    private ArrayList<CitySearchObject> searchCitiesArrayList = new ArrayList<>();
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        selectCityTextView = (TextView)findViewById(R.id.selectCityTextView);
        lottieAnimationView = (LottieAnimationView)findViewById(R.id.animationView);

        lottieAnimationView.setAnimation(R.raw.weather);
        lottieAnimationView.playAnimation();

        fetchCitiesList();

        selectCityTextView.setOnClickListener(v -> showSearchDialog());

        setTitle("Weather Forecast");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void showSearchDialog() {
        new CitySearchDialogCompat(WeatherForecastActivity.this, getString(R.string.search_title),
                getString(R.string.search_hint), null, searchCitiesArrayList,
                (SearchResultListener<CitySearchObject>) (dialog, item, position) -> {
                    Intent intent = WeatherActivity.getStartIntent(WeatherForecastActivity.this, item.getName(),
                            item.getId(), true);
                    startActivity(intent);
                    dialog.dismiss();
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void fetchCitiesList() {
        searchCitiesArrayList.add(new CitySearchObject("Piatra Neamț", R.drawable.piatra_neamt, 100));
        searchCitiesArrayList.add(new CitySearchObject("Brașov", R.drawable.brasov,  101));
        searchCitiesArrayList.add(new CitySearchObject("Sighișoara", R.drawable.sighisoara, 102));
        searchCitiesArrayList.add(new CitySearchObject("Iași", R.drawable.iasi, 103));
    }
}

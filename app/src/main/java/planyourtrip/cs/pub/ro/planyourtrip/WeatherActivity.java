package planyourtrip.cs.pub.ro.planyourtrip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WeatherActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    //private TextView emptyTextView;
    private TextView weatherConditionTextView;
    private TextView tempTextView;
    private ImageView weatherIconImageView;
    private TextView dayOfWeekTextView;
    private TextView todayTextView;
    private TextView minTempTextView;
    private TextView maxTempTextView;
    //RecyclerView forecastList;

    private City city;
    //private String apiKey = "48628f0e73047d040da372c21b8b5d2e";
    private String currentWeatherUrlPNeamt = "https://api.openweathermap.org/data/2.5/weather?q=Piatra%20Neamț&units=metric&appid=48628f0e73047d040da372c21b8b5d2e";
    private String currentWeatherUrlBrasov = "https://api.openweathermap.org/data/2.5/weather?q=Brașov&units=metric&appid=48628f0e73047d040da372c21b8b5d2e";
    private String currentWeatherUrlSighisoara = "https://api.openweathermap.org/data/2.5/weather?q=Sighișoara&units=metric&appid=48628f0e73047d040da372c21b8b5d2e";
    private String currentWeatherUrlIasi = "https://api.openweathermap.org/data/2.5/weather?q=Iași&units=metric&appid=48628f0e73047d040da372c21b8b5d2e";

    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        animationView = (LottieAnimationView)findViewById(R.id.animationView);
        //emptyTextView = (TextView)findViewById(R.id.emptyTextView);
        weatherConditionTextView = (TextView)findViewById(R.id.weatherConditionTextView);
        tempTextView = (TextView)findViewById(R.id.tempTextView);
        weatherIconImageView = (ImageView)findViewById(R.id.weatherIconImageView);
        dayOfWeekTextView = (TextView)findViewById(R.id.dayOfWeekTextView);
        todayTextView = (TextView)findViewById(R.id.todayTextView);
        minTempTextView = (TextView)findViewById(R.id.minTempTextView);
        maxTempTextView = (TextView)findViewById(R.id.maxTempTextView);
        //forecastList = (RecyclerView)findViewById(R.id.forecastListRecyclerView);

        maxTempTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_upward,
                0, 0, 0);
        minTempTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_downward,
                0, 0, 0);


        Intent intent = getIntent();

        //check if it's called after searching for a city in utilities
        boolean isCalledFromUtilities = intent.getBooleanExtra(Constants.EXTRA_MESSAGE_CALLED_FROM_UTILITIES,
                false);
        if (isCalledFromUtilities) {
            //create a new city object by getting values from received intent
            String cityNickname = intent.getStringExtra(Constants.EXTRA_MESSAGE_CITY_NAME);
            String cityID = String.valueOf(intent.getIntExtra(Constants.EXTRA_MESSAGE_CITY_ID, -1));
            city = new City(cityNickname, cityID);
            try {
                fetchWeatherForecast();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //get reference to current City
            /*mCity = (City) intent.getSerializableExtra(Constants.EXTRA_MESSAGE_CITY_OBJECT);
            //get current temperature from FinalCityInfo
            mCurrentTemp = intent.getStringExtra(Constants.CURRENT_TEMP);
            if (mCurrentTemp != null) {
                //if called from within a FinalCityInfo activity
                //directly fetch weather info
                fetchWeatherForecast();
            } else {
                //if called directly from cities list then
                //first fetch current temp here
                fetchCurrentTemp();
            }*/

        }
        //emptyTextView.setText(String.format(getString(R.string.city_not_found), city.getNickname()));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(city.getNickname());
    }

    public static Intent getStartIntent(Context context, City city, String currentTemp) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(Constants.EXTRA_MESSAGE_CITY_OBJECT, city);
        intent.putExtra(Constants.CURRENT_TEMP, currentTemp);
        return intent;
    }

    public static Intent getStartIntent(Context context, String cityName,
                                        int cityId, boolean calledFromUtilities) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(Constants.EXTRA_MESSAGE_CITY_NAME, cityName);
        intent.putExtra(Constants.EXTRA_MESSAGE_CITY_ID, cityId);
        intent.putExtra(Constants.EXTRA_MESSAGE_CALLED_FROM_UTILITIES, calledFromUtilities);
        return intent;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void fetchWeatherForecast() throws XmlPullParserException, IOException {

        new GetWeatherForecast().execute();
    }

    class GetWeatherForecast extends AsyncTask<String, String, String> {

        String currentTemp = null;
        String minTemp = null;
        String maxTemp = null;
        String weatherCondition = null;
        String icon = null;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // getting JSON Object
            // Note that create product url accepts POST method
            String url = null;

            String cityName = city.getNickname();
            if(cityName.equals("Brașov"))
                url = currentWeatherUrlBrasov;
            else if(cityName.equals("Piatra Neamț"))
                url = currentWeatherUrlPNeamt;
            else if(cityName.equals("Iași"))
                url = currentWeatherUrlIasi;
            else if(cityName.equals("Sighișoara"))
                url = currentWeatherUrlSighisoara;

            JSONObject json = jsonParser.makeHttpRequest(url,
                    "GET", null);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {

                if (json != null) {
                    JSONObject jresponse = new JSONObject(json.toString());
                    final String responseString = jresponse.getString("main");
                    String[] components = responseString.split(":|,");

                    currentTemp = components[1];
                    minTemp = components[7];
                    maxTemp = components[9].substring(0, components[9].length() - 1);

                    final String responseString2 = jresponse.getString("weather");
                    String[] components2 = responseString2.split(":|,");
                    weatherCondition = components2[3].substring(1, components2[3].length() - 1);
                    icon = components2[7].substring(1, components2[7].length() - 3);

                } else {
                    JSONObject jresponse = new JSONObject(json.toString());

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            final String DEGREE  = "\u00b0";
            String temp = currentTemp + DEGREE + "C";
            tempTextView.setText(temp);

            minTempTextView.setText(minTemp);
            maxTempTextView.setText(maxTemp);

            weatherConditionTextView.setText(weatherCondition);
            String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());

            dayOfWeekTextView.setText(weekday_name);


            Log.d("DJSADASKSAD", icon);
            String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";

            Picasso.with(getApplicationContext()).load(iconUrl).into(weatherIconImageView);
            cancelAnimation();
        }

    }

    private void cancelAnimation() {
        if (animationView != null) {
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);
        }
    }


}

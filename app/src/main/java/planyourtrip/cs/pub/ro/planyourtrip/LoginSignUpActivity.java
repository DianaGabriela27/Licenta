package planyourtrip.cs.pub.ro.planyourtrip;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginSignUpActivity extends AppCompatActivity {

    LinearLayout loginLayout;
    LinearLayout signUpLayout;
    EditText emailEditText;
    EditText emailSignUpEditText;
    EditText passwordEditText;
    EditText passwordSignUpEditText;
    EditText confirmPasswordEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    TextView loginTextView;
    TextView signUpTextView;
    FlatButton signUpButton;
    FlatButton loginButton;

    JSONParser jsonParser = new JSONParser();

    private static String url_register_user = "http://192.168.43.26:8080/android_registration/add_user.php";
    private static String url_login_user = "http://192.168.43.26:8080/android_registration/login_user.php";

    private static final String TAG_SUCCESS = "success";

    private ProgressDialog pDialog;

    private SharedPreferences sharedPreferences;

    private LoginButtonClickListener loginButtonClickListener = new LoginButtonClickListener();
    private class LoginButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String emailString = emailEditText.getText().toString();
            String passString = passwordEditText.getText().toString();
            //mLoginPresenter.ok_login(emailString, passString, mHandler);
            new LoginUser().execute();
        }
    }

    private SignUpButtonClickListener signUpButtonClickListener = new SignUpButtonClickListener();
    private class SignUpButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String email = emailSignUpEditText.getText().toString();
            String password = passwordSignUpEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if(validateEmail(email)) {
                if(validatePassword(password)) {
                    if(password.equals(confirmPassword)) {
                        new RegisterNewUser().execute();
                    }
                    else {
                        Toast.makeText(LoginSignUpActivity.this, R.string.passwords_check, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private LoginTextViewClickListener loginTextViewClickListener = new LoginTextViewClickListener();
    private class LoginTextViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            loginTextView.setVisibility(View.GONE);
            signUpTextView.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.VISIBLE);
            signUpLayout.setVisibility(View.GONE);
        }
    }

    private SignUpTextViewClickListener signUpTextViewClickListener = new SignUpTextViewClickListener();
    private class SignUpTextViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            signUpTextView.setVisibility(View.GONE);
            loginTextView.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            signUpLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onPause() {
        super.onPause();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        final View decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                        decorView.setSystemUiVisibility(uiOptions);
                    }
                });

        loginLayout = (LinearLayout)findViewById(R.id.login_layout);
        signUpLayout = (LinearLayout)findViewById(R.id.sign_up_layout);

        emailEditText = (EditText)findViewById(R.id.emailEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        emailSignUpEditText = (EditText)findViewById(R.id.emailSignUpEditText);
        passwordSignUpEditText = (EditText)findViewById(R.id.passwordSignUpEditText);
        confirmPasswordEditText = (EditText)findViewById(R.id.confirmPasswordEditText);
        firstNameEditText = (EditText)findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText)findViewById(R.id.lastNameEditText);

        loginTextView = (TextView)findViewById(R.id.loginTextView);
        signUpTextView = (TextView)findViewById(R.id.signUpTextView);

        signUpButton = (FlatButton)findViewById(R.id.signUpButton);
        loginButton = (FlatButton)findViewById(R.id.loginButton);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getRunTimePermissions();
        checkUserSession();

        signUpButton.setOnClickListener(signUpButtonClickListener);
        loginButton.setOnClickListener(loginButtonClickListener);
        loginTextView.setOnClickListener(loginTextViewClickListener);
        signUpTextView.setOnClickListener(signUpTextViewClickListener);
    }

    public void getRunTimePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.CAMERA,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_ADMIN,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.WAKE_LOCK,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.INTERNET,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, 0);
            if (ContextCompat.checkSelfPermission(LoginSignUpActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.VIBRATE,}, 0);
        }
    }

    public void checkUserSession() {
        if (sharedPreferences.getString(Constants.USER_TOKEN, null) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class RegisterNewUser extends AsyncTask<String, String, String> {

        int success = 0;
        String email = null;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginSignUpActivity.this);
            pDialog.setMessage("Adding User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String email = emailSignUpEditText.getText().toString();
            String password = passwordSignUpEditText.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("first_name", firstName));
            params.add(new BasicNameValuePair("last_name", lastName));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_register_user,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                } else {
                    JSONObject jresponse = new JSONObject(json.toString());
                    final String responseString = jresponse.getString("message");

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LoginSignUpActivity.this, responseString, Toast.LENGTH_SHORT).show();
                        }
                    });
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
            // dismiss the dialog once done
            pDialog.dismiss();
            if(success == 1) {
                loginTextView.setVisibility(View.GONE);
                signUpTextView.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.VISIBLE);
                signUpLayout.setVisibility(View.GONE);

                emailEditText.setText(email);

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginSignUpActivity.this, "SignUp Succeeded, please login.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    class LoginUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginSignUpActivity.this);
            pDialog.setMessage("Logging User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString().trim();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_login_user,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    JSONObject jresponse = new JSONObject(json.toString());
                    final String token = jresponse.getString("pid");

                    editor.putString(Constants.USER_TOKEN, token);
                    editor.putString(Constants.USER_EMAIL, email);
                    editor.apply();


                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {

                    JSONObject jresponse = new JSONObject(json.toString());
                    final String responseString = jresponse.getString("message");

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LoginSignUpActivity.this, responseString, Toast.LENGTH_SHORT).show();
                        }
                    });


                    // failed to create product
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
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

    public boolean validateEmail(String email) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        if (!email.equals("") && matcher.matches()) {
            return true;
        } else {
            Toast.makeText(LoginSignUpActivity.this, R.string.invalid_email_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean validatePassword(String passString) {
        if (passString.length() >= 8) {
            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(passString);

            if (matcher.matches()) {
                return true;
            } else {
                Toast.makeText(LoginSignUpActivity.this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(LoginSignUpActivity.this, R.string.password_length, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}

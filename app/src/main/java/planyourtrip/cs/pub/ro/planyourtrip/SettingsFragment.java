package planyourtrip.cs.pub.ro.planyourtrip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.dd.processbutton.iml.ActionProcessButton;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    @BindView(R.id.oldPassword)
    EditText oldPasswordText;
    @BindView(R.id.newPassword)
    EditText newPasswordText;
    @BindView(R.id.confirmPassword)
    EditText confirmPasswordText;
    @BindView(R.id.saveSettingsButton)
    ActionProcessButton saveSettingsButton;

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private static String url_update_user = "http://192.168.43.26:8080/android_registration/update-password.php";
    private static final String TAG_SUCCESS = "success";

    private String mToken;
    private Handler mHandler;
    private Activity mActivity;
    private SharedPreferences mSharedPrefrences;
    private View mView;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    private SaveSettingsButtonClickListener saveSettingsButtonClickListener = new SaveSettingsButtonClickListener();
    private class SaveSettingsButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            hideKeyboard();
            if (checkEmptyText())
                checkPasswordMatch();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, mView);

        mSharedPrefrences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mToken = mSharedPrefrences.getString(Constants.USER_TOKEN, null);
        mHandler = new Handler(Looper.getMainLooper());

        saveSettingsButton.setMode(ActionProcessButton.Mode.ENDLESS);
        saveSettingsButton.setOnClickListener(saveSettingsButtonClickListener);

        return mView;
    }

    private boolean checkEmptyText() {
        if (oldPasswordText.getText().toString().equals("")) {
            oldPasswordText.setError(getString(R.string.required_error));
            oldPasswordText.requestFocus();
            return false;
        } else if (newPasswordText.getText().toString().equals("")) {
            newPasswordText.setError(getString(R.string.required_error));
            newPasswordText.requestFocus();
            return false;
        } else if (confirmPasswordText.getText().toString().equals("")) {
            confirmPasswordText.setError(getString(R.string.required_error));
            confirmPasswordText.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private void checkPasswordMatch() {
        String oldPassword = oldPasswordText.getText().toString();
        String newPassword = newPasswordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();

        if (validatePassword(newPassword)) {
            if (newPassword.equals(confirmPassword)) {
                new UpdateUser().execute();
            } else {
                Toast.makeText(mActivity, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            }
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
                Toast.makeText(mActivity, R.string.invalid_password, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(mActivity, "Password length should be > 8", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Plays the network lost animation in the view
     */
   /* private void networkError() {
        layout.setVisibility(View.INVISIBLE);
        animationView.setVisibility(View.VISIBLE);
        animationView.setAnimation(R.raw.network_lost);
        animationView.playAnimation();
    }*/

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
    }

    private void clearText() {
        oldPasswordText.setText("");
        newPasswordText.setText("");
        confirmPasswordText.setText("");
    }

    class UpdateUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(mActivity);
            pDialog.setMessage("Changing Password...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String email = mSharedPrefrences.getString(Constants.USER_EMAIL, null);
            String newPassword = newPasswordText.getText().toString();
            String oldPassword = oldPasswordText.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("old_password", oldPassword));
            params.add(new BasicNameValuePair("new_password", newPassword));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_user,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    JSONObject jresponse = new JSONObject(json.toString());
                    final String response = jresponse.getString("message");

                    mActivity.runOnUiThread(new Runnable() {

                        public void run() {

                            Toast.makeText(mActivity, response, Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    JSONObject jresponse = new JSONObject(json.toString());
                    final String response = jresponse.getString("message");

                    mActivity.runOnUiThread(new Runnable() {

                        public void run() {

                            Toast.makeText(mActivity, response, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String response) {
            // dismiss the dialog once done
            pDialog.dismiss();
            clearText();
            //Toast.makeText(mActivity, response, Toast.LENGTH_SHORT).show();
        }

    }
}

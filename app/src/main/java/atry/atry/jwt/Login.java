package atry.atry.jwt;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {

    EditText emailET, passwordET;
    Button loginButton;
    String url;
    SharedPreferences prefs;
    String token;
    FragmentTransaction ft;
    String LOG_TAG = "Login";
    BottomNavigationView navigation;
    ProgressDialog progressDialog;
    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (Button) rootView.findViewById(R.id.loginButton);
        emailET = (EditText) rootView.findViewById(R.id.emailET);
        passwordET = (EditText) rootView.findViewById(R.id.passwordET);
        navigation = (BottomNavigationView)getActivity().findViewById(R.id.navigation);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Signing in...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                loginCall();
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        token = prefs.getString("token","Bearer ");

        return rootView;
    }

    private void loginCall() {
        url = getActivity().getResources().getString(R.string.BASE_URL) + "login?email="+emailET.getText().toString().trim()+"&password="+passwordET.getText().toString().trim();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(LOG_TAG, String.valueOf(response));

                        Fragment forgotFragment = new Forgot();
                        Bundle bundle = new Bundle();
                        bundle.putString("email", emailET.getText().toString());
                        forgotFragment.setArguments(bundle);
                        ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content, forgotFragment).commit();
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(LOG_TAG,url);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Something wrong",Toast.LENGTH_SHORT).show();
                        Log.v(LOG_TAG, String.valueOf(error));
                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                String headers = response.headers.get("Authorization");
                Log.v(LOG_TAG,String.valueOf(headers));

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("token",headers);
                editor.commit();
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);

        queue.add(jsObjRequest);
    }

}

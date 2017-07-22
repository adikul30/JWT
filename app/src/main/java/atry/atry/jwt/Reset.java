package atry.atry.jwt;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Reset extends Fragment {

    String LOG_TAG = "Reset";
    Button resetButton;
    String resetKey,email;
    EditText newPasswordET;
    SharedPreferences prefs;
    String token;
    TextView headerText;
    ProgressDialog progressDialog;

    public Reset() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reset, container, false);

        resetButton = (Button) rootView.findViewById(R.id.resetButton);
        newPasswordET = (EditText)rootView.findViewById(R.id.newPasswordET);
        headerText = (TextView)rootView.findViewById(R.id.headerText);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        token = prefs.getString("token","Bearer ");

        Bundle bundle =this.getArguments();
        if(bundle!=null){
            resetKey = bundle.getString("resetKey");
            email = bundle.getString("email");
        }

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Resetting your password");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                resetCall();
            }
        });
        return rootView;
    }

    private void resetCall() {
        String url = getActivity().getResources().getString(R.string.BASE_URL) + "reset?password=" + newPasswordET.getText().toString() + "&re_password=" + newPasswordET.getText().toString() + "&email=" + email + "&reset_key=" + resetKey;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(LOG_TAG, String.valueOf(response));
                        progressDialog.dismiss();
                        headerText.setText(String.valueOf(response));

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(LOG_TAG, String.valueOf(error));
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                }) {

//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("password", newPassword.getText().toString());
//                params.put("re_password", confirmNewPassword.getText().toString());
//                params.put("forgot_email", sessionManager.getEMAIL());
//                params.put("reset_key", resetKey.getText().toString());
//
//                return params;
//            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
                headers.put("Authorization",token);
                return headers;
            }

        };
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        queue.add(jsObjRequest);
    }

}

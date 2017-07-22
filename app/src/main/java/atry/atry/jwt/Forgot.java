package atry.atry.jwt;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class Forgot extends Fragment {

    String LOG_TAG = "Forgot";
    FragmentTransaction ft;
    Button forgotButton,submitBtn;
    String email;
    Boolean success = false;
    EditText resetKeyET;
    TextView headerText;
    ProgressDialog progressDialog;


    public Forgot() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_forgot, container, false);

        forgotButton = (Button) rootView.findViewById(R.id.forgotButton);
        submitBtn = (Button) rootView.findViewById(R.id.submitBtn);
        resetKeyET = (EditText)rootView.findViewById(R.id.resetKey);
        headerText = (TextView)rootView.findViewById(R.id.headerText);

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotCall();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Sending the reset key via email");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(success) {
                    Fragment resetFragment = new Reset();
                    Bundle bundle = new Bundle();
                    bundle.putString("email",email);
                    bundle.putString("resetKey",resetKeyET.getText().toString().trim());
                    resetFragment.setArguments(bundle);
                    ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content, resetFragment).commit();
                }
                else {
                    Toast.makeText(getActivity(),"Something wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bundle bundle = this.getArguments();

        if(bundle!=null) {
            email = bundle.getString("email");
            Log.v(LOG_TAG,email);
        }


        return rootView;
    }

    private void forgotCall() {
        String url = getActivity().getResources().getString(R.string.BASE_URL) + "forgot?email="+email;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(LOG_TAG, String.valueOf(response));
                        String status = response.optString("status");
                        String message = response.optString("message");
                        if (status.equals("success") && message.equals("Password reset email sent successfully")) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Check your email for the key",Toast.LENGTH_SHORT).show();
                            headerText.setText(String.valueOf(response));
                            success = true;
                            resetKeyET.setVisibility(View.VISIBLE);
                            submitBtn.setVisibility(View.VISIBLE);
                            forgotButton.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(LOG_TAG, String.valueOf(error));
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);

        queue.add(jsObjRequest);
    }

}

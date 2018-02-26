package com.inception.bettingadmin.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.inception.bettingadmin.CreateUser;
import com.inception.bettingadmin.R;
import com.inception.bettingadmin.UserDetails;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsers extends Fragment {

    TextView create_user ;

    JSONArray jsonArray;

    RecyclerView recyclerView ;


    public AllUsers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_users, container, false);

        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL , false));




        create_user = v.findViewById(R.id.create_user);

        create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext() , CreateUser.class));
            }
        });

        get_users();
        return v;
    }


    private void get_users()
    {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("module", "get_user");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( "http://edvt.in/betting_app_api/webapi.php", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);

                try {
                    jsonArray = response.getJSONArray("result");


                    Log.i("adapter to recycler --","");
                    Adapter adapter = new Adapter();
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000 ,2 ,2 ));

        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
    }

    private class Adapter extends RecyclerView.Adapter<view_holder>
    {

        @Override
        public view_holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new view_holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_cell , parent , false));

        }

        @Override
        public void onBindViewHolder(view_holder holder, int position) {

            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(position);

                holder.sr_num.setText(jsonObject.getString("id"));
                holder.username.setText(jsonObject.getString("username"));

                if(jsonObject.getString("status").equals("1"))
                {
                    holder.isactive.setImageDrawable(getResources().getDrawable(R.drawable.active_user));
                }

                else {
                    holder.isactive.setImageDrawable(getResources().getDrawable(R.drawable.block_user));

                }

                holder.balance.setText("0");

                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(getContext() , UserDetails.class);

                        try {
                            i.putExtra("name" , jsonObject.getString("username"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(i);

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }
    }

    private class view_holder extends RecyclerView.ViewHolder
    {

        TextView sr_num ,  username , balance ;

        ImageView isactive;

        public view_holder(View itemView) {
            super(itemView);

            sr_num = itemView.findViewById(R.id.sr_num);
            isactive = itemView.findViewById(R.id.isActive);
            username = itemView.findViewById(R.id.username_);

            balance = itemView.findViewById(R.id.balance_);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        get_users();
    }
}

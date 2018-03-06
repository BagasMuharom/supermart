package com.supermart.supermart.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.supermart.supermart.Adapter.DaftarPesananAdapter;
import com.supermart.supermart.R;
import com.supermart.supermart.Util;

import org.json.JSONArray;
import org.json.JSONException;

@SuppressLint("ValidFragment")
public class Dikirim extends Fragment {

    ListView daftar_pesanan_lv;

    JSONArray daftar_pesanan;

    RequestQueue requestQueue;

    ViewPager viewPager;

    public Dikirim(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.daftar_pesanan_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestQueue = Volley.newRequestQueue(this.getContext());

        this.initDaftarPesanan();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.daftar_pesanan_lv = (ListView) view.findViewById(R.id.daftar_pesanan);
    }

    public void initDaftarPesanan() {
        String url = "https://supermart.herokuapp.com/daftar/pesanan/dikirim/" + Util.imei;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Dikirim.this.daftar_pesanan = new JSONArray(response);
                    DaftarPesananAdapter adapter = new DaftarPesananAdapter(Dikirim.this.daftar_pesanan, Dikirim.this, true, viewPager);
                    Dikirim.this.daftar_pesanan_lv.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Dikirim.this.getContext(), "Gagal mengambil data !", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

}

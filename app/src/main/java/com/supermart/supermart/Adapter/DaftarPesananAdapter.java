package com.supermart.supermart.Adapter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.supermart.supermart.Fragment.Dikirim;
import com.supermart.supermart.R;
import com.supermart.supermart.RincianPesananActivity;
import com.supermart.supermart.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DaftarPesananAdapter extends BaseAdapter {

    JSONArray daftar_pesanan;

    Fragment fragment;

    boolean masih_dikirim;

    ViewPager viewPager;

    public DaftarPesananAdapter(JSONArray daftar_pesanan, Fragment fragment, boolean masih_dikirim, ViewPager viewPager) {
        this.daftar_pesanan = daftar_pesanan;
        this.fragment = fragment;
        this.masih_dikirim = masih_dikirim;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return this.daftar_pesanan.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return this.daftar_pesanan.getJSONObject(i);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        try {
            return this.daftar_pesanan.getJSONObject(i).getLong("id");
        } catch (JSONException e) {
            return -1;
        }
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if(view == null)
            view = this.fragment.getLayoutInflater().inflate(R.layout.daftar_pesanan_detail, viewGroup, false);

        TextView nopesanan = (TextView) view.findViewById(R.id.no_pesanan);
        TextView dipesan_pada = (TextView) view.findViewById(R.id.dipesan_pada);
        TextView harga = (TextView) view.findViewById(R.id.harga);
        Button terima = (Button) view.findViewById(R.id.terima);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DaftarPesananAdapter.this.fragment.getContext(), RincianPesananActivity.class);
                intent.putExtra("idpesanan", DaftarPesananAdapter.this.getItemId(i));
                intent.putExtra("masih_dikirim", masih_dikirim);
                DaftarPesananAdapter.this.fragment.getActivity().startActivity(intent);
            }

        });

        if(this.masih_dikirim)
            terima.setVisibility(View.VISIBLE);

        terima.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(DaftarPesananAdapter.this.fragment.getContext());
                String url = "https://supermart.herokuapp.com/terima/pesanan/" + DaftarPesananAdapter.this.getItemId(i);
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getBoolean("success")) {

                                if(masih_dikirim) {
                                    ((Dikirim) fragment).initDaftarPesanan();
                                }

                                viewPager.setCurrentItem(3);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(request);
            }

        });

        JSONObject pesanan = (JSONObject) this.getItem(i);

        nopesanan.setText(Util.imei + "" + this.getItemId(i));
        try {
            dipesan_pada.setText(pesanan.getString("dipesan_pada"));
            harga.setText("Rp " + pesanan.getInt("jumlah"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}

package com.supermart.supermart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.supermart.supermart.Adapter.DaftarPesananAdapter;
import com.supermart.supermart.Fragment.Dikirim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RincianPesananActivity extends AppCompatActivity {

    long id_pesanan;

    RequestQueue queue;

    LinearLayout daftar_produk_ll;

    TextView no_pesanan;

    TextView penerima;

    TextView telp;

    TextView alamat;

    Button terima;

    boolean masih_dikirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_pesanan);

        this.initUI();
        this.initData();
        this.syncData();
    }

    private void syncData() {
        String url = "https://supermart.herokuapp.com/rincian/pesanan/" + this.id_pesanan;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("daftar");
                    JSONObject alamat = jsonObject.getJSONObject("alamat");

                    no_pesanan.setText(Util.imei + id_pesanan);
                    penerima.setText(alamat.getString("penerima"));
                    telp.setText(alamat.getString("telp"));
                    RincianPesananActivity.this.alamat.setText(alamat.getString("alamat"));
                    RincianPesananActivity.this.initDaftarProduk(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        this.queue.add(request);
    }

    private void initDaftarProduk(JSONArray jsonArray) {
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject rincian = jsonArray.getJSONObject(i);
                View view = getLayoutInflater().inflate(R.layout.produk_rincian_pesanan, null);
                TextView nama = (TextView) view.findViewById(R.id.nama);
                TextView jumlah = (TextView) view.findViewById(R.id.jumlah);
                TextView harga = (TextView) view.findViewById(R.id.harga);

                try {
                    int _jumlah = rincian.getInt("jumlah");
                    int _harga = rincian.getInt("harga");
                    int _total = _jumlah * _harga;
                    nama.setText(rincian.getString("nama"));
                    jumlah.setText("Jumlah : " + _jumlah);
                    harga.setText("Rp " + _total);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                this.daftar_produk_ll.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initData() {
        this.id_pesanan = this.getIntent().getLongExtra("idpesanan", 0);
        this.queue = Volley.newRequestQueue(this);
        this.masih_dikirim = getIntent().getBooleanExtra("masih_dikirim", false);

        if(this.masih_dikirim) {
            this.terima.setVisibility(View.VISIBLE);
            this.terima.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    RequestQueue queue = Volley.newRequestQueue(RincianPesananActivity.this);
                    String url = "https://supermart.herokuapp.com/terima/pesanan/" + id_pesanan;
                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(jsonObject.getBoolean("success")) {
                                    Intent intent = new Intent(RincianPesananActivity.this, DaftarPesananActivity.class);
                                    intent.putExtra("tab", 3);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
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
        }
    }

    private void initUI() {
        this.daftar_produk_ll = (LinearLayout) findViewById(R.id.daftar_produk);
        this.no_pesanan = (TextView) findViewById(R.id.no_pesanan);
        this.penerima = (TextView) findViewById(R.id.penerima);
        this.telp = (TextView) findViewById(R.id.telp);
        this.alamat = (TextView) findViewById(R.id.alamat);
        this.terima = (Button) findViewById(R.id.terima);
    }

}

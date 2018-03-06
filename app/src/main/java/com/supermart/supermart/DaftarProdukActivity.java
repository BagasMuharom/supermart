package com.supermart.supermart;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DaftarProdukActivity extends AppCompatActivity {

    GridView lv_daftar_produk;

    JSONArray daftar_produk;

    RequestQueue requestQueue;

    RelativeLayout cart_layout;

    TextView jumlah_pesanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_produk);

        this.requestQueue = Volley.newRequestQueue(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
        actionBar.setTitle(getIntent().getStringExtra("namaKategori"));

        this.lv_daftar_produk = (GridView) findViewById(R.id.daftar_produk);
        this.updateCartBadge();
        this.initDaftarProduk();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.pesanan_saya:
                Intent intent = new Intent(DaftarProdukActivity.this, DaftarPesananActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem cart = menu.findItem(R.id.cart_icon);
        cart.setActionView(R.layout.cart_icon_badge);

        View cart_layout = cart.getActionView();

        this.jumlah_pesanan = cart_layout.findViewById(R.id.jumlah_pesanan);

        cart_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DaftarProdukActivity.this, DaftarDiCartActivity.class);
                startActivity(intent);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void initDaftarProduk() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        long idkategori = this.getIntent().getLongExtra("idkategori", 1);
        String url = "https://supermart.herokuapp.com/daftar/produk/" + idkategori + "/" + Util.imei;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    DaftarProdukActivity.this.daftar_produk = new JSONArray(response);
                    DaftarProdukAdapter adapter = new DaftarProdukAdapter(DaftarProdukActivity.this.daftar_produk);
                    lv_daftar_produk.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);

    }

    private class DaftarProdukAdapter extends BaseAdapter {

        JSONArray data;

        public DaftarProdukAdapter(JSONArray data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return this.data.length();
        }

        @Override
        public Object getItem(int i) {
            try {
                return this.data.getJSONObject(i);
            } catch (JSONException e) {
                return new JSONObject();
            }
        }

        @Override
        public long getItemId(int i) {
            try {
                return this.data.getJSONObject(i).getLong("id");
            } catch (JSONException e) {
                return -1;
            }
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(DaftarProdukActivity.this).inflate(R.layout.produk_detail, viewGroup, false);

            TextView nama = (TextView) view.findViewById(R.id.nama);
            TextView harga = (TextView) view.findViewById(R.id.harga);
            Button kurang = (Button) view.findViewById(R.id.kurang);
            Button tambah = (Button) view.findViewById(R.id.tambah);
            final EditText jumlah = (EditText) view.findViewById(R.id.jumlah);

            jumlah.addTextChangedListener(new TextWatcher() {

                int jumlahsebelum = 0;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        this.jumlahsebelum = Integer.parseInt(charSequence.toString());
                    }
                    catch (NumberFormatException err) {
                        //
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    final int _jumlah;

                    try {
                        _jumlah = Integer.parseInt(editable.toString());
                    }
                    catch (NumberFormatException err) {
                        return;
                    }

                    String url = "https://supermart.herokuapp.com/tambah-ke-cart";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getBoolean("success")) {
                                    DaftarProdukActivity.this.updateCartBadge();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(DaftarProdukActivity.this, "Gagal !", Toast.LENGTH_SHORT).show();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("konsumen", Util.imei);
                            Log.d("produk", String.valueOf(getItemId(i)));
                            params.put("produk", String.valueOf(getItemId(i)));
                            Log.d("jumlah", String.valueOf(_jumlah));
                            params.put("jumlah", String.valueOf(_jumlah));
                            return params;
                        }

                    };

                    requestQueue.add(stringRequest);
                }

            });

            kurang.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int temp = Integer.parseInt(jumlah.getText().toString());

                    if(temp > 0)
                        jumlah.setText(--temp + "");
                }

            });

            tambah.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int temp = Integer.parseInt(jumlah.getText().toString());
                    jumlah.setText(++temp + "");
                }

            });

            try {
                nama.setText(((JSONObject) this.getItem(i)).getString("nama"));
                harga.setText("Rp " + ((JSONObject) this.getItem(i)).getString("harga"));
                jumlah.setText(((JSONObject) this.getItem(i)).getString("jumlah"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }
    }

    private void updateCartBadge() {
        String url = "https://supermart.herokuapp.com/jumlah/produk/cart/" + Util.imei;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);

                    if(result.has("jumlah")) {
                        jumlah_pesanan.setText(result.getString("jumlah"));
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

        requestQueue.add(request);
    }

}

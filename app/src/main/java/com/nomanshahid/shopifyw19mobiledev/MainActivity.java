package com.nomanshahid.shopifyw19mobiledev;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private List<String> tagsList;
    private Hashtable<String, List<Product>> tagsTable;
    ArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagsList = new ArrayList<>();
        tagsTable = new Hashtable<>();

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagsList);
        final ListView listView = findViewById(R.id.list1);
        listView.setAdapter(listAdapter);

        String shopifyURL = "https://shopicruit.myshopify.com/admin/products.json?page=1&" +
                "access_token=c32313df0d0ef512ca64d5b336a0d7c6";

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(shopifyURL)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Add toast to main thread here
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);
                            generateUniqueTagsSet(jsonObject);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listAdapter.notifyDataSetChanged();
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            String tag = (String) adapterView.getItemAtPosition(i);
                                            List<Product> currProductList = tagsTable.get(tag);
                                            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
                                            intent.putExtra("tag", tag);
                                            intent.putParcelableArrayListExtra("productList", (ArrayList<? extends Parcelable>) currProductList);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "There was an error. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No network available! Please connect and " +
                            "try again.",
                    Toast.LENGTH_LONG).show();
        }

    }

    // Checks to make sure network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;

    }

    // Generates the set of unique tags
    private void generateUniqueTagsSet(JSONObject jsonObject) throws JSONException {
        JSONArray products = jsonObject.getJSONArray("products");
        for (int i = 0; i < products.length(); ++i) {
            JSONObject jsonProduct = products.getJSONObject(i);
            int inventoryCount = 0;
            JSONArray variants = jsonProduct.getJSONArray("variants");
            for (int j = 0; j < variants.length(); ++j) {
                JSONObject variant = variants.getJSONObject(j);
                inventoryCount += variant.getInt("inventory_quantity");
            }
            Product product = new Product(jsonProduct.getString("title"), inventoryCount);
            List<String> tags = Arrays.asList(jsonProduct.getString("tags").split(", "));
            for (int j = 0; j < tags.size(); ++j) {
                List list =  tagsTable.get(tags.get(j));
                if (list == null) list = new ArrayList();
                list.add(product);
                tagsTable.put(tags.get(j),list);
            }
        }
        tagsList.addAll(tagsTable.keySet());

    }
}


package com.nomanshahid.shopifyw19mobiledev;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    List<Product> productList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        listView = findViewById(R.id.list2);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productList = bundle.getParcelableArrayList("productList");
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, productList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);
                text1.setText(productList.get(position).getName());
                text2.setText("Inventory count: " + productList.get(position).getInventoryCount());
                return view;
            }
        };
        listView.setAdapter(adapter);
    }
}

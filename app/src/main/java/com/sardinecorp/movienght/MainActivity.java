package com.sardinecorp.movienght;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.sardinecorp.movienght.UI.Discover;
import com.sardinecorp.movienght.UI.Search;
import com.sardinecorp.movienght.utils.APIUtils;
import com.sardinecorp.movienght.utils.Common;

import java.net.InetAddress;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.SearchCard)
    CardView mSearchCard;
    @BindView(R.id.DiscoverCard)
    CardView mDiscoverCard;

    public static Map<String, Integer> mListOfGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSearchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Search.class);
                startActivity(intent);
            }
        });

        mDiscoverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Discover.class);
                startActivity(intent);
            }
        });

        if (!Common.isNetworkConnected(MainActivity.this)) {
            // Create an alert dialog that warns the user
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("No Inernet Connection")
                    .setMessage("Please connect to the internet to fully enjoy this app")
                    .setPositiveButton("Ok", null);

            alert.show();
        }

        mListOfGenres = APIUtils.genreIdMap();
    }
}



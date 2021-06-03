package com.example.beeradviser;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;

public class FindBeerActivity extends Activity {
    private BeerExpert expert = new BeerExpert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beer);
    }

    public void onClickFindBeer(View view) {
        Spinner color = (Spinner) findViewById(R.id.color);
        String beerColor = String.valueOf(color.getSelectedItem());

        TextView brands = (TextView) findViewById(R.id.brands);
        List<String> brandsList = expert.getBrands(beerColor);
        StringBuilder brandsFormated = new StringBuilder();
        for (String brand : brandsList) {
            brandsFormated.append(brand).append("\n");
        }
        brands.setText(brandsFormated);
    }
}

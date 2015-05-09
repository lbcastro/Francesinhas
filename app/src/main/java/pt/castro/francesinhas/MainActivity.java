package pt.castro.francesinhas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import icepick.Icepick;
import pt.castro.francesinhas.list.ItemHolder;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.setItems(generateDummyList());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int[] images = {R.drawable.francesinha1, R.drawable.francesinha2, R.drawable.francesinha3, R.drawable.francesinha4, R.drawable.francesinha5, R.drawable.francesinha6, R.drawable.francesinha7, R.drawable.francesinha8, R.drawable.francesinha9};
    private String[] names = {"Alicantina", "Cufra", "Cunha", "Santiago", "Capa Negra", "Paquete", "Galiza", "Porto Beer", "Rio de Janeiro"};
    private String[] locations = {"Porto", "Vila do Conde", "Matosinhos", "Gaia", "Maia", "Povoa do Varzim", "Baixa", "Ribeira", "Antas"};

    private List<ItemHolder> generateDummyList() {
        final int size = 50;
        final List<ItemHolder> items = new ArrayList<>();
        int lastInt = -1;
        int number = -1;
        for (int x = 0; x < size; x++) {
            Random rand = new Random();
            while (number == lastInt) {
                number = rand.nextInt((8 - 0) + 1) + 0;
            }
            lastInt = number;
            ItemHolder itemHolder = new ItemHolder();
            itemHolder.setRanking(x + 1);
            itemHolder.setName(names[number]);
            itemHolder.setLocation(locations[number]);
            itemHolder.setImageResource(images[number]);
            items.add(itemHolder);
        }
        return items;
    }
}

package pt.castro.francesinhas;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.communication.EndpointGetItems;
import pt.castro.francesinhas.communication.EndpointsAsyncTask;
import pt.castro.francesinhas.events.ListRetrievedEvent;
import pt.castro.francesinhas.events.ScoreChangeEvent;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        new EndpointGetItems().execute();
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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


    private List<ItemHolder> generateDummyList() {
        int[] images = {R.drawable.francesinha1, R.drawable.francesinha2, R.drawable.francesinha3, R.drawable.francesinha4, R.drawable.francesinha5, R.drawable.francesinha6, R.drawable.francesinha7, R.drawable.francesinha8, R.drawable.francesinha9};
        String[] names = {"Alicantina", "Cufra", "Cunha", "Santiago", "Capa Negra", "Paquete", "Galiza", "Porto Beer", "Rio de Janeiro"};
        String[] locations = {"Porto", "Vila do Conde", "Matosinhos", "Gaia", "Maia", "Povoa do Varzim", "Baixa", "Ribeira", "Antas"};
        final List<ItemHolder> items = new ArrayList<>();
        for (int x = 0; x < names.length; x++) {
            ItemHolder itemHolder = new ItemHolder();
            itemHolder.setName(names[x]);
            itemHolder.setLocation(locations[x]);
            itemHolder.setImageResource(images[x]);
            itemHolder.setId(new Long(x + 1));

            items.add(itemHolder);
            EndpointsAsyncTask task = new EndpointsAsyncTask(EndpointsAsyncTask.ADD);
            task.execute(itemHolder);
        }
        return items;
    }

    public void onEvent(ScoreChangeEvent scoreChangeEvent) {
//        Toast.makeText(this, "Score for " + scoreChangeEvent.itemHolder.getName() + " is " +
//                scoreChangeEvent.itemHolder.getRanking(), Toast.LENGTH_LONG).show();
    }

    public void onEvent(ListRetrievedEvent listRetrievedEvent) {
        final MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.setItems(listRetrievedEvent.list);
    }

}

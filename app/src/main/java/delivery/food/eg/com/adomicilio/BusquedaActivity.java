package delivery.food.eg.com.adomicilio;

import android.content.Intent;
import android.database.MatrixCursor;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


public class BusquedaActivity extends ActionBarActivity {

    private final String LOG_TAG = BusquedaActivity.class.getSimpleName();
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,new BusquedaTestFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_busqueda, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class BusquedaTestFragment extends Fragment {

        private ListView mListView;
        private ArrayList<String> dataSearch;

        public BusquedaTestFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_busqueda, container, false);
            dataSearch=new ArrayList<String>();
            dataSearch.add("Pizza Hut");
            dataSearch.add("Burguer King");
            dataSearch.add("Mac Donalds");
            mListView=(ListView)rootView.findViewById(R.id.listview_restaurants);
            String[] columns = new String[] { "_id", "name", "category","minimum","open" };

            MatrixCursor cursorPlace= new MatrixCursor(columns);
            //startManagingCursor(cursorPlace);

            cursorPlace.addRow(new Object[] { 1, "Pizzeria la grizzly", "pizzeria","$10-20", true });
            cursorPlace.addRow(new Object[] { 1, "Pizzeria la grizzly", "pizzeria","$10-20", true });
            cursorPlace.addRow(new Object[] { 1, "Pizzeria la grizzly", "pizzeria","$10-20", true });
            cursorPlace.addRow(new Object[] { 1, "Pizzeria la grizzly", "pizzeria","$10-20", true });
            cursorPlace.addRow(new Object[] { 1, "Pizzeria la grizzly", "pizzeria","$10-20", true });
            cursorPlace.addRow(new Object[] { 1, "Pizzeria la grizzly", "pizzeria","$10-20", true });
            cursorPlace.addRow(new Object[] { 1, "Pizzeria la grizzly", "pizzeria","$10-20", true });


            PlaceAdapter restaurantsAdapter=new PlaceAdapter(getActivity().getApplicationContext(),cursorPlace,0);
            mListView.setAdapter(restaurantsAdapter);
            return rootView;
        }
    }
}

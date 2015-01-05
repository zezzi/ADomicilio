package delivery.food.eg.com.adomicilio;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by zezzi on 12/30/14.
 */
public class DetailActivity extends ActionBarActivity {

    public static final String IDPLACE = "place_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_place);

        if (savedInstanceState == null) {
            String idPlace = getIntent().getStringExtra(IDPLACE);

            Bundle arguments = new Bundle();
            arguments.putString(DetailActivity.IDPLACE, idPlace);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.place_detail_container, fragment)
                    .commit();
        }
    }
}

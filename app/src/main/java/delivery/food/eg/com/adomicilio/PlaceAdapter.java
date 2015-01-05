package delivery.food.eg.com.adomicilio;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by zezzi on 12/17/14.
 */
public class PlaceAdapter extends CursorAdapter {


    ImageLoader imageLoader;


    public static class ViewHolder {
        public final NetworkImageView iconView;
        public final TextView placeView;
        public final TextView categoryView;
        public final TextView minimumView;
        public final TextView openView;

        public ViewHolder(View view) {
            iconView = (NetworkImageView) view.findViewById(R.id.list_item_icon);
            placeView = (TextView) view.findViewById(R.id.list_item_name_place);
            categoryView = (TextView) view.findViewById(R.id.list_item_category_place);
            minimumView = (TextView) view.findViewById(R.id.list_item_minimum_to_order);
            openView = (TextView) view.findViewById(R.id.list_item_open_or_closed);
        }
    }

    private boolean usePrincipalLayout = true;

    public PlaceAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        VolleyHelper.init(MyApplication.getAppContext());

    }

    public PlaceAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        VolleyHelper.init(MyApplication.getAppContext());

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.item_place;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int viewType = getItemViewType(cursor.getPosition());
       // viewHolder.iconView.setImageResource(R.drawable.defaultrestaurant);
        //viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(
        //cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
        String placeName=cursor.getString(BusquedaFragment.COLUMN_NAME); //"Restaurante de Prueba";
        viewHolder.placeView.setText(placeName);

        String categoryView=cursor.getString(BusquedaFragment.COLUMN_SHORT_DESC); //"Hamburguesas";
        viewHolder.categoryView.setText(categoryView);

        String minimumView=cursor.getString(BusquedaFragment.COLUMN_MIN_ORDER);// "10-20$";
        viewHolder.minimumView.setText("Min Order   "+minimumView);
        viewHolder.minimumView.setTypeface(viewHolder.minimumView.getTypeface(), Typeface.ITALIC);

        String openOrClosed=Utility.getOpenNow(); //"Open";
        viewHolder.openView.setText(openOrClosed);
        //Imagen
        String urlToLoad=cursor.getString(BusquedaFragment.COLUMN_IMAGE_URL); ;
        imageLoader=VolleyHelper.getImageLoader();
        viewHolder.iconView.setImageUrl(urlToLoad, imageLoader);
    }

    public void setUseTodayLayout(boolean usePrincipalLayout) {
        usePrincipalLayout = usePrincipalLayout;
    }
}

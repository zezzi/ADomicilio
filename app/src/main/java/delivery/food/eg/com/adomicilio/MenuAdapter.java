package delivery.food.eg.com.adomicilio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import delivery.food.eg.com.adomicilio.data.ADomicilioContract;

/**
 * Created by zezzi on 1/2/15.
 */
public class MenuAdapter extends CursorAdapter {


    Cursor l;
    Context con;
    ImageLoader imageLoader;

    public static class ViewHolder {
        public final NetworkImageView bannerView;
        public final TextView titleView;
        public final TextView shortDescView;
        public final TextView priceView;
        public final ImageButton imageAdd;


        public ViewHolder(View view) {
            bannerView = (NetworkImageView) view.findViewById(R.id.list_menu_icon);
            titleView = (TextView) view.findViewById(R.id.list_menu_name_title);
            shortDescView = (TextView) view.findViewById(R.id.list_menu_dhortdesc);
            priceView = (TextView) view.findViewById(R.id.list_item_price);
            imageAdd=(ImageButton)view.findViewById(R.id.addButton);
        }
    }

    private boolean usePrincipalLayout = true;

    public MenuAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.l = c;
        this.con=context;
    }

    public MenuAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.l = c;
        this.con=context;
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.item_menu;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int viewType = getItemViewType(cursor.getPosition());
        final String menuID=cursor.getString(DetailFragment.COLUMN_MENU_ID);
        final String placeID=cursor.getString(DetailFragment.COLUMN_COUNT_KEY);

        String title = cursor.getString(DetailFragment.COLUMN_NAMEMENU); //"Restaurante de Prueba";
        viewHolder.titleView.setText(title);

        String shortDesc = cursor.getString(DetailFragment.COLUMN_SHORTDESC); //"Hamburguesas";
        viewHolder.shortDescView.setText(shortDesc);

        String costMenu = cursor.getString(DetailFragment.COLUMN_PRICE);// "10-20$";
        viewHolder.priceView.setText(costMenu);

        String urlToLoad=cursor.getString(DetailFragment.COLUMN_IMAGEURL); ;
        imageLoader=VolleyHelper.getImageLoader();
        viewHolder.bannerView.setImageUrl(urlToLoad, imageLoader);

        viewHolder.imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ONClikButton "," CLICK "+ menuID);
                ContentValues menuToAdd=new ContentValues();
                menuToAdd.put(ADomicilioContract.CartEntry.COLUMN_MENU, menuID);
                menuToAdd.put(ADomicilioContract.CartEntry.COLUMN_PLACE_ID, placeID);
                menuToAdd.put(ADomicilioContract.CartEntry.COLUMN_AMOUNT, 1);
                MyApplication.getAppContext().getContentResolver().insert(ADomicilioContract.CartEntry.CONTENT_URI,menuToAdd);
                Toast toast = Toast.makeText(MyApplication.getAppContext(), "Item Agregado", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void setUseTodayLayout(boolean usePrincipalLayout) {
        usePrincipalLayout = usePrincipalLayout;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView(mContext, mCursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, mContext, mCursor);
        return v;
    }
}
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import delivery.food.eg.com.adomicilio.DetailFragment;
import delivery.food.eg.com.adomicilio.MyApplication;
import delivery.food.eg.com.adomicilio.R;
import delivery.food.eg.com.adomicilio.VolleyHelper;
import delivery.food.eg.com.adomicilio.data.ADomicilioContract;

/**
 * Created by zezzi on 1/4/15.
 */
public class CartAdapter extends CursorAdapter {


    Cursor l;
    Context con;
    ImageLoader imageLoader;

    public static class ViewHolder {
        public final NetworkImageView bannerView;
        public final TextView titleView;
        public final TextView shortDescView;
        public final TextView priceView;



        public ViewHolder(View view) {
            bannerView = (NetworkImageView) view.findViewById(R.id.list_cart_icon);
            titleView = (TextView) view.findViewById(R.id.list_cart_name_title);
            shortDescView = (TextView) view.findViewById(R.id.list_cart_dhortdesc);
            priceView = (TextView) view.findViewById(R.id.list_cart_price);

        }
    }

    private boolean usePrincipalLayout = true;

    public CartAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.l = c;
        this.con = context;
    }

    public CartAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.l = c;
        this.con = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.item_cart;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int viewType = getItemViewType(cursor.getPosition());

        String title = cursor.getString(DetailFragment.COLUMN_NAMEMENU); //"Restaurante de Prueba";
        viewHolder.titleView.setText(title);

        String shortDesc = cursor.getString(DetailFragment.COLUMN_SHORTDESC); //"Hamburguesas";
        viewHolder.shortDescView.setText(shortDesc);

        String costMenu = cursor.getString(DetailFragment.COLUMN_PRICE);// "10-20$";
        viewHolder.priceView.setText(costMenu);

        String urlToLoad = cursor.getString(DetailFragment.COLUMN_IMAGEURL);
        imageLoader = VolleyHelper.getImageLoader();
        viewHolder.bannerView.setImageUrl(urlToLoad, imageLoader);


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
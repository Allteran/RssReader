package ua.ck.geekhub.prozapas.ghprozapasrssreader.activities;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.fragments.DetailsFragment;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.helpers.RssDatabaseHelper;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const;

/**
 * Created by Allteran on 16.11.2014.
 * The only reason to create single activity is need to create NavigationDrawer
 */
public class DetailsActivity extends BaseActivity {

    private RssDatabaseHelper mDatabaseHelper;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<RssItem> mRssItemList;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDatabaseHelper = new RssDatabaseHelper(this);
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(Const.KEY_POSITION);
            mRssItemList = (ArrayList<RssItem>) savedInstanceState.getSerializable(Const.KEY_RSSLIST);
        } else {
            Bundle extras = getIntent().getExtras();
            mRssItemList = (ArrayList<RssItem>) extras.getSerializable(Const.BUNDLE_ENTRIES);
            mPosition = extras.getInt(Const.BUNDLE_POSITION);
        }
        setTitle(mRssItemList.get(mPosition).getTitle());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerList.setAdapter(new NavigationDrawerAdapter(this));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetails(mRssItemList.get(position));
                setTitle(mRssItemList.get(position).getTitle());
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        mDrawerList.setSelectionFromTop(mPosition, 0);

        showDetails(mRssItemList.get(mPosition));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Const.KEY_POSITION, mPosition);
        outState.putSerializable(Const.KEY_RSSLIST, mRssItemList);
        super.onSaveInstanceState(outState);
    }

    private void showDetails(RssItem rssItem) {
        DetailsFragment detailsFragment = DetailsFragment.newInstance(rssItem);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.details_fragment_content, detailsFragment);
        fragmentTransaction.commit();
    }

    /**
     * Simple adapter for Navigation Drawer
     */
    private class NavigationDrawerAdapter extends BaseAdapter {
        private ViewHolder mViewHolder;
        private LayoutInflater mLayoutInflater;

        public NavigationDrawerAdapter(Context context) {
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mRssItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRssItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public RssItem getRssItem(int position) {
            return (RssItem) getItem(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.fragment_nav_drawer_adapter_view, parent, false);

                mViewHolder = new ViewHolder();
                mViewHolder.mTitle = (TextView) convertView.findViewById(R.id.text_view_rsslistitem_title);

                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            RssItem rssItem = getRssItem(position);

            if (rssItem != null) {
                mViewHolder.mTitle.setText(rssItem.getTitle());
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        private TextView mTitle;
    }
}

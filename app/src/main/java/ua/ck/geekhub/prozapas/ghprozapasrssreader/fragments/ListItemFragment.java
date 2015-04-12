package ua.ck.geekhub.prozapas.ghprozapasrssreader.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.activities.GPlusActivity;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.databases.RealmHelper;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.tasks.DownloadString;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.tasks.ParseFromJSON;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import static ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const.SHARED_PREFERECES_KEY;

/**
 * Created by Allteran on 16.11.2014.
 */
public class ListItemFragment extends BaseFragment {
    private final String TAG = this.getClass().getSimpleName();
    private ArrayList<RssItem> mRssItemList = new ArrayList<>();
    private ListView mListView;
    private OnListFragmentEvent mEvent;
    private SharedPreferences mSharedPreferences;
    private String mDownloadedString;
    private PullToRefreshLayout mSwipeLayout;
    private RssAdapter mArticlesAdapter;
    private ActionBar mActions;
    private int mSpinnerPosition;
    private Realm mRealm;
    private RealmHelper mRealmHelper;
    private ActionBarActivity mActivity;

    public interface OnListFragmentEvent {
        public void onListFragmentItemClick(int position, ArrayList<RssItem> rssItemList);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mEvent = (OnListFragmentEvent) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_item, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity = (ActionBarActivity) getActivity();
        try {
            mRealm = Realm.getInstance(getActivity());
        } catch (RealmMigrationNeededException e) {
            e.printStackTrace();
            Realm.deleteRealmFile(mActivity);
        }
        mRealmHelper = new RealmHelper();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (mSharedPreferences.getString(SHARED_PREFERECES_KEY, null) != null) {
            mDownloadedString = mSharedPreferences.getString(SHARED_PREFERECES_KEY, null);
        }
        mArticlesAdapter = new RssAdapter(getActivity());
        mListView = (ListView) view.findViewById(R.id.list_item);
        mListView.setAdapter(mArticlesAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEvent.onListFragmentItemClick(position, mRssItemList);
            }
        });

        mActions = ((ActionBarActivity) getActivity()).getSupportActionBar();

        /**
         * This part of code will create a drop down menu to chose article page
         * or favorites articles page
         */
        SpinnerAdapter dropDownAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.drop_down_main_menu,
                        R.layout.drop_down_line);
        //Callback
        ActionBar.OnNavigationListener callback = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {
                mSpinnerPosition = i;
                ((ActionBarActivity) getActivity()).supportInvalidateOptionsMenu();
                switch (i) {
                    case 0:
                        if (mDownloadedString == null) {
                            startDownload();
                        } else {
                            updateListItems();
                        }
                        return true;
                    case 1:
                        mRssItemList = mRealmHelper.getAllArticles(mRealm);
                        if (!mRssItemList.isEmpty()) {
                            mArticlesAdapter.notifyDataSetChanged();
                            showContent(getView());
                        } else {
                            showNoData(getView(), getString(R.string.no_saved_articles_message));
                        }
                        return true;
                }
                return true;
            }
        };
        mArticlesAdapter.notifyDataSetChanged();
        assert mActions != null;

        mActions.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_LIST);
        mActions.setDisplayShowTitleEnabled(false);
        mActions.setListNavigationCallbacks(dropDownAdapter, callback);

        ViewGroup viewGroup = (ViewGroup) view;
        mSwipeLayout = new PullToRefreshLayout(viewGroup.getContext());

        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(R.id.list_item, android.R.id.empty)
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        startDownload();
                    }
                })
                .setup(mSwipeLayout);
        if (savedInstanceState != null) {
            mActions.setSelectedNavigationItem(savedInstanceState.getInt(Const.DB_CHECKER_KEY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Const.DB_CHECKER_KEY, mSpinnerPosition);
    }

    //parse from JSON downloaded string and filling mRssItemList with that
    private void updateListItems() {
        JSONObject dataToParse;
        try {
            dataToParse = new JSONObject(mDownloadedString);
            ParseFromJSON parsedData = new ParseFromJSON(dataToParse);
            mRssItemList = parsedData.getRssList();
        } catch (JSONException e) {
            mDownloadedString = mSharedPreferences.getString(SHARED_PREFERECES_KEY, null);
            e.printStackTrace();
        }
        mArticlesAdapter.notifyDataSetChanged();
        showContent(getView());
    }

    public void saveStringInPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SHARED_PREFERECES_KEY, mDownloadedString);
        editor.apply();

    }

    //call DownloadRssTask
    public void startDownload() {
        if (isOnline()) {
            new DownloadRssTask().execute(Const.URL);
        } else {
            showNoData(getView(), getResources().getString(R.string.no_connection_message));
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    //we call this method to add the clear-icon to action bar, when saved articles are on screen
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean visibility = mSpinnerPosition == 1 && !mRssItemList.isEmpty();
        menu.findItem(R.id.clear_all_favorites).setVisible(visibility);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                new AlertDialog.Builder(getActivity())
                        .setTitle("About")
                        .setMessage(getResources().getString(R.string.about_message))
                        .show();
                break;
            case R.id.action_gplus_signin:
                startActivity(new Intent(getActivity(), GPlusActivity.class));
                break;
            case R.id.clear_all_favorites:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Clear Favorites")
                        .setMessage(R.string.clear_all_favorites_message)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mRealmHelper.deleteAllArticles(mRealm, mActivity, mRssItemList);
                                mArticlesAdapter.notifyDataSetChanged();
                                mRealm = Realm.getInstance(mActivity);
                                showNoData(getView(), getString(R.string.no_saved_articles_message));
                                mActivity.supportInvalidateOptionsMenu();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //just download the string in another background thread
    class DownloadRssTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            DownloadString downloadString = new DownloadString(url);
            mDownloadedString = downloadString.getDownloadedString();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateListItems();
            mSwipeLayout.setRefreshComplete();
            saveStringInPreferences();
        }
    }

    class RssAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ViewHolder mViewHolder;

        public RssAdapter(Context context) {
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
                convertView = mLayoutInflater.inflate(R.layout.fragment_adapter_view, parent, false);

                mViewHolder = new ViewHolder();
                mViewHolder.mTitle = (TextView) convertView.findViewById(R.id.text_view_rsslistitem_title);
                mViewHolder.mDescription = (TextView) convertView.findViewById(R.id.text_view_rsslistitem_description);

                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            RssItem rssItem = getRssItem(position);

            if (rssItem != null) {
                mViewHolder.mTitle.setText(rssItem.getTitle());
                mViewHolder.mDescription.setText(rssItem.getContentSnippet().replace("\n", ""));
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        private TextView mTitle, mDescription;
    }
}

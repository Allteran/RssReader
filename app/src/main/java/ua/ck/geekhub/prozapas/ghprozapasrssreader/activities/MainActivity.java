package ua.ck.geekhub.prozapas.ghprozapasrssreader.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.facebook.AppEventsLogger;

import java.util.ArrayList;

import io.realm.Realm;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.fragments.DetailsFragment;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.fragments.ListItemFragment;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.services.RssPullService;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const;

public class MainActivity extends BaseActivity implements ListItemFragment.OnListFragmentEvent {
    private ArrayList<RssItem> mRssItemList = null;
    private boolean isContentHere = true;
    private DetailsFragment mDetailsFragment;
    private ListItemFragment mListItemFragment;
    private Intent mPullServiceIntent;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);

        mPullServiceIntent = new Intent(this, RssPullService.class);
        startService(mPullServiceIntent);

        isContentHere = (findViewById(R.id.details_fragment_content) != null);

        mListItemFragment = new ListItemFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            fragmentTransaction.replace(R.id.list_fragment, mListItemFragment);
        }
        if (isContentHere) {
            mDetailsFragment = new DetailsFragment();
            fragmentTransaction.add(R.id.details_fragment_content, mDetailsFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(mPullServiceIntent);
    }

    @Override
    public void onListFragmentItemClick(int position, ArrayList<RssItem> rssItemList) {
        this.mRssItemList = rssItemList;
        showDetails(position);
    }

    private void showDetails(int position) {
        if (isContentHere) {
            mDetailsFragment = DetailsFragment.newInstance(mRssItemList.get(position));
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.details_fragment_content, mDetailsFragment);
            fragmentTransaction.commit();
            setTitle(mRssItemList.get(position).getTitle());
        } else {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(Const.BUNDLE_ENTRIES, mRssItemList);
            intent.putExtra(Const.BUNDLE_POSITION, position);
            startActivity(intent);
        }
        mPosition = position;
    }
}

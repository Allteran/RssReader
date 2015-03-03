package ua.ck.geekhub.prozapas.ghprozapasrssreader.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;

import com.facebook.AppEventsLogger;

import java.util.ArrayList;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.fragments.DetailsFragment;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.fragments.ListItemFragment;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.helpers.RssDatabaseHelper;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.services.RssPullService;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const;

public class MainActivity extends BaseActivity implements ListItemFragment.OnListFragmentEvent {
    private ArrayList<RssItem> mRssItemList = null;
    private boolean isContentHere = true;
    private DetailsFragment mDetailsFragment;
    private ListItemFragment mListItemFragment;
    private Intent mPullServiceIntent;
    private RssDatabaseHelper mDatabaseHelper;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        mDatabaseHelper = new RssDatabaseHelper(this);

        mPullServiceIntent = new Intent(this, RssPullService.class);
        startService(mPullServiceIntent);

        isContentHere = (findViewById(R.id.details_fragment_content) != null);

        mListItemFragment = new ListItemFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            fragmentTransaction.replace(R.id.list_fragment, mListItemFragment);
        }
//        else {
//            fragmentTransaction.replace(R.id.list_fragment, mListItemFragment);
//        }
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


    //        if (getString(R.string.screen_type).equals("sw600dp")) {
//            menu.add(0, Const.ACTION_ADD_TO_FAV_ID, 2, R.string.action_add_to_favorites)
//                    .setIcon(R.drawable.ic_action_important).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//            menu.add(0, Const.ACTION_SHARE, 2, R.string.action_share)
//                    .setIcon(R.drawable.ic_action_share)
//                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        }
//        super.onCreateOptionsMenu(menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case Const.ACTION_REFRESH_ID:
//                mListItemFragment.startDownload();
//                break;
//            case Const.ACTION_SHOW_FAV_ID:
//                Intent favoritesIntent = new Intent(MainActivity.this, FavoritesActivity.class);
//                startActivity(favoritesIntent);
//                break;
//            case Const.ACTION_ADD_TO_FAV_ID:
//                mDatabaseHelper.addArticle(mRssItemList.get(mPosition));
//                Toast.makeText(this, getString(R.string.added_to_fav_message), Toast.LENGTH_SHORT).show();
//                break;
//            //Share only via Facebook for now
//            case Const.ACTION_SHARE:
//                if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
//                    FacebookDialog fbShareDialog = new FacebookDialog.ShareDialogBuilder(this)
//                            .setName(mRssItemList.get(mPosition).getTitle())
//                            .setLink(mRssItemList.get(mPosition).getLink())
//                            .setDescription((Html.fromHtml(mRssItemList.get(mPosition).getContent())).toString())
//                            .build();
//                    super.mUiHelper.trackPendingDialogCall(fbShareDialog.present());
//                } else {
//                    new ShareFacebookHelper(this, mRssItemList.get(mPosition)).login(this);
//                }
//                break;
//            case Const.ACTION_GPLUS_SINGIN:
//                startActivity(new Intent(this, GPlusActivity.class));
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}

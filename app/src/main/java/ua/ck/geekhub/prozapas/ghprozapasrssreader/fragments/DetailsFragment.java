package ua.ck.geekhub.prozapas.ghprozapasrssreader.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.FacebookDialog;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.activities.MainActivity;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.databases.RealmHelper;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.helpers.ShareFacebookHelper;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const;

/**
 * Created by Allteran on 16.11.2014.
 */
public class DetailsFragment extends BaseFragment {

    private RssItem mRssItem;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private RealmHelper mRealmHelper;
    private Realm mRealm;
    private ActionBarActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity = (ActionBarActivity) getActivity();
        mRealm = Realm.getInstance(mActivity);
        mRealmHelper = new RealmHelper();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRealmHelper = new RealmHelper();
        showLoadingBar(getView());

        if (getArguments() == null) {
            showNoData(getView(), getResources().getString(R.string.no_data_article));
        } else {
            mRssItem = (RssItem) getArguments().getSerializable(Const.ARG_ENTRY);

            ImageView image = (ImageView) view.findViewById(R.id.details_image);
            try {
                mRssItem.setImageURL(mRssItem.getContent().substring(mRssItem.getContent().lastIndexOf("http"), mRssItem.getContent().indexOf("jpg") + 3));
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
            try {
                Picasso.with(getActivity()).load(mRssItem.getImageURL()).into(image);
            } catch (Exception e) {
                e.printStackTrace();
                image.setImageResource(R.mipmap.no_photo);
            }
            String contentWithoudImage = mRssItem.getContent().replace(mRssItem.getContent().substring(mRssItem.getContent()
                    .lastIndexOf("<img"), mRssItem.getContent().indexOf("br") + 4), "");
            TextView textViewDescription = (TextView) view.findViewById(R.id.details_description);
            textViewDescription.setText(Html.fromHtml(contentWithoudImage));

            TextView textViewTitle = (TextView) view.findViewById(R.id.details_title);
            textViewTitle.setText(mRssItem.getTitle());

            TextView textViewLink = (TextView) view.findViewById(R.id.details_link);
            textViewLink.setText(getResources().getString(R.string.before_link_message) + " " + mRssItem.getLink());

            Date incDate = new Date();
            try {
                incDate = mSimpleDateFormat.parse(mRssItem.getPublishDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm");
            String publishDate = dateFormat.format(incDate);

            TextView textViewPublishedDate = (TextView) view.findViewById(R.id.details_published_date);
            textViewPublishedDate.setText(getResources().getString(R.string.before_date_message) + " " + publishDate);

            Button gplusShareButton = (Button) view.findViewById(R.id.gplus_share_button);
            gplusShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new PlusShare.Builder(getActivity())
                            .setType("text/plain")
                            .setText(mRssItem.getTitle())
                            .setContentUrl(Uri.parse(mRssItem.getLink()))
                            .getIntent();

                    startActivityForResult(shareIntent, 0);
                }
            });
            showContent(getView());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mRealmHelper.isArticleInDatabase(mRealm, mRssItem)) {
            menu.findItem(R.id.action_add_to_favorite).setIcon(R.drawable.ic_action_important);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_to_favorite:
                if (mRealmHelper.isArticleInDatabase(mRealm, mRssItem)) {
                    mRealmHelper.deleteArticle(mRealm, mRssItem);
                    Toast.makeText(mActivity, R.string.delete_article_message, Toast.LENGTH_SHORT).show();
                    mActivity.supportInvalidateOptionsMenu();
                } else {
                    mRealmHelper.addArticle(mRssItem, mRealm);
                    mActivity.supportInvalidateOptionsMenu();
                    Toast.makeText(mActivity, R.string.added_to_fav_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_share:
                if (FacebookDialog.canPresentShareDialog(getActivity(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    FacebookDialog fbShareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
                            .setName(mRssItem.getTitle())
                            .setLink(mRssItem.getLink())
                            .setDescription((Html.fromHtml(mRssItem.getContent())).toString())
                            .build();
                    MainActivity activity = (MainActivity) getActivity();
                    activity.getUiHelper().trackPendingDialogCall(fbShareDialog.present());
                } else {
                    new ShareFacebookHelper(mActivity, mRssItem).login(mActivity);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static DetailsFragment newInstance(RssItem rssItem) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.ARG_ENTRY, rssItem);
        detailsFragment.setArguments(args);
        return detailsFragment;
    }
}

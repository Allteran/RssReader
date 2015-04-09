package ua.ck.geekhub.prozapas.ghprozapasrssreader.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.models.RssItem;

/**
 * Created by Allteran on 16.01.2015.
 */
public class ShareFacebookHelper {
    private Context mContext;
    private SessionStatusCallback mStatusCallback = new SessionStatusCallback();
    private RssItem mCurrentFeed;

    public ShareFacebookHelper(Context context, RssItem currentFeed) {
        mContext = context;
        mCurrentFeed = currentFeed;
    }

    public void publishFeedDialog() {
        Bundle params = new Bundle();
        params.putString("name", mCurrentFeed.getTitle());
        params.putString("description", mCurrentFeed.getContent());
        params.putString("link", mCurrentFeed.getLink());
        params.putString("picture", mCurrentFeed.getImageURL());

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(mContext,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                Toast.makeText(mContext,
                                        mContext.getString(R.string.posted_article_message) + " \"" + mCurrentFeed.getTitle() + "\"",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // User clicked the Cancel button
                                Toast.makeText(mContext.getApplicationContext(),
                                        R.string.publish_cancelled_message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            Toast.makeText(mContext.getApplicationContext(),
                                    R.string.publish_cancelled_message,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Generic, ex: network error
                            Toast.makeText(mContext.getApplicationContext(),
                                    R.string.error_publishing_message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .build();
        feedDialog.show();
    }

    public void login(Activity context) {
        Session currentSession = Session.getActiveSession();
        if (!currentSession.isOpened() && !currentSession.isClosed()) {
            currentSession.openForRead(new Session.OpenRequest(context).setCallback(mStatusCallback));
        } else {
            Session.openActiveSession(context, true, mStatusCallback);
        }
    }

    private void whenLoggedIn() {
        publishFeedDialog();
    }

    private class SessionStatusCallback implements Session.StatusCallback {

        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                whenLoggedIn();
            }
        }
    }
}

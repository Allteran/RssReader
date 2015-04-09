package ua.ck.geekhub.prozapas.ghprozapasrssreader.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;

/**
 * Created by Allteran on 10.01.2015.
 */
public class GPlusActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int RQ_CODE_SIGN = 0;
    private GoogleApiClient mPlusClient;
    private boolean mIntentInProgress;
    private boolean mSingButtonPressed;
    private ConnectionResult mConnectionResult;
    private TextView mLoginName;
    private ImageView mAvatarImage;
    private View mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gplus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.gplus_singin_button).setOnClickListener(this);
        mPlusClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mLoginName = (TextView) findViewById(R.id.gplus_login_name);
        mAvatarImage = (ImageView) findViewById(R.id.gplus_login_photo);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_CODE_SIGN) {
            mIntentInProgress = false;
            if (resultCode != RESULT_OK) {
                mSingButtonPressed = false;
            }

            mIntentInProgress = false;

            if (!mPlusClient.isConnecting()) {
                mPlusClient.connect();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        findViewById(R.id.gplus_singin_button).setVisibility(View.GONE);
        mSingButtonPressed = false;

        new AsyncTask<Void, Void, Void>() {
            public String userName;
            public Bitmap userImage;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgress = findViewById(R.id.gplus_progress_dialog);
                if(mProgress !=null) {
                    mProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (Plus.PeopleApi.getCurrentPerson(mPlusClient) != null) {
                    Person currentUser = Plus.PeopleApi.getCurrentPerson(mPlusClient);
                    userName = currentUser.getDisplayName();
                    String personPhotoURL = currentUser.getImage().getUrl();
                    personPhotoURL += "0";

                    try {
                        InputStream in = new java.net.URL(personPhotoURL).openStream();
                        userImage = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgress.setVisibility(View.GONE);
                mLoginName.setText(userName);
                mAvatarImage.setVisibility(View.VISIBLE);
                mAvatarImage.setImageBitmap(userImage);
                getSupportActionBar().setTitle(R.string.its_you);
                Toast.makeText(getApplicationContext(), R.string.gplus_successfully_signed_message, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlusClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RQ_CODE_SIGN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mPlusClient.connect();
            }
        }

        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSingButtonPressed) {
                resolveSignInError();
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.gplus_singin_button && !mPlusClient.isConnecting()) {
            mSingButtonPressed = true;
            mPlusClient.connect();
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult != null) {
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                            RQ_CODE_SIGN, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mPlusClient.connect();
                }
            }
        }
    }
}
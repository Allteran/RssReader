package ua.ck.geekhub.prozapas.ghprozapasrssreader.fragments;

import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;

/**
 * Created by Allteran on 16.11.2014.
 */
public abstract class BaseFragment extends Fragment {

    public void showLoadingBar(final View v) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View prgrssBar = v.findViewById(R.id.loading_indicator);
                if (prgrssBar != null) {
                    prgrssBar.setVisibility(View.VISIBLE);
                }

                View contentContainer = v.findViewById(R.id.content_container);
                if (contentContainer != null) {
                    contentContainer.setVisibility(View.GONE);
                }

                View noDataContentContainer = v.findViewById(R.id.no_data_container);
                if (noDataContentContainer != null) {
                    noDataContentContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    public void showContent(final View v) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View prgrssBar = v.findViewById(R.id.loading_indicator);
                if (prgrssBar != null) {
                    prgrssBar.setVisibility(View.GONE);
                }

                View contentContainer = v.findViewById(R.id.content_container);
                if (contentContainer != null) {
                    contentContainer.setVisibility(View.VISIBLE);
                }

                View noDataContentContainer = v.findViewById(R.id.no_data_container);
                if (noDataContentContainer != null) {
                    noDataContentContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    public void showNoData(final View v, final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View prgrssBar = v.findViewById(R.id.loading_indicator);
                if (prgrssBar != null) {
                    prgrssBar.setVisibility(View.GONE);
                }

                View contentContainer = v.findViewById(R.id.content_container);
                if (contentContainer != null) {
                    contentContainer.setVisibility(View.GONE);
                }

                View noDataContentContainer = v.findViewById(R.id.no_data_container);
                if (noDataContentContainer != null){
                    noDataContentContainer.setVisibility(View.VISIBLE);
                }

                TextView noDataMessage = (TextView) v.findViewById(R.id.no_data_text_view);
                if (noDataMessage != null) {
                    noDataMessage.setText(message);
                }
            }
        });
    }
}

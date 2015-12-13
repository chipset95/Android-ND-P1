package chipset.pone.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chipset.pone.R;

/**
 * Developer: chipset
 * Package : chipset.pone.fragments
 * Project : Popular Movies
 * Date : 13/12/15
 */
public class DefaultFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_default, container, false);
    }
}

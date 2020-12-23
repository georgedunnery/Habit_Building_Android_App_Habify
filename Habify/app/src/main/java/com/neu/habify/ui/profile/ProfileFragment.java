package com.neu.habify.ui.profile;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neu.habify.R;
import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.User;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.scrolling.ComplexRecyclerViewAdapter;
import com.neu.habify.data.scrolling.ListMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String PROF_FRAG_TAG = "ProfileFragment";
    private ProfileViewModel profileViewModel;
    private RecyclerView recyclerView;
    private ComplexRecyclerViewAdapter adapter;
    private TextView firstName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_shareable_profile) {
            Navigation.findNavController(getView()).navigate(R.id.nav_profile_to_shareable);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeProfileImage();
        setProfileName();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(PROF_FRAG_TAG, "onViewCreated");
        this.profileViewModel.receiveCallback(this);


        recyclerSetup();
    }

    public void notifyBadgesListReady() {
        Log.i(PROF_FRAG_TAG, "notifyBadgesListReady");
        observerSetup();
    }

    private void changeProfileImage() {
        final ImageView gender = (ImageView)getView().findViewById(R.id.gender);
        final ImageView profile = (ImageView)getView().findViewById(R.id.profile_image);

        gender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Drawable drawable = gender.getDrawable();
                Drawable female = getResources().getDrawable(R.drawable.female);
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                Bitmap bitmap2 = ((BitmapDrawable)female).getBitmap();
                if(bitmap == bitmap2) {
                    gender.setImageResource(R.drawable.male);
                    profile.setImageResource(R.drawable.profile_summoner);
                } else {
                    gender.setImageResource(R.drawable.female);
                    profile.setImageResource(R.drawable.profile_lux);
                }

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Drawable drawable1 = profile.getDrawable();
                Drawable female1 = getResources().getDrawable(R.drawable.profile_lux);
                Bitmap bitmap3 = ((BitmapDrawable)drawable1).getBitmap();
                Bitmap bitmap4 = ((BitmapDrawable)female1).getBitmap();
                if(bitmap3 == bitmap4) {
                    gender.setImageResource(R.drawable.male);
                    profile.setImageResource(R.drawable.profile_summoner);
                } else {
                    gender.setImageResource(R.drawable.female);
                    profile.setImageResource(R.drawable.profile_lux);
                }

            }
        });
    }

    private void setProfileName(){
        firstName = getView().findViewById(R.id.firstName);

        this.profileViewModel.findName();
        this.profileViewModel.getSearchName().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            TextView firstName = getView().findViewById(R.id.firstName);

            @Override
            public void onChanged(@Nullable final List<User> users) {
                if (users.size() > 0) {
                    String str = users.get(0).getFirstName();
                    String name = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
                    firstName.setText(name);

                } else {
                    firstName.setText("No Match");
                }
            }
        });
    }

    private void observerSetup() {
        Log.i(PROF_FRAG_TAG, "observerSetup");
        profileViewModel.getBadges().observe(getViewLifecycleOwner(), new Observer<List<Badge>>() {
            @Override
            public void onChanged(List<Badge> listItems) {
                setListItems(listItems);
            }
        });
    }



    private void recyclerSetup() {
        this.recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_profile);
        this.adapter = new ComplexRecyclerViewAdapter();

        // TODO Set list through a database query
        this.adapter.setItems(new LinkedList<Object>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // This adds the horizontal gray lines between the list items
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void setListItems(List<Badge> observed) {
        // Will pass a List<Object> to the complex recycler view adapter
        List<Object> profileList = new ArrayList<>();

        // Query the habits list, include a message if there are no habits for today
        if (observed.size() > 0) {
            // TODO setup a header of the profile info
            //profileList.add(profileHeader);
            profileList.addAll(observed);
        }
        // Display message saying there are no habits for today
        else {
            profileList.add(new ListMessage(getString(R.string.no_badges_earned_yet)));
        }
        adapter.setItems(profileList);
        Log.i(PROF_FRAG_TAG, "Set List Items");
    }
}
package com.neu.habify.ui.profile;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.User;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.room.ProfileRepository;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private static final String PROF_VIEW_TAG = "ProfileViewModel";
    private MutableLiveData<String> mText;
    private ProfileRepository profileRepo;
    private LiveData<List<Badge>> badges;
    private ProfileFragment callback;
    private LiveData<List<User>> allUsers;
    private MutableLiveData<List<User>> searchResults;

    public ProfileViewModel(Application app) {
        super(app);
        mText = new MutableLiveData<>();
        mText.setValue("This is the profile fragment");
        profileRepo = new ProfileRepository(app, this);
        allUsers = profileRepo.getAllUsers();
        searchResults = profileRepo.getSearchName();
        setupBadgesList();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void receiveCallback(ProfileFragment callback) {
        this.callback = callback;
        setupBadgesList();
        Log.i(PROF_VIEW_TAG, "received callback");
    }

    public MutableLiveData<List<User>> getSearchName() {
        return searchResults;
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }


    public void findName() {
        profileRepo.findName();
    }

    private void setupBadgesList() {
        Log.i(PROF_VIEW_TAG, "setup badges list");
        this.profileRepo.getEarnedBadges();
    }

    public void setBadges(LiveData<List<Badge>> badges) {
        this.badges = badges;
        this.callback.notifyBadgesListReady();
    }

    public LiveData<List<Badge>> getBadges() {
        return this.badges;
    }
}
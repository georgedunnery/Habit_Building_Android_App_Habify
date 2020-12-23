package com.neu.habify.data.room;

import android.app.Application;

/**
 * Class responsible for centralizing common methods and fields among repository objects.
 */
public abstract class HabifyRepository {

    protected HabifyDao dao;

    public HabifyRepository(Application app) {
        this.dao = HabifyRoomDatabase.getDatabase(app).habifyDao();
    }

    // TODO make generic query AsyncTask that takes a HabifyDao method as an argument!!!!!

    // TODO make generic insert AsyncTask that takes a HabifyDao method as an argument!!!
}

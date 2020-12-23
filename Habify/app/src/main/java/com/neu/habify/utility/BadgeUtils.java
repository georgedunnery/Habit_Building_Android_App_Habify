package com.neu.habify.utility;
/**
 * Utilities for working with badges
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.neu.habify.R;
import com.neu.habify.data.entity.Award;
import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.Event;
import com.neu.habify.data.entity.Record;
import com.neu.habify.data.room.HabifyDao;

public class BadgeUtils {
    /**
     * Display a badge achievement dialog
     * @param activity The activity to show the badge achievement dialog in
     * @param badge    The badge object to get display data (icon, topic) from.
     */
    public static void displayBadgeCongratsDialog(FragmentActivity activity, Badge badge) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View badgeDialogView = activity.getLayoutInflater().inflate(R.layout.dialog_new_badge_received, null);
        TextView topicView = badgeDialogView.findViewById(R.id.topic);
        ImageView imageView = badgeDialogView.findViewById(R.id.imageView);
        topicView.setText(badge.getName());
        Context context = imageView.getContext();
        int imageSourceId = context.getResources().getIdentifier(badge.getIcon(), "drawable", context.getPackageName());
        imageView.setImageResource(imageSourceId);
        builder.setView(badgeDialogView);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    /**
     * calculate the correct badge type to award, and commit new award to db.
     *
     * The calculation is based on the current streak track record
     * Description:
     * 1. how many records has been achieved in a row for the related habit
     * 2. map count to badge id
     * 3. commit new award record to db
     */
    public static void addNewAward(final HabifyDao dao, final Record record, FragmentActivity callbackActivity){
        if (!record.isEvaluation()) return;

        final int habitID = record.getHabitId();
        final FragmentActivity activity = callbackActivity;

        class GetBadgeAsync extends AsyncTask<Integer, Void, Badge> {
            @Override
            protected Badge doInBackground(Integer... integers) {
                return dao.getBadge(integers[0]).get(0);
            }

            @Override
            protected void onPostExecute(Badge badge) {
                // TODO: need to obtain activity here, don't know how
                BadgeUtils.displayBadgeCongratsDialog(activity, badge);
            }
        }

        class GetLastAwardAsync extends AsyncTask<Void, Void, Award> {
            @Override
            protected Award doInBackground(Void... any) {
                return dao.getLastAward(habitID).get(0);
            }
            @Override
            protected void onPostExecute(Award award) {
                new GetBadgeAsync().execute(award.getBadgeId());
            }
        }

        class InsertAwardAsync extends AsyncTask<Integer, Void, Void> {
            @Override
            protected Void doInBackground(Integer... badgeId) {
                // map streakCount to badge_id value
                dao.insertAward(1, badgeId[0], record.getEventId());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                new GetLastAwardAsync().execute();
            }
        }

        class CheckDupAward extends AsyncTask<Integer, Void, Integer> {
            private int badgeId;
            @Override
            protected Integer doInBackground(Integer... streakCounts) {
                badgeId = streakCounts[0];
                // map streakCount to badge_id value
                return dao.checkAwards(badgeId, record.getEventId());
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (integer == 0)
                    new InsertAwardAsync().execute(badgeId);
            }
        }

        class CountStreakForHabit extends AsyncTask<Integer, Void, Integer> {
            @Override
            protected Integer doInBackground(Integer... habitIds) {
                return dao.countStreak(habitIds[0]);
            }
            @Override
            protected void onPostExecute(Integer streakCount) {
                new CheckDupAward().execute(streakCount);
            }
        }

        CountStreakForHabit countStreakForHabit = new CountStreakForHabit();
        countStreakForHabit.execute(habitID);
    }

}
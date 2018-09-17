package planet.it.limited.callbutton.services;

/**
 * Created by Tarikul on 9/13/2018.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;
import android.util.Log;

public class ScheduledService extends JobIntentService{
    private static final int UNIQUE_JOB_ID=1337;

   public static void enqueueWork(Context ctxt) {
        enqueueWork(ctxt, ScheduledService.class, UNIQUE_JOB_ID,
                new Intent(ctxt, ScheduledService.class));
    }

    @Override
    public void onHandleWork(Intent i) {
        Log.d(getClass().getSimpleName(), "I ran!");
    }
}

package org.georgemalandrakis.archion.scheduledtasks;

import io.dropwizard.jobs.Job;
import io.dropwizard.jobs.annotations.DelayStart;
import io.dropwizard.jobs.annotations.Every;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DelayStart("15s")
@Every("24h")
public class DeleteDuplicates extends Job {
    DeleteGeneral deleteGeneral;

    @Override
    public void doJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        /*
            There is an extremely small probability (way less that one in a trillion) of an SHA-1 collision.
            The probability for it to happen for files of the same user is even more quantum.
            If this ever happens here, god is to be blamed and not the developers.
         */
        deleteGeneral.removeDuplicates(); //every 24h
    }

    public void setNecessaryClasses(DeleteGeneral deleteGeneral) {
        this.deleteGeneral = deleteGeneral;
    }
}

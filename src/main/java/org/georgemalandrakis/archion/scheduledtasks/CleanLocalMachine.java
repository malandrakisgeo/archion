package org.georgemalandrakis.archion.scheduledtasks;

import io.dropwizard.jobs.Job;
import io.dropwizard.jobs.annotations.DelayStart;
import io.dropwizard.jobs.annotations.Every;
import org.georgemalandrakis.archion.core.ArchionConstants;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
    Removes from the local machine files not accessed
 */
@DelayStart("10s")
@Every("24h")
public class CleanLocalMachine extends Job {
    DeleteGeneral deleteGeneral;

    @Override
    public void doJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        deleteGeneral.cleanLocalMachine(); //every 24h

    }

    public void setNecessaryClasses(DeleteGeneral deleteGeneral) {
        this.deleteGeneral = deleteGeneral;
    }

}

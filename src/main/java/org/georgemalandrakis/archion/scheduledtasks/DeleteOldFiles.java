package org.georgemalandrakis.archion.scheduledtasks;

import io.dropwizard.jobs.Job;
import io.dropwizard.jobs.annotations.Every;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Every("24h")
public class DeleteOldFiles extends Job {

    DeleteGeneral deleteGeneral;

    @Override
    public void doJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        deleteGeneral.runDelete("default"); //every 24h

    }

    public void setNecessaryClasses(DeleteGeneral deleteGeneral) {
        this.deleteGeneral = deleteGeneral;
    }


}

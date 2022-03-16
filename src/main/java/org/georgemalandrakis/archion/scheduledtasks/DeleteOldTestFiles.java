package org.georgemalandrakis.archion.scheduledtasks;

import io.dropwizard.jobs.Job;
import io.dropwizard.jobs.annotations.Every;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Every("5m")
public class DeleteOldTestFiles extends Job {

    DeleteGeneral deleteGeneral;

    @Override
    public void doJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        deleteGeneral.runDelete("test");

    }
    public void setNecessaryClasses(DeleteGeneral deleteGeneral) {
        this.deleteGeneral = deleteGeneral;
    }
}

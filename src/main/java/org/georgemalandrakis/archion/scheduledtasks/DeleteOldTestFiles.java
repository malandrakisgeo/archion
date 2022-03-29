package org.georgemalandrakis.archion.scheduledtasks;

import io.dropwizard.jobs.Job;
import io.dropwizard.jobs.annotations.DelayStart;
import io.dropwizard.jobs.annotations.Every;
import org.georgemalandrakis.archion.core.ArchionConstants;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DelayStart("5s")
@Every("1mn")
public class DeleteOldTestFiles extends Job {

    DeleteGeneral deleteGeneral;

    @Override
    public void doJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        deleteGeneral.runDelete(ArchionConstants.FILES_TEST_FILETYPE);
    }


    public void setNecessaryClasses(DeleteGeneral deleteGeneral) {
        this.deleteGeneral = deleteGeneral;
    }
}

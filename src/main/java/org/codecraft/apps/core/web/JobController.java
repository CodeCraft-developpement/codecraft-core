package org.codecraft.apps.core.web;

import com.axelor.meta.db.MetaSchedule;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import org.codecraft.apps.core.quartz.ReflectSchedulerProvider;
import org.quartz.SchedulerException;

public class JobController {

  public void checkJobStatus(ActionRequest request, ActionResponse response)
      throws SchedulerException {

    MetaSchedule metaSchedule = request.getContext().asType(MetaSchedule.class);

    Integer status = null;

    if (metaSchedule != null) {
      status = ReflectSchedulerProvider.getJobStatusByMetaSchedule(metaSchedule);
    }

    response.setValue("$ccJobStatusSelect", status == null ? 0 : status);
  }
}

package org.codecraft.apps.core.quartz;

import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaSchedule;
import com.axelor.quartz.SchedulerModule;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.Key;

public class ReflectSchedulerProvider {

  private static Class<SchedulerModule> schedulerModule;

  private static Object schedulerProviderClassInstance;

  @SuppressWarnings("unchecked")
  public static Scheduler reflectGetScheduler() {

    Scheduler scheduler = null;

    try {

      if (schedulerModule == null) {
        schedulerModule =
            (Class<SchedulerModule>) Class.forName("com.axelor.quartz.SchedulerProvider");
      }

      if (schedulerProviderClassInstance == null) {
        schedulerProviderClassInstance =
            Beans.get(Class.forName("com.axelor.quartz.SchedulerProvider"));
      }

      Method method = schedulerProviderClassInstance.getClass().getMethod("get");

      method.setAccessible(true);

      scheduler = (Scheduler) method.invoke(schedulerProviderClassInstance);

    } catch (ClassNotFoundException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    return scheduler;
  }

  public static Integer getJobStatusByMetaSchedule(MetaSchedule metaSchedule)
      throws SchedulerException {

    if (!metaSchedule.getActive()) {
      return null;
    }

    List<? extends Trigger> triggerList =
        reflectGetScheduler().getTriggersOfJob(getJobKeyByMetaSchedule(metaSchedule));

    Trigger.TriggerState triggerState = null;

    if (!triggerList.isEmpty()) {
      for (Trigger trigger : triggerList) {
        // We consider that there can only be one job with the same name.
        triggerState = reflectGetScheduler().getTriggerState(trigger.getKey());
        break;
      }
    }

    if (triggerState != null) {
      switch (triggerState) {
        case NONE:
          return 0;
        case NORMAL:
          return 2;
        case PAUSED:
          return 3;
        case COMPLETE:
          return 4;
        case ERROR:
          return 5;
        case BLOCKED:
          return 6;
        default:
          return null;
      }
    }

    return null;
  }

  public static JobDetail getJobDetailByMetaSchedule(MetaSchedule metaSchedule) {
    try {
      return reflectGetScheduler().getJobDetail(getJobKeyByMetaSchedule(metaSchedule));
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  public static JobKey getJobKeyByMetaSchedule(MetaSchedule metaSchedule) {
    return getJobKeyByNameAndGroup(metaSchedule.getName(), Key.DEFAULT_GROUP);
  }

  public static JobKey getJobKeyByNameAndGroup(String jobName, String jobGroup) {
    JobKey jobKey = null;
    try {
      for (JobKey itJobKey :
          reflectGetScheduler().getJobKeys(GroupMatcher.jobGroupEquals(jobGroup))) {
        if (itJobKey.getName().equals(jobName)) {
          jobKey = itJobKey;
          break;
        }
      }
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }

    return jobKey;
  }
}

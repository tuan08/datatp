package net.datatp.registry.activity;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.datatp.registry.activity.Activity;
import net.datatp.registry.activity.ActivityCoordinator;
import net.datatp.registry.activity.ActivityExecutionContext;
import net.datatp.registry.activity.ActivityService;
import net.datatp.registry.activity.ActivityStep;

@Singleton
public class HelloActivityCoordinator extends ActivityCoordinator {
  final static public String ACTIVITIES_PATH = "/activities" ;
  
  @Inject
  private HelloActivityStepWorkerService workerService ;
  
  public void onResume(ActivityService service, Activity activity) {
    System.err.println("On resume activity: " + activity.getDescription()) ;
  }

  @Override
  protected <T> void execute(ActivityExecutionContext context, Activity activity, ActivityStep step) throws Exception {
    workerService.exectute(context, activity, step);
  }
}
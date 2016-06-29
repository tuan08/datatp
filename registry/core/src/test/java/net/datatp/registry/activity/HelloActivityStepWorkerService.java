package net.datatp.registry.activity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import net.datattp.registry.RegistryException;
import net.datattp.registry.activity.ActivityStepWorkerService;

@Singleton
public class HelloActivityStepWorkerService extends ActivityStepWorkerService<String> {
  @Inject
  public HelloActivityStepWorkerService(Injector container) throws RegistryException {
    super("HelloWorker");
  }
}

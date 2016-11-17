package net.datatp.registry.activity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import net.datatp.registry.RegistryException;
import net.datatp.registry.activity.ActivityStepWorkerService;

@Singleton
public class HelloActivityStepWorkerService extends ActivityStepWorkerService<String> {
  @Inject
  public HelloActivityStepWorkerService(Injector container) throws RegistryException {
    super("HelloWorker");
  }
}

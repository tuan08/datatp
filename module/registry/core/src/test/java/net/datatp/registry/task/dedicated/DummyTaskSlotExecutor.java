package net.datatp.registry.task.dedicated;

import java.util.Random;

import net.datatp.registry.task.TaskDescriptor;
import net.datattp.registry.task.TaskExecutorDescriptor;
import net.datattp.registry.task.dedicated.DedicatedTaskContext;
import net.datattp.registry.task.dedicated.TaskExecutorEvent;
import net.datattp.registry.task.dedicated.TaskSlotExecutor;

public class DummyTaskSlotExecutor extends TaskSlotExecutor<TaskDescriptor> {
  private Random random = new Random();
  
  public DummyTaskSlotExecutor(DedicatedTaskContext<TaskDescriptor> taskContext) {
    super(taskContext);
  }

  @Override
  public void onEvent(TaskExecutorEvent event) throws Exception {
  }
  
  @Override
  public long executeSlot() throws Exception {
    DedicatedTaskContext<TaskDescriptor> context = getTaskContext();
    TaskExecutorDescriptor executor = context.getTaskExecutorDescriptor();
    Thread.sleep(100);
    if(random.nextInt(3) == 1) {
      getTaskContext().setComplete();
      System.out.println("Task " + context.getTaskId() + ", execute by executor " + executor.getId() + ", complete = true");
    } else {
      System.out.println("Task " + context.getTaskId() + ", execute by executor " + executor.getId() + ", complete = false");
    }
    return 100;
  }
}

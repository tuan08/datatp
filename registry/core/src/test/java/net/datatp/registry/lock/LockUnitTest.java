package net.datatp.registry.lock;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.datatp.util.io.FileUtil;
import net.datatp.zookeeper.tool.server.EmbededZKServer;
import net.datattp.registry.ErrorCode;
import net.datattp.registry.Node;
import net.datattp.registry.NodeCreateMode;
import net.datattp.registry.Registry;
import net.datattp.registry.RegistryConfig;
import net.datattp.registry.RegistryException;
import net.datattp.registry.lock.Lock;
import net.datattp.registry.lock.LockId;
import net.datattp.registry.zk.RegistryImpl;

public class LockUnitTest {
  static {
    System.setProperty("log4j.configuration", "file:src/test/resources/test-log4j.properties") ;
  }
  
  final static String LOCK_DIR = "/locks" ;
  
  private EmbededZKServer zkServerLauncher ;
  private AtomicLong lockOrder ;
  
  @Before
  public void setup() throws Exception {
    FileUtil.removeIfExist("./build/data", false);
    lockOrder = new AtomicLong() ;
    zkServerLauncher = new EmbededZKServer("./build/data/zookeeper") ;
    zkServerLauncher.start();
  }
  
  @After
  public void teardown() throws Exception {
    zkServerLauncher.shutdown();
  }

  private Registry newRegistry() {
    return new RegistryImpl(RegistryConfig.getDefault()) ;
  }
  
  @Test
  public void testConcurrentLock() throws Exception {
    String DATA = "lock directory";
    Registry registry = newRegistry().connect(); 

    Node lockDir = registry.create(LOCK_DIR, DATA.getBytes(), NodeCreateMode.PERSISTENT) ;
    registry.shutdown();
    Worker[] worker = new Worker[50];
    ExecutorService executorPool = Executors.newFixedThreadPool(worker.length);
    for(int i = 0; i < worker.length; i++) {
      worker[i] = new Worker("worker-" + (i + 1)) ;
      executorPool.execute(worker[i]);
      if(i % 10 == 0) Thread.sleep(new Random().nextInt(50));
    }
    executorPool.shutdown();
    executorPool.awaitTermination(15 * 60 * 1000, TimeUnit.MILLISECONDS);
    for(int i = 0; i < worker.length; i++) {
      Assert.assertTrue(worker[i].complete);
    }
  }
  
  public class Worker implements Runnable {
    String name ;
    LockId lockId ;
    boolean complete = false;
    
    public Worker(String name) {
      this.name = name ;
    }
    
    public void run() {
      try {
        Random random = new Random() ;
        Thread.sleep(random.nextInt(100));
        Registry registry = newRegistry().connect();
        Node lockDir =  registry.get(LOCK_DIR) ;
        Lock lock = lockDir.getLock("write", "test write lock") ;
        lockId = lock.lock(60 * 1000) ; //wait max 15s for lock
        System.out.println("\nWorker " + name + " acquires the lock: " + lockId);
        long execTime = random.nextInt(1000) ;
        Thread.sleep(execTime);
        System.out.println(" Process in " + execTime);
        Assert.assertEquals(lockOrder.getAndIncrement(), lockId.getSequence()) ;
        lock.unlock();
        System.out.println("Worker " + name + " releases the lock: " + lockId);
        complete = true ;
        registry.shutdown();
      } catch(RegistryException e) {
        if(e.getErrorCode() == ErrorCode.Timeout) {
          complete = true ;
          System.err.println(e.getMessage()) ;
        } else {
          e.printStackTrace();
        }
      } catch(Exception e) {
        e.printStackTrace();
      } 
    }
  }
}

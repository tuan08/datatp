/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.datatp.zk.curator;

import org.apache.curator.utils.CloseableUtils;
import org.junit.Test;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockingDemoTest {
  private static final int    QTY         = 5;
  private static final int    REPETITIONS = QTY * 10;

  private static final String PATH        = "/examples/locks";

  @Test
  public void test() throws Exception {
    // all of the useful sample code is in ExampleClientThatLocks.java

    // FakeLimitedResource simulates some external resource that can only be
    // access by one process at a time
    final FakeLimitedResource resource = new FakeLimitedResource();

    ExecutorService service = Executors.newFixedThreadPool(QTY);
    final TestingServer server = new TestingServer();
    try {
      for (int i = 0; i < QTY; ++i) {
        final int index = i;
        Callable<Void> task = new Callable<Void>() {
          @Override
          public Void call() throws Exception {
            CuratorFramework client = 
              CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            try {
              client.start();

              ExampleClientThatLocks example = new ExampleClientThatLocks(client, PATH, resource, "Client " + index);
              for (int j = 0; j < REPETITIONS; ++j) {
                example.doWork(10, TimeUnit.SECONDS);
              }
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            } catch (Exception e) {
              e.printStackTrace();
              // log or do something
            } finally {
              CloseableUtils.closeQuietly(client);
            }
            return null;
          }
        };
        service.submit(task);
      }

      service.shutdown();
      service.awaitTermination(10, TimeUnit.MINUTES);
    } finally {
      CloseableUtils.closeQuietly(server);
    }
  }
  
  static public class ExampleClientThatLocks {
    private final InterProcessMutex   lock;
    private final FakeLimitedResource resource;
    private final String              clientName;

    public ExampleClientThatLocks(CuratorFramework client, String lockPath, FakeLimitedResource resource, String clientName) {
      this.resource = resource;
      this.clientName = clientName;
      lock = new InterProcessMutex(client, lockPath);
    }

    public void doWork(long time, TimeUnit unit) throws Exception {
      if (!lock.acquire(time, unit)) {
        throw new IllegalStateException(clientName + " could not acquire the lock");
      }
      try {
        System.out.println(clientName + " has the lock");
        resource.use();
      } finally {
        System.out.println(clientName + " releasing the lock");
        lock.release(); // always release the lock in a finally block
      }
    }
  }

  /**
   * Simulates some external resource that can only be access by one process at a time
   */
  public class FakeLimitedResource {
    private final AtomicBoolean inUse = new AtomicBoolean(false);

    public void use() throws InterruptedException {
      // in a real application this would be accessing/manipulating a shared
      // resource

      if (!inUse.compareAndSet(false, true)) {
        throw new IllegalStateException("Needs to be used by one client at a time");
      }

      try {
        Thread.sleep((long) (3 * Math.random()));
      } finally {
        inUse.set(false);
      }
    }
  }

}

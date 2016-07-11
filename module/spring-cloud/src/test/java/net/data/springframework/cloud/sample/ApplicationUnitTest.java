/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.data.springframework.cloud.sample;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.datatp.util.io.FileUtil;
import net.datatp.zk.tool.server.EmbededZKServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest(randomPort = false)
public class ApplicationUnitTest {

  static EmbededZKServer zkServer ;
  
  @BeforeClass
  static public void before() throws Exception {
    FileUtil.removeIfExist("build/zookeeper/data", false);
    zkServer = new EmbededZKServer("build/zookeeper/data", 2182);
    zkServer.start();
  }

  @AfterClass
  static public void clean() throws IOException {
    zkServer.shutdown();
  }

  @Test 
  public void contextLoads() throws InterruptedException {
    Thread.sleep(3000000);
  }
}

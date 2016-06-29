package net.datatp.facebook;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restfb.types.User;

import net.datatp.facebook.FBClient;
import net.datatp.facebook.FBClientManager;


public class FBClientManagerIntegrationTest {
  String accountId = "100000017446992";
  FBClientManager manager ;
  
  @Before
  public void setup() {
    manager = new FBClientManager();
  }
  
  @After
  public void teardown() {
  }
  
  @Test
  public void testValidateAccessToken() {
    FBClient[] fbClients = manager.getFBClients();
    FBClient fbClient  = null;
    try {
      for(int i = 0; i < fbClients.length; i++) {
        fbClient = fbClients[i];
        User user = fbClient.fetchUser(accountId);
        String userJson = fbClient.getJsonMapper().toJson(user, true);
        System.out.println("User");
        System.out.println("  Id: "  + user.getId());
        System.out.println("  updated time: " + user.getUpdatedTime());
        System.out.println("  Secure Browsing: "  + user.getSecuritySettings());
        System.out.println(userJson);
        user = fbClient.getJsonMapper().toJavaObject(userJson, User.class);
      }
    } catch(Throwable t) {
      if(fbClient != null) {
        System.err.println("ACCESS TOKEN: " + fbClient.getAccessToken());
        throw t;
      }
    }
  }
}

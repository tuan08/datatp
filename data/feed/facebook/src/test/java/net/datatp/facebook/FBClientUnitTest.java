package net.datatp.facebook;

import static java.lang.System.currentTimeMillis;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.restfb.Connection;import com.restfb.exception.FacebookGraphException;
import com.restfb.types.Comment;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;

import net.datatp.facebook.FBClient;
import net.datatp.facebook.FBClientManager;

public class FBClientUnitTest {
  String accountId = "100000039508197";
  
  private FBClient fbClient ;
  
  @Before
  public void setup() {
    fbClient = new FBClientManager().getFBClient();
  }
  
  @After
  public void teardown() {
  }
  
  @Test
  public void testFBClient() {
    User user = fbClient.fetchUser(accountId);
    String userJson = fbClient.getJsonMapper().toJson(user, true);
    System.out.println("User");
    System.out.println("  Id: "  + user.getId());
    System.out.println("  updated time: " + user.getUpdatedTime());
    System.out.println("  Secure Browsing: "  + user.getSecuritySettings());
    System.out.println(userJson);
    user = fbClient.getJsonMapper().toJavaObject(userJson, User.class);
    
    Date oneMonthAgo = new Date(currentTimeMillis() - 1000L * 60L * 60L * 24L * 365L);
    //Connection<Post> posts = fbClient.fetchPosts(accountId, oneMonthAgo, new Date(), 10);
    Connection<Post> postConn = fbClient.fetchPosts(accountId);
    List<Post> currPagePosts = postConn.getData();
    System.out.println("Posts:");
    System.out.println("  Total Count:        " + postConn.getTotalCount());
    System.out.println("  Current Page Count: " + currPagePosts.size());
    System.out.println("  Next Page Url     : " + postConn.getNextPageUrl());
    System.out.println("  Messages: ");
    Post post = null;
    for(int i = 0; i < currPagePosts.size(); i++) {
      post = currPagePosts.get(i);
      System.out.println("    " + post.getId() + " - " + post.getLikesCount());
    }
    System.out.println("Post Sample:");
    System.out.println("  id  : " + post.getId());
    System.out.println("  from id  : " + post.getFrom().getId());
    System.out.println("  Type: " + post.getType());
    System.out.println("  Message: " + post.getMessage());
    System.out.println(fbClient.getJsonMapper().toJson(post, true));
    
    System.out.println("Post Comment:");
    Connection<Comment> commentConn = fbClient.fetchComments(post.getId());
    System.out.println("  Count:        " + commentConn.getData().size());
    System.out.println("  First Message:        " + commentConn.getData().get(0).getMessage());
    
    System.out.println("Friends:");
    Connection<User>  friends = fbClient.fetchFriends(accountId);
    System.out.println("  Total Count:        " + friends.getTotalCount());
    System.out.println("  Current Page Count: " + friends.getData().size());
    System.out.println("  Previous Page Url : " + friends.getPreviousPageUrl());
    System.out.println("  Next Page Url     : " + friends.getNextPageUrl());
    System.out.println("  Before Cursor     : " + friends.getBeforeCursor());
    System.out.println("  Next Cursor       : " + friends.getNextPageUrl());
    
    List<User> users = fbClient.fetchUsers(accountId);
    System.out.println("Fetch Users:");
    for(User sel : users) {
      System.out.println("  " + sel.getName());
    }
    
    //System.out.println(friends);
    Page page = fbClient.fetchPage("cocacola");
    String pageJson = fbClient.getJsonMapper().toJson(page, true);
    System.out.println(pageJson);
    page = fbClient.getJsonMapper().toJavaObject(pageJson, Page.class);
  }
  
  //Test object does not exist or permission problem
  @Test
  public void testObjectIdAccessPermission() {
    try {
      fbClient.fetchComments("000064217283_1213224492023013");
    } catch(FacebookGraphException ex) {
      System.err.println("message: " + ex.getMessage());
      System.err.println("error code: " + ex.getErrorCode());
      System.err.println("error sub code: " + ex.getErrorSubcode());
      System.err.println("error Type: " + ex.getErrorType());
    }
  }
}

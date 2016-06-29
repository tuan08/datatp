package net.datatp.facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookGraphException;
import com.restfb.json.JsonObject;
import com.restfb.types.Comment;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;

public class FBClient {
  private String         accessToken;
  private FacebookClient restfbClient;

  public FBClient(String accessToken) {
    this.accessToken = accessToken;
    restfbClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_0);
  }

  public String getAccessToken() {
    return this.accessToken;
  }

  public JsonMapper getJsonMapper() {
    return restfbClient.getJsonMapper();
  }

  public User fetchUser(String id) {
    User user = restfbClient.fetchObject(id, User.class);
    return user;
  }

  public List<User> fetchUsers(String... id) throws FacebookGraphException {
    if(id == null || id.length == 0) return new ArrayList<User>();
    return fetchUsers(Arrays.asList(id));
  }

  public List<User> fetchUsers(List<String> ids) throws FacebookGraphException {
    JsonObject results = restfbClient.fetchObjects(ids, JsonObject.class);
    Iterator<?> keys = results.keys();
    List<User> users = new ArrayList<>();
    while (keys.hasNext()) {
      String key = (String) keys.next();
      User user = restfbClient.getJsonMapper().toJavaObject(results.getString(key), User.class);
      users.add(user);
    }
    return users;
  }

  public Connection<User> fetchFriends(String id) throws FacebookGraphException {
    Connection<User> friends = restfbClient.fetchConnection(id + "/friends", User.class);
    return friends;
  }

  public Connection<Post> fetchPosts(String id) throws FacebookGraphException {
    Parameter[] params = { 
        Parameter.with("fields", "id,message,type,from"), 
        Parameter.with("limit", 100) 
    };
    Connection<Post> posts = restfbClient.fetchConnection(id + "/feed", Post.class, params);
    return posts;
  }
  
  public Connection<Post> fetchNextPostPage(Connection<Post> conn) throws FacebookGraphException {
    if(conn.getNextPageUrl() != null) {
      return restfbClient.fetchConnectionPage(conn.getNextPageUrl(), Post.class);
    }
    return null;
  }

  public Connection<Post> fetchPosts(String id, Date from, Date to, int limit) throws FacebookGraphException {
    Parameter[] params = { 
        Parameter.with("fields", "id,message,type,from"), 
        Parameter.with("since", from),
        Parameter.with("until", to), Parameter.with("limit", limit) 
    };
    Connection<Post> posts = restfbClient.fetchConnection(id + "/feed", Post.class, params);
    return posts;
  }

  public Connection<Comment> fetchComments(String id) throws FacebookGraphException {
    Parameter[] params = {Parameter.with("limit", 100)};
    Connection<Comment> comments = restfbClient.fetchConnection(id + "/comments", Comment.class, params);
    return comments;
  }
  
  public Connection<Comment> fetchNextCommentPage(Connection<Comment> conn) throws FacebookGraphException {
    if(conn.getNextPageUrl() != null) {
      return restfbClient.fetchConnectionPage(conn.getNextPageUrl(), Comment.class);
    }
    return null;
  }

  public Page fetchPage(String pageId) throws FacebookGraphException {
    Page page = restfbClient.fetchObject(pageId, Page.class);
    return page;
  }
}

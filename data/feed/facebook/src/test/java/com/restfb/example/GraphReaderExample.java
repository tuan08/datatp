package com.restfb.example;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.Url;
import com.restfb.types.User;

/**
 * Examples of RestFB's Graph API functionality.
 * 
 * @author <a href="http://restfb.com">Mark Allen</a>
 */
@SuppressWarnings("deprecation")
public class GraphReaderExample {
//  static {
//    try {
//      LogManager.getLogManager().readConfiguration(GraphReaderExample.class.getResourceAsStream("/logging.properties"));
//    } catch (Exception e) {
//      throw new IllegalStateException("Could not read in logging configuration", e);
//    }
//  }
  
  /**
   * RestFB Graph API client.
   */
  private final FacebookClient fbClient23;
  private final FacebookClient fbClient20;

  /**
   * Entry point. You must provide a single argument on the command line: a valid Graph API access token.
   * 
   * @param args
   *          Command-line arguments.
   * @throws IllegalArgumentException
   *           If no command-line arguments are provided.
   */
  public static void main(String[] args) {
    String oauthToken = "EAAAAUaZA8jlABAH8UbbKniORy2v22ywAVa45lDGHUxN6rKZCLJN5UNZC04daU4PZAfG2tTP67K533VQsb4I9U0WoXxbCLiqUH41iSYTYxfTcCFhomKYRqRW6Tl27Pcas4Adb9xfsBh0QLn14FSujAn3mbFeIOGZABUkFbV5vcAfz2OrN5G1ZAg";
    if (args.length >= 1) {
      oauthToken = args[0];
    }
    new GraphReaderExample(oauthToken).runEverything();
  }

  GraphReaderExample(String accessToken) {
    fbClient23 = new DefaultFacebookClient(accessToken, Version.VERSION_2_3);
    fbClient20 = new DefaultFacebookClient(accessToken, Version.VERSION_2_0);
  }

  void runEverything() {
    fetchObject();
    fetchObjects();
    //fetchObjectsAsJsonObject();
    fetchConnections();
    fetchDifferentDataTypesAsJsonObject();
    query();
    multiquery();
    search();
    metadata();
    paging();
    selection();
    parameters();
    rawJsonResponse();
  }

  void fetchObject() {
    out.println("* Fetching single objects *");

    User user = fbClient23.fetchObject("me", User.class);
    Page page = fbClient23.fetchObject("cocacola", Page.class);

    out.println("User name: " + user.getName());
    out.println("Page likes: " + page.getLikes());
  }

  void fetchObjectsAsJsonObject() {
    out.println("* Fetching multiple objects at once as a JsonObject *");

    List<String> ids = new ArrayList<String>();
    ids.add("4");
    ids.add("http://www.imdb.com/title/tt0117500/");

    // Make the API call
    JsonObject results = fbClient23.fetchObjects(ids, JsonObject.class);

    System.out.println(results.toString());

    // Pull out JSON data by key and map each type by hand.
    JsonMapper jsonMapper = new DefaultJsonMapper();
    User user = jsonMapper.toJavaObject(results.getString("4"), User.class);
    Url url = jsonMapper.toJavaObject(results.get("http://www.imdb.com/title/tt0117500/").toString(), Url.class);

    out.println("User is " + user);
    out.println("URL is " + url);
  }

  void fetchObjects() {
    out.println("* Fetching multiple objects at once *");

    FetchObjectsResults fetchObjectsResults = fbClient23.fetchObjects(Arrays.asList("me", "cocacola"), FetchObjectsResults.class);

    out.println("User name: " + fetchObjectsResults.me.getName());
    out.println("Page likes: " + fetchObjectsResults.page.getLikes());
  }

  void fetchDifferentDataTypesAsJsonObject() {
    out.println("* Fetching different types of data as JsonObject *");

    JsonObject zuck = fbClient23.fetchObject("4", JsonObject.class);
    out.println(zuck.getString("name"));

    JsonObject photosConnection = fbClient23.fetchObject("me/photos", JsonObject.class);
    JsonArray photosConnectionData = photosConnection.getJsonArray("data");

    if (photosConnectionData.length() > 0) {
      String firstPhotoUrl = photosConnectionData.getJsonObject(0).getString("source");
      out.println(firstPhotoUrl);
    }

    String query = "SELECT uid, name FROM user WHERE uid=4 or uid=11";
    List<JsonObject> queryResults = fbClient20.executeFqlQuery(query, JsonObject.class);

    if (!queryResults.isEmpty())
      out.println(queryResults.get(0).getString("name"));
  }

  /**
   * Holds results from a "fetchObjects" call.
   */
  public static class FetchObjectsResults {
    @Facebook
    User me;

    @Facebook(value = "cocacola")
    Page page;
  }

  void fetchConnections() {
    out.println("* Fetching connections *");

    Connection<User> myFriends = fbClient23.fetchConnection("me/friends", User.class);
    Connection<Post> myFeed    = fbClient23.fetchConnection("me/feed", Post.class);

    out.println("Count of my friends: " + myFriends.getData().size());

    if (!myFeed.getData().isEmpty())
      out.println("First item in my feed: " + myFeed.getData().get(0).getMessage());
  }

  void query() {
    out.println("* FQL Query *");

    List<FqlUser> users = fbClient20.executeFqlQuery("SELECT uid, name FROM user WHERE uid=4 or uid=11", FqlUser.class);

    out.println("User: " + users);
  }

  void multiquery() {
    out.println("* FQL Multiquery *");

    Map<String, String> queries = new HashMap<String, String>();
    queries.put("users", "SELECT uid, name FROM user WHERE uid=4 OR uid=11");
    queries.put("likers", "SELECT user_id FROM like WHERE object_id=122788341354");

    MultiqueryResults multiqueryResults = fbClient20.executeFqlMultiquery(queries, MultiqueryResults.class);

    out.println("Users: " + multiqueryResults.users);
    out.println("People who liked: " + multiqueryResults.likers);
  }

  /**
   * Holds results from an "executeQuery" call.
   * <p>
   * Be aware that FQL fields don't always map to Graph API Object fields.
   */
  public static class FqlUser {
    @Facebook
    String uid;

    @Facebook
    String name;

    @Override
    public String toString() {
      return format("%s (%s)", name, uid);
    }
  }

  /**
   * Holds results from an "executeQuery" call.
   * <p>
   * Be aware that FQL fields don't always map to Graph API Object fields.
   */
  public static class FqlLiker {
    @Facebook("user_id")
    String userId;

    @Override
    public String toString() {
      return userId;
    }
  }

  /**
   * Holds results from a "multiquery" call.
   */
  public static class MultiqueryResults {
    @Facebook
    List<FqlUser> users;

    @Facebook
    List<FqlLiker> likers;
  }

  void search() {
    out.println("* Searching connections *");

    // Connection<Post> publicSearch =
    // facebookClient23.fetchConnection("search", Post.class, Parameter.with("q", "watermelon"),
    // Parameter.with("type", "post"));

    Connection<User> targetedSearch = fbClient23.fetchConnection("search", User.class,
      Parameter.with("q", "Mark"), Parameter.with("type", "user"));

    // if (publicSearch.getData().size() > 0)
    // out.println("Public search: " + publicSearch.getData().get(0).getMessage());

    out.println("Posts on my wall by friends named Mark: " + targetedSearch.getData().size());
  }

  void metadata() {
    out.println("* Metadata *");

    User userWithMetadata = fbClient23.fetchObject("me", User.class, Parameter.with("metadata", 1));

    out.println("User metadata: has albums? " + userWithMetadata.getMetadata().getConnections().hasAlbums());
  }

  void paging() {
    out.println("* Paging support *");

    Connection<User> myFriends = fbClient23.fetchConnection("me/friends", User.class);
    Connection<Post> myFeed = fbClient23.fetchConnection("me/feed", Post.class, Parameter.with("limit", 100));

    out.println("Count of my friends: " + myFriends.getData().size());

    if (!myFeed.getData().isEmpty())
      out.println("First item in my feed: " + myFeed.getData().get(0));

    for (List<Post> myFeedConnectionPage : myFeed)
      for (Post post : myFeedConnectionPage)
        out.println("Post from my feed: " + post);
  }

  void selection() {
    out.println("* Selecting specific fields *");

    User user = fbClient23.fetchObject("me", User.class, Parameter.with("fields", "id,name"));

    out.println("User name: " + user.getName());
  }

  void parameters() {
    out.println("* Parameter support *");

    Date oneWeekAgo = new Date(currentTimeMillis() - 1000L * 60L * 60L * 24L * 7L);

    Connection<Post> filteredFeed = 
      fbClient23.fetchConnection("me/feed", Post.class, Parameter.with("limit", 3), Parameter.with("until", "yesterday"), Parameter.with("since", oneWeekAgo));

    out.println("Filtered feed count: " + filteredFeed.getData().size());
  }

  void rawJsonResponse() {
    out.println("* Raw JSON *");
    out.println("User object JSON: " + fbClient23.fetchObject("me", String.class));
  }
}
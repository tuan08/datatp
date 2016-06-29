package net.datatp.wanalytic.facebook;

import static java.lang.System.currentTimeMillis;

import java.util.Date;
import java.util.List;

import com.restfb.Connection;
import com.restfb.exception.FacebookGraphException;
import com.restfb.types.Comment;
import com.restfb.types.Post;
import com.restfb.types.User;

import net.datatp.facebook.FBClient;
import net.datatp.facebook.FBClientError;
import net.datatp.facebook.FBClientManager;
import net.datatp.facebook.FBUtil;
import net.datatp.kafka.KafkaTool;
import net.datatp.kafka.producer.AckKafkaWriter;
import net.datatp.model.message.Message;
import net.datatp.model.net.facebook.FBComment;
import net.datatp.model.net.facebook.FBPost;
import net.datatp.model.net.facebook.FBUser;

public class FBObjectFetcher {
  final static public String USER_QUEUE    = "facebook.crawler.data";
  final static public String POST_QUEUE    = "facebook.crawler.data";
  final static public String COMMENT_QUEUE = "facebook.crawler.data";
  
  private int              id;
  private String           zkConnects;
  private FBClientManager  fbClientManager;
  private AckKafkaWriter   kafkaWriter;
  private Thread           fetchThread;
  private FBObjectIdQueue  objectIdQueue;
  
  public FBObjectFetcher(int id, String zkConnects, FBObjectIdQueue objectIdQueue) {
    this.id              = id;
    this.zkConnects      = zkConnects;
    this.objectIdQueue   = objectIdQueue;
    this.fbClientManager = new FBClientManager();
  }
  
  public void start() throws Exception {
    KafkaTool kafkaTool = new KafkaTool("KafkaTool", zkConnects);
    kafkaWriter = new AckKafkaWriter("FBObjectFetcher-" + id, kafkaTool.getKafkaBrokerList()) ;
    kafkaTool.close();
    
    fetchThread = new Thread() {
      public void run() { crawl(); }
    };
    fetchThread.start();
  }
  
  public void shutdown() throws Exception {
    fetchThread.interrupt();
    kafkaWriter.close();
  }
  
  synchronized public void waitForTermination(long timeout) throws InterruptedException {
    wait(timeout);
  }
  
  synchronized public void notifyTermination() {
    notifyAll();
  }
  
  void crawl() {
    try {
      List<String> idHolder = objectIdQueue.nextIdChunk(25);
      while(idHolder != null) {
        new FetchUserOperation(idHolder).execute();
        idHolder = objectIdQueue.nextIdChunk(25);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      notifyTermination();
    }
  }
  
  public class FetchUserOperation extends Operation {
    List<String> accountIds ;
    
    public FetchUserOperation(List<String> accountIds) { this.accountIds = accountIds; }
    
    @Override
    protected void doExecute() throws FacebookGraphException, Exception {
      List<User> users = fbClientManager.nextFBClient().fetchUsers(accountIds);
      for(int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        System.out.println("Fetch User: " + user.getName() + " - " + user.getId());
        write(user);
        new FetchPostOperation(user.getId()).execute();
      }
    }
    
    void write(User user) throws Exception {
      FBUser fbUser = FBUtil.toFBUser(user);
      String id = fbUser.getPersonalInfo().getId();
      kafkaWriter.send(USER_QUEUE, id, new Message(id, fbUser, "fb.user"), 30000);
    }
  }
  
  public class FetchPostOperation extends Operation {
    String accountId ;
    
    public FetchPostOperation(String accountId) { this.accountId = accountId; }
    
    @Override
    protected void doExecute() throws FacebookGraphException, Exception {
      FBClient fbClient = fbClientManager.nextFBClient();
      Date fromDate = new Date(currentTimeMillis() - 1000L * 60L * 60L * 24L * 31L);
      Date toDate   = new Date();
      Connection<Post> connPosts = fbClient.fetchPosts(accountId, fromDate, toDate, 100);
      while(connPosts != null) {
        List<Post> postHolder = connPosts.getData();
        System.out.println("  Fetch Post: " + postHolder.size() + ", total count = " + connPosts.getTotalCount());
        for(int i = 0; i < postHolder.size(); i++) {
          Post post = postHolder.get(i);
          write(post);
          new FetchPostCommentOperation(fbClient, post).execute();
        }
        connPosts = fbClient.fetchNextPostPage(connPosts);
      }
    }
    
    void write(Post post) throws Exception {
      FBPost fbPost = FBUtil.toFBPost(post);
      String id = fbPost.getId();
      kafkaWriter.send(POST_QUEUE, id, new Message(id, fbPost, "fb.post"), 30000);
    }
  }
  
  public class FetchPostCommentOperation extends Operation {
    FBClient fbClient;
    Post post ;
    
    public FetchPostCommentOperation(FBClient fbClient, Post post) { 
      this.fbClient = fbClient;
      this.post = post; 
    }
    
    @Override
    protected void doExecute() throws FacebookGraphException, Exception {
      Connection<Comment> commentConn = fbClient.fetchComments(post.getId());
      while(commentConn != null) {
        List<Comment> commentHolder = commentConn.getData();
        for(int i = 0; i < commentHolder.size(); i++) {
          Comment comment = commentHolder.get(i);
          write(post, comment);
        }
        System.out.println("    Fetch Comment: " + commentHolder.size() + ", post comment count" +  post.getCommentsCount() + ", total count = " + commentConn.getTotalCount());
        commentConn = fbClient.fetchNextCommentPage(commentConn);
      }
    }
    
    void write(Post post, Comment comment) throws Exception {
      if(comment == null) return;
      FBComment fbComment = FBUtil.toFBComment(post, comment);
      String id = fbComment.getId();
      kafkaWriter.send(COMMENT_QUEUE, id, new Message(id, fbComment, "fb.comment"), 30000);
    }
  }
  
  static abstract public class Operation {
    public void execute() throws Exception {
      try {
        doExecute();
      } catch(FacebookGraphException ex) {
        FBClientError error = FBClientError.getFBClientError(ex);
        if(error.isIgnorable()) return;
        throw ex;
      }
    }

    abstract protected void doExecute() throws FacebookGraphException, Exception ;
  }
  
}
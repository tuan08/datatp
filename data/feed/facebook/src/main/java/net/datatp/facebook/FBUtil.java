package net.datatp.facebook;

import java.util.ArrayList;
import java.util.List;

import com.restfb.types.Comment;
import com.restfb.types.Comments;
import com.restfb.types.Post;
import com.restfb.types.User;

import net.datatp.model.location.Location;
import net.datatp.model.net.facebook.FBComment;
import net.datatp.model.net.facebook.FBPost;
import net.datatp.model.net.facebook.FBUser;
import net.datatp.model.personal.PersonalInfo;

public class FBUtil {
  static public FBUser toFBUser(User user) {
    FBUser fbUser = new FBUser() ;
    fbUser.setTimestamp(user.getUpdatedTime());
    PersonalInfo pInfo = new PersonalInfo();
    pInfo.setId(user.getId());
    pInfo.setFullName(user.getName());
    pInfo.setFirstName(user.getFirstName());
    pInfo.setLastName(user.getLastName());
    pInfo.setBirthday(user.getBirthdayAsDate());
    pInfo.setReligion(user.getReligion());
    pInfo.setGender(user.getGender());
    pInfo.setLocale(user.getLocale());
    pInfo.setAbout(user.getAbout());
    pInfo.setBio(user.getBio());
    fbUser.setPersonalInfo(pInfo);
    if(user.getLocation() != null) {
      Location location = new Location();
      location.setId(user.getLocation().getId());
      location.setName(user.getLocation().getName());
      location.setType(user.getLocation().getType());
      fbUser.setLocation(location);
    }
    return fbUser;
  }
  
  
  static public FBPost toFBPost(Post post) {
    FBPost fbPost = new FBPost();
    fbPost.setTimestamp(post.getCreatedTime());
    fbPost.setId(post.getId());
    fbPost.setFromId(post.getFrom().getId());
    fbPost.setType(post.getType());
    fbPost.setMessage(post.getMessage());
    return fbPost;
  }
  
  static public List<FBComment> getFBComments(Post post) {
    List<FBComment> holder = new ArrayList<>();
    Comments comments = post.getComments();
    List<Comment> data = comments.getData();
    for(int i = 0; i < data.size(); i++) {
      addComment(holder, post, data.get(i));
    }
    return holder;
  }
  
  static void addComment(List<FBComment> holder, Post post, Comment comment) {
    holder.add(toFBComment(post, comment));
    Comment.Comments comments = comment.getComments();
    List<Comment> data = comments.getData();
    if(data == null) return;
    for(int i = 0; i < data.size(); i++) {
      addComment(holder, post, data.get(i));
    }
  }
  
  static public FBComment toFBComment(Post post, Comment comment) {
    FBComment fbComment = new FBComment();
    fbComment.setTimestamp(comment.getCreatedTime());
    fbComment.setId(comment.getId());
    fbComment.setForObjectId(post.getId());
    fbComment.setFromId(comment.getFrom().getId());
    fbComment.setLikeCount(comment.getLikeCount());
    fbComment.setMessage(comment.getMessage());
    return fbComment;
  }
}

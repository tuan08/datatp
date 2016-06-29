package net.datatp.storage.batchdb.util;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSUtil {
	
  static public Configuration getDaultConfiguration() {
    Configuration conf = new Configuration(true) ;
    return conf ;
  }
  
  static public Configuration getClusterConfiguration() {
    Configuration conf = new Configuration(false) ;
    conf.addResource("core-default.xml") ;
    conf.addResource("hdfs-default.xml") ;
    conf.addResource("mapred-default.xml") ;
    
    conf.addResource("core-site.xml") ;
    conf.addResource("hdfs-site.xml") ;
    conf.addResource("mapred-site.xml") ;
    return conf ;
  }
  
  static public boolean removeIfExists(FileSystem fs, String path) throws IOException {
    Path p = new Path(path) ;
    if(fs.exists(p)) return fs.delete(p, true) ;
    return true ;
  }
  
  static public boolean removeIfExists(FileSystem fs, Path path) throws IOException {
    if(fs.exists(path)) return fs.delete(path, true) ;
    return true ;
  }
  
  static public boolean mkdirs(FileSystem fs, String path) throws IOException {
    Path p = new Path(path) ;
    if(!fs.exists(p)) return fs.mkdirs(p) ;
    return true ;
  }
  
  static public void mv(FileSystem fs, String src, String dest) throws IOException {
    Path destPath = new Path(dest) ;
    if(!fs.exists(destPath.getParent())) fs.mkdirs(destPath.getParent()) ;
    if(!fs.rename(new Path(src), destPath)) {
      throw new IOException("Cannot mv " + src  + " to " + dest) ;
    }
  }
  
  static public void mv(FileSystem fs, Path src, Path dest) throws IOException {
    if(!fs.exists(dest.getParent())) fs.mkdirs(dest.getParent()) ;
    if(!fs.rename(src, dest)) {
      throw new IOException("Cannot mv " + src  + " to " + dest) ;
    }
  }
  
  static public void findDescendantFiles(FileSystem fs, List<Path> holder, Path path, String ext) throws IOException {
    FileStatus[] children = fs.listStatus(path) ;
    if(children == null) return ;
    for(int i = 0; i < children.length; i++) {
      if(!children[i].isDir()) {
        Path childPath = children[i].getPath() ;
        if(ext == null) {
          holder.add(childPath) ;
        } else if(childPath.toString().endsWith(ext)){
          holder.add(childPath) ;
        }
      } else {
        findDescendantFiles(fs, holder, children[i].getPath(), ext) ;
      }
    }
  }
}
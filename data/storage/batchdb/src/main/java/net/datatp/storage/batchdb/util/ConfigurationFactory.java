package net.datatp.storage.batchdb.util;

import java.util.Enumeration;

import org.apache.hadoop.conf.Configuration;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ConfigurationFactory {
  static private String compressionCodec ;

  static public void setCompressionCodec(String string) {compressionCodec = string; }

  static public Configuration getConfiguration() {
    Configuration conf = new Configuration(false) ;
    conf.addResource("core-default.xml") ;
    conf.addResource("hdfs-default.xml") ;
    conf.addResource("mapred-default.xml") ;

    conf.addResource("core-site.xml") ;
    conf.addResource("hdfs-site.xml") ;
    conf.addResource("mapred-site.xml") ;

    if(compressionCodec != null) {
      if("lzo".equalsIgnoreCase(compressionCodec)) {
        conf.set("compression.default.codec", "lzo") ;
        conf.set("mapred.output.compression.codec", "com.hadoop.compression.lzo.LzoCodec") ;
      } else if("gzip".equalsIgnoreCase(compressionCodec)) {
        conf.set("compression.default.codec", "gzip") ;
        conf.set("mapred.output.compression.codec", "org.apache.hadoop.io.compress.GzipCodec") ;
      } else {
        conf.set("compression.default.codec", "default") ;
        conf.set("mapred.output.compression.codec", "org.apache.hadoop.io.compress.DefaultCodec") ;
      }
    }

    Enumeration e =  System.getProperties().propertyNames() ;
    while(e.hasMoreElements()) {
      String name = (String) e.nextElement() ;
      if(name.startsWith("hadoop.master")) {
        String value = System.getProperty(name) ;
        conf.set("fs.default.name", "hdfs://" + value + ":9000") ;
        conf.set("mapred.job.tracker",  value + ":9001") ;
      } else if(name.startsWith("hadoop.") || name.startsWith("mapred.") || 
                name.startsWith("dfs.") || name.startsWith("fs.")) {
        String value = System.getProperty(name) ;
        if(value != null) conf.set(name, value);
      }
    }
    return conf ;
  }

  static void print(Configuration conf){
    System.out.println("Hadoop directory: "              + conf.get("hadoop.dir"));
    System.out.println(" + Hadoop DFS directory: "       + conf.get("hadoop.dfs.dir"));
    System.out.println(" + Hadoop mapred directory: "    + conf.get("hadoop.mapred.dir"));
    System.out.println(" + Hadoop working directory: "   + conf.get("hadoop.working.dir"));
    System.out.println(" + Hadoop tmp directory: "       + conf.get("hadoop.tmp.dir"));
   
    System.out.println("\nFile system default: "                             + conf.get("fs.default.name"));
    System.out.println("The frequency of server threads for the namenode: "     + conf.get("dfs.namenode.handler.count"));
    System.out.println("The frequency of server threads for the datanode: "     + conf.get("dfs.datanode.handler.count"));
    System.out.println("An upper bound on connections was added in Hadoop: " + conf.get("dfs.datanode.max.xcievers"));
    
    System.out.println("\nMapred job tracker: "            + conf.get("mapred.job.tracker"));
    System.out.println("Mapred temporary directory: "      + conf.get("mapred.temp.dir"));
    System.out.println("Mapred output compression type: "  + conf.get("mapred.output.compression.type"));
    System.out.println("Mapred output compression codec: " + conf.get("mapred.output.compression.codec"));
    System.out.println("***********************************************\n");
  }

  static public void main(String[] args) throws Exception {
    // Default
    Configuration conf = ConfigurationFactory.getConfiguration() ;
    ConfigurationFactory.print(conf);
    
    // 
    System.setProperty("hadoop.master",  "localhost") ;
    conf = ConfigurationFactory.getConfiguration() ;
    ConfigurationFactory.print(conf);
    
    // Start with hadoop
    System.setProperty("hadoop.dir",         "target1/hadoop") ;
    System.setProperty("hadoop.dfs.dir",     "target2/hadoop/dfs") ;
    System.setProperty("hadoop.mapred.dir",  "target3/hadoop/mapred") ;
    System.setProperty("hadoop.working.dir", "target4/hadoop/working") ;
    
    // Start with dfs 
    System.setProperty("fs.default.name",             "hdfs://192.168.5.13:9000") ;
    System.setProperty("dfs.namenode.handler.count",  "50") ;
    System.setProperty("dfs.datanode.handler.count",  "8") ;
    System.setProperty("dfs.datanode.max.xcievers",   "2048") ;
    
    // Start with mapred
    System.setProperty("mapred.temp.dir",                 "target1/mapred/temp") ;
    System.setProperty("mapred.job.tracker",              "192.168.5.13:9001") ;
    System.setProperty("mapred.output.compression.type",  "RECORD") ;
    System.setProperty("mapred.output.compression.codec", "org.apache.hadoop.io.compress.GzipCodec") ;
    
    conf = ConfigurationFactory.getConfiguration() ;
    ConfigurationFactory.print(conf);
  }
}
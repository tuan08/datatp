package net.datatp.es.server;

public class Main {
  static public void main(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[] {
          "--es:server.name=elasticsearch-1",
          "--es:path.data=./build/elasticsearch-1"
      } ;
    }
  }
}
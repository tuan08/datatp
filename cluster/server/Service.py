from sys import path
from os.path import  dirname, abspath
#Make sure the package is computed from cluster directory
path.insert(0, dirname(dirname(abspath(__file__))))

class Process(object):
  def __init__(self, name, service):
    self.name    = name;
    self.service = service

class Service(object):
  def __init__(self, name, config):
    self.name   = name;
    self.config = config;
    self.homeDir = self.config.install.destination
    self.init(name, config)
    
  def init(self, name, config): raise NotImplementedError
  
  @staticmethod
  def createService(name, config):
    if   name == 'zookeeper': return ZKService(name, config)
    elif name == 'kafka': return KafkaService(name, config)
    elif name == 'elasticsearch': return ESService(name, config)
    elif name == 'spark': return SparkService(name, config)
    elif name == 'hadoop': return HadoopService(name, config)
    elif name == 'tracking': return TrackingService(name, config)
    return None

class ZKService(Service):
  def init(self, name, config): 
    self.cmdClean   = "rm -rf %s/data && rm -rf %s/logs" % (self.homeDir, self.homeDir)
    self.processes = {
      'QuorumPeerMain': {
        'cmdFindPid': 'pgrep -f org.apache.zookeeper.server.quorum.QuorumPeerMain',
        'cmdStart':   self.homeDir + '/bin/zkServer.sh start',
        'cmdStop':    self.homeDir + '/bin/zkServer.sh stop'
      }
    }
    
class KafkaService(Service):
  def init(self, name, config): 
    self.cmdClean   = "rm -rf %s/data && rm -rf %s/logs" % (self.homeDir, self.homeDir)
    self.processes = {
      'Kafka': {
        'cmdFindPid': 'pgrep -f kafka.Kafka',
        'cmdStart':   self.homeDir + '/bin/kafka-server-start.sh -daemon ' + self.homeDir + '/config/server.properties',
        'cmdStop':    self.homeDir + '/bin/kafka-server-stop.sh'
      }
    }

class ESService(Service):
  def init(self, name, config): 
    self.cmdClean   = "rm -rf %s/data && rm -rf %s/logs" % (self.homeDir, self.homeDir)
    self.processes = {
      'Main': {
        'cmdFindPid': 'pgrep -f org.elasticsearch.bootstrap.Elasticsearch',
        'cmdStart':   self.homeDir + '/bin/elasticsearch -d',
        'cmdStop':    'pkill -9 `pgrep -f org.elasticsearch.bootstrap.Elasticsearch`'
      }
    }

class SparkService(Service):
  def init(self, name, config): 
    self.cmdClean   = "rm -rf %s/data && rm -rf %s/logs" % (self.homeDir, self.homeDir)
    self.processes = {
      'Main': {
      }
    }
    
class HadoopService(Service):
  def init(self, name, config): 
    self.cmdClean   = "rm -rf %s/data && rm -rf %s/logs" % (self.homeDir, self.homeDir)
    self.processes = {
      'namenode': {
        'cmdFindPid': 'pgrep -f org.apache.hadoop.hdfs.server.namenode.NameNode',
        'cmdStart':   self.homeDir + '/sbin/hadoop-daemon.sh start namenode',
        'cmdStop':    self.homeDir + '/sbin/hadoop-daemon.sh stop  namenode'
      },
      'secondarynamenode': {
        'cmdFindPid': 'pgrep -f org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode',
        'cmdStart':   self.homeDir + '/sbin/hadoop-daemon.sh start secondarynamenode',
        'cmdStop':    self.homeDir + '/sbin/hadoop-daemon.sh stop  secondarynamenode'
      },
      'datanode': {
        'cmdFindPid': 'pgrep -f org.apache.hadoop.hdfs.server.datanode.DataNode',
        'cmdStart':   self.homeDir + '/sbin/hadoop-daemon.sh start datanode',
        'cmdStop':    self.homeDir + '/sbin/hadoop-daemon.sh stop  datanode'
      },
      'resourcemanager': {
        'cmdFindPid': 'pgrep -f org.apache.hadoop.yarn.server.resourcemanager.ResourceManager',
        'cmdStart':   self.homeDir + '/sbin/yarn-daemon.sh start resourcemanager',
        'cmdStop':    self.homeDir + '/sbin/yarn-daemon.sh stop  resourcemanager'
      },
      'nodemanager': {
        'cmdFindPid': 'pgrep -f org.apache.hadoop.yarn.server.nodemanager.NodeManager',
        'cmdStart':   self.homeDir + '/sbin/yarn-daemon.sh start nodemanager',
        'cmdStop':    self.homeDir + '/sbin/yarn-daemon.sh stop  nodemanager'
      }
    }

class TrackingService(Service):
  def init(self, name, config): 
    self.cmdClean   = "rm -rf %s/data && rm -rf %s/logs" % (self.homeDir, self.homeDir)
    self.processes = {
      'kafka': {
        'cmdFindPid': 'pgrep -f net.tuan08.tracking.kafka.KafkaTrackingApp',
        'cmdStart':   'sleep 10 && ' +self.homeDir + '/bin/run-tracking.sh kafka --daemon',
        'cmdStop':    'kill -9 `pgrep -f net.tuan08.tracking.kafka.KafkaTrackingApp`'
      },

      'elasticsearch': {
        'cmdFindPid': 'pgrep -f net.tuan08.tracking.es.ESTrackingApp',
        'cmdStart':   'sleep 10 && ' +self.homeDir + '/bin/run-tracking.sh elasticsearch --daemon',
        'cmdStop':    'kill -9 `pgrep -f net.tuan08.tracking.es.ESTrackingApp`'
      }
    }

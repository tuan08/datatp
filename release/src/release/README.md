#Service#

##Elasticsearch##

Launch elasticsearch as daemon
```
$./elasticsearch/bin/elasticsearch -d
```

Shutdown the elasticsearch service
```
# Shutdown all nodes in the cluster
$ curl -XPOST 'http://localhost:9200/_shutdown'
```

#Problem##

##Elasticsearch##

Error:
```
ERROR: bootstrap checks failed
initial heap size [67108864] not equal to maximum heap size [268435456]; this can cause resize pauses and prevents mlockall from locking the entire heap
```

To fix:
```
sudo sysctl -w vm.max_map_count=262144
```

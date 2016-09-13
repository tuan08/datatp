#!/bin/bash

curl -XGET 'http://localhost:9200/xdoc/_search?pretty=true' -d '{ 
  "query": { 
    "query_string" : { 
      "default_field" : "entity.content.content", 
      "query" : "entity.content.title:gmat" 
     } 
  }
}'



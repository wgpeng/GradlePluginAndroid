package com.ytc.wpplugin

import groovy.transform.Field

/**
 * Created by wangpeng on 17-9-19.
 */

@Field module = new LinkedHashMap<String,List<Map<String,String>>>();
@Field static enable;
@Field all = "all"
@Field  static filterTask ="filterWord";
@Field final static filterWord = "filterTask";
def put(moduleName,key,vlaue){
    if(!module[moduleName]){
        module[moduleName] = new LinkedList();
    }
    def entry = new LinkedHashMap();
    entry[key] = vlaue;
    module[moduleName].add(entry)
}

def getList(String moduleName){
    return module[moduleName]
}

def get(moduleName,key){
    for(Map<String,String> map :module[moduleName]){
        def iterator = map.iterator()
        while (iterator.hasNext()){
            Map.Entry entry = iterator.next()
            if(entry.key == key){
                return entry.value;
            }
        }
    }
    return ""
}

def getConfigVlaue(String key){
    module[enable].each {
        it.each { mapkey,mapvalue->
            println " $key  ---> $mapkey ---->$mapvalue"
            if(mapkey == key){
                return mapvalue;
            }

        }
    }
    return "";
}


def get(int index){
    def point =0
    module.each {key,value->
        if(point == index){
            return value
        }
        point++;
    }
}
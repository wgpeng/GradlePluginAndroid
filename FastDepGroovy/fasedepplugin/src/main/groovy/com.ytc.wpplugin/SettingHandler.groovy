package com.ytc.wpplugin

import org.gradle.api.initialization.Settings

/**
 * Created by wangpeng on 17-9-19.
 */
import groovy.util.logging.Slf4j

//@Grab('ch.qos.logback:logback-classic:1.2.+')

@Slf4j
class SettingHandler {

    Settings settings
    def defautConfigFilename = "fastDep.properties"
    def propertiesPath ;
    SettingHandler(settings) {
        this.settings = settings
        propertiesPath = "$settings.rootDir/$defautConfigFilename"
    }

    def handler(){
       def setingConfig = initModuleCompile(propertiesPath)
        handlerInclud(setingConfig)
    }

    def handlerInclud(settingConfig){
        if(!settingConfig&&!Boolean.valueOf(settingConfig.enable)){
            log.error "　错误　： 没有选择一个主工程来进行激活"
            return
        }

        def allInclude = settingConfig.get(settingConfig.enable,settingConfig.all);
        println " 包含所有 ×× Moudle '$allInclude' "+Boolean.valueOf(allInclude)
        Lg.error settingConfig.getList(settingConfig.enable).toString()
        settingConfig.getList(settingConfig.enable).each{
            it.each{key,value->
                File tagertFile = new File("${settings.rootDir.getAbsolutePath()}/libs/$key-release.aar")
                 if(!tagertFile.parentFile.exists()){
                     tagertFile.parentFile.mkdir()
                 }


              //  log.error ">> $key   >> $tagertFile.path"+!tagertFile.exists()+" .》》》　"+Boolean.valueOf(allInclude)+"   >>>"+Boolean.valueOf(value)
                if(!tagertFile.exists()||Boolean.valueOf(allInclude)||Boolean.valueOf(value)){
                    if("all"!=key){ // 过滤掉all,因为all不属于工程
                        println "构建子工程$key"
                        this.settings.include(":$key")
                    }
                }
            }
        }

        return settingConfig
    }


    def initModuleCompile(propertiesPath) {
        def config = new SettingConfig();
        def stream = null;
        try {
            Properties properties = new Properties();
            File file = new File(propertiesPath)
            stream = file.newDataInputStream();
            properties.load(stream)
            Iterator itr = properties.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry e = (Map.Entry) itr.next();
                String key = e.getKey();
                String[] keys = key.split('\\.');
                if(keys.length>2||keys.length==0){
                    log.error " $defautConfigFilename 文件中　${key}不合法"
                    break;
                }

                if(keys.length==1){
                    if(keys[0] == SettingConfig.filterWord){
                        SettingConfig.filterTask = keys[0]
                    }
                    if(Boolean.valueOf(e.getValue())){
                        config.enable = keys[0]
                    }
                    config.put(keys[0],keys[0],e.getValue())
                }else {
                    config.put(keys[0],keys[1],e.getValue())
                    Lg.error config.getList(config.enable).toString()
                }
            }
        } catch (Exception e) {
            println '\n' + e.printStackTrace()
        } finally {
            stream?.close()
        }
        return config
    }
}

package com.ytc.wpplugin
/**
 * Created by wangpeng on 17-9-19.
 */

def configBuildScript(project){
    project.buildscript {
       dependencies {
           //格式为-->group:module:version
           def calssName = this.class.toString()
           classpath "${calssName.substring(calssName.indexOf(" "),calssName.lastIndexOf(".")).trim()}:${new BuildInfo().packageName}:${new BuildInfo().version.trim()}"
          // classpath 'com.ytc.wpplugin:fasedepplugin:1.0.0'
       }

       repositories {
           maven {//本地Maven仓库地址
               url 'file:../libPlugin'
            //   url "${new BuildInfo().mavenUrl}"
           }
       }
   }
}

def handlerNoRootProject(project) {
    project.allprojects{proj->
       if(project != proj) handerRootProject(proj)
    }
}

def handerRootProject(project){
    println " WPPlugin :   工程... ... $project.name"
    project.repositories {
        flatDir {
            dirs "${project.rootDir.getAbsolutePath()}/libs"
        }
    }
   def closure = {modulePath->
       Lg.error("　处理依赖　："+modulePath)
        modulePath = modulePath.trim();
        if(modulePath?.startsWith(":")){
            def moduleName = modulePath.substring(1);
            def settingConfig = project.gradle.settingConfig
            File tagertFile = new File("${project.rootDir.getAbsolutePath()}/libs/$moduleName-release.aar")
            if(!tagertFile.exists()||Boolean.valueOf(settingConfig.get(settingConfig.enable,settingConfig.all))||Boolean.valueOf(settingConfig.get(settingConfig.enable,moduleName))){
                Lg.error " 依赖的模块名　--> $moduleName"
                return project.project(modulePath)
            }else {
                handlerSubProjectDep(project,moduleName)
                Lg.error " 依赖了arr $moduleName-release.aar "
                return [name: "$moduleName-release", ext: 'aar']
            }
        }
    }
    project.dependencies.ext.project=closure;
}

def handlerSubProjectDep(parentProject,subProjectName){
    def rootProjectDep = parentProject.gradle.rootProjectDep
    List closures= rootProjectDep.getClosureStringsByName(subProjectName)
    Lg.error(" 解析脚本　${subProjectName} 工程是否有脚本 ${closures?.size()}")
    if(!closures)  return
    closures.each {
        Closure closure =  RunScript.runScript("return $it")
        closure.metaClass.setProperty(closure,"owner",parentProject)
        Lg.error("  解析依赖子工程${subProjectName}的脚本")
        closure.delegate = parentProject.dependencies
        closure.resolveStrategy  =Closure.DELEGATE_FIRST
        parentProject.dependencies(closure)
        Lg.error("  已将脚本依赖到工程$parentProject ")
    }

}

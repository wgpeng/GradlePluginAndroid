package com.ytc.wpplugin

import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.TaskState

public class  FaseDepPlugin implements Plugin<Object> {
    @Override
    void apply(Object project) {
        SwingTest.raunder()
       println "***********************************"
        def log =  """

       　　　王   　 　　王  　
             王   王 　王
               王 王 王 　　　
       　　     王   王
                       　
"""
        if(project instanceof Settings){
            println log;
            def settingHandler = new SettingHandler(project)
            println " WPPlugin : 工程初始化... ... "
            project.gradle.ext.settingConfig =settingHandler.handler()
            project.gradle.ext.rootProjectDep = ParserProject.parserAllProject(project.rootDir.absolutePath,project.gradle.ext.settingConfig.enable)
            addListener(project.gradle)
            def g = project.getGradle()
            g.beforeProject {
                if(it.name == it.rootProject.name){
                    def rootProject = new RootProjectHandler();
                    rootProject.configBuildScript(it)
                }

            }
            g.afterProject {
                if(it.name == it.rootProject.name){
                    def rootProject = new RootProjectHandler();
                    rootProject.handlerNoRootProject(it)
                }
            }
            g.buildFinished {
                println "WPPlugin　：   编译完成  "
            }

        }
    }


    def addListener(gradle){
        gradle.addListener(new TaskExecutionListener(){
            @Override
            void beforeExecute(Task task) {
            //    println "  >>>>>" +task+"  >>"+SettingConfig.filterTask
                if(task.name.contains(SettingConfig.filterTask)){  // 不要debug版本，提高编译效率
                    task.enabled = false
                }
                if(task.project.name!=gradle.settingConfig.enable){
                    if(task.name == "assembleRelease"||task.name=="bundleRelease"){//compileLint bundleRelease assembleRelease
                        def out = null;
                        try {
                            out = task.project.extensions.getByName("android").libraryVariants[0].outputs[0];
                        }catch (Exception e){
                        }

                        if (!out) return
                        def outDir = out.splitFolder

                        outDir.listFiles().each {
                            def srcFile = it;
                            if(it.name.contains(task.project.name)&&it.name.contains('release')&&it.name.endsWith(".aar")){
                            File targetFile = new File("$task.project.rootDir/libs/${task.project.name}-release.aar")
                                Lg.error " $targetFile 是否存在${targetFile.exists()}"
                                if(targetFile.exists()){
                               // targetFile.listFiles().each {
                                //    Lg.error("22222222222222222222222222")
                                   targetFile.delete()
                                    println " 发现旧的文件　$it.name, 已删除"
                                   /* if(it.name.contains(task.project.name)){
                                        Lg.error("3333333333333333333333")

                                    }*/
                                //}
                                  }else {
                                     println "要依赖的文件不存在　${task.project.name}-release.aar, 请检查是不是第一次编译此文件对应的子工程"
                                  }

                                targetFile.withOutputStream{ os->
                                    srcFile.withInputStream{ ins->
                                        println "开始复制新的aar ${srcFile.name}"
                                        os << ins   //利用OutputStream的<<操作符重载，完成从inputstream到OutputStream
                                        //的输出
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            void afterExecute(Task task, TaskState taskState) {
            }
        })

    }
}
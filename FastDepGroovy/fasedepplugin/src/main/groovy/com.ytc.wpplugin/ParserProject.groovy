package com.ytc.wpplugin

import java.util.regex.Matcher
/**
 * Created by wangpeng on 17-9-23.
 *
 */

    def static parserAllProject(rootProjectPath,mainProjectName){
        def rootProjectDep = new ProjectDep(projectPath:rootProjectPath,projectName:mainProjectName)
        parserProject(rootProjectDep)
        Lg.info(" 解析已经完成 ，已创建解析树")
        return rootProjectDep
    }


    def static parserProject(projectDep) {
        def file = new File("$projectDep.projectPath/$projectDep.projectName/build.gradle")
        println "正在解析项目 ：$projectDep.projectName"
        if (!file.exists()) {
            Lg.error("$file.absolutePath  文件不存在")
            return false
        }
        def buildText = file.text
        def modl = /\{([^\{\}]*(?:(?:\{[^\{\}]*)+?(?:\}[^\{\}]*)+?)*)\}/
        Matcher matcher = buildText =~ /[^\/\/|\/\*]\s*dependencies\s*($modl)\s*/
        def isMatched = false;
        while (matcher.find()) {
            isMatched = true;
            def closureContent = matcher.group(2)
            if (!closureContent) {
                Lg.error("获取工程${projectDep.projectName} 依赖内容不存在")
                return false;
            }
            def closureString = matcher.group(1)
            if (!(closureString =~ /.*?classpath[\s+|\('"].*/).find() || (closureString =~ /.*?[\w+C|c]ompile[\s+|\('"].*/).find()) {
                projectDep + closureString
                def listMoudleDeps = parserMoudleDep(closureContent)
                projectDep+listMoudleDeps
            }
        }
         if(!isMatched) Lg.error("解析工程${projectDep.projectName}失败,未匹配到工程的dependencies")
        if (projectDep.depOns.size()==0) return false
        projectDep.depOns.each {
            String rootPath = projectDep.projectPath
            it.projectPath = rootPath
            parserProject(it)
        }
    }

    def static parserMoudleDep(closureContent){
        Matcher matcher = closureContent =~ /project\s*\(?['|"]:(.+?)['|"]\)/
        Lg.error " 文件project 解析 开始......"
        def list  = []
        def isMatched = false
        while (matcher.find()){
            isMatched = true
            def depProjectName = matcher.group(1)
            ProjectDep projectDep = new ProjectDep(projectName: depProjectName)
            list << projectDep
            Lg.error " 文件project 解析  成功"+"   依赖了　工程$depProjectName"
        }
        if(!isMatched) Lg.error("warn: 为解析到依赖的module")
        return list
    }

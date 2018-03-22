package com.ytc.wpplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public class  UpLoadArchivesMaven implements Plugin<Project> {
    def mavenReleasePath = "http://192.168.19.102:8089/nexus/content/repositories/Android_Releases"
    @Override
    void apply(Project project) {
        Lg.error  "插件运行》》》》》》》》》》》》》"
        project.extensions.create("publishMaven",UpLoadExt)

        project.apply(plugin: 'maven')
        project.repositories{
            maven{
                url mavenReleasePath
            }
        project.tasks.create("configUploadArchives").doLast{
            project.uploadArchives{
                repositories{
                    mavenDeployer{
                        //  url 'file:///home/wangpeng/Arrrrrrs'
                        //  repository(url:uri("../Arrrrrrs"))
                        def uploadext =  project.publishMaven
                        if(uploadext.publish){
                            repository(url:mavenReleasePath){
                                authentication(userName: "admin", password: "admin123")
                            }
                        }else{
                            if(!uploadext.localMaven) throw  new IllegalStateException(" \n    >>>>>    if you set  'uploadext.publish = flase' in publishMaven{...} ,you must set value of localMaven for local url")
                            repository(url:project.uri(uploadext.localMaven))
                            /*  project.repositories.maven{
                                  url project.uri(project.publishMaven.localMaven)
                              }*/
                        }
                        if(!uploadext.version||!uploadext.group) throw new IllegalArgumentException("\n    >>>     when publish a moudle to Maven,you config 'group' and 'version' in publishMaven{...}      <<<<<")
                        pom.version  = uploadext.version
                        pom.artifactId = uploadext.moudleName
                        pom.groupId = uploadext.group
                        pom.name = uploadext.moudleName
                        pom.packaging = uploadext.type
                    }
                }
            }
        }
         def uploadArchivesTask =  project.tasks.getByName("uploadArchives")
            if(uploadArchivesTask) uploadArchivesTask.dependsOn(project.tasks.getByName("configUploadArchives"))

           /* all { ArtifactRepository repo ->

                if (repo instanceof MavenArtifactRepository) {

                    def url = repo.url.toString()

                    Lg.error "所有Maven　Url >>>>>>>>>"+url

                    if (url.startsWith('https://repo1.maven.org/maven2') || url.startsWith('https://jcenter.bintray.com/')) {

                        project.logger.lifecycle "Repository${repo.url}replaced by$REPOSITORY_URL."

                        remove repo

                    }

                }
             }*/
        }
    }

}
package ytc.wp.costomdep

import org.gradle.api.Plugin
import org.gradle.api.Project
import ytc.wp.costomdep.net.BasicHttpParam
import ytc.wp.costomdep.net.HttpDownload
import ytc.wp.costomdep.net.HttpUpLoad
import ytc.wp.costomdep.net.ResponseBody


class CostomMoudleDepPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
          project.extensions.create("upload",UpLoadExtension)

          UpLoadExtension upLoadExtension = project.extensions.getByName("upload")

     /*     project.afterEvaluate {


          }*/
        project.repositories {
            flatDir {
                dirs "${project.rootDir.getAbsolutePath()}/ytcfLibs"
            }
        }


        project.ext.yctfCompile = { String depPath->
            def depPaths =  depPath.split(":")
            def depName = depPaths[1]
            def depVersion = depPaths[2]
            Lg.error " 依赖名==="+depName+"   依赖版本=="+depVersion
            def tagerPath ="${project.rootDir.getAbsolutePath()}/ytcfLibs/${depName}.aar"
            def file = new File(tagerPath)
            if(!file.exists()){
                if(!file.parentFile.exists()){
                    file.parentFile.mkdirs()
                }
                downloadarr(upLoadExtension.remoteUrl,tagerPath)
            }
            project.dependencies{
                compile (name:depName,ext:'aar')
            }
        }


          project.task("uploadarr").doLast{

              def srcPath = upLoadExtension.uplodeDir
              def tagerPath = upLoadExtension.remoteUrl
              HttpUpLoad httpUpLoad = new HttpUpLoad(tagerPath,srcPath)
              ResponseBody<String> responseBody =  httpUpLoad.post(new BasicHttpParam() {
                  @Override
                  BasicHttpParam setParams(String key, String value) {
                      return null
                  }

                  @Override
                  String getParamString() {
                      return null
                  }
              })

              if(responseBody.code==HttpURLConnection.HTTP_OK){
                  Lg.error " 工程发布成功 "
              }else {
                  Lg.error " 工程发布失败　网络失败失败码：${responseBody.code} 　${responseBody.responBody} "
              }

            }


        project.task("downloadarr").doLast{
            def tagerPath = project.rootDir.absolutePath+'/ytcfLibs/test.aar'
            def srcPath = upLoadExtension.remoteUrl
            HttpDownload httpDownload = new HttpDownload(srcPath,tagerPath)
            ResponseBody responseBody = httpDownload.get(new BasicHttpParam() {
                @Override
                BasicHttpParam setParams(String key, String value) {
                    return null
                }

                @Override
                String getParamString() {
                    return null
                }
            })


        }
    }


    def downloadarr(srcPath,tagerPath){
        HttpDownload httpDownload = new HttpDownload(srcPath,tagerPath)
        ResponseBody responseBody = httpDownload.get(new BasicHttpParam() {
            @Override
            BasicHttpParam setParams(String key, String value) {
                return null
            }

            @Override
            String getParamString() {
                return null
            }
        })
    }

}

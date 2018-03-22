package com.ytc.wpplugin
/**
 * Created by wangpeng on 17-9-23.
 */
class ProjectDep {
    String projectPath;
    List<String> closureStrings =new ArrayList();
    String projectName;
    List<ProjectDep> depOns = new ArrayList<ProjectDep>() {};

    def plus(content){
        if(content instanceof String){
            closureStrings<<content
        }
        if(content instanceof ProjectDep){
            depOns << content
        }
        if(content instanceof  List){
            for(def element : content){
                depOns << element
            }
        }
    }

    List<String> getClosureStringsByName(projectName){
       // Lg.error(" getClosureStringsByName ${this.projectName} 脚本：${closureStrings}")
        if(this.projectName == projectName){
            return closureStrings
        }

        for(ProjectDep projectDep :depOns ){
              def closureStrings = projectDep.getClosureStringsByName(projectName)
              if(closureStrings) return closureStrings
        }

        return []
    }


    @Override
    public String toString() {
        return "ProjectDep{" +
                "projectPath='" + projectPath + '\'' +
                ", closureStrings=" + closureStrings +
                ", projectName='" + projectName + '\'' +
                ", depOns=" + depOns +
                '}';
    }
}

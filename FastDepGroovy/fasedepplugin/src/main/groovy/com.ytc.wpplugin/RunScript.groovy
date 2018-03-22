package com.ytc.wpplugin

import groovy.transform.Field

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
//创建engine实例直接解析文本内容

@Field final static cl = GroovyShell.class.getClassLoader();
def static runScript(stringScript){
 ScriptEngineManager factory = new ScriptEngineManager(cl);
//每次生成一个engine实例
ScriptEngine engine = factory.getEngineByName("groovy");
//如果script文本来自文件,请首先获取文件内容
 engine.eval(stringScript)
}



/*def runScript(Settings settings){

    final ClassLoader cl = GroovyShell.class.getClassLoader();
    ScriptEngineManager factory = new ScriptEngineManager(cl);

//每次生成一个engine实例
    ScriptEngine engine = factory.getEngineByName("groovy");
    System.out.println(engine.toString());
    assert engine != null;
    Bindings binding = engine.createBindings();
    binding.put("date", new Date());

//如果script文本来自文件,请首先获取文件内容

    engine.eval("def getTime(){return date.getTime();}", binding);
    engine.eval("def sayHello(name,age){return 'Hello,I am ' + name + ',age' + age;}");
    Long time = (Long) ((Invocable) engine).invokeFunction("getTime", null);
    System.out.println(time);
    String message = (String) ((Invocable) engine).invokeFunction("sayHello", "zhangsan", new Integer(12));

    def cou = engine.eval("return { println '>>>>> wo shi bibao  '+owner}")
    println cou()
    println(message+">>>>>>>>>>>>>>>>>>>>>>>>>>");
}*/

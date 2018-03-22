package com.ytc.wpplugin

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*

/**
 * Created by wangpeng on 17-9-27.
 */
class SwingTest {


    def static raunder(){
        //创建生成器
     /*   def sb = new SwingBuilder()
        def mainPanel = {
            sb.panel(layout:new BorderLayout()){
                hbox(constraints: BorderLayout.WEST){ // hbox可以换成vbox,变成纵向排列
                    def buttons = ['One','Two','Three','Four']
                    buttons.each{but -> sb.widget(new FixedButton(text: but))}
                }
            }
        }

        def frame = sb.frame(title:'ToysStore',location:[100,100] ,size:[400,300],
                defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE){mainPanel()}
        frame.pack()
        frame.setVisible(true)
        //Swing 的伪方法改为大写，再在前面加上J，就是相应组件了*/

    }

}

class FixedButton extends JButton{ // 使按钮大小固定
    Dimension getMinimumSize(){return BUTTONSIZE}
    Dimension getMaximunSize(){return BUTTONSIZE}
    Dimension getPreferredSize(){return BUTTONSIZE}
    private static final BUTTONSIZE = new Dimension(80,30)
}
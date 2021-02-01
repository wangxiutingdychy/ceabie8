# DexKnife
==========================
A simple android gradle plugin to use the patterns of package to smart split the specified classes to second dex.<br/>
一个简单的将指定使用通配符包名分包到第二个dex中gradle插件。

Usage:<br/>
使用方法：

1、Copy the repo folder to your project's root (now version 1.5.0)<br/>
1、在复制 repo 文件夹到你的工程的根目录下（目前版本为 1.5.0）

2、In your project's build.gradle, buildscript.repositories add the root's repo folder<br/>

2、在你的工程的 build.gradle 中 buildscript.repositories 增加本地repo的引用.<br/>

    buildscript {
        repositories {
            maven { url uri('./repo') }
            ....
        }
        
        dependencies {
            ....
            classpath 'com.android.tools.build:gradle:1.5.0'  // or 1.3.0
            classpath 'com.ceabie.dextools:dexknife:1.5.0'
        }
    }

3、Create a 'dexknife.txt' in your App's module, and config the patterns of classes path that wants to put into sencond dex.<br/>
3、在App模块下创建 dexknife.txt，并填写要放到第二个dex中的包名路径的通配符.

    Patterns may include:<br/>

    '*' to match any number of characters<br/>
    '?' to match any single character<br/>
    '**' to match any number of directories or files<br/>
    Either '.' or '/' may be used in a pattern to separate directories. Patterns ending with '.' or '/' will have '**' automatically appended.

    Also see: https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/util/PatternFilterable.html

Other config key:

    # if you want to keep some classes in main dex, use '-keep'.<br/>
    -keep android.support.v4.view.**

    # this path will to be split to second dex.<br/>
    android.support.v?.**


4、add to your app's build.gradle, add this line:<br/>
4、在你的App模块的build.gradle 增加：<br/>

apply plugin: 'com.ceabie.dexnkife'


5、run your app



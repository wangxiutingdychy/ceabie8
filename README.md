# DexKnife

A simple android gradle plugin to use the patterns of package to smart split the specified classes to second dex.<br/>
一个简单的将指定使用通配符包名分包到第二个dex中gradle插件。

- **Notes: android gradle plugin only less than version 2.0.0. The instant-run of higher 2.0.0 interfere with generation.
           You can develop on higher 2.0.0, and build release on 1.5.0 or 1.3.0 **

- **注意：只能使用在 android gradle plugin 小于 2.0.0 的版本，高于 2.0.0的instant-run 特性会干扰代码生成过程。
          你可以在 2.0.0版本以上进行开发，在 1.5.0 或者 1.3.0进行打包。**

Usage:<br/>
使用方法：

1、In your project's build.gradle, buildscript.repositories add the bintray's maven.<br/>
1、在你的工程的 build.gradle 中 buildscript.repositories 增加bintray的仓库.<br/>

    buildscript {
        repositories {
            maven {
                url "https://dl.bintray.com/ceabie/gradle-plugins"
            }
            ....
        }
        
        dependencies {
            ....
            classpath 'com.android.tools.build:gradle:1.5.0'  // or 1.3.0
            classpath 'com.ceabie.dextools:gradle-dexknife-plugin:1.5.0'
        }
    }

2、Create a 'dexknife.txt' in your App's module, and config the patterns of classes path that wants to put into sencond dex.<br/>
2、在App模块下创建 dexknife.txt，并填写要放到第二个dex中的包名路径的通配符.

    Patterns may include:

    '*' to match any number of characters
    '?' to match any single character
    '**' to match any number of directories or files
    Either '.' or '/' may be used in a pattern to separate directories.
    Patterns ending with '.' or '/' will have '**' automatically appended.


Also see: https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/util/PatternFilterable.html


其他配置：<br/>
Other config key:

    '#' is the comment.

    # if you want to keep some classes in main dex, use '-keep'.
    -keep android.support.v4.view.**

    # this path will to be split to second dex.
    android.support.v?.**

    # do not use suggest of the maindexlist that android gradle plugin generate.
    -donot-use-suggest

    # without --minimal-main-dex, only spliting at dex id > 65536 . --minimal-main-dex is default
    -auto-maindex

    # log the main dex classes.
    -log-mainlist


3、add to your app's build.gradle, add this line:<br/>
3、在你的App模块的build.gradle 增加：

    apply plugin: 'com.ceabie.dexnkife'

and then, set your app

    multiDexEnabled true

   - **Notes: You want to set 'multiDexEnabled true' in 'defaultConfig' or 'buildTypes', otherwise ineffective.**
   - **注意：要在 defaultConfig 或者 buildTypes中打开 multiDexEnabled true，否则不起作用。**

4、run your app



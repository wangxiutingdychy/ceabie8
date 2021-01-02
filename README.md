# DexKnife
A simple android gradle plugin to use the wildcards of package to smart split the specified classes to second dex.
一个简单的将指定使用通配符包名分包到第二个dex中gradle插件。

Usage:
使用方法：

1、Copy the repo folder to your project's root (gradle plugin 1.3.0 and 1.5.0 are in conflict, only published a different version.)
1、在复制 repo 文件夹到你的工程的根目录下（目前 1.3.0 与 1.5.0的插件代码有冲突，只能发布成两个版本）

2、In your project's build.gradle, buildscript.repositories add the repo folder
   If you are using the tools version is higher than 1.5.0, then dexknife also set to 1.5.0

2、在你的工程的 build.gradle 中 buildscript.repositories 增加本地repo的引用
   假如你使用 tools 的版本是高于 1.5.0，那么dexknife 也要设置成 1.5.0
   
    buildscript {
        repositories {
            maven { url uri('./repo') }
            ....
        }
        
        dependencies {
            ....
            classpath 'com.android.tools.build:gradle:1.5.0'  // or 1.3.0
            classpath 'com.ceabie.dextools:dexknife:1.5.0'    // or set the same 1.3.0
        }
    }

3、Create a second_dexpackage_list.txt In your App's module, and config the prefix of classes path that wants to put into sencond dex
3、在App模块下创建 second_dex_package_list.txt，并填写要放到第二个dex中的包名路径的前缀

4、add to your app's build.gradle, add this line:
4、在你的App模块的build.gradle 增加：

apply plugin: 'com.ceabie.dexnkife'


5、run your app



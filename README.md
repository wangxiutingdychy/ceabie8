[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Download](https://api.bintray.com/packages/ceabie/gradle-plugins/gradle-dexknife-plugin/images/download.svg)](https://bintray.com/ceabie/gradle-plugins/gradle-dexknife-plugin/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-DexKnifePlugin-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/4001)

# DexKnife

>A simple android gradle plugin to use the patterns of package to smart split the specified classes to multi dex.<br />
Also supports android gradle plugin 2.2.0 multidex.

- **Notes: Because instant-run of 2.0.0 above is incompatible with multidex, DexKnife is auto disabled when instant-run mode.
It will auto enable when disabled instant-run or in packaging release.**

###Update Log
    1.5.5: support individual filter for suggest maindexlist. (单独的maindexlist过滤设置)
    1.5.5.alpha: Experimentally support android gradle plugin on 2.2.0. (实验性的支持 2.2.0 plugin)
    1.5.4: auto disabled when instant run mode.(instant run 模式时自动禁用DexKnife)
    1.5.3: add some track logs and skip DexKnife when jarMerging is null.(增加跟踪日志，并在jarMerging为null跳过处理)
    1.5.2: fix the include and exclude path, and supports filtering single class.(修复include和exclude, 并支持过滤单个类)
    1.5.1.exp: Experimentally support android gradle plugin on 2.1.0 （实验性的支持 2.1.0 plugin）
    1.5.1: fix the proguard mode

###Usage
1.In your project's build.gradle, buildscript.

    buildscript {
            ....

        dependencies {
            ....
            classpath 'com.android.tools.build:gradle:2.2.0-beta2'  // or other
            classpath 'com.ceabie.dextools:gradle-dexknife-plugin:1.5.5'
        }
    }

**please make sure gradle version is compatible with the android gradle plugin, otherwise it can causes some sync error, such as:<br />
Gradle sync failed: Unable to load class 'com.android.builder.core.EvaluationErrorReporter'.**

2.Create a 'dexknife.txt' in your App's module, and config the patterns of classes path that wants to put into sencond dex.

    Patterns may include:

    '*' to match any number of characters
    '?' to match any single character
    '**' to match any number of directories or files
    Either '.' or '/' may be used in a pattern to separate directories.
    Patterns ending with '.' or '/' will have '**' automatically appended.

Also see: https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/util/PatternFilterable.html <br />

**Note: if you want to filter the inner classes, use $\*, such as: SomeClass$\*.class <br />**

Other config key:

    '#' is the comment, config is disabled when '#' adds on line start.
 
    # Global filter, don't apply with suggest maindexlist if -filter-suggest is DISABLE.
    # this path will to be split to second dex.
    android.support.v?.**
    
    # if you want to keep some classes in main dex, use '-keep'.
    -keep android.support.v4.view.**

    # you can keep single class in main dex, end with '.class', use '-keep'.
    -keep android.support.v7.app.AppCompatDialogFragment.class

    # do not use suggest of the maindexlist that android gradle plugin generate.
    -donot-use-suggest

    # the global filter apply with maindexlist, if -donot-use-suggest is DISABLE.
    -filter-suggest

    # Notes: Split dex until the dex's id > 65536. --minimal-main-dex is default.
    -auto-maindex  # default is not used.

    # dex additional parameters, such as --set-max-idx-number=50000
    -dex-param --set-max-idx-number=50000

    # log the main dex classes.
    -log-mainlist

    # log the filter classes of suggest maindexlist, if -filter-suggest is enabled..
    -log-filter-suggest
    
    # if you only filter the suggest maindexlist, use -suggest-split and -suggest-keep.
    # Global filter will merge into them if -filter-suggest is ENABLE at same time.
    -suggest-split **.MainActivity2.class
    -suggest-keep android.support.multidex.**
    
3.add to your app's build.gradle, add this line:

    apply plugin: 'com.ceabie.dexnkife'

and then, set your app

    multiDexEnabled true

   - **Notes: You want to set 'multiDexEnabled true' in 'defaultConfig' or 'buildTypes', otherwise ineffective.**

4.run your app

# 中文

>一个简单的将指定使用通配符包名分包到第二个dex中gradle插件。<br />
同时支持 android gradle plugin 2.2.0 multidex.

- **注意：由于高于 2.0.0 的 instant-run 特性与 multidex不兼容，DexKnife会暂时禁用。当instant-run被禁用或者release打包时会自动启用。**

###更新日志
    1.5.5: 增加单独的maindexlist过滤设置
    1.5.5.alpha: 实验性的支持 2.2.0 plugin
    1.5.4: instant run 模式时自动禁用DexKnife
    1.5.3: 增加跟踪日志，并在jarMerging为null时跳过处理
    1.5.2: 修复include和exclude, 并支持过滤单个类
    1.5.1.exp: 实验性的支持 2.1.0 plugin
    1.5.1: fix the proguard mode

###使用方法
1.在你的工程的 build.gradle 中 buildscript:

    buildscript {
            ....

        dependencies {
            ....
            classpath 'com.android.tools.build:gradle:2.2.0-beta2'  // or other
            classpath 'com.ceabie.dextools:gradle-dexknife-plugin:1.5.5'
        }
    }

 **注意，请确保使用的gradle版本和android gradle plugin兼容，否则会出现同步错误，例如：<br />
      Gradle sync failed: Unable to load class 'com.android.builder.core.EvaluationErrorReporter'.**

2.在App模块下创建 dexknife.txt，并填写要放到第二个dex中的包名路径的通配符.

    Patterns may include:

    '*' to match any number of characters
    '?' to match any single character
    '**' to match any number of directories or files
    Either '.' or '/' may be used in a pattern to separate directories.
    Patterns ending with '.' or '/' will have '**' automatically appended.

更多参见: https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/util/PatternFilterable.html <br />

**注意: 如果你要过滤内部类, 使用$\*，例如: SomeClass$\*.class <br />**

其他配置：

    使用 # 进行注释, 当行起始加上 #, 这行配置被禁用.

    # 全局过滤, 如果没设置 -filter-suggest 并不会应用到 建议的maindexlist.
    # 如果你想要某个包路径在maindex中，则使用 -keep 选项，即使他已经在分包的路径中.
    -keep android.support.v4.view.**

    # 这条配置可以指定这个包下类在第二dex中.
    android.support.v?.**

    # 使用.class后缀，代表单个类.
    -keep android.support.v7.app.AppCompatDialogFragment.class

    # 不包含Android gradle 插件自动生成的miandex列表.
    -donot-use-suggest

    # 将 全局过滤配置应用到 建议的maindexlist中, 但 -donot-use-suggest 要关闭.
    -filter-suggest

    # 不进行dex分包， 直到 dex 的id数量超过 65536.
    -auto-maindex

    # dex 扩展参数, 例如 --set-max-idx-number=50000
    -dex-param --set-max-idx-number=50000

    # 显示miandex的日志.
    -log-mainlist
    
    # 如果你只想过滤 建议的maindexlist, 使用 -suggest-split 和 -suggest-keep.
    # 如果同时启用 -filter-suggest, 全局过滤会合并到它们中.
    -suggest-split **.MainActivity2.class
    -suggest-keep android.support.multidex.**

3.在你的App模块的build.gradle 增加：

    apply plugin: 'com.ceabie.dexnkife'

最后，在app工程中设置：

    multiDexEnabled true

   - **注意：要在 defaultConfig 或者 buildTypes中打开 multiDexEnabled true，否则不起作用。**

4.编译你的应用

## License

```
Copyright (C) 2016 ceabie (http://blog.csdn.net/ceabie)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

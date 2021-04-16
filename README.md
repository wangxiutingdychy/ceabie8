# DexKnife

A simple android gradle plugin to use the patterns of package to smart split the specified classes to multi dex.<br />
Supports <= android gradle plugin 2.2.0, above 2.2.0 is in dev branch. Clean build folders after switch branch.

- **Notes:Because instant-run of 2.0.0 above is incompatible with multidex, so DexKnife is auto disabled.**

Update Log
----------
    1.5.5.alpha: Experimentally support android gradle plugin on 2.2.0, not support < 2.2.0.(实验性的支持 2.2.0 plugin)
    1.5.4: auto disabled when instant run mode.(instant run 模式时自动禁用DexKnife)
    1.5.3: add some track logs and skip DexKnife when jarMerging is null.(增加跟踪日志，并在jarMerging为null跳过处理)
    1.5.2: fix the include and exclude path, and supports filtering single class.(修复include和exclude, 并支持过滤单个类)
    1.5.1.exp: Experimentally support android gradle plugin on 2.1.0 （实验性的支持 2.1.0 plugin）
    1.5.1: fix the proguard mode

Usage
--------

1、In your project's build.gradle, buildscript.

    buildscript {
            ....

        dependencies {
            ....
            classpath 'com.android.tools.build:gradle:2.1.3'  // or other
            classpath 'com.ceabie.dextools:gradle-dexknife-plugin:1.5.4' // AGP <= 2.2.0
        }
    }

**please make sure gradle version is compatible with the android gradle plugin, otherwise it can causes some sync error, such as:<br />
Gradle sync failed: Unable to load class 'com.android.builder.core.EvaluationErrorReporter'.**

2、Create a 'dexknife.txt' in your App's module, and config the patterns of classes path that wants to put into sencond dex.

    Patterns may include:

    '*' to match any number of characters
    '?' to match any single character
    '**' to match any number of directories or files
    Either '.' or '/' may be used in a pattern to separate directories.
    Patterns ending with '.' or '/' will have '**' automatically appended.

Also see: https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/util/PatternFilterable.html

Other config key:

    '#' is the comment.

    # this path will to be split to second dex.
    android.support.v?.**

    # if you want to keep some classes in main dex, use '-keep'.
    -keep android.support.v4.view.**

    # you can keep single class in main dex, end with '.class', use '-keep'.
    -keep android.support.v7.app.AppCompatDialogFragment.class

    # do not use suggest of the maindexlist that android gradle plugin generate.
    -donot-use-suggest

    # Notes: Split dex until the dex's id > 65536. --minimal-main-dex is default.
    # -auto-maindex  # default is not used.

    # log the main dex classes.
    -log-mainlist

3、add to your app's build.gradle, add this line:

    apply plugin: 'com.ceabie.dexnkife'

and then, set your app

    multiDexEnabled true

   - **Notes: You want to set 'multiDexEnabled true' in 'defaultConfig' or 'buildTypes', otherwise ineffective.**

4、run your app

# 中文

一个简单的将指定使用通配符包名分包到第二个dex中gradle插件。<br />
只支持 android gradle plugin 小于 2.2.0, 高于 2.2.0 在dev分支中。切换分支后清理build目录

- **注意：由于高于 2.0.0 的 instant-run 特性与 multidex不兼容，所以DexKnife自动禁用。**

更新日志
--------
    1.5.5.alpha: 实验性的支持 2.2.0 plugin, 不支持 小于2.2.0的版本
    1.5.4: instant run 模式时自动禁用DexKnife
    1.5.3: 增加跟踪日志，并在jarMerging为null时跳过处理
    1.5.2: 修复include和exclude, 并支持过滤单个类
    1.5.1.exp: 实验性的支持 2.1.0 plugin
    1.5.1: fix the proguard mode

使用方法
--------

1、在你的工程的 build.gradle 中 buildscript:

    buildscript {
            ....

        dependencies {
            ....
            classpath 'com.android.tools.build:gradle:2.1.3'  // or other
            classpath 'com.ceabie.dextools:gradle-dexknife-plugin:1.5.4' // AGP <= 2.2.0
        }
    }

 **注意，请确保使用的gradle版本和android gradle plugin兼容，否则会出现同步错误，例如：<br />
      Gradle sync failed: Unable to load class 'com.android.builder.core.EvaluationErrorReporter'.**

2、在App模块下创建 dexknife.txt，并填写要放到第二个dex中的包名路径的通配符.

    Patterns may include:

    '*' to match any number of characters
    '?' to match any single character
    '**' to match any number of directories or files
    Either '.' or '/' may be used in a pattern to separate directories.
    Patterns ending with '.' or '/' will have '**' automatically appended.

更多参见: https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/util/PatternFilterable.html

其他配置：

    使用 # 进行注释.

    # 如果你想要某个包路径在maindex中，则使用 -keep 选项，即使他已经在分包的路径中.
    -keep android.support.v4.view.**

    # 这条配置可以指定这个包下类在第二dex中.
    android.support.v?.**

    # 使用.class后缀，代表单个类.
    -keep android.support.v7.app.AppCompatDialogFragment.class

    # 不包含Android gradle 插件自动生成的miandex列表.
    -donot-use-suggest

    # 不进行dex分包， 直到 dex 的id数量超过 65536.
    # -auto-maindex

    # 显示miandex的日志.
    -log-mainlist


3、在你的App模块的build.gradle 增加：

    apply plugin: 'com.ceabie.dexnkife'

最后，在app工程中设置：

    multiDexEnabled true

   - **注意：要在 defaultConfig 或者 buildTypes中打开 multiDexEnabled true，否则不起作用。**

4、编译你的应用

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

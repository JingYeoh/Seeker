# Seeker
:hammer: A plugin can change method modifier with Hide annotation and build jar and aar automatically. 
一个可以改变字节码中方法的modifier为指定的值的gradle插件，并自动打包出jar/aar

![Platform](https://img.shields.io/badge/platform-gradle-green.svg)
[![JingYeoh](https://img.shields.io/badge/author-JingYeoh-red.svg)](http://blog.justkiddingbaby.com/)


#### Feature
[x] hook java 字节码
[x] 变为 private 的方法，使用反射缓存代理类调用方法
[x] 插件多种属性可支持配置
[x] 改变方法的 modifier

#### TODO
[ ] hook java 源代码
[ ] @Hide 支持类
[ ] @Hide 支持成员变量

#### Versions
|lib|version|
|---|-------|
|seeker|[ ![Download](https://api.bintray.com/packages/jkb/maven/seeker/images/download.svg) ](https://bintray.com/jkb/maven/seeker/_latestVersion)|
|seeker-plugin|[ ![Download](https://api.bintray.com/packages/jkb/maven/seeker/images/download.svg) ](https://bintray.com/jkb/maven/seeker-plugin/_latestVersion)|
|seeker-annotation|[ ![Download](https://api.bintray.com/packages/jkb/maven/seeker/images/download.svg) ](https://bintray.com/jkb/maven/seeker-annotation/_latestVersion)|
|seeker-processor|[ ![Download](https://api.bintray.com/packages/jkb/maven/seeker/images/download.svg) ](https://bintray.com/jkb/maven/seeker-processor/_latestVersion)|

#### Usage example
>给你的方法添加 @Hide 注解

```java
public class Mock{
    @Hide(Modifier.PROTECTED)
    public void mock(){
    }
}
```

>运行 `./gradlew :xxx:uploadArchives` 命令，查看输出的 `jar/aar`。

#### Configuration
> project root `build.gradle` 中添加 `seeker-plugin` 的依赖

```
buildscript {
    //...
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "com.yeoh.seeker:seeker-plugin:${version}"
    }
}
```
> 在 library 的 `build.gradle` 中添加依赖

```groovy
apply plugin: 'seeker-plugin'
// ...
dependencies {
    implementation "com.yeoh.seeker:seeker:${version}"
    implementation "com.yeoh.seeker:seeker-annotation:${version}"
    annotationProcessor "com.yeoh.seeker:seeker-processor:${version}"
}
```

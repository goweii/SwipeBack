# SwipeBack

- 低耦合，低代码侵入，不需要继承基类
- 支持4个方向滑动返回
- 支持自定义滑动时底部Activity联动动效，有2个自带效果，分别为视差（类似微信）和收缩（类似头条）
- 支持设置全局和仅边缘可滑动返回
- 支持在全局时边缘强制可滑动返回（用于解决长滚动布局在滚动到中间时不方便触发滑动返回的情况）
- 支持内部滚动布局的多层嵌套，不影响全局滑动返回
- 支持自定义边缘阴影颜色和宽度
- 支持自定义底部Activity的遮罩颜色


# 集成

- 添加jitpack仓库

```groovy
// Project:build.gradle
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}
```

- 添加依赖

[![](https://jitpack.io/v/goweii/SwipeBack.svg)](https://jitpack.io/#goweii/SwipeBack)

```groovy
// Module:build.gradle
dependencies {
    implementation 'com.github.goweii:SwipeBack:2.0.5'
}
```

# 使用

- 初始化

```java
SwipeBack.getInstance().init(application);
```

- 全局配置

```java
SwipeBack.getInstance().setXxx();
```

- 单独配置

```java
Activity implements SwipeBackAbility.Xxx
```


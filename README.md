# Balance-Calculator (剩余金额计算器)

一个简单实用的剩余金额计算工具，提供Java桌面版和Web版本两种实现。

## 功能特点

- 日期选择（自定义日历）
- 时间单位选择（年、季度、月、184天）
- 总价格输入
- 剩余金额计算
- 重置功能

## 项目结构

```
Balance-Calculator/
├── java-version/
│   └── BalanceCalculator.java  # Java桌面版实现
└── web-version/
    └── calculator.html         # Web版本实现
```

## Java版本

### 功能特点
- 美观的图形界面
- 自定义日历选择器
- 键盘快捷键支持（Enter计算，Esc重置）
- 实时响应的按钮效果
- 格式化的结果显示

### 使用方法
1. 进入java-version目录
2. 编译Java文件：`javac BalanceCalculator.java`
3. 运行程序：`java BalanceCalculator`
4. 选择开通日期
5. 选择时间单位
6. 输入价格
7. 点击计算按钮或按Enter键计算

## Web版本

### 功能特点
- 轻量级实现
- 无需安装，浏览器直接运行
- 响应式设计
- 简洁的用户界面

### 使用方法
1. 进入web-version目录
2. 直接在浏览器中打开calculator.html
3. 按照界面提示输入相关信息
4. 点击计算按钮获取结果

## 系统要求

### Java版本
- JDK 8或更高版本
- 图形界面支持

### Web版本
- 现代浏览器（Chrome、Firefox、Edge等）
- JavaScript支持

## 开发者信息

本项目提供两种实现方式：
1. Java Swing实现的桌面应用
2. HTML+JavaScript实现的Web应用

选择适合您需求的版本使用。如果您需要在企业环境中使用，推荐使用Java版本；如果需要快速使用或分享，推荐使用Web版本。
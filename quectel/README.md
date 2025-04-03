> 此版本 对应 https://github.com/kekingcn/kkFileView.git . 

## 一、部署方式

### 1、kkfileview-base运行镜像构建并提交（仅首次搭建）
docker build --tag st-harbor.quectel.com/keking/kkfileview-base:4.4.0 .
docker push st-harbor.quectel.com/keking/kkfileview-base:4.4.0

### 2、打包
```shell script
## 在根目录下 打包 jdk8
mvn clean install -DskipTests
```

### 3、提交到服务构建镜像
```
docker 部署
本地打包，执行Dockerfile构建运行镜像即可
```

## 二、升级
### 1、添加远程源进行更新
git remote add github https://github.com/joman-jiang/kkFileView.git
git fetch
git pull origin github

## 三、修改内容
- 1、个性化关键字修改 2025-04-02
- 2、修正s3存储url无法下载问题 && 增加自定义字段加密，实现水印功能 2025-04-02



 

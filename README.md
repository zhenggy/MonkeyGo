## Monkey测试学习

1. 在AndroidStudio上连接测试机，在命令行执行相关命令（adb.ext 在 \sdk\platform-tools）
2. adb devices 列出连接的测试机   （出错的话请参考错误1）
3. adb shell 在设备上运行shell命令  （出错的话请参考错误2）
4. su 获取权限  （出错的话参考错误3）
5. ls data/data 列出安装在手机上的包名   （出错的话参考错误3）
6. adb -s (devices name) shell monkey -v -p (you app package name) 500 执行monkey测试

```
错误1：
  错误提示：'adb' 不是内部或外部命令，也不是可运行的程序或批处理文件。
  错误原因：没把adb.exe添加到系统的环境变量中 或 命令行路径不在adb.exe文件夹下
  解决办法：把adb.exe添加到系统环境变量中 或 在命令行进入adb.exe文件夹下

  错误提示：没找到连接的设备
  错误原因：设备没正常连接，设备需开启usb调试
  解决办法：如上

错误2：
  错误提示：error: no devices/emulators found
  错误原因：如上错误1的第二条
  解决办法：如上错误1的第二条

  错误提示：error: unknown host service
  错误原因：电脑的5037端口被其他程序占用 我的电脑上是360MobileLoader.exe
  解决办法：netstat -ano 查看所有占用端口的PID
           tasklist|findstr "8772" 查看该PID是哪个应用程序
           在任务管理器中杀死这个应用程序，如果杀不死就卸载，我卸载了360

错误3：
  错误提示：没有权限
  错误原因：需要root权限
  解决办法：推荐在电脑上下载应用宝，在工具箱中有个一键root功能

  错误提示：connection to ui timed out
  错误原因：需要root权限
  解决办法：很多时候是因为卸掉了kingroot导致的，重装它即可
```


Monkey的具体用法可以参考：
[这个](http://wiki.jikexueyuan.com/project/android-test-course/monkey-commond-tools.html)


应用截图：

![主界面](http://oe6wdchwh.bkt.clouddn.com/17-3-14/95186744-file_1489488382424_5f29.png)
![正常日志](http://oe6wdchwh.bkt.clouddn.com/17-3-14/14582731-file_1489488379852_849f.png)
![崩溃日志](http://oe6wdchwh.bkt.clouddn.com/17-3-14/45777657-file_1489488376576_7e34.png)

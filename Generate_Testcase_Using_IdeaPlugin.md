本文将会带您在五分钟内通过IDEA插件完成一次用例生成，体验用例生成带来的效能提升！
#### 1 构建Auto-Unit-Test-Case-Generator
在进行IDEA插件操作之前，我们首先需要构建出最新版本的Auto-Unit-Test-Case-Generator。
构建过程如下：

- clone当前开源仓库代码至本地
- 执行命令：mvn clean install -Dmaven.test.skip=true

构建完成后，在您本地的mvn仓库内即可发现构建好的用例生成jar包，用例生成jar包的默认路径：${user.home}/.m2/repository/org/smartut/smartut-master/1.1.0/smartut-master-1.1.0.jar。

#### 2 安装IDEA插件
构建完成Auto-Unit-Test-Case-Generator后，我们接下来进行IDEA插件的安装。
安装过程如下：

- 下载IDEA插件安装包，安装包地址：[https://github.com/TRaaSStack/auto-unit-test-case-generator/blob/main/tools/smart_ut_intellij_plugin.zip](https://github.com/TRaaSStack/auto-unit-test-case-generator/blob/main/tools/smart_ut_intellij_plugin.zip)
- 本地解压缩smart_ut_intellij_plugin.zip后得到IDEA插件jar包：smart_ut_intellij.jar
- 安装IDEA插件：打开IntelliJ IDEA -> Preferences -> Plugins -> Install Plugin from Disk。在弹出的选择框中，选择smart_ut_intellij.jar。

![image.png](https://intranetproxy.alipay.com/skylark/lark/0/2022/png/252069/1671421979901-30269c6b-8c16-4d06-b9eb-78560236c85f.png#clientId=ud39a54e0-0501-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=797&id=u7e4d45b5&margin=%5Bobject%20Object%5D&name=image.png&originHeight=1594&originWidth=2880&originalType=binary&ratio=1&rotation=0&showTitle=false&size=795055&status=done&style=none&taskId=u030f6cdc-fecb-41a4-a95f-1ea3ef7fb32&title=&width=1440)

- 重启IDEA插件。重启后打开您希望进行用例生成的Java项目，右键单击任意java文件，即可看到用例生成的入口。

![image.png](https://intranetproxy.alipay.com/skylark/lark/0/2022/png/252069/1671422190453-b9c0cd41-b687-4acf-9c09-8509b4715aff.png#clientId=ud39a54e0-0501-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=900&id=u7fb1a79e&margin=%5Bobject%20Object%5D&name=image.png&originHeight=1800&originWidth=2880&originalType=binary&ratio=1&rotation=0&showTitle=false&size=1135822&status=done&style=none&taskId=u201dd4f1-d8db-4a53-adff-cea1bd2a6b9&title=&width=1440)

#### 3 一键生成用例
 IDEA插件安装完成后，您就可以在您的本地工程中尽情使用用例生成了。在任意待生成的java文件上右键选择"SmartUT用例生成"，即可看到如下图所示的用例生成页面。
![image.png](https://intranetproxy.alipay.com/skylark/lark/0/2022/png/252069/1671776172233-3b464cf5-97ec-433d-89b1-c7ac2db7b651.png#clientId=u6eaa3d9b-a1ca-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=797&id=u7011934d&margin=%5Bobject%20Object%5D&name=image.png&originHeight=1594&originWidth=2880&originalType=binary&ratio=1&rotation=0&showTitle=false&size=1000870&status=done&style=none&taskId=u163c78a1-5451-4be2-a891-4a0c9ae848f&title=&width=1440)
用例生成页面中，只需要填写三项：用例生成jar包路径、生成结果存放路径、生成时间。

- 用例生成jar包路径：填入在2.1中构建出的用例生成jar包的路径。
- 生成结果存放路径：填入您的工程中测试用例存放路径，如src/test/java。
- 生成时间：填入预期的用例生成时间，建议5min。

**填写完成后点击开始，即可开始用例生成！**
![image.png](https://intranetproxy.alipay.com/skylark/lark/0/2022/png/252069/1671423796800-0ef7d1de-8ee6-4b6d-827e-0965f66e2fdc.png#clientId=ud39a54e0-0501-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=797&id=u9d2a0991&margin=%5Bobject%20Object%5D&name=image.png&originHeight=1594&originWidth=2880&originalType=binary&ratio=1&rotation=0&showTitle=false&size=894215&status=done&style=none&taskId=u75fde5f1-7c8b-4b55-b03a-2dfc44cc786&title=&width=1440)
用例生成完成后，在对应目录下可以找到生成的测试用例文件。
![image.png](https://intranetproxy.alipay.com/skylark/lark/0/2022/png/252069/1671432504843-efeca759-f765-470c-8923-a96fae98cdda.png#clientId=ua72cf33b-d7b7-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=797&id=u5b4b0e71&margin=%5Bobject%20Object%5D&name=image.png&originHeight=1594&originWidth=2880&originalType=binary&ratio=1&rotation=0&showTitle=false&size=1281775&status=done&style=none&taskId=u3cf02588-e84d-4e30-8f11-edfd65d7a8f&title=&width=1440)

### 联系我们
如果您对测试用例自动生成感兴趣，或者在使用插件进行用例自动生成时遇到了任何问题，都欢迎随时联系我们。钉钉群号：44961098，也可通过以下二维码扫码入群。期待您的意见和建议！
![image.png](https://intranetproxy.alipay.com/skylark/lark/0/2022/png/252069/1671679079257-50678584-2d2a-45c4-90b7-325668b1fc64.png#clientId=u7c273dd1-cdd3-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=441&id=u3b0ac3eb&margin=%5Bobject%20Object%5D&name=image.png&originHeight=1068&originWidth=828&originalType=binary&ratio=1&rotation=0&showTitle=false&size=198673&status=done&style=none&taskId=uc249633f-1d46-46cc-9fb5-5e3ee2efdc3&title=&width=342)

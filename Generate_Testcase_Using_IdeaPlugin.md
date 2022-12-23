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

![image](https://user-images.githubusercontent.com/106229399/209289677-250936d4-d276-4959-9dfb-ed2ecd4f4013.png)


- 重启IDEA插件。重启后打开您希望进行用例生成的Java项目，右键单击任意java文件，即可看到用例生成的入口。
![image](https://user-images.githubusercontent.com/106229399/209289752-77d86ca8-8c8a-443f-b11c-9fe775c6688b.png)


#### 3 一键生成用例
 IDEA插件安装完成后，您就可以在您的本地工程中尽情使用用例生成了。在任意待生成的java文件上右键选择"SmartUT用例生成"，即可看到如下图所示的用例生成页面。
![image](https://user-images.githubusercontent.com/106229399/209289779-935a2f62-43af-4241-ab4f-73cccf98dd5c.png)

用例生成页面中，只需要填写三项：用例生成jar包路径、生成结果存放路径、生成时间。

- 用例生成jar包路径：填入在2.1中构建出的用例生成jar包的路径。
- 生成结果存放路径：填入您的工程中测试用例存放路径，如src/test/java。
- 生成时间：填入预期的用例生成时间，建议5min。

**填写完成后点击开始，即可开始用例生成！**
![image](https://user-images.githubusercontent.com/106229399/209289820-aca093c1-9ba7-459d-ac21-a85c38e57bee.png)

用例生成完成后，在对应目录下可以找到生成的测试用例文件。
![image](https://user-images.githubusercontent.com/106229399/209289837-db0681de-6b67-4601-9515-5cf1e20f3507.png)


### 联系我们
如果您对测试用例自动生成感兴趣，或者在使用插件进行用例自动生成时遇到了任何问题，都欢迎随时联系我们。钉钉群号：44961098，也可通过以下二维码扫码入群。期待您的意见和建议！
![image](https://user-images.githubusercontent.com/106229399/209289867-98284710-7d63-4ee4-bce4-d525ab3c37c7.png)


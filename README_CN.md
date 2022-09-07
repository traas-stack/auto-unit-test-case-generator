**Auto-Unit-Test-Case-Generator**是一款工业级智能单元测试用例生成产品，致力于解决单测领域下用例的自动生成、执行、汰换、管理。使用Auto-Unit-Test-Case-Generator，能够在无人介入的情况下自动生成高覆盖率、高有效性的单元测试用例集，智能化提升质量水位、提高研发效能。目前在蚂蚁内部已经有超过1000个工程在使用Auto-Unit-Test-Case-Generator进行用例生成。

# 核心能力
Auto-Unit-Test-Case-Generator是基于EvoSuite开发的智能单元测试生成产品。与EvoSuite相比，Auto-Unit-Test-Case-Generator优化了一系列算法，使得用例的覆盖率、有效性、可读性都有提升。
Auto-Unit-Test-Case-Generator的核心能力包括：

- **适配Spring框架**

目前业界大量的JAVA工程使用Spring框架，Auto-Unit-Test-Case-Generator能够自动适配Spring工程的结构，对需要隔离的服务进行提前mock，确保生成的用例可以正常运行。

- **复杂业务场景生成**

在业界的实际系统中，往往需要自动生成的用例能够覆盖复杂业务场景。Auto-Unit-Test-Case-Generator对用例数据生成的算法进行了优化，使得相较于传统的随机搜索算法(Random Search Algorithm)能够达到更高的分支命中能力。

- **高可读的用例结构**

我们推荐将自动生成的单测用例合入工程代码库中进行持续CI，因此要求自动生成的用例具备高可读性。Auto-Unit-Test-Case-Generator在进行用例生成过程中结合了最优调用序列算法，使得自动生成的用例结构合理、语义可读。
# 构建Auto-Unit-Test-Case-Generator
安装Auto-Unit-Test-Case-Generator 的命令如下：
```shell
mvn clean install -Dmaven.test.skip=true
```
mvn install后，将构建出的smartut.jar拷贝到使用路径下：
```shell
cp ${user.home}/.m2/repository/org/smartut/smartut-master/1.1.0/smartut-master-1.1.0.jar smartut.jar
```

# 使用Auto-Unit-Test-Case-Generator
## 1) 依赖准备
在使用Auto-Unit-Test-Case-Generator，需要先对被测工程进行编译和依赖准备，命令如下：
```shell
mvn clean compile
mvn clean install -Dmaven.test.skip=true
mvn dependency:copy-dependencies
```
## 2) Setup
依赖准备完成后，首先需要使用smartut的setup命令进行环境准备，命令如下：
```shell
java -jar ./smartut.jar -setup example/target/classes/ example/target/dependency/*.jar
```
## 3) Class级别用例生成
针对单个class进行用例生成的命令如下：
```shell
java -jar ./smartut.jar -class com.alipay.test.example
```
## 4) Module级别用例生成
针对单个module进行用例生成的命令如下：
```shell
java -jar ./smartut.jar -target example/target/classes/
```
# 新闻报道
信通院软件质效领航者优秀案例：[https://mp.weixin.qq.com/s/DOgXE66ldZJWpJ3SBWNfJg](https://mp.weixin.qq.com/s/DOgXE66ldZJWpJ3SBWNfJg)
# 联系我们
在使用Auto-Unit-Test-Case-Generator过程中，如果遇到任何问题欢迎联系我们：

- 邮箱：[smartunit_opensource@service.alipay.com](mailto:smartunit_opensource@service.alipay.com)
- 钉钉群：44961098

同时，Auto-Unit-Test-Case-Generator目前也提供了SaaS服务，对SaaS服务感兴趣欢迎访问[https://smartunit.opentrs.com](https://smartunit.opentrs.com).

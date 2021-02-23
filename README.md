# netty-learning

netty-learning主要由**《让天下没有难学的Netty》**免费专栏与**Netty二次开发封装的框架**构成。



## 1、《让天下没有难学的Netty》

《让天下没有难学的Netty》专栏将从通道篇、内存篇、性能篇、实战篇详细剖析Netty的实现原理、设计理念，同时通过抽取Apache顶级项目RocketMQ的网络通信模块，构建一个可直接使用的Netty网络通信框架。

### 1.1 网络通道篇

1. [Netty4 Channel 概述](https://mp.weixin.qq.com/s/mIq-NjA9vir4wHCe5qpqBQ)
2. [Netty4 ChannelHandler 概述](https://mp.weixin.qq.com/s/n4duWYQIWyau7YLBqvYTHw)
3. [Netty4 事件处理传播机制](https://mp.weixin.qq.com/s/5dlUN0bzW3aKfcg1PSR5Ow)
4. [图文并茂剖析Netty编解码以及背后的设计理念](https://mp.weixin.qq.com/s/8uQlY-SthNTeg1xVPN5jzA)
5. [一文揭晓通信协议设计的奥妙，直接"秒杀"面试官](https://mp.weixin.qq.com/s?__biz=MzIzNzgyMjYxOQ==&mid=2247485622&idx=1&sn=5697d1193cd3d9323013866325a85333&chksm=e8c3ff42dfb4765488912d83b715bf701efccef6014a4edace05dc51f194a77f7ec5159ccef8&token=1812684394&lang=zh_CN#rd)
6. [面试官：Netty的线程模型可不是Reactor这么简单](https://mp.weixin.qq.com/s/kcSI0yQH3HxZt5KFU-M8_w)
7. [Netty进阶：手把手教你如何编写一个NIO客户端](https://mp.weixin.qq.com/s/vrf8bO_K1YIac96A-HEV8A)
8. [Netty进阶：手把手教你如何编写一个NIO服务端](https://mp.weixin.qq.com/s/QZIXOT1fSmpu4di16vfyuQ)



连载中。。。





## 2、netty-framework

### 2.1 客户端类图设计

![1](https://dingwpmz.gitee.io/blogimage/1.png)



### 2.2 网络通信协议设计

通信协议主要是约定客户端、服务端协的通讯格式，目前主要是基于 请求头(Header) + 请求体(Body)，并且通常Header长度固定，并且会包含一个请求体中长度。

在Netty中默认提供了 请求头 + Body 的封装实现，LengthFieldBasedFrameDecoder，在原理篇会详细介绍它的实现原理，我们来看一下RocketMQ中的通信协议格式：

![2](https://dingwpmz.gitee.io/blogimage/2.png)



### 2.3 事件处理模型

![3](https://dingwpmz.gitee.io/blogimage/3.png)



### 2.4 事件处理模型

![4](https://dingwpmz.gitee.io/blogimage/4.png)

业务处理线程池按照请求命令进行**线程池隔离**，即不同的业务使用单独的业务线程池。

### 2.5 网络组件交互设计

![5](https://dingwpmz.gitee.io/blogimage/5.png)



## 3、优质资源推荐

本项目由『中间件兴趣圈』公众号维护，Netty专栏是12个专栏的其中一个，所有专栏如下图所示：

![7](https://dingwpmz.gitee.io/blogimage/7.png)

其整个体系详情如下：

![6](https://dingwpmz.gitee.io/blogimage/6.png)

其中RocketMQ专栏如下图所示：

![8](https://dingwpmz.gitee.io/blogimage/8.png)

欢迎关注公众号『中间件兴趣圈』，回复 专栏 即可免费获得。

![qrcode](https://dingwpmz.gitee.io/blogimage/qrcode.jpg)

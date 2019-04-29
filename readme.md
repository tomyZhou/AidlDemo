1.startServie
  
  onCreate onStartCommand onStart 
  我们看到，后面即使多点几下这个开启服务，但是也只会调onStartCommand方法，onCreate方法并不会重复调用，那是因为我们点击Service，
  
  由于该service已经存在，所以并不会重新创建，所以onCreate方法只会调用一次。
  
  有些朋友可能注意到了，我们刚刚那种启动服务的方式，好像除了对Service进行开启和销毁以外，很难在activity里进行对Service进行控制，什么意思呢？举个例子，如果说
  
我现在用Service进行下载某些东西，我现在在Service写有下载这两个东西的方法，方法a，方法b，那么我怎样在activity里面控制什么时候调用方法a，什么时候调用方法b呢，如果

按照原本的启动方式，好像并不好实现，或者说灵活性很差，那么有没有办法办到呢，接着看Service另一种启动方式

2.bindService

在ServiceTest中增加了一个内部类Mybind

可以看到，这里我们首先创建了一个ServiceConnection的匿名类，在里面重写了onServiceConnected()方法和onServiceDisconnected()方法，这两个方法分别会在Activity与Service

建立关联和解除关联的时候调用。在onServiceConnected()方法中，我们又通过向下转型得到了MyBind的实例，有了这个实例，Activity和Service之间的关系就变得非常紧密了。

MyBind中返回Service句柄，现在我们可以在Activity中的onServiceConnected中拿到MyBinder，继而可以拿到Service对象，就可以调用Service的方法了。

即实现了Activity指挥Service干什么Service就去干什么的功能。

注意：Service 是运行在后台，没有可视化的页面，我们很多时候会把耗时的操作放在Service中执行，但是注意，Service是运行在主线程的，不是在子线程中，

Service和Thread没有半毛钱的关系，所以如果在Service中执行耗时操作，一样是需要开起线程，否则会引起ANR，这个需要区别开来。

我们可以把Servcie通过Manifest.xm中配置process的方法，让Service在一个单独的进程中运行。单独进程有以下几个好处：

1. 可以多分配内存，不容易出现OOM。

2. 多进程可以相互相守，防止进程被杀死

3. 独立于主进程完成任务，不会因为主进程出问题而受影响。为及时推送消息，和及时数据统计不受当前进程退出的影响，我们可以使用独立的进程来完成这个任务。


3.远程服务 AIDL

AIDL（Android Interface Definition Language）是Android接口定义语言的意思，它可以用于让某个Service与多个应用程序组件之间进行跨进程通信，从而可以实现多个应用程序共享同一个Service的功能。

实际上实现跨进程之间通信的有很多： 
   1.Bundle 传递已经序列化的对象
   2.直接通过文件存储的方式保存序列化对象的方式
   3.Messenger的方式（内部使用AIDL封装）
   4.比如广播
   5.Content Provider，
   
  但是AIDL的优势在于速度快(系统底层直接是共享内存)，性能稳，效率高，一般进程间通信就用它。
  
远程服务和bindServer服务的代码看起来是不是很熟悉，唯一不一样的就是原来在本地服务的时候内部类继承的是Binder,而现在继承的是MyAIDLService.Stub，

继承的是我们刚刚建立的aidl文件，然后实现我们刚刚的定义的getString()方法，在这里，我们只是返回一句话，"我是从服务起返回的"

客户端代码变化也不大，就是原来的onServiceConnected返回的IBinder对象，现在用myAIDLService = MyAIDLService.Stub.asInterface(service)获得IBinder对象


说明：
如何使用AIDL文件来完成跨进程通信？
在进行跨进程通信的时候，在AIDL中定义的方法里包含非默认支持的数据类型与否，我们要进行的操作是不一样的。如果不包含，那么我们只需要编写一个AIDL文件，如果包含，

那么我们通常需要写 n+1 个AIDL文件（ n 为非默认支持的数据类型的种类数）——显然，包含的情况要复杂一些。所以我接下来将只介绍AIDL文件中包含非默认支持的数据类型的情况，

至于另一种简单些的情况相信大家是很容易从中触类旁通的。

使数据类实现 Parcelable 接口
由于不同的进程有着不同的内存区域，并且它们只能访问自己的那一块内存区域，所以我们不能像平时那样，传一个句柄过去就完事了——句柄指向的是一个内存区域，

现在目标进程根本不能访问源进程的内存，那把它传过去又有什么用呢？所以我们必须将要传输的数据转化为能够在内存之间流通的形式。这个转化的过程就叫做序列化与反序列化。

简单来说是这样的：比如现在我们要将一个对象的数据从客户端传到服务端去，我们就可以在客户端对这个对象进行序列化的操作，将其中包含的数据转化为序列化流，

然后将这个序列化流传输到服务端的内存中去，再在服务端对这个数据流进行反序列化的操作，从而还原其中包含的数据——通过这种方式，我们就达到了在一个进程中访问另一个进程的数据的目的。
   
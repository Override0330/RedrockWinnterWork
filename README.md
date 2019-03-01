## 红岩网校移动开发部Android寒假考核作业-黄龙-2018214137

### Alpha 1.30 Update —— 2019.3.1 22:16

1. 性能优化，增加了在bihu问答广场划至底部后自动加载下一页的功能，缩短了加载时间
  <img src="gif/bihu_get_next.gif" width="200" alt="bihugetnext" >

2. 增加了注册时用户名最多12个字符的限制

3. 修复了发布问题时上传图片失败的bug

4. 修复了进入问题详情界面后自动弹出键盘的bug

5. 修复了在NaviagtionView中点击登录登陆成功或者登录界面的注册成功后，不会及时更改NaviagtionView的用户id的bug

6. 修复了在NaviagtionView中点击登录界面的注册页面并注册成功后（即用户头像未设置），NaviagtionView中的头像为空的bug

   > 现在在这种情况下会显示默认头像

7. 更改了一些UI的参数

8. 目前 Meizu 16th(Android 8.1 Google APIs)有未知的问题导致bihu所有网络请求没有响应

   > 感谢[Sajo](https://github.com/Sajotim)提供的使用反馈

### Alpha 1.20 Update —— 2019.3.1 12:01

1. 兼容到 API 19  Android 4.4(Google APIs)

   > 如果发现在API 19下出现经过旧版APP上传的图片(头像，发布的问题内容，回答的问题)无法显示，已经发布的问题内容和回答的问题的图片的Url无法修改无解，但头像可以重新更换。原因是API 19无法拿到名字带空格的文件名

2. 增加了发布问题和回答问题上传图片的数量限制，最多5张

   > 感谢[Fxy4ever](https://github.com/Fxy4ever)提供的建议和暴力测试
   >
   > Tips：请勿点击由提问者fxy发布的内容为“233"的问题“多发几张。。”可能会导致应用程序崩溃

3. 增加了将被采纳的问题置顶的功能

4. 增加了在问题详情页面长按问题内容和回答内容进行复制的功能

5. 修复了正在游览Bihu任何内容时，突然断开连接后，主动刷新却无法加载缓存内容(进入离线模式)的bug

   > 现在被可以正常刷新并显示缓存的内容，同时进入离线模式
   >
   > Tips：离线模式查看我的收藏不会显示

6. 修复了正在游览Bihu任何内容时，突然断开连接后，点击点赞等在离线模式不应该能使用的功能时显示的不正确Toast的bug

   > 现在它可以正确识别目前网络状态并给出正确的Toast来引导用户操作

7. 修复了离线模式下进入自己发布的问题闪退的bug

   > 现在在离线模式可以进入自己发布的问题，可以正常显示已采纳的回答，但是不会有采纳的按钮

8. 修复了更换到离线模式下顶部导航栏的头像没有更改的bug

9. 性能优化，将较大的图片进行压缩缓存和显示

10. 性能优化，微调了一些我都忘记了的东西

11. 代码优化，增加代码复用率(6219line+633line)

     

### 一、功能简述(多图预警！！！本来想挂七牛的然后发现七牛30d之后就没了😭)
1. 获得最新的Gank资讯📰

| Gank资讯主页面 | Gank资讯点击及复制URL |
| ------------- | ----           |
| <img src="gif/ganknews1.gif" width="200" alt="GankNew"> | <img src="gif/ganknews2.gif" width="200" alt="GankNew"> |

2. 获得最新的Gank闲读📖api真的很慢。。。。。。

  <img src="gif/xiandu1.gif" width="200" alt="xiandu" >

3. [Bi乎](https://github.com/jay68/bihu_web/wiki/%E9%80%BC%E4%B9%8EAPI%E6%96%87%E6%A1%A3)api的完整解决方案😆

  | 使用默认账号进入问答广场                                     | 登录                                                     | 赞、踩、收藏（离线更新）                                    |
  | ------------------------------------------------------------ | -------------------------------------------------------- | ----------------------------------------------------------- |
  | <img src="gif/bihu_question_list.gif" width="200" alt="xiandu" > | <img src="gif/bihu_login.gif" width="200" alt="xiandu" > | <img src="gif/bihu_favorite.gif" width="200" alt="xiandu" > |

  | 发布问题(带图片）                                           | 问题详情界面                                                 | 回答问题（带图片）                                        |
  | ----------------------------------------------------------- | ------------------------------------------------------------ | --------------------------------------------------------- |
  | <img src="gif/bihu_publish2.gif" width="200" alt="xiandu" > | <img src="gif/bihu_detail_question1.gif" width="200" alt="xiandu" > | <img src="gif/bihu_answer.gif" width="200" alt="xiandu" > |

  |      无网络状态| 恢复网络     |   更换头像   |
  | ---- | ---- | ---- |
  |  <img src="gif/bihu_withoutNetwork.gif" width="200" alt="xiandu" >    |  <img src="gif/bihu_fixNetwork.gif" width="200" alt="xiandu" >     |    <img src="gif/bihu_changeavatar.gif" width="200" alt="xiandu" >  |

  
### 二、性能优化👾
1. 使用通过将图片保存在本地来缓存图像
2. 在无网络连接或者网络连接超时时会使用先前缓存的数据
### 三、使用的第三方包👇
1. [七牛](https://developer.qiniu.com/kodo/sdk/1236/android)

### 四、What ‘s new?🧐
1. 📦封装了很多方法，一部分放在developtools包内，另一部分在bihu包里，不知道算不算封装工具类，主要包括：
* 网络请求类-HttpsRequestHelper(GET&POST)  
* 图片加载、处理类-MyImageTools(包含：从网络获取图片，从本地获取图片，保存图片到本地，int类型的资源地址转换成Bitmap，将Bitmap切成圆形)
* 实现Bihu所有api的BihuPostTools在bihu包里，还有离线时的处理方法
* 用了红岩的第一次作业的时间轴转换工具来计算最新更新时间
2. 😆在没有登陆自己的bihu时默认使用游客账号登陆并可以查看问答，触发相关需要登陆用户自己账号的事件的时候会弹出login界面，登陆成功后正常使用(整个应用程序网络请求使用唯一用户(User)：BihuFragment.nowUser)
3. 😟在无网络连接(非连接超时)时进入离线模式😌，用户id替换成id(上次登录的)+(离线)，头像更换为无网络连接的头像，所有数据为缓存数据，Bihu相关功能被禁用，在个人中心点击头像登陆后若网络恢复即可继续正常使用。(捕获UnknownHostException 并处理)
4. 在网络连接超时时自动加载缓存的数据😌(捕获TimeoutException 并处理)
5. 所有数据加载的时候模式都为：获取到json后优先加载文字数据，有图片的先使用占位图代替，然后再开线程加载图片，加载完成后及时更新😝(在ViewHolder中开线程然后在使用提前获取到的mainHandler(构造adapter时就已经把Activity的Handler传进来)来更新ui)
6. 整个应用程序加载图片时使用唯一方法MyImageTools.getBitmap(String url),该方法封装了从url获得图片和从本地获得图片😬
7. 在Bihu发布问题，回答问题时可以选择多张图片,问题详情页面可以正常显示全部图片(LinearLayout.addView)
8. 由于ganknew和闲读有些资讯、Bihu有些问题没有图片😫，使用对应的占位图来代替😌
9. 图片加载，RecyclerView首次加载时会有淡入动画，但recyclerview里的图片由于机制问题会在第一次之后依然有淡入动画😭
10. 一些较为人性化的优化设计😆，比如activity之间的跳转，注册成功后会自动跳过登录界面直接登录等

### 五、已知BUG😭
1. 部分机型无法从相册获得照片的uri，欢迎提交issue

### 五、感受体会🤔
#### 1.学习了很多很多新的东西，在做考核前我甚至不知道怎么post请求🙄……

* POST请求
* 利用接口回调给RecyclerView和RecyclerView中的一个控件添加点击事件
* 将handler传入RecyclerView从而进行实时UI更新。
* LinearLayout的一般使用，减少对约束布局的使用
* 自定义动画anim的超简单实用(透明度的更改Alpha)
* 打包有签名的apk
* log的疯狂使用
* ……省略无数个坑，在代码中都有相对应的注释

#### 2. 真的很累😔
> 一是因为要补考所以寒假结束前一周没得做，二也是最重要的是因为自己懒了，压到ddl再做。
> 这几天一直在熬夜肝已经连续5天肝到3点了，仔细回想来学校前不过是做了gank和闲读的adapter，界面和bihu的问题列表，登陆也没写，一直都是默默在后台登录，大部分工作是来了学校补考完再和sajo玩了两天之后，也就是这5天做的。
>虽然明显是自找的，但是我第一次写代码写到吐了……
#### 3.自己知道的不足之处还有很多，但是我最近想休息一下了，学长学姐们或者同学们记得提意见给我发issue嗷☺️☺️☺️
* 没有设计预加载
* ~~Bihu是一次性加载完所有数据(以后的版本会增加下一页的功能~~
* 没有欢迎界面
* 闲读的api真的非常非常非常之慢，我尽力了
* 代码其实不规范，很多无用或者过于复杂的设计
* 做得太慢了

### 创作于2019.2.28 22:32 📝

# 作业的具体实现：
## 设置多个不同关卡
1. 通过 levelNum 记录当前的白点个数，默认是3个，代表第0关。
2. 构造RandomLevel类，随机生成n个白点的布局，
3. 添加makeLevel方法，功能是通过levelNum构造一个RandomLevel的实例，赋值给level，然后通过这个实例更新视图，*一个边界情况是随机生成的Level已经是过关状态，此时会重新生成level直到产生一个不是过关状态的level*
4. 每次congratulations后，levelNum++，然后判断levelNum是否为9，假如是的话，则将levelNum赋值为3，然后调用makeLevel方法，否则直接调用makeLevel方法

## 持久化记录闯关进度
1. 我首先看了第一行代码activity的生命周期的概念，然后在和onCreate相对的onDestroy方法中，构建了一个sharedPerences，将当前的levelNum写入其中，使用的是editor的apply方法，防止阻塞在主线程。
2. 在onCreate方法中，获取levelNumber，若是默认值3的话，就调用原本的initLevel方法，若是其他的，则根据这个数据去随机生成level。*之所以只是记录当前的关卡编号而不是关卡的整体布局，是因为当游戏继续拓展变成5\*5,6\*6的时候
，保存全部布局需要更大的空间，可能会占用更多的实现，导致写入文件不及时，以及在编码上，保存全部布局，也只是向文件中写入更多的键值对，和保存一个levelNum本质上没有区别。*

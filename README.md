# Android_Homework
Homework of bytedance

1. 进入App后，默认为图片中的布局，其中最下方的白色图片更改为了蓝色图片
2. 点击重新开始，则更新布局，采取的方式是生成两组共6个在0-8不一致的随机数*通过HashSet*,一组将container里的三个元素赋值为1，另一组将dotArr里三个元素赋值为1
2.1 初次进入App的时候，first 变量为默认值true,此时会按照指定布局加载，同时更改first为false
3. 点击初始化布局，则将布局更新为图片中的布局，采用的方式是将first更新为true，然后调用initLevel函数
4. 序号为偶数的点，被填充时，会被涂抹为蓝色。

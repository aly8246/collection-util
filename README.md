

## 著名顶顶的N+1问题让很多人望而却步，闲暇之余觉得无聊就写了个小插件
为什么会出现n+1的问题？
在代码编写过程中我们经常有一种需求，一个主表对应多个子表，而且还需要对主表进行分页，对子表进行搜索
1. 子查询 ，先查询主表并且分页，然后再根据主表的id来查询子表，将子表填充到主表里，这也是常用的方式，但是有效率问题

```sql
select * from user;
```
```sql
select * from phone where user_id=#{userId};
```
```java
//如果分页大小为10，查询用户1条sql，为10个用户中的每个用户查询手机就是10条sql，加起来11条
List<User> userList=userService.selectAllUser(xxx);
userList.forEach(e->{
	e.setPhoneList(phoneService.selectPhoneByUserId(e.getId()));;
})
```


2. 内存分页，先将所有的数据都查出来然后放到内存里进行排序过滤，将得到的结果进行分页，这种方式适合数据量小的时候，如果数据量一旦超过10万就开始肉眼可见的缓慢

```
log.error("死亡代码直接pass");
```

3. 主表左连接子表，mysql里limit物理分页，不存在性能问题，随意对子表进行过滤，但是却存在了n+1的问题,当左连接的时候，子表查询出3个数据，对主表进行分页时会导致分页错乱，明明11个用户，分页插件却告诉我有55个，当分页大小为1的时候，数据却不完整，现在这些问题得到了完善的解决方案

```
log.info("存在n+1问题，但是有了解决方案");
```




**先上成功案例**
[点此查看查询结果](http://148.70.16.82:10000/user/collectionUtil?page=1&pageSize=1)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190927185109401.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM1NDI1MjQz,size_16,color_FFFFFF,t_70)


---
[点此查看失败结果](http://148.70.16.82:10000/user/pageHelper?page=1&pageSize=1)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190927185244755.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM1NDI1MjQz,size_16,color_FFFFFF,t_70)
---
在以上的展示中我们得知，用户1拥有三个手机，在n+1的问题中
当分页大小为1的时候，一个用户只拥有了一个手机，而正确的结果是三个手机

先上源码
[分页插件源码：github链接](https://github.com/aly8246/collection-util)

表结构：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190927185543378.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM1NDI1MjQz,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20190927185559306.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM1NDI1MjQz,size_16,color_FFFFFF,t_70)
一个用户可能拥有多个手机

当sql为的时候就是查询所有的用户和它的手机，通过mybatis的collection来映射结果

```sql
 select user.*
             , p.id   as phone_id
             , p.name as phone_name
        from user 
        left join phone p on user.id = p.user_id
```

这里是resultMap：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019092718574163.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM1NDI1MjQz,size_16,color_FFFFFF,t_70)返回的vo也很简单
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190927185813177.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM1NDI1MjQz,size_16,color_FFFFFF,t_70)
------
开始引出问题：
当我想为用户1查询所有的手机，并且给所有的用户分页

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190927185918644.png)![](https://img-blog.csdnimg.cn/20190927185925675.png)

从上图得知用户1拥有三个手机
，而我们通过查询用户1和他的手机的sql的时候

```sql
 select user.*
             , p.id   as phone_id
             , p.name as phone_name
        from user 
        left join phone p on user.id = p.user_id
				WHERE user.id =1
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190927190036717.png)这是以上用户1的查询结果，用户只有一个数据，手机却查出3个数据，就产生了数据库n+1问题，只是通过映射把三个手机都放在一个用户里面
如果分页条件为分页大小=1的时候就产生分页连断问题
![在这里插入图片描述](https://img-blog.csdnimg.cn/201909271901567.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM1NDI1MjQz,size_16,color_FFFFFF,t_70)通过传统的分页插件只查出了一个手机，而正确的分页条件应该是查询3个手机，
原理待续。。。。。

---
注：本人知识浅薄，mybatis插件方面是通过学习mybatisPageHelper来知道怎么写的，也多多少少也跑了各大搜索引擎，由衷感谢mybatisPageHelper作者！	

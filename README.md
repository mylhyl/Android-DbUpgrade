## DbUpgrade
封装了对数据库表结构的升级，并且内部实现了升级逻辑，并支持xUtils3、greenDAO框架
  * 升级思路
    - 创建临时表并复制旧表的表结构
    - 删除旧表
    - 创建新表
    - 恢复旧表的数据到新表

#### 使用Gradle构建时
```javascript
    
```
    
#### 常用升级逻辑做法
```java
    if (oldVersion == 1){
        //升级 TableA
    }
    if(oldVersion == 2){
        //升级 TableB
        //升级 TableC
    }
```
然而该库只需要这样
```java
    DbUpgrade dbUpgrade = new DbUpgrade(oldVersion);
    DbUpgrade.Xutils with = dbUpgrade.withXutils(db);//切换到Xutil3
    with.setEntityType(TableA.class, 1)
            .build()
            .upgrade();

    with.setEntityType(TableB.class, 2)
            .build()
            .setEntityType(TableC.class, 2)
            .build()
            .upgrade();
```

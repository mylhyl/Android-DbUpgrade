## DbUpgrade
是一款在不丢失源数据库中的内容情况下，对sqLite数据库表结构升级的工具库，且实现跨版本升级业务，目前暂
支持xUtils3、greenDAO框架，如需支持其它框架请[issues](https://github.com/mylhyl/Android-DbUpgrade/issues)
  * 升级思路
    - 创建临时表并复制旧表的表结构
    - 删除旧表
    - 创建新表
    - 恢复旧表的数据到新表

#### 使用Gradle构建时
```javascript
    compile 'com.mylhyl:DbUpgrade:1.0.0'
```
    
#### 常用升级逻辑做法
```java
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 1:
                    //升级 TableA
                    break;
                case 2:
                    break;
                    //升级 TableB
                    //升级 TableC
                default:
                    break;
            }
        }
```
然而该库只需要这样
```java
        DbUpgrade dbUpgrade = new DbUpgrade(oldVersion, newVersion);
        DbUpgrade.GreenDao with = dbUpgrade.withGreenDao(db);//切换greenDao
            with.setUpgradeVersion(1)
                    .setUpgradeTable(TableA.class)
                    //每个版本都必须 upgrade()一次
                    .upgrade();
        
            with.setUpgradeVersion(2)
                    .setUpgradeTable(TableB.class)
                    .setUpgradeTable(TableC.class)
                    //每个版本都必须 upgrade()一次
                    .upgrade();
```

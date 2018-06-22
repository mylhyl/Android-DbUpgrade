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
    compile 'com.mylhyl:DbUpgrade:1.1.0'
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
        GreenDao with = dbUpgrade.withGreenDao(db);//切换greenDao
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

#### 使用方法
```java

        DbUpgrade dbUpgrade = new DbUpgrade(oldVersion, newVersion);//oldVersion旧版本 newVersion新版本
        GreenDao greenDao = dbUpgrade.withGreenDao(db);//Greendao框架
        greenDao.setUpgradeVersion(1)//在此版本上升级
                //注意调用setUpgradeTable一次，是对另一个表的设置，再次调用setUpgradeTable之前所调用方法只会对此表生效
                /*
                    例如
                    字段1是表A的
                    字段2是表B的

                    setUpgradeTable(表A)
                    .addColumn(字段1)
                    setUpgradeTable(表B)
                    .addColumn(字段2)
                 */
                .setUpgradeTable(abstractDao)//数据库实体类的DAO
                .setUpgradeTable(abstractDao, sqlCreateTable)//abstractDao 数据实体的DAO；sqlCreateTable创建数据库的sql
                .addColumn(columnName, fieldType)//表添加字段 columnName为表列名，fieldType为数据类型 ColumnType枚举
                .upgrade();//每setUpgradeVersion一个版号必须要调用此方法；默认true；是否走升级策略(1建临时，2删旧表，3建新表，4还原数据)
        //true走升级策略(1建临时，2删旧表，3建新表，4还原数据)
        //如果表只有添加字段，建议设 false 提高升级的效率，但 addColumn 为空也会走升级策略
        //如果表既有添加也有删除字段，那么是否走升级策略，开发者自己定夺
        //.upgrade(migration);
        greenDao.setUpgradeVersion(2)//在此版本上升级
                .setUpgradeTable()
                .upgrade();

        Xutils xutils = dbUpgrade.withXutils(db);//Xutils3框架
        xutils.setUpgradeVersion(1)//在此版本上升级
                .setUpgradeTable(entityType)//数据库实体类
                .setUpgradeTable(entityType, sqlCreateTable)//entityType 数据库实体类；sqlCreateTable创建数据库的sql
                .addColumn(columnName, fieldType)//表添加字段 columnName为表列名，fieldType为数据类型 ColumnType枚举
                .upgrade();//每setUpgradeVersion一个版号必须要调用此方法

        OrmLite ormLite = dbUpgrade.withOrmLite(db, connectionSource);//OrmLite框架
        ormLite.setUpgradeVersion(1)//在此版本上升级
                .setUpgradeTable(entityType)//数据库实体类
                .setUpgradeTable(entityType, sqlCreateTable)//entityType 数据库实体类；sqlCreateTable创建数据库的sql
                .addColumn(columnName, fieldType)//表添加字段 columnName为表列名，fieldType为数据类型 ColumnType枚举
                .upgrade();//每setUpgradeVersion一个版号必须要调用此方法

        Original original = dbUpgrade.with(db);//原生的
        original.setUpgradeVersion(1)//在此版本上升级
                .setUpgradeTable(tableName)//表名
                .setUpgradeTable(tableName, sqlCreateTable)//tableName表名；sqlCreateTable创建数据库的sql
                .removeColumn(columnName)//表删除的列名
                .addColumn(columnName, fieldType)//表添加字段 columnName为表列名，fieldType为数据类型 ColumnType枚举
                .upgrade();//每setUpgradeVersion一个版号必须要调用此方法
```
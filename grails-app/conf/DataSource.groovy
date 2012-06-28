dataSource {
  pooled = true
  driverClassName = "com.mysql.jdbc.Driver"
  username = "blog"
  password = "123456"
}
hibernate {
  cache.use_second_level_cache = true
  cache.use_query_cache = false
  cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
  development {
    dataSource {
      dbCreate = "create-drop" // one of 'create', 'create-drop','update'
      url = "jdbc:mysql://localhost/orm_test"
    }
  }
  test {
    dataSource {
      dbCreate = "create-drop" // one of 'create', 'create-drop','update'
      url = "jdbc:mysql://localhost/orm_test"
    }
  }
  production {
    dataSource {
      dbCreate = "update"
      url = "jdbc:h2:prodDb;MVCC=TRUE"
      pooled = true
      properties {
        maxActive = -1
        minEvictableIdleTimeMillis = 1800000
        timeBetweenEvictionRunsMillis = 1800000
        numTestsPerEvictionRun = 3
        testOnBorrow = true
        testWhileIdle = true
        testOnReturn = true
        validationQuery = "SELECT 1"
      }
    }
  }
}

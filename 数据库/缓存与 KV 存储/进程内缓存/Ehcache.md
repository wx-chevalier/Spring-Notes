# Ehcache

- [Ehcache 详细解读](http://www.blogjava.net/libin2722/articles/406569.html)
- [ehcache-documentation](http://www.ehcache.org/documentation/3.0/getting-started.html)

```java
CacheManager cacheManager
    = CacheManagerBuilder.newCacheManagerBuilder()
    .withCache("preConfigured",
        CacheConfigurationBuilder.newCacheConfigurationBuilder()
            .buildConfig(Long.class, String.class))
    .build(false);
cacheManager.init();

Cache<Long, String> preConfigured =
    cacheManager.getCache("preConfigured", Long.class, String.class);

Cache<Long, String> myCache = cacheManager.createCache("myCache",
    CacheConfigurationBuilder.newCacheConfigurationBuilder().buildConfig(Long.class, String.class));

myCache.put(1L, "da one!");
String value = myCache.get(1L);

cacheManager.removeCache("preConfigured");

cacheManager.close();
```

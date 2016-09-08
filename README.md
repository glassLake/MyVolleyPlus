# MyVolleyPlus

fork from [VolleyPlus](https://github.com/DWorkS/VolleyPlus)

[![](https://jitpack.io/v/glassLake/MyVolleyPlus.svg)](https://jitpack.io/#glassLake/MyVolleyPlus)



# Change

## 1.0.0 (相对于原项目):

将原先由eclipse迁移到as的目录结构更改: 完全新建一个as工程,手动拷贝文件夹.  目的: 避免莫名其妙的编译不通过

修改Request基类中getCacheKey()和shouldCache()方法内部逻辑,取消原项目中只允许get请求缓存的限制.方便后续实现 完全的客户端控制的缓存.



# Usage

# gradle



Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```

**Step 2.** Add the dependency

```
	dependencies {
	        compile 'com.github.glassLake:MyVolleyPlus:1.0.0'
	}
```




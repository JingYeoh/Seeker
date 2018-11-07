package com.yeoh.seeker.plugin

/**
 * 用于存储 Seeker 的配置和临时的一些缓存
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-09-27
 */
class DataSource {

    def static seekerConfig = [:]
    private static Map<String, List<String>> processedRef = [:]

    def static clear() {
        seekerConfig = [:]
        processedRef = [:]
    }

    /**
     * 标记类已经处理过反射缓存了
     * @param className 要处理的类名
     * @param referencedClass 缓存代理类名
     */
    static void putToRefCache(String className, String referencedClass) {
        List<String> referencedClasses = processedRef.get(className)
        if (referencedClasses == null) {
            referencedClasses = new ArrayList<>()
        }
        if (!referencedClasses.contains(referencedClass)) {
            referencedClasses.add(referencedClass)
        }
        processedRef.put(className, referencedClasses)
    }

    /**
     * 是否已经处理过反射缓存了
     * @param className 要处理的类名
     * @param referencedClass 缓存代理类
     * @return 是否缓存过
     */
    static boolean isProcessedRefDelegate(String className, String referencedClass) {
        List<String> referencedClasses = processedRef.get(className)
        if (referencedClasses == null) {
            return false
        }
        return referencedClasses.contains(referencedClass)
    }

}
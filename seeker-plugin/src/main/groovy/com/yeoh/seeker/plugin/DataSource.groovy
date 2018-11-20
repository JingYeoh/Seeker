package com.yeoh.seeker.plugin

/**
 * 用于存储 SeekerExtension 的配置和临时的一些缓存
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-09-27
 */
class DataSource {

    public static final String ANNOTATION_HIDE = "com.yeoh.seeker.annotation.Hide"
    public static final String ENUM_MODIFIER = "com.yeoh.seeker.annotation.Modifier"

    def static seekerConfig = [:]
    private static Map<String, List<String>> processedRef = [:]
    // aar/jar　等解压后的路径，task　执行完毕后需要删除这些临时文件
    static Set<String> TEMP_DIRS = new HashSet<>()
    // 依赖的第三方库 jar 解压后的路径，用于在编译时生效
    static Set<String> DEPENDENCIES_JARS_PATH = new HashSet<>()
    // 依赖的第三方库
    static Set<String> DEPENDENCIES_PATH = []

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
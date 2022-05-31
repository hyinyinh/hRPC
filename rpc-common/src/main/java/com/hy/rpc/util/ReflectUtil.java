package com.hy.rpc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/26 15:50
 */
public class ReflectUtil {

    //异常处理类
    public static String getStackTrace(){
        StackTraceElement[] stack = new Throwable().getStackTrace();
        return stack[stack.length-1].getClassName();
    }

    public static Set<Class<?>> getClasses(String packageName){
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.','/');
        //Enumeration接口中定义了一些方法，通过这些方法可以枚举（一次获得一个）对象集合中的元素。
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader()
                    .getResources(packageDirName);
            //boolean hasMoreElements( ) 测试此枚举是否包含更多的元素。
            while(dirs.hasMoreElements()){
                //Object nextElement( ) 如果此枚举对象至少还有一个可提供的元素，则返回此枚举的下一个元素。
                URL url = dirs.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //如果是文件
                if("file".equals(protocol)){
                    //获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //以文件的方式扫描整个包下的文件，并添加到集合中
                    findAndAddClassesInPackageByFile(packageName,filePath,recursive,classes);
                }else if("jar".equals(protocol)){
                    //定义一个jarFile
                    JarFile jar;
                    try{
                        //openConnection 返回一个 URLConnection 对象，它表示到 URL 所引用的远程对象的连接。
                        jar = ((JarURLConnection)url.openConnection()).getJarFile();
                        //遍历jar包 获取一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        //循环遍历
                        while(entries.hasMoreElements()){
                            //当前实体
                            JarEntry jarEntry = entries.nextElement();
                            String name = jarEntry.getName();
                            //如果是以/开头的
                            if(name.charAt(0) == '/'){
                                //获取/后面的字符串
                                name = name.substring(1);
                            }
                            //如果前半部分和定义的包名相同
                            if(name.startsWith(packageDirName)){
                                int idx = name.lastIndexOf('/');
                                //如果是以”/“结尾 是一个包
                                if(idx != -1){
                                    //获取包名 把“/”替换成“.”
                                    packageName = name.substring(0,idx).replace('/','.');
                                }
                                //如果可以迭代下去 并且是一个包
                                if((idx != -1) || recursive){
                                    //如果是一个.class文件 并且不是目录
                                    String className = name.substring(packageName.length()+1,
                                            name.length()-6);
                                    try {
                                        classes.add(Class.forName(packageName+'.'+className));
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void findAndAddClassesInPackageByFile(String packageName,
                                                         String filePath,
                                                         final boolean recursive,
                                                         Set<Class<?>> classes) {
        //获取此包的目录 建立一个file
        File dir = new File(filePath);
        //如果不存在或者不是目录直接返回
        if(!dir.exists() || !dir.isDirectory()){
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            @Override
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for(File file : dirfiles){
            //如果是目录 则继续扫描
            if(file.isDirectory()){
                findAndAddClassesInPackageByFile(packageName+"."+file.getName(),
                        file.getAbsolutePath(),recursive,classes);
            }else{
                //如果是java类文件 去掉后面的.class只留下类名
                String className = file.getName().substring(0,file.getName().length()-6);
                try{
                    // 添加到集合中去
                    //classes.add(Class.forName(packageName + '.' + className));
                    //这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName+"."+className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

// cc FileCopyWithProgress Copies a local file to a Hadoop filesystem, and shows progress

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

// vv FileCopyWithProgress
public class FileCopyWithProgress {
    public static void main(String[] args) throws Exception {
        // 定义文件输入的位置
        String localSrc = args[0];
        // 定义文件在HDFS上保存的位置
        String dst = args[1];
        // 创建一个缓冲输入流，传一个子节输入流类对象，子节输入流需要传入一个路径，标识文件从哪里输入
        InputStream in = new BufferedInputStream(new FileInputStream(localSrc));
        // Configuration 对象封装了客户端或服务器的配置，通过设置配置文件读取类路径来实现（如etc/hadoop/core-site.xml）
        Configuration conf = new Configuration();
        // 通过给定的URI方案和权限来确定要使用的文件系统，如果给定URI中没有指定方案，则返回默认文件系统。
        // 具体介绍见我的博客 HDFS 的 JAVA API 操作
        FileSystem fs = FileSystem.get(URI.create(dst), conf);
        // 在指定的路径上创建一个FSDataOutputStream对象，这里向上转型为OutputStream(),同时重载方法Progressable，实现回调函数
        // create()方法能够为需要写入且当前不存在的文件创建父目录，
        // 如果希望父目录不存在就导致文件写入失败，则应该先调用exists()函数检查父目录是否存在
        // 另一种方案就是使用FileContext，允许你可以控制是否创建父目录
        // 重载方法Progressable 用于传递回调接口
        // FSDataOutputStream类不允许在文件中定位，这是应为HDFS只允许对一个已打开的文件顺序写入，或者对现有文件的末尾追加数据。
        OutputStream out = fs.create(new Path(dst), new Progressable() {
            public void progress() {
                System.out.print(".");
            }
        });
        // 这里对应两个重载方法，因为没有后续操作了，我这边没法判断是哪个
        // 第一个参数：用于读取的InputStream（子节输入流)
        // 第二个参数：要写入的OutputStream（子节输出流）
        // 重载的方法区别：1. 缓冲区大小，2. 要复制的子节数
        // 第四个参数：是否关闭InputStream和OutputStream。
        IOUtils.copyBytes(in, out, 4096, true);
    }
}
// ^^ FileCopyWithProgress

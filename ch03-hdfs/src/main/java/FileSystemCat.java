// cc FileSystemCat Displays files from a Hadoop filesystem on standard output by using the FileSystem directly
import java.io.InputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

// vv FileSystemCat
public class FileSystemCat {

  public static void main(String[] args) throws Exception {
    String uri = args[0];
    // Configuration 对象封装了客户端或服务器的配置，通过设置配置文件读取类路径来实现（如etc/hadoop/core-site.xml）
    Configuration conf = new Configuration();
    // 通过给定的URI方案和权限来确定要使用的文件系统，如果给定URI中没有指定方案，则返回默认文件系统。
    // 具体介绍见我的博客 HDFS 的 JAVA API 操作
    FileSystem fs = FileSystem.get(URI.create(uri), conf);
    // 定义一个输入流对象
    InputStream in = null;
    try {
      // 获取一个输入流，传入一个path路径作为参数
      in = fs.open(new Path(uri));
      // 这里对应两个重载方法，因为没有后续操作了，我这边没法判断是哪个
      // 第一个参数：用于读取的InputStream（子节输入流)
      // 第二个参数：要写入的OutputStream（子节输出流）
      // 重载的方法区别：1. 缓冲区大小，2. 要复制的子节数（这里应该是字节数）
      // 第四个参数：是否关闭InputStream和OutputStream。
      IOUtils.copyBytes(in, System.out, 4096, false);
    } finally {
      IOUtils.closeStream(in);
    }
  }
}
// ^^ FileSystemCat

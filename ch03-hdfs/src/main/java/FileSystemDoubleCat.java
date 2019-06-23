// cc FileSystemDoubleCat Displays files from a Hadoop filesystem on standard output twice, by using seek
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

// vv FileSystemDoubleCat
public class FileSystemDoubleCat {

  public static void main(String[] args) throws Exception {
    String uri = args[0];
    // Configuration 对象封装了客户端或服务器的配置，通过设置配置文件读取类路径来实现（如etc/hadoop/core-site.xml）
    Configuration conf = new Configuration();
    // 通过给定的URI方案和权限来确定要使用的文件系统，如果给定URI中没有指定方案，则返回默认文件系统。
    // 具体介绍见我的博客 HDFS 的 JAVA API 操作
    FileSystem fs = FileSystem.get(URI.create(uri), conf);
    // 创建一个数据输入流，这个对象继承了java.io.DataInputStream，支持随机访问，可以从流的任意位置读取数据。
    // 数据输入流允许应用程序以独立于机器的方式从底层输入流读取原始Java数据类型。 应用程序使用数据输出流来写入稍后可以被数据输入流读取的数据。
    // DataInputStream对于多线程访问来说不一定是安全的。
    FSDataInputStream in = null;
    try {
      // 在指定的路径处打开FSDataInputStream，传入一个path路径。返回一个FSDataInputStream对象
      in = fs.open(new Path(uri));
      // 这里对应两个重载方法，因为没有后续操作了，我这边没法判断是哪个
      // 第一个参数：用于读取的InputStream（子节输入流)
      // 第二个参数：要写入的OutputStream（子节输出流）
      // 重载的方法区别：1. 缓冲区大小，2. 要复制的子节数（这里应该是字节数）
      // 第四个参数：是否关闭InputStream和OutputStream。
      IOUtils.copyBytes(in, System.out, 4096, false);
      // 定位到文件中指定位置，如果定位大于文件长度的位置会引发IOExcption异常
      // 与java.io.InputStream的skip()函数不同seek()可以移到文件中的任意一个绝对位置，而skip()函数只能相对于当前位置定位到另一个新的位置。
      // 这里是定位到起始位置
      in.seek(0); // go back to the start of the file
      // 重新复制一遍
      IOUtils.copyBytes(in, System.out, 4096, false);
    } finally {
      IOUtils.closeStream(in);
    }
  }
}
// ^^ FileSystemDoubleCat

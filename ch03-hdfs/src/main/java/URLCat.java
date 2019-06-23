// cc URLCat Displays files from a Hadoop filesystem on standard output using a URLStreamHandler
import java.io.InputStream;
import java.net.URL;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;

// vv URLCat
public class URLCat {

  //这部分使静态代码块，用于初始化类，为类属性初始化，只在类加载的时候加载且只执行一次。
  // 在给定的java虚拟机当中，此方法最多调用一次
  static {
    // 设置应用程序的 FsUrlStreamHandlerFactory
    URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
  }
  
  public static void main(String[] args) throws Exception {
    // 定义一个输入流对象
    InputStream in = null;
    try {
      // 初始化输入流对象
      // 通过在指定的上下文中解析给定的规范来创建一个URL。
      in = new URL(args[0]).openStream();
      // 这里对应两个重载方法，因为没有后续操作了，我这边没法判断是哪个
      // 第一个参数：用于读取的InputStream（子节输入流)
      // 第二个参数：要写入的OutputStream（子节输出流）
      // 重载的方法区别：1. 缓冲区大小，2. 要复制的子节数
      // 第四个参数：是否关闭InputStream和OutputStream。
      IOUtils.copyBytes(in, System.out, 4096, false);
    } finally {
      IOUtils.closeStream(in);
    }
  }
}
// ^^ URLCat

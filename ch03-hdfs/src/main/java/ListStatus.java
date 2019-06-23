// cc ListStatus Shows the file statuses for a collection of paths in a Hadoop filesystem 
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

// vv ListStatus
public class ListStatus {

  public static void main(String[] args) throws Exception {
    String uri = args[0];
    // Configuration 对象封装了客户端或服务器的配置，通过设置配置文件读取类路径来实现（如etc/hadoop/core-site.xml）
    Configuration conf = new Configuration();
    // 通过给定的URI方案和权限来确定要使用的文件系统，如果给定URI中没有指定方案，则返回默认文件系统。
    // 具体介绍见我的博客 HDFS 的 JAVA API 操作
    FileSystem fs = FileSystem.get(URI.create(uri), conf);
    // 定义一个数组保存传入的路径，类型是Path对象
    Path[] paths = new Path[args.length];
    for (int i = 0; i < paths.length; i++) {
      paths[i] = new Path(args[i]);
    }
    // 如果路径是目录，则列出给定路径中的文件/目录的状态。
    // 不保证以排序顺序返回文件/目录列表状态。
    // 参数：给定路径
    // 返回：给定路径中文件/目录的状态
    FileStatus[] status = fs.listStatus(paths);
    // 将FileStatus数组转换为Path数组
    //参数：FileStatus对象的数组
    //返回：与输入对应的Path数组
    Path[] listedPaths = FileUtil.stat2Paths(status);
    for (Path p : listedPaths) {
      System.out.println(p);
    }
  }
}
// ^^ ListStatus

//cc ConfigUpdater An application that updates a property in ZooKeeper at random times
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;

// vv ConfigUpdater

/**
 * 范例21-6说随机更新zookeeper中配置的属性值是有错误的，当然可能是翻译的锅
 * 这里是应该使用随机值来更新一个名为config的znode
 * 20190518pengt
 */
public class ConfigUpdater {

  // 定义路径（znode）：可能是这个名字造成误解
  public static final String PATH = "/config";

  // 使用作者之前自己写的类，包含两个方法，一个write，一个read
  private ActiveKeyValueStore store;
  // 随机类，用于生成随机数
  private Random random = new Random();

  /**
   * 这个类用于创建之前作者自己写的类对象并且连接zookeeper,
   * pengt
   * @param hosts zookeeper服务的主机地址（可以指定端口，默认端口为2181）
   * @throws IOException
   * @throws InterruptedException
   */
  public ConfigUpdater(String hosts) throws IOException, InterruptedException {
    store = new ActiveKeyValueStore();
    store.connect(hosts);
  }

  // 这个方法随机更新数据
  public void run() throws InterruptedException, KeeperException {
    // 无限循环
    while (true) {
      // 随机生成一个100以内的int值
      String value = random.nextInt(100) + "";
      // 调用之前作者写的类，将值保存到 /config 路径下
      // 该方法见 ActiveKeyValueStore 类
      store.write(PATH, value);
      System.out.printf("Set %s to %s\n", PATH, value);
      // 线程睡眠会，然后起来接着改
      TimeUnit.SECONDS.sleep(random.nextInt(10));
    }
  }
  
  public static void main(String[] args) throws Exception {
    ConfigUpdater configUpdater = new ConfigUpdater(args[0]); // 创建当前类对象
    configUpdater.run();  // 将随机值保存到 /config路径下
  }
}
// ^^ ConfigUpdater

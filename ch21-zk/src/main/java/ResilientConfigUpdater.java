//== ResilientConfigUpdater
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;

public class ResilientConfigUpdater {
  // 定义操作的路径（znode）
  public static final String PATH = "/config";

  // 使用作者修改后的类
  private ResilientActiveKeyValueStore store;
  // 随机类，用于生成随机数
  private Random random = new Random();

  /**
   * 这个类用于创建之前作者自己写的类对象并且连接zookeeper,
   * pengt
   * @param hosts zookeeper服务的主机地址（可以指定端口，默认端口为2181）
   * @throws IOException
   * @throws InterruptedException
   */
  public ResilientConfigUpdater(String hosts) throws IOException,
      InterruptedException {
    store = new ResilientActiveKeyValueStore();
    store.connect(hosts);
  }
  // 有修改部分
  public void run() throws InterruptedException, KeeperException {
    // 无限循环
    while (true) {
      // 随机生成一个100以内的int值
      String value = random.nextInt(100) + "";
      // 调用之前作者写的类，将值保存到 /config 路径下
      // 该方法见 ResilientActiveKeyValueStore 类
      store.write(PATH, value);
      System.out.printf("Set %s to %s\n", PATH, value);
      // 线程睡眠会，然后起来接着改
      TimeUnit.SECONDS.sleep(random.nextInt(10));
    }
  }
  
//vv ResilientConfigUpdater
  // 这里有新增，处理ResilientActiveKeyValueStore 中抛出的异常
  public static void main(String[] args) throws Exception {
    /*[*/while (true) {
      try {/*]*/
        ResilientConfigUpdater configUpdater =
          new ResilientConfigUpdater(args[0]);    // 创建当前类对象
        configUpdater.run();    // 将随机值保存到 /config路径下
      /*[*/} catch (KeeperException.SessionExpiredException e) {  //接收上一个类抛出的异常并处理
        // start a new session
      } catch (KeeperException e) {
        // already retried, so exit
        e.printStackTrace();
        break;
      }
    }/*]*/
  }
//^^ ResilientConfigUpdater
}

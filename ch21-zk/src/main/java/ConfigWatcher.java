//cc ConfigWatcher An application that watches for updates of a property in ZooKeeper and prints them to the console
import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

// vv ConfigWatcher

/**
 * 范例21-7 pengt
 * 这个类是一个观察者类，它向zookeeper注册znode的数据监听，触发监听事件后，从zookeeper获取数据变化的值
 */
public class ConfigWatcher implements Watcher {
  // 还是之前作者写的类，不过这里主要用来调用read方法
  private ActiveKeyValueStore store;

  /**
   * 这个类用于创建之前作者自己写的类对象并且连接zookeeper,
   * @param hosts zookeeper服务的主机地址（可以指定端口，默认端口为2181）
   * @throws IOException
   * @throws InterruptedException
   */
  public ConfigWatcher(String hosts) throws IOException, InterruptedException {
    store = new ActiveKeyValueStore();
    store.connect(hosts);
  }

  /**
   * 这个方法是向zookeeper注册一个监听
   * 注意：触发只有一次有效，触发后就失效了，如果想要再次触发，就需要再次向zookeeper注册相同的监听
   * @throws InterruptedException
   * @throws KeeperException
   */
  public void displayConfig() throws InterruptedException, KeeperException {
    // 这里调用的是作者之前写的类的方法，这个方法向zookeeper注册了一个对znode内容的监听
    // 当指定的znode里保存的数据改变后，就会触发监听
    String value = store.read(ConfigUpdater.PATH, this);
    // 输出变化内容
    System.out.printf("Read %s as %s\n", ConfigUpdater.PATH, value);
  }

  /**
   * 这个方法是Watcher 接口需要重写的方法，用于回调，向zookeeper获取数据
   * @param event
   */
  @Override
  public void process(WatchedEvent event) {
    // EventType.NodeDataChanged 是一个枚举值，对应监听到的事件，这个值表示监听的znode所保存的数据发生变化
    if (event.getType() == EventType.NodeDataChanged) {
      try {
        // 上面的方法，重新设置监听，并把收到的内容打印出来
        displayConfig();

        // 异常处理
      } catch (InterruptedException e) {  // 操作被中断的处理
        System.err.println("Interrupted. Exiting.");        
        Thread.currentThread().interrupt(); // 对阻塞的进程调用这个方法可以取消阻塞
      } catch (KeeperException e) { // zookeeper服务器发出一个错误信号或者与服务器存在通信问题，
        System.err.printf("KeeperException: %s. Exiting.\n", e);   // 如果操作的znode不存在也会发生这个错误
          // 这个错误类型很多种，需要具体问题具体分析
      }
    }
  }
  
  public static void main(String[] args) throws Exception {
    ConfigWatcher configWatcher = new ConfigWatcher(args[0]); // 创建当前类对象
    configWatcher.displayConfig();  // 开始监听
    
    // stay alive until process is killed or thread is interrupted
    // 这里只是为了监听，并不需要做什么，所以等着就好
    Thread.sleep(Long.MAX_VALUE);
  }
}
//^^ ConfigWatcher

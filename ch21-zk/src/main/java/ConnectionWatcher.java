//cc ConnectionWatcher A helper class that waits for the connection to ZooKeeper to be established
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

// vv ConnectionWatcher
// 该辅助类等待与zookeeper建立连接
// pengt 20190518
public class ConnectionWatcher implements Watcher {
  
  private static final int SESSION_TIMEOUT = 5000;  // 设置超时时间为5秒

  protected ZooKeeper zk;   // 定义一个zookeeper类，
  // java并发辅助类，实现类似计数器的功能，后面的 1 表示需要等待一个线程结束才能执行
  private CountDownLatch connectedSignal = new CountDownLatch(1); //1 为count值

  /**
   * @param hosts
   * @throws IOException
   * @throws InterruptedException
   * 创建与zookeeper的连接对象
   */
  public void connect(String hosts) throws IOException, InterruptedException {
    // 初始化 ZooKeeper 实例(zk 地址、会话超时时间，与系统默认一致、watcher)
    // 第一个参数：zookeeper服务的主机地址（可以指定端口，默认端口为2181）
    // 第二个参数：会话超时时间(以毫秒计)，这里是上面定义的5秒
    // 第三个参数：是一个watcher 监听类，可以使用匿名内部类来处理，但是这里没有，因为这里的主类就是watcher类
    // watcher监听类对象接收雷子zookeeper的回调，回调函数为：process
    zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
    // 为了确认在使用时与zookeeper已经正常连接，避免重复连接，这里采用之前按说的java并发辅助类的一个方法
    // 这个方法会挂起当前线程，直到count 值为 0，
    connectedSignal.await();
  }

  /**
   * 回调方法：watcher 监听类下面需要重写的方法，时获取zookeeper时间的回调方法
   * @param event
   */
  @Override
  public void process(WatchedEvent event) {
    // 查询事件的通知状态是否为连接事件
    // event.getState() 函数是获取事件的通知状态（该API可以参看我的博客zookeeper的javaAPI）
    // KeeperState.SyncConnected 是一个枚举值，表示接收到一个连接事件，来源于watch接口
    if (event.getState() == KeeperState.SyncConnected) {
      // 这里采用之前按的java并发辅助类，调用其中的方法，将count值减一
      // 因为之前设置的是 1 ，所以这里线程不在挂起，开始执行，
      // 即zookeeper连接正常了就不再挂起线程
      connectedSignal.countDown();
    }
  }

  /**
   * 关闭与zookeeper的连接，释放资源
   * @throws InterruptedException
   */
  public void close() throws InterruptedException {
    zk.close();
  }
}
// ^^ ConnectionWatcher
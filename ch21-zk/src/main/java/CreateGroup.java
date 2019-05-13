//cc CreateGroup A program to create a znode representing a group in ZooKeeper

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

// vv CreateGroup
public class CreateGroup implements Watcher {
  
  private static final int SESSION_TIMEOUT = 5000;      // 设置超时时间为5秒
  
  private ZooKeeper zk;
  // java并发辅助类，实现类似计数器的功能，后面的 1 表示需要等待一个线程结束才能执行
  private CountDownLatch connectedSignal = new CountDownLatch(1);   //1 为count值
  
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

  // watcher 监听类下面需要重写的方法，时获取zookeeper时间的回调方法
  @Override
  public void process(WatchedEvent event) { // Watcher interface
    if (event.getState() == KeeperState.SyncConnected) {
      connectedSignal.countDown();
    }
  }
  
  public void create(String groupName) throws KeeperException,
      InterruptedException {
    String path = "/" + groupName;
    // 这里表示创建一个非序列化的持久节点，
    // 第一个参数指定的是节点创建的位置，参数类型是String类型
    // 第二个节点是保存的数据，参数类型是 Bytes[]
    // 第三个参数是权限限制，完全开发的ACL（访问控制列表的简称），允许客户端对znode进行读/写
    // 第四个为创建节点的类型，当你输入Create之后IDEA就会有提示
    // 返回值是zookeeper所创建的节点路径
    String createdPath = zk.create(path, null/*data*/, Ids.OPEN_ACL_UNSAFE,
        CreateMode.PERSISTENT);
    System.out.println("Created " + createdPath);
  }
  
  public void close() throws InterruptedException {
    zk.close();
  }

  public static void main(String[] args) throws Exception {
    CreateGroup createGroup = new CreateGroup();  // 创建一个与zookeeper相关的监听类
    createGroup.connect(args[0]); // 初始化zookeeper实例
    createGroup.create(args[1]);  // 创建一个持久节点
    createGroup.close();          // 关闭与zookeeper的连接
  }
}
// ^^ CreateGroup

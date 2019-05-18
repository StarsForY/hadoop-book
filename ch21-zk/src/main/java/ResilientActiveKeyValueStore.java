//== ResilientActiveKeyValueStore-Write

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * P635页作者写的类的修改版本
 */
public class ResilientActiveKeyValueStore extends ConnectionWatcher {

  private static final Charset CHARSET = Charset.forName("UTF-8");
  private static final int MAX_RETRIES = 5; // 设置最大的重试次数 5
  private static final int RETRY_PERIOD_SECONDS = 10; // 设置两次重试之间的时间间隔 10秒

//vv ResilientActiveKeyValueStore-Write

  /**
   * 修改后的write类
   * @param path  路径
   * @param value 值
   * @throws InterruptedException
   * @throws KeeperException
   */
  public void write(String path, String value) throws InterruptedException,
      KeeperException {
    int retries = 0;  // 初始化重试次数
    while (true) {
      try {
        // 首先判断指定路径的znode是否存在
        // 第一个参数：指定的路径
        // 第二个参数：是否设置监听
        Stat stat = zk.exists(path, false); // 先判断节点是否存在
        if (stat == null) { // 如果不存在则创建，创建的语法不再描述，详情见 CreateGroup 类中
          zk.create(path, value.getBytes(CHARSET), Ids.OPEN_ACL_UNSAFE,
              CreateMode.PERSISTENT);
        } else {  // 存在，则更新节点内容
          // 更新节点内容
          // 第一个参数：指定路径
          // 第二个参数：更新后保存的数据，参数类型是 Bytes[]
          // 第三个参数：版本号，这里应该是一个int值，但是这里使用的这个方法在API中没有找到
          // 暂时理解stat.getVersion() 方法为作者自己写的，获取版本号的方法
          zk.setData(path, value.getBytes(CHARSET), stat.getVersion());
        }
        return;
      } catch (KeeperException.SessionExpiredException e) { // 会话超时或者会话关闭异常
        throw e;
      } catch (KeeperException e) { //其他异常
        if (retries++ == MAX_RETRIES) {   // 重试
          throw e;
        }
        // sleep then retry
        // 重试间隔
        TimeUnit.SECONDS.sleep(RETRY_PERIOD_SECONDS);
      }
    }
  }
//^^ ResilientActiveKeyValueStore-Write
  /**
   * 这个 方法也是作者写的
   * 用来读取节点数据，可以理解用来读取配置或者读取账户和密码
   * @param path
   * @param watcher
   * @return
   * @throws InterruptedException
   * @throws KeeperException
   */
  public String read(String path, Watcher watcher) throws InterruptedException,
      KeeperException {
    // 使用getdata() 方法来获取数据
    // 第一个参数：表示你要获取那个路径下的节点
    // 第二个参数：表示是否要开启监听，true表示是，false表示否
    // 第三个参数：节点的状态
    byte[] data = zk.getData(path, watcher, null/*stat*/);
    return new String(data, CHARSET);
  }
}

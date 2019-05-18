//== ActiveKeyValueStore
//== ActiveKeyValueStore-Read
//== ActiveKeyValueStore-Write

import java.nio.charset.Charset;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

// vv ActiveKeyValueStore
/**
 * pengt 20190518
 * 这个类继承的是范例 21-3，增加了范例21-3中的连接zookeeper和关闭连接的方法
 */
public class ActiveKeyValueStore extends ConnectionWatcher {

  // 定义一个默认字符集变量：CHARSET
  // 并赋值为： utf-8类型的字符集
  // 并且此变量为 公共的静态和最终态变量，
  // 此类中定义的所有方法都可以安全地被多个并发线程使用。
  private static final Charset CHARSET = Charset.forName("UTF-8");

//vv ActiveKeyValueStore-Write

  /**
   * 这个方法是作者自己编写的一个方法，不是zookeeper所有的
   * 将配置服务以键值对的形式通过创建或者更新znode来保存
   * @param path  路径
   * @param value 值
   * @throws InterruptedException
   * @throws KeeperException
   */
  public void write(String path, String value) throws InterruptedException,
      KeeperException {
    // 首先判断指定路径的znode是否存在
    // 第一个参数：指定的路径
    // 第二个参数：是否设置监听
    Stat stat = zk.exists(path, false);
    if (stat == null) { // 如果不存在则创建，创建的语法不再描述，详情见 CreateGroup 类中
      zk.create(path, value.getBytes(CHARSET), Ids.OPEN_ACL_UNSAFE,
          CreateMode.PERSISTENT);
    } else {  // 存在，则更新节点内容
      // 更新节点内容
      // 第一个参数：指定路径
      // 第二个参数：更新后保存的数据，参数类型是 Bytes[]
      // 第三个参数：版本号，-1 表示版本号不用手工维护，由系统来维护
      zk.setData(path, value.getBytes(CHARSET), -1);
    }
  }
//^^ ActiveKeyValueStore-Write
//^^ ActiveKeyValueStore
//vv ActiveKeyValueStore-Read

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
    return new String(data, CHARSET); // 将获取的数据返回，第二个值是上面的字符变量设置类对象
  }
//^^ ActiveKeyValueStore-Read
//vv ActiveKeyValueStore
}
//^^ ActiveKeyValueStore

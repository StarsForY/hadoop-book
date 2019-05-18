//cc JoinGroup A program that joins a group

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

// vv JoinGroup

/**
 * pengt 20190518
 * 这个类继承的是范例 21-3，增加了范例21-3中的连接zookeeper和关闭连接的方法
 */
public class JoinGroup extends ConnectionWatcher {

  /**
   * 这里实现的是将zookeeper加入一个组，就是创建一个子节点，
   * 这里是过了几天才开始看的，刚开始这个组把我整懵了，回去扫了一眼才发现，组就是父节点
   * @param groupName 父节点路径
   * @param memberName 子节点路径（包括子节点名称）
   * @throws KeeperException
   * @throws InterruptedException
   */
  public void join(String groupName, String memberName) throws KeeperException,
      InterruptedException {
    String path = "/" + groupName + "/" + memberName; // 先拼接子节点的路径
    //创建一个临时的子节点
    // 第一个参数指定的是节点创建的位置，参数类型是String类型，就是上面拼接的
    // 第二个节点是保存的数据，参数类型是 Bytes[]
    // 第三个参数是权限限制，完全开发的ACL（访问控制列表的简称），允许客户端对znode进行读/写（这个涉及到客户端的验证问题，分开说）
    // 第四个为创建节点的类型，当你输入Create之后IDEA就会有提示
    // 返回值是zookeeper所创建的节点路径
    String createdPath = zk.create(path, null/*data*/, Ids.OPEN_ACL_UNSAFE,
      CreateMode.EPHEMERAL);
    System.out.println("Created " + createdPath);
  }
  
  public static void main(String[] args) throws Exception {
    JoinGroup joinGroup = new JoinGroup();  // 父类ConnectionWatcher实现了Watcher接口，所以当前类也属于Watcher的实现类
    joinGroup.connect(args[0]); // 连接zookeeper
    joinGroup.join(args[1], args[2]); //创建子节点
    
    // stay alive until process is killed or thread is interrupted
    // 通过线程长时间的睡眠，模拟进行某种操作，直到线程被强行终止，
    // 线程终止后，短暂的znode节点就会被删除
    Thread.sleep(Long.MAX_VALUE); // 线程睡眠
  }
}
// ^^ JoinGroup

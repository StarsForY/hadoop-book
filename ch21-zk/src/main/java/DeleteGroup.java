//cc DeleteGroup A program to delete a group and its members
import java.util.List;

import org.apache.zookeeper.KeeperException;

// vv DeleteGroup

/**
 * 突然想说一句：向伟人致敬，给编书的大佬递帽 pengt 190518
 *
 * 删除一个节点
 */
public class DeleteGroup extends ConnectionWatcher {
    
  public void delete(String groupName) throws KeeperException,
      InterruptedException {
    // 拼接路径
    String path = "/" + groupName;
    
    try { // try是为了输出没有查询指定路径不存在的异常
      // 先获取所有的子节点，然后增强for循环删除
      List<String> children = zk.getChildren(path, false);
      for (String child : children) {
        zk.delete(path + "/" + child, -1);
      }
      //删除节点
      // 第一个参数：指定路径
      // 第二个参数：指定版本，删除时版本需要和当前的版本保持一致，否则出错
      // 这是一种乐观锁机制，是客户端能够检测到对znode的修改冲突
      // -1之前有说过，修改时设置版本为-1是将版本交给zookeeper自己去管理，我们不用管，
      // 这里 -1 是不检测版本，直接干掉
      zk.delete(path, -1);
    } catch (KeeperException.NoNodeException e) { // 输出没有节点不存在的异常
      System.out.printf("Group %s does not exist\n", groupName);
      System.exit(1);
    }
  }
  
  public static void main(String[] args) throws Exception {
    DeleteGroup deleteGroup = new DeleteGroup();  // 创建当前类对象
    deleteGroup.connect(args[0]); // 连接zookeeper
    deleteGroup.delete(args[1]);  // 删除指定路径及其下的所有子节点
    deleteGroup.close();          // 关闭连接
  }
}
// ^^ DeleteGroup

//cc ListGroup A program to list the members in a group
import java.util.List;

import org.apache.zookeeper.KeeperException;

// vv ListGroup

/**
 * pengt 20190518
 * 这个类继承的是范例 21-3，增加了范例21-3中的连接zookeeper和关闭连接的方法
 */
public class ListGroup extends ConnectionWatcher {
    
  public void list(String groupName) throws KeeperException,
      InterruptedException {
    // 定义一个字符串，保存想要查看的路径
    String path = "/" + groupName;
    
    try { // try是为了输出没有查询指定路径不存在的异常
      // zk.getChildren() 方法返回具有盖顶路径的无序子节点数组
      // 第一个参数：给定的路径
      // 第二个参数：是否设置监听 true：设置，false：不设置
      List<String> children = zk.getChildren(path, false);
      if (children.isEmpty()) { // 判断是否为空
        System.out.printf("No members in group %s\n", groupName);
        System.exit(1);
      }
      for (String child : children) {   // 循环输出znode的名称
        System.out.println(child);
      }
    } catch (KeeperException.NoNodeException e) { // 输出没有节点不存在的异常
      System.out.printf("Group %s does not exist\n", groupName);
      System.exit(1);
    }
  }
  
  public static void main(String[] args) throws Exception {
    ListGroup listGroup = new ListGroup();  // 父类ConnectionWatcher实现了Watcher接口，所以当前类也属于Watcher的实现类
    listGroup.connect(args[0]); // 连接zookeeper
    listGroup.list(args[1]);    // 获取指定路径下的所有节点
    listGroup.close();          // 关闭连接
  }
}
// ^^ ListGroup

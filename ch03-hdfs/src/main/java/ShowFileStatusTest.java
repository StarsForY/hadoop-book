// 范例3-5.展示文件状态信息
public class ShowFileStatusTest {
// 这个示例竟然缺了，没办法，只能手动补齐，但是因为配置文件的问题，我这边没法导入包，所以只能这样了，大家将就的看吧

    private MiniDFSCluster cluster;//use an in-process HDFS cluster for testing
    private FileSystem fs;
    @Before
    public void setUp()throws IOException {
        // Configuration 对象封装了客户端或服务器的配置，通过设置配置文件读取类路径来实现（如etc/hadoop/core-site.xml）
        Configuration conf=new Configuration();

        // java.lang.System
        // 获取指定参数指示的系统属性。
        // 参数：系统属性的名称。
        // 返回值：系统属性的字符串值，如果没有该键的属性， null
        if(System.getProperty("test.build.data")==null) {
            // java.lang.System
            // 设置由指定键指示的系统属性。
            // 参数1：系统属性的名称。
            // 参数2：系统属性的值。
            // 两个参数都不能为null
            System.setProperty("test.build.data","/tmp");
        }
        // API里面没有找到这个类，上网查说是这个类在Hadoop 1.0.4 的时候就已经被废弃了
        // MiniDFSCluster是一个本地单进程的模拟hdfs集群框架。
        // 里面的方法:
        //1）NameNodeRunner：用来开启NameNode的一个线程；
        //2）DataNodeRunner：用来开启DataNode的一个线程；
        //3）shutdown方法：用来关闭上面开启的NameNode与DataNode；
        //4）getFileSystem方法：得到模拟hdfs的文件系统；
        cluster=new MiniDFSCluster.Builder(conf).build();
        // 得到模拟hdfs的文件系统；
        fs=cluster.getFilesystem();
        outputStream out=fs.create(new Path("/dir/file"));
        out.write("content".getBytes("UTF-8"));
        out.close();
    }
    @After
    public void tearDown()throws IOException {
        // 测试完后关闭流
        if(fs != nul1) {
            fs.close();
        }
        if(cluster != nul1) {
            cluster.shutdown();
        }
    }
    @Test(expected=FileNotFoundException.class)
    public void throwsFileNotFoundForNonExistentFile()throws IOException {
        fs.getFileStatus(new Path("no-such-file"));
    }
    @Test
    public void filestatusForFile()throws IOException {
        Path file=new Path("/dir/file");
        // 返回表示路径的文件状态对象。
        // 参数：我们想要信息的路径
        // 返回：一个FileStatus对象
        // FileStatus类封装了文件系统中文件和目录的元数据，包括文件长度，块大小，复本，修改时间，所有者以及权限信息。
        Filestatus stat=fs.getFileStatus(file);
        assertThat(stat.getPath().toUri().getPath(),is("/dir/file"));
        assertThat(stat.isDirectory(),is(false));
        assertThat(stat.getLen(),is(7L));
        assertThat(stat.getModificationTime(),is(1essThanorEqualTo(System.currentTimeMillis())));
        assertThat(stat.getReplication(),is((short)1));
        assertThat(stat.getBlockSize(),is(128*1024*1024L));
        assertThat(stat.getOwner(),is(System.getProperty("user.name")));
        assertThat(stat.getGroup(),is("supergroup"));
        assertThat(stat.getPermission().tostring(),is("rw-r--r--"));
    }
    @Test
    public void fileStatusForDirectory()throws IOException {
        Path dir=new Path("/dir");
        FileStatus stat=fs.getFilestatus(dir);
        assertThat(stat.getPath().toUri().getPath(),is("/dir"));
        assertThat(stat.isDirectory(),is(true));
        assertThat(stat.getLen(),is(0L));
        assertThat(stat.getModificationTime(),is(lessThanOrEqualTo(System.currentTimeMil1is())));
        assertThat(stat.getReplication(),is((short)0));
        assertThat(stat.getBlockSize(),is(0L));
        assertThat(stat.getowner(),is(System.getProperty("user.name")));
        assertThat(stat.getGroup(),is("supergroup"));
        assertThat(stat.getPermission().tostring(),is("rwxr-xr-x"));
    }
}
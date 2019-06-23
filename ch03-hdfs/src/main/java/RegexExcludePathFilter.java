// cc RegexExcludePathFilter A PathFilter for excluding paths that match a regular expression
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

// vv RegexExcludePathFilter
public class RegexExcludePathFilter implements PathFilter {
  // 创建一个存储正则表达式的字符串
  private final String regex;

  public RegexExcludePathFilter(String regex) {
    this.regex = regex;
  }
  // 判断文件是否匹配正则表达式
  public boolean accept(Path path) {
    return !path.toString().matches(regex);
  }
}
// ^^ RegexExcludePathFilter

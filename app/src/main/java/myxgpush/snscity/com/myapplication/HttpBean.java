package myxgpush.snscity.com.myapplication;

/**
 * Created by lyb .
 * Data on 2015/10/20
 * Class
 */
public class HttpBean {

    String name;
    String url;
    int ErrorCount;//记录访问失败次数
    boolean isRun;
    int CountSize;//记录访问次数

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public boolean isRun() {
        return isRun;
    }

    public void setIsRun(boolean isRun) {
        this.isRun = isRun;
    }

    public int getCountSize() {
        return CountSize;
    }

    public void setCountSize(int countSize) {
        CountSize = countSize;
    }

    public int getErrorCount() {
        return ErrorCount;
    }

    public void setErrorCount(int errorCount) {
        ErrorCount = errorCount;
    }
}

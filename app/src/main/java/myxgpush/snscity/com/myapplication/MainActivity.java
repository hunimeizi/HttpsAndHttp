package myxgpush.snscity.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    TextView txt;

    String code = "";

    TextView btn_add;

    DecimalFormat df = new DecimalFormat("#");
    DBHelper dbHelper;

    LinearLayout lly_body;

    List<HttpBean> httpNodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(mContext);
        initView();
        initData();
    }

    public void initView() {
        btn_add = (TextView) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        lly_body = (LinearLayout) findViewById(R.id.lly_body);
        txt = (TextView) findViewById(R.id.txt);
    }


    public void initData() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<HttpBean> beanList = dbHelper.selectHttpBeanList();
                if (beanList.size() > 0) {

                    lly_body.setVisibility(View.VISIBLE);

                    if(httpNodes.size() >0 && httpNodes.size() != beanList.size() ){
                        for (HttpBean beans : httpNodes) {
                            for (HttpBean bean : beanList) {
                                if (beans.getName().equals(bean.getName())) {
                                    beanList.remove(bean);
                                    break;
                                }
                            }
                        }
                    }

                    if(beanList.size()>0 && httpNodes.size() != beanList.size()){
                        for (HttpBean bean : beanList) {
                            HttpAndHttpsNode node = new HttpAndHttpsNode();
                            node.generateView(bean);
                            httpNodes.add(bean);
                        }
                        txt.setVisibility(View.GONE);
                    }



                } else {
                    //提示用户添加数据
                    txt.setText("您还没有添加过出去，赶快去添加吧！");
                    txt.setVisibility(View.VISIBLE);
                    lly_body.setVisibility(View.GONE);
                }
            }
        });
    }

    class HttpAndHttpsNode {

        HttpBean httpBean;

        public TextView mTextView;
        public TextView btn_startOrDel;
        public TextView btn_del;
        public TextView txt_sucess;

        public TextView txt_error;
        public TextView txt_count;

        public TextView txt_errorstatus;

        public void initViewData() {

            mTextView.setText(httpBean.getName());


            if (httpBean.getErrorCount() >= 3) {
                //显示错误图片
                txt_errorstatus.setVisibility(View.VISIBLE);
            } else {
                txt_errorstatus.setVisibility(View.GONE);
            }

        }

        public void generateView(final HttpBean bean) {
            this.httpBean = bean;

            final View view = View.inflate(MainActivity.this,
                    R.layout.my_text_view, null);
            mTextView = (TextView) view.findViewById(R.id.text);
            btn_startOrDel = (TextView) view.findViewById(R.id.btn_startOrDel);
            btn_del = (TextView) view.findViewById(R.id.btn_del);
            txt_sucess = (TextView) view.findViewById(R.id.txt_sucess);

            txt_error = (TextView) view.findViewById(R.id.txt_error);
            txt_count = (TextView) view.findViewById(R.id.txt_count);
            txt_errorstatus = (TextView) view.findViewById(R.id.txt_errorstatus);

            btn_startOrDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(btn_startOrDel.getText().toString().equals("暂停")){
                        btn_startOrDel.setText("开始");
                        httpBean.setIsRun(false);
                    }else{
                        btn_startOrDel.setText("暂停");
                        //判断网络信息
                        httpBean.setIsRun(true);
                        if (isNetworkConnected(MainActivity.this)) {

                            if (httpBean.getUrl().contains("https://")) {
                                doHttpsRequest(txt_sucess, txt_error, txt_count, txt_errorstatus,btn_startOrDel, httpBean);
                            } else if (httpBean.getUrl().contains("http://")) {
                                doHttpRequest(txt_sucess, txt_error, txt_count, txt_errorstatus,btn_startOrDel, httpBean);
                            } else {//默认使用http协议访问
                                doHttpRequest_addHead(txt_sucess, txt_error, txt_count, txt_errorstatus, btn_startOrDel,httpBean);
                            }
                        } else {
                            httpBean.setErrorCount(httpBean.getErrorCount() + 1);
                            if (httpBean.getErrorCount() >= 3) {
                                //显示错误图片
                                startMediaPlayer();
                                httpBean.setIsRun(false);
                                btn_startOrDel.setText("暂停");
                                txt_errorstatus.setVisibility(View.VISIBLE);
                            } else {
                                txt_errorstatus.setVisibility(View.GONE);
                            }
                        }

                    }


                }
            });
            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    httpNodes.remove(httpBean);
                    LinearLayout lly  = (LinearLayout) v.getParent();
                    LinearLayout llys = (LinearLayout) lly.getParent();
                    lly_body.removeView(llys);
                    //从数据库中删除此条数据
                    dbHelper.deleteData(bean.getName());
                }
            });
            lly_body.addView(view);
            initViewData();
        }
    }

    private class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            
            return true;
        }

    }

    private class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

    public void doHttpsRequest(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus,final TextView btn_startOrDel, final HttpBean httpBean) {
        new Thread() {
            @Override
            public void run() {

                try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
                    String urls = httpBean.getUrl();
                    HttpsURLConnection urlCon = (HttpsURLConnection) new URL(urls).openConnection();
                    urlCon.setDoOutput(true);
                    urlCon.setDoInput(true);
                    urlCon.connect();
                    //返回打开连接读取的输入流
                    DataInputStream dis = new DataInputStream(urlCon.getInputStream());
                    //判断是否正常响应数据
                    if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (httpBean.isRun()) {
                                    httpBean.setErrorCount(httpBean.getErrorCount() + 1);
                                    if (httpBean.getErrorCount() >= 3) {
                                        //显示错误图片
                                        startMediaPlayer();
                                        txtview_errorstatus.setVisibility(View.VISIBLE);
                                    } else {
                                        txtview_errorstatus.setVisibility(View.GONE);
                                    }
                                    httpBean.setIsRun(false);
                                    btn_startOrDel.setText("暂停");
                                    setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, httpBean);
                                }
                            }
                        });
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //判断网络信息
                                if (httpBean.isRun()) {
                                    if (isNetworkConnected(MainActivity.this)) {

                                        httpBean.setCountSize(httpBean.getCountSize() + 1);

                                        setSucessData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, httpBean);
                                        doHttpRequest(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, btn_startOrDel,httpBean);
                                    } else {
                                        httpBean.setErrorCount(httpBean.getErrorCount() + 1);

                                        if (httpBean.getErrorCount() >= 3) {
                                            startMediaPlayer();
                                            //显示错误图片
                                            httpBean.setIsRun(false);
                                            btn_startOrDel.setText("暂停");
                                            txtview_errorstatus.setVisibility(View.VISIBLE);
                                        } else {
                                            txtview_errorstatus.setVisibility(View.GONE);
                                        }
                                        setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,httpBean);
                                    }
                                }
                            }
                        });
                    }
                    dis.close();
                    urlCon.disconnect();


                } catch (Exception e) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (httpBean.isRun()) {
                                httpBean.setErrorCount(httpBean.getErrorCount() + 1);

                                if (httpBean.getErrorCount() >= 3) {
                                    startMediaPlayer();
                                    //显示错误图片
                                    httpBean.setIsRun(false);
                                    btn_startOrDel.setText("暂停");
                                    txtview_errorstatus.setVisibility(View.VISIBLE);
                                } else {
                                    txtview_errorstatus.setVisibility(View.GONE);
                                }
                                setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, httpBean);
                                doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,btn_startOrDel, httpBean);
                            }
                        }
                    });
                    Log.e(this.getClass().getName(), e.getMessage());
                }


            }
        }.start();
    }


    public void doHttpRequest(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus,final TextView btn_startOrDel, final HttpBean httpBean) {
        new Thread() {
            @Override
            public void run() {

                try {
                    URL url = new URL(httpBean.getUrl());
                    final HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setConnectTimeout(30000);
                    urlCon.setReadTimeout(30000);
                    //返回打开连接读取的输入流
                    DataInputStream dis = new DataInputStream(urlCon.getInputStream());
                    //判断是否正常响应数据
                    if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (httpBean.isRun()) {
                                    httpBean.setErrorCount(httpBean.getErrorCount() + 1);

                                    if (httpBean.getErrorCount() >= 3) {
                                        startMediaPlayer();
                                        //显示错误图片
                                        httpBean.setIsRun(false);
                                        btn_startOrDel.setText("暂停");
                                        txtview_errorstatus.setVisibility(View.VISIBLE);
                                    } else {

                                        txtview_errorstatus.setVisibility(View.GONE);
                                    }
                                    setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,httpBean);
                                }
                            }
                        });
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //判断网络信息
                                if (httpBean.isRun()) {
                                    if (isNetworkConnected(MainActivity.this)) {

                                        httpBean.setCountSize(httpBean.getCountSize() + 1);

                                        setSucessData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, httpBean);

                                        doHttpRequest(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,btn_startOrDel, httpBean);
                                    } else {
                                        httpBean.setErrorCount(httpBean.getErrorCount() + 1);

                                        if (httpBean.getErrorCount() >= 3) {
                                            startMediaPlayer();
                                            httpBean.setIsRun(false);
                                            btn_startOrDel.setText("暂停");
                                            //显示错误图片
                                            txtview_errorstatus.setVisibility(View.VISIBLE);
                                        } else {
                                            txtview_errorstatus.setVisibility(View.GONE);
                                        }
                                        setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, httpBean);
                                    }
                                }
                            }
                        });
                    }
                    dis.close();
                    urlCon.disconnect();

                } catch (Exception e) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (httpBean.isRun()) {
                                httpBean.setErrorCount(httpBean.getErrorCount() + 1);

                                if (httpBean.getErrorCount() >= 3) {
                                    startMediaPlayer();
                                    httpBean.setIsRun(false);
                                    btn_startOrDel.setText("暂停");
                                    //显示错误图片
                                    txtview_errorstatus.setVisibility(View.VISIBLE);
                                } else {

                                    txtview_errorstatus.setVisibility(View.GONE);
                                }
                                setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,httpBean);
                                doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,btn_startOrDel, httpBean);

                            }
                        }
                    });
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void setSucessData(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus, final HttpBean httpBean) {
        if ((httpBean.CountSize + httpBean.getErrorCount()) > 0) {
            txtview_count.setVisibility(View.VISIBLE);
        } else {
            txtview_count.setVisibility(View.VISIBLE);
        }
        if (httpBean.CountSize > 0) {
            txtview_sucess.setVisibility(View.VISIBLE);
        } else {
            txtview_sucess.setVisibility(View.GONE);
        }
        txtview_sucess.setText("正常:" + df.format(httpBean.getCountSize()));
        txtview_count.setText("总计:" + df.format(httpBean.getErrorCount() + httpBean.getCountSize()));
    }

    /**
     * 设置参数
     *
     * @param txtview_sucess
     * @param txtview_error
     * @param txtview_count
     * @param txtview_errorstatus
     */
    public void setErrorData(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus,final HttpBean httpBean) {

        if (httpBean.getErrorCount() > 0) {
            txtview_error.setVisibility(View.VISIBLE);
        } else {
            txtview_error.setVisibility(View.GONE);
        }
        if ((httpBean.CountSize + httpBean.getErrorCount()) > 0) {
            txtview_count.setVisibility(View.VISIBLE);
        } else {
            txtview_count.setVisibility(View.VISIBLE);
        }


        if (httpBean.CountSize > 0) {
            txtview_sucess.setVisibility(View.VISIBLE);
        } else {
            txtview_sucess.setVisibility(View.GONE);
        }
        txtview_error.setText("异常:" + df.format(httpBean.getErrorCount()));
        txtview_count.setText("总计:" + df.format(httpBean.getErrorCount() + httpBean.getCountSize()));
    }


    public void startMediaPlayer() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.voip_ring_called);

                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void doHttpRequest_addHead(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus,final TextView btn_startOrDel, final HttpBean httpBean) {
        new Thread() {
            @Override
            public void run() {

                try {
                    URL url = new URL("http://" + httpBean.getUrl());
                    final HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setConnectTimeout(30000);
                    urlCon.setReadTimeout(30000);
                    //返回打开连接读取的输入流
                    DataInputStream dis = new DataInputStream(urlCon.getInputStream());
                    //判断是否正常响应数据
                    if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (httpBean.isRun()) {
                                    httpBean.setErrorCount(httpBean.getErrorCount() + 1);

                                    if (httpBean.getErrorCount() >= 3) {
                                        startMediaPlayer();
                                        httpBean.setIsRun(false);
                                        btn_startOrDel.setText("暂停");
                                        //显示错误图片
                                        txtview_errorstatus.setVisibility(View.VISIBLE);
                                    } else {

                                        txtview_errorstatus.setVisibility(View.GONE);
                                    }
                                    setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,httpBean);
                                }
                            }
                        });
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //判断网络信息
                                if (httpBean.isRun()) {
                                    if (isNetworkConnected(MainActivity.this)) {

                                        httpBean.setCountSize(httpBean.getCountSize() + 1);
                                        setSucessData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, httpBean);
                                        doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,btn_startOrDel, httpBean);

                                    } else {
                                        httpBean.setErrorCount(httpBean.getErrorCount() + 1);
                                        if (httpBean.getErrorCount() >= 3) {
                                            startMediaPlayer();
                                            httpBean.setIsRun(false);
                                            btn_startOrDel.setText("暂停");
                                            //显示错误图片
                                            txtview_errorstatus.setVisibility(View.VISIBLE);
                                        } else {
                                            txtview_errorstatus.setVisibility(View.GONE);
                                        }
                                        setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, httpBean);
                                    }

                                }
                            }
                        });
                    }
                    dis.close();
                    urlCon.disconnect();

                } catch (Exception e) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (httpBean.isRun()) {
                                httpBean.setErrorCount(httpBean.getErrorCount() + 1);


                                if (httpBean.getErrorCount() >= 3) {
                                    startMediaPlayer();
                                    httpBean.setIsRun(false);
                                    btn_startOrDel.setText("暂停");
                                    //显示错误图片
                                    txtview_errorstatus.setVisibility(View.VISIBLE);
                                } else {

                                    txtview_errorstatus.setVisibility(View.GONE);
                                }
                                setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, httpBean);
                                doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus,btn_startOrDel, httpBean);

                            }
                        }
                    });
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 判断有无网络信息
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 200) {
            initData();
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                //添加Item
                Intent it = new Intent();
                it.setClass(mContext, AddActivity.class);
                startActivityForResult(it, 200);
                break;
        }
    }
}

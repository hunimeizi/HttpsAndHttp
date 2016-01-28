package myxgpush.snscity.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by lyb .
 * Data on 2015/10/15
 * Class
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
    List<HttpBean> beanList = new ArrayList<>();

    DBHelper dbHelper;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    DecimalFormat df = new DecimalFormat("#");

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }


    public List<HttpBean> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<HttpBean> beanList) {
        this.beanList = beanList;
    }

    // Provide a reference to the type of views that you are using
    // (custom viewholder)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView btn_start;
        public TextView btn_stop;
        public TextView txt_sucess;

        public TextView txt_error;
        public TextView txt_count;

        public TextView txt_errorstatus;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);

            btn_start = (TextView) view.findViewById(R.id.btn_startOrDel);
            btn_stop = (TextView) view.findViewById(R.id.btn_del);
            txt_sucess = (TextView) view.findViewById(R.id.txt_sucess);

            txt_error = (TextView) view.findViewById(R.id.txt_error);
            txt_count = (TextView) view.findViewById(R.id.txt_count);
            txt_errorstatus = (TextView) view.findViewById(R.id.txt_errorstatus);

        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)

    Activity mActivity;

    public MyAdapter(List<HttpBean> beanLists, Activity activity) {
        beanList.addAll(beanLists);
        mActivity = activity;
        dbHelper = new DBHelper(mActivity);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setOnClickListener(this);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(beanList.get(position).getName());
        holder.itemView.setTag(beanList.get(position).getName());


        holder.btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lly = (LinearLayout) v.getParent();

                TextView btnstart = (TextView) lly.getChildAt(1);
                TextView btnstop = (TextView) lly.getChildAt(2);

                TextView txtview_sucess = (TextView) lly.getChildAt(3);
                TextView txtview_error = (TextView) lly.getChildAt(4);
                TextView txtview_count = (TextView) lly.getChildAt(5);
                TextView txtview_errorstatus = (TextView) lly.getChildAt(6);

                //判断网络信息
                beanList.get(position).setIsRun(true);
                if (isNetworkConnected(mActivity)) {
                    btnstart.setBackgroundResource(R.drawable.btn_gray);
                    btnstop.setBackgroundResource(R.drawable.btn_green);


                    if (beanList.get(position).getUrl().contains("https://")) {
                        doHttpsRequest(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                    } else if (beanList.get(position).getUrl().contains("http://")) {
                        doHttpRequest(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                    } else {//默认使用http协议访问
                        doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                    }
                } else {
                    beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);
                    if (beanList.get(position).getErrorCount() >= 3) {
                        //显示错误图片
                        startMediaPlayer();
                        beanList.get(position).setIsRun(false);
                        txtview_errorstatus.setVisibility(View.VISIBLE);
                    } else {
                        txtview_errorstatus.setVisibility(View.GONE);
                    }
                }
            }
        });
        holder.btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lly = (LinearLayout) v.getParent();
                TextView btnstart = (TextView) lly.getChildAt(1);
                TextView btnstop = (TextView) lly.getChildAt(2);

                btnstart.setBackgroundResource(R.drawable.btn_green);
                btnstop.setBackgroundResource(R.drawable.btn_gray);

                if (beanList.get(position).isRun) {
                    beanList.get(position).setIsRun(false);
                } else {
                    Toast.makeText(mActivity, "已经暂停访问url:" + beanList.get(position).getUrl(), Toast.LENGTH_SHORT).show();
                }


            }
        });


        if (beanList.get(position).isRun) {
            holder.btn_start.setBackgroundResource(R.drawable.btn_green);
            holder.btn_stop.setBackgroundResource(R.drawable.btn_gray);
        } else {
            holder.btn_start.setBackgroundResource(R.drawable.btn_gray);
            holder.btn_stop.setBackgroundResource(R.drawable.btn_green);
        }

        if (beanList.get(position).getErrorCount() >= 3) {
            //显示错误图片
            holder.txt_errorstatus.setVisibility(View.VISIBLE);
        } else {
            holder.txt_errorstatus.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (null == beanList) ? 0 : beanList.size();
    }

    private class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }

    }

    private class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public void doHttpsRequest(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus, final int position) {
        new Thread() {
            @Override
            public void run() {

                try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
                    String urls = beanList.get(position).getUrl();
                    HttpsURLConnection urlCon = (HttpsURLConnection) new URL(urls).openConnection();
                    urlCon.setDoOutput(true);
                    urlCon.setDoInput(true);
                    urlCon.connect();
                    //返回打开连接读取的输入流
                    DataInputStream dis = new DataInputStream(urlCon.getInputStream());
                    //判断是否正常响应数据
                    if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (beanList.get(position).isRun()) {
                                    beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);
                                    if (beanList.get(position).getErrorCount() >= 3) {
                                        //显示错误图片
                                        startMediaPlayer();
                                        beanList.get(position).setIsRun(false);
                                        txtview_errorstatus.setVisibility(View.VISIBLE);
                                    } else {

                                        txtview_errorstatus.setVisibility(View.GONE);
                                    }
                                    setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                }
                            }
                        });
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //判断网络信息
                                if (beanList.get(position).isRun()) {
                                    if (isNetworkConnected(mActivity)) {

                                        beanList.get(position).setCountSize(beanList.get(position).getCountSize() + 1);

                                        setSucessData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                        doHttpRequest(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                    } else {
                                        beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);

                                        if (beanList.get(position).getErrorCount() >= 3) {
                                            startMediaPlayer();
                                            //显示错误图片
                                            beanList.get(position).setIsRun(false);
                                            txtview_errorstatus.setVisibility(View.VISIBLE);
                                        } else {
                                            txtview_errorstatus.setVisibility(View.GONE);
                                        }
                                        setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                    }
                                }
                            }
                        });
                    }
                    dis.close();
                    urlCon.disconnect();


                } catch (Exception e) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (beanList.get(position).isRun()) {
                                beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);

                                if (beanList.get(position).getErrorCount() >= 3) {
                                    startMediaPlayer();
                                    //显示错误图片
                                    beanList.get(position).setIsRun(false);
                                    txtview_errorstatus.setVisibility(View.VISIBLE);
                                } else {
                                    txtview_errorstatus.setVisibility(View.GONE);
                                }
                                setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                            }
                        }
                    });
                    Log.e(this.getClass().getName(), e.getMessage());
                }


            }
        }.start();
    }


    public void doHttpRequest(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus, final int position) {
        new Thread() {
            @Override
            public void run() {

                try {
                    URL url = new URL(beanList.get(position).getUrl());
                    final HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setConnectTimeout(30000);
                    urlCon.setReadTimeout(30000);
                    //返回打开连接读取的输入流
                    DataInputStream dis = new DataInputStream(urlCon.getInputStream());
                    //判断是否正常响应数据
                    if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (beanList.get(position).isRun()) {
                                    beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);

                                    if (beanList.get(position).getErrorCount() >= 3) {
                                        startMediaPlayer();
                                        //显示错误图片
                                        beanList.get(position).setIsRun(false);
                                        txtview_errorstatus.setVisibility(View.VISIBLE);
                                    } else {

                                        txtview_errorstatus.setVisibility(View.GONE);
                                    }
                                    setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                }
                            }
                        });
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //判断网络信息
                                if (beanList.get(position).isRun()) {
                                    if (isNetworkConnected(mActivity)) {

                                        beanList.get(position).setCountSize(beanList.get(position).getCountSize() + 1);

                                        setSucessData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);

                                        doHttpRequest(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                    } else {
                                        beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);

                                        if (beanList.get(position).getErrorCount() >= 3) {
                                            startMediaPlayer();
                                            beanList.get(position).setIsRun(false);
                                            //显示错误图片
                                            txtview_errorstatus.setVisibility(View.VISIBLE);
                                        } else {
                                            txtview_errorstatus.setVisibility(View.GONE);
                                        }
                                        setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                    }
                                }
                            }
                        });
                    }
                    dis.close();
                    urlCon.disconnect();

                } catch (Exception e) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (beanList.get(position).isRun()) {
                                beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);

                                if (beanList.get(position).getErrorCount() >= 3) {
                                    startMediaPlayer();
                                    beanList.get(position).setIsRun(false);
                                    //显示错误图片

                                    txtview_errorstatus.setVisibility(View.VISIBLE);
                                } else {

                                    txtview_errorstatus.setVisibility(View.GONE);
                                }
                                setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);

                            }
                        }
                    });
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void setSucessData(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus, final int position) {
        if ((beanList.get(position).CountSize + beanList.get(position).getErrorCount()) > 0) {
            txtview_count.setVisibility(View.VISIBLE);
        } else {
            txtview_count.setVisibility(View.VISIBLE);
        }
        if (beanList.get(position).CountSize > 0) {
            txtview_sucess.setVisibility(View.VISIBLE);
        } else {
            txtview_sucess.setVisibility(View.GONE);
        }
        txtview_sucess.setText("正常:" + df.format(beanList.get(position).getCountSize()));
        txtview_count.setText("总计:" + df.format(beanList.get(position).getErrorCount() + beanList.get(position).getCountSize()));
    }

    /**
     * 设置参数
     *
     * @param txtview_sucess
     * @param txtview_error
     * @param txtview_count
     * @param txtview_errorstatus
     * @param position
     */
    public void setErrorData(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus, final int position) {

        if (beanList.get(position).getErrorCount() > 0) {
            txtview_error.setVisibility(View.VISIBLE);
        } else {
            txtview_error.setVisibility(View.GONE);
        }
        if ((beanList.get(position).CountSize + beanList.get(position).getErrorCount()) > 0) {
            txtview_count.setVisibility(View.VISIBLE);
        } else {
            txtview_count.setVisibility(View.VISIBLE);
        }


        if (beanList.get(position).CountSize > 0) {
            txtview_sucess.setVisibility(View.VISIBLE);
        } else {
            txtview_sucess.setVisibility(View.GONE);
        }
        txtview_error.setText("异常:" + df.format(beanList.get(position).getErrorCount()));
        txtview_count.setText("总计:" + df.format(beanList.get(position).getErrorCount() + beanList.get(position).getCountSize()));
    }


    public void startMediaPlayer() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaPlayer mediaPlayer = MediaPlayer.create(mActivity, R.raw.voip_ring_called);

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

    public void doHttpRequest_addHead(final TextView txtview_sucess, final TextView txtview_error, final TextView txtview_count, final TextView txtview_errorstatus, final int position) {
        new Thread() {
            @Override
            public void run() {

                try {
                    URL url = new URL("http://" + beanList.get(position).getUrl());
                    final HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setConnectTimeout(30000);
                    urlCon.setReadTimeout(30000);
                    //返回打开连接读取的输入流
                    DataInputStream dis = new DataInputStream(urlCon.getInputStream());
                    //判断是否正常响应数据
                    if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (beanList.get(position).isRun()) {
                                    beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);

                                    if (beanList.get(position).getErrorCount() >= 3) {
                                        startMediaPlayer();
                                        beanList.get(position).setIsRun(false);
                                        //显示错误图片

                                        txtview_errorstatus.setVisibility(View.VISIBLE);
                                    } else {

                                        txtview_errorstatus.setVisibility(View.GONE);
                                    }
                                    setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                }
                            }
                        });
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //判断网络信息
                                if (beanList.get(position).isRun()) {
                                    if (isNetworkConnected(mActivity)) {

                                        beanList.get(position).setCountSize(beanList.get(position).getCountSize() + 1);
                                        setSucessData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                        doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);

                                    } else {
                                        beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);
                                        if (beanList.get(position).getErrorCount() >= 3) {
                                            startMediaPlayer();
                                            beanList.get(position).setIsRun(false);
                                            //显示错误图片
                                            txtview_errorstatus.setVisibility(View.VISIBLE);
                                        } else {
                                            txtview_errorstatus.setVisibility(View.GONE);
                                        }
                                        setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                    }

                                }
                            }
                        });
                    }
                    dis.close();
                    urlCon.disconnect();

                } catch (Exception e) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (beanList.get(position).isRun()) {
                                beanList.get(position).setErrorCount(beanList.get(position).getErrorCount() + 1);


                                if (beanList.get(position).getErrorCount() >= 3) {
                                    startMediaPlayer();
                                    beanList.get(position).setIsRun(false);
                                    //显示错误图片
                                    txtview_errorstatus.setVisibility(View.VISIBLE);
                                } else {

                                    txtview_errorstatus.setVisibility(View.GONE);
                                }
                                setErrorData(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);
                                doHttpRequest_addHead(txtview_sucess, txtview_error, txtview_count, txtview_errorstatus, position);

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

}
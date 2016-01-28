package myxgpush.snscity.com.myapplication;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends BaseActivity implements View.OnClickListener {

    TextView btn_back;
    TextView btn_save;

    TextView txt_name;
    TextView txt_url;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        dbHelper = new DBHelper(mContext);
        initView();

    }

    public void initView() {
        btn_back = (TextView) findViewById(R.id.btn_back);
        btn_save = (TextView) findViewById(R.id.btn_save);
        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_url = (TextView) findViewById(R.id.txt_url);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_save:
                // 保存

                if (txt_name.getText().toString().isEmpty()) {
                    Toast.makeText(AddActivity.this, "请输入备注", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txt_url.getText().toString().isEmpty()) {
                    Toast.makeText(AddActivity.this, "请输入URL", Toast.LENGTH_SHORT).show();
                    return;
                }
                //保存至数据库中
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", txt_name.getText().toString());
                contentValues.put("url", txt_url.getText().toString());
                dbHelper.addData(contentValues);

                HttpBean bean = new HttpBean();
                bean.setName(txt_name.getText().toString());
                bean.setUrl(txt_url.getText().toString());
                bean.setIsRun(false);

//                Gson gson = new Gson();
//                EventService.getInstance().signEvent(EVENT_NOTIFYCATION, gson.toJson(bean), null);
                finish();
                break;
        }
    }
}

package myxgpush.snscity.com.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    DBConnection helper;
    static Context context;

    public DBHelper(Context _ctx) {
        helper = new DBConnection(_ctx);
        this.context = _ctx;
    }

    public DBHelper() {
    }

    /**
     * 删除表
     *
     * @param TB_NAME
     */
    public void DropTable(String TB_NAME) {
        try {

            SQLiteDatabase db = helper.getWritableDatabase();
            String sql = "DROP TABLE " + TB_NAME;
            db.execSQL(sql);
        } catch (Exception ex) {

        }
    }

    /**
     * 删除数据库名
     *
     * @param ctx
     * @param DATABASE_NAME
     */
    public void DropDatabase(Context ctx, String DATABASE_NAME) {
        try {
            ctx.deleteDatabase(DATABASE_NAME);
        } catch (Exception ex) {

        }
    }

    /**
     * 删除数据库
     *
     * @param DBName
     */
    public void delDB(String DBName) {

        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DROP Database " + DBName);
        db.close();
    }

    public static class DBConnection extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "SP.db";
        private static final int DATABASE_VERSION = 1;

        private DBConnection(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {

                db.execSQL("create table HttpBean ( Id Integer not null, name varchar(60) not null, url varchar(30) not null,constraint PK_DEALERPHONE primary key (Id));");


                Toast.makeText(context, "创建数据成功", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, "创建数据失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            // String alter_sql = "ALTER TABLE movement";
            // db.execSQL(alter_sql);

        }

    }

    /**
     * 修该数据
     *
     * @param values
     * @param where
     * @param whereArgs
     */
    public void update(String tableName, ContentValues values, String where,
                       String[] whereArgs) {

        SQLiteDatabase db = helper.getWritableDatabase();
        db.update(tableName, values, where, whereArgs);
        db.close();
    }

    /**
     * @param Id 参数名
     * @return
     */
    public HttpBean selectHttpBean(String Id) {

        HttpBean httpBean = null;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from  HttpBean where Id =?", new String[]{Id});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                httpBean = new HttpBean();
                httpBean.setName(cursor.getString(cursor
                        .getColumnIndex("name")));
                httpBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));

                cursor.close();
            }
        }
        db.close();
        return httpBean;
    }

    public List<HttpBean> selectHttpBeanList() {

        List<HttpBean> tableName_list = new ArrayList<HttpBean>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from  HttpBean order by Id desc",
                new String[]{});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HttpBean httpBean = new HttpBean();
                httpBean.setName(cursor.getString(cursor
                        .getColumnIndex("name")));
                httpBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                httpBean.setIsRun(true);
                tableName_list.add(httpBean);
            }
            cursor.close();
        }
        db.close();
        return tableName_list;
    }

    /**
     * 公共方法
     * 保存数据
     *
     * @param values
     */
    public void addData(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert("HttpBean", null, values);
        db.close();

    }

    /**
     * 公共方法
     * 删除数据库数据
     */
    public void deleteData(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from HttpBean where name = '"+name+"'");
        db.close();
    }

}
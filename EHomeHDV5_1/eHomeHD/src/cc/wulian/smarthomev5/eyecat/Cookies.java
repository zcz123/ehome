package cc.wulian.smarthomev5.eyecat;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.eques.icvss.api.ICVSSUserInstance;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import cc.wulian.smarthomev5.bean.Session;

/**
 * Created by Administrator on 2017/3/28.
 */

public class Cookies {
    public static Session session;
    public static void saveSession(Context context, Session s) {
        String bid = s.getBid();
        String uid = s.getUid();
        if (bid.equals("") || uid.equals("")) {
            session = null;
            return;
        } else {
            if (session == null)
                session = new Session();
        }
        session.setBid(bid);
        session.setUid(uid);
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("session", Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.putString("bid", bid);
        editor.putString("uid", uid);
        editor.commit();

    }

    public static void loadSession(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("session", Context.MODE_APPEND);
        String bid = pref.getString("bid", "");
        String uid = pref.getString("uid", "");
        if (bid.equals("") || uid.equals("")) {
            session = null;
        } else {
            if (session == null)
                session = new Session();
            session.setBid(bid);
            session.setUid(uid);
        }
    }

}

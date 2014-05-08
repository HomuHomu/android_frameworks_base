package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.util.Slog;

class FeliCa_Service extends Binder
{
  private static final String TAG = "FeliCaService";
  private static boolean mDebugFlag = false;
  Runnable execute_wait = new Runnable()
  {
    public void run()
    {
      FeliCa_Service.Debug.d("FeliCaService", "Start FeliCaService thread.");
      if (!FeliCa_Service.this.itrOpen())
      {
        FeliCa_Service.Debug.d("FeliCaService", "itrOpen false.");
        return;
      }
      while (true)
      {
        FeliCa_Service.Debug.d("FeliCaService", "waitevent call.");
        int i = FeliCa_Service.this.waitevent();
        FeliCa_Service.Debug.d("FeliCaService", "waitevent return=" + i);
        if (i == 601)
          FeliCa_Service.this.handler.post(FeliCa_Service.this.mfcIntent);
      }
    }
  };
  private Handler handler = new Handler();
  private Context mContext;
  private final Runnable mfcIntent = new Runnable()
  {
    public void run()
    {
      Intent localIntent = new Intent();
      localIntent.setClassName("com.felicanetworks.mfc", "com.felicanetworks.adhoc.AdhocReceiver");
      localIntent.setFlags(268435456);
      FeliCa_Service.this.mContext.startActivity(localIntent);
    }
  };

  static
  {
    System.loadLibrary("felicasrv_jni");
  }

  public FeliCa_Service(Context paramContext)
  {
    this.mContext = paramContext;
    Debug.d("FeliCaService", "Start FeliCaService.");
    new Thread(this.execute_wait).start();
  }

  public native boolean itrOpen();

  public void onDaemonConnected()
  {
    Debug.d("FeliCaService", "onDaemonConnected");
  }

  public boolean onEvent(int paramInt, String paramString, String[] paramArrayOfString)
  {
    Debug.d("FeliCaService", "onEvent(code=" + paramInt + ")");
    Intent localIntent = new Intent();
    if (paramInt == 601)
    {
      localIntent.setClassName("com.felicanetworks.mfc", "com.felicanetworks.adhoc.AdhocReceiver");
      localIntent.setFlags(268435456);
      this.mContext.startActivity(localIntent);
    }
    return true;
  }

  public native int waitevent();

  private static class Debug
  {
    public static int d(String paramString1, String paramString2)
    {
      boolean bool = FeliCa_Service.mDebugFlag;
      int i = 0;
      if (bool)
        i = Slog.d(paramString1, paramString2);
      return i;
    }

    public static int e(String paramString1, String paramString2)
    {
      boolean bool = FeliCa_Service.mDebugFlag;
      int i = 0;
      if (bool)
        i = Slog.e(paramString1, paramString2);
      return i;
    }
  }

  class FeliCa_Response_Code
  {
    public static final int INT_L = 601;

    FeliCa_Response_Code()
    {
    }
  }
}


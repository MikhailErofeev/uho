package mikhailerofeev.com.github.uho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;


public class DialerActivity extends Activity {

  public static final String DEBUG = "debug";
  public static final String ERROR = "error";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dialer);
    performDial("0500");
  }


  private void performDial(String numberString) {
    if (!numberString.equals("")) {
      Uri number = Uri.parse("tel:" + numberString);
      Intent dial = new Intent(Intent.ACTION_CALL, number);
      TelephonyManager telManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
      PhoneStateListener phoneStateListener = new CallListener();
      telManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

      startActivity(dial);
    }
  }

  MediaRecorder callRecorder = null;

  private void callAndCapture() {
    if (callRecorder == null) {
      callRecorder = createAndPrepareRecorder();
    } else {
      callRecorder.reset();
      prepareRecorder(callRecorder);
    }
    try {
      callRecorder.start();
    } catch (IllegalStateException e) {
      Log.e(null, null, e);
      throw e;
    }
  }

  private MediaRecorder createAndPrepareRecorder() {
    final MediaRecorder callRecorder = new MediaRecorder();
    prepareRecorder(callRecorder);

    try {
      callRecorder.prepare();
    } catch (IllegalStateException e) {
      System.out.println("Error is happened here in Prepare Method1");
      e.printStackTrace();
    } catch (IOException e) {

      //throwing I/O Exception
      System.out.println("Error is happened here in Prepare Method2");
      e.printStackTrace();
    }
    return callRecorder;
  }

  private void prepareRecorder(MediaRecorder callRecorder) {
    try {
      callRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
      callRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      callRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
      String filesDir = getApplicationContext().getCacheDir().getAbsolutePath();
      ;
      File file = new File(filesDir + "/audio.3gp");
      file.delete();
      file.createNewFile();
      if (!file.exists()) {
        throw new IllegalStateException();
      }
      callRecorder.setOutputFile(file.getPath());
    } catch (Exception e) {
      Log.e(ERROR, "create recorder fail", e);
      throw new RuntimeException(e);
    }
  }

  private boolean offHookActive = false;
  static int listenersIds = 0; //debug, todo remove

  private class CallListener extends PhoneStateListener {
    int id = listenersIds++;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
      if (TelephonyManager.CALL_STATE_RINGING == state) {
        Log.i(DEBUG, "RINGING, number: " + incomingNumber);
      }
      if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
        offHookActive = true;
        callAndCapture();
        //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
        Log.i(DEBUG, "OFFHOOK");
      }
      if (TelephonyManager.CALL_STATE_IDLE == state) {
        if (offHookActive) {
          callRecorder.stop();
        }
        offHookActive = false;
        //when this state occurs, and your flag is set, restart your app
        Log.i(DEBUG, "IDLE");
      }
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_dialer, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}

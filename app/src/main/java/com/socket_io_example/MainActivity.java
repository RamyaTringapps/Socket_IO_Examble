package com.socket_io_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements ServerTaskListener {

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mSocket = IO.socket("http://108.59.82.80:8080/");
            mSocket.connect();

            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("message", onNewMessage);
            Button bt = (Button) findViewById(R.id.send);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SocketAsyncTask(MainActivity.this);
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    private void attemptSend(JSONObject object1) {
        try {

            if (object1 != null) {
                JSONObject object = new JSONObject();
                object.put("userId", object1.get("userId"));
                object.put("channelToken", object1.get("channelToken"));
                object.put("socketId", mSocket.id());
                mSocket.emit(Socket.EVENT_CONNECT, object);
                Log.i("message ", "" + mSocket.id());
            }

        } catch (JSONException e) {
            Log.e("error ", e.getMessage());
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "connect", Toast.LENGTH_LONG).show();
                    JSONObject object = (JSONObject) args[0];
                    mSocket.emit("addUser", object);
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "disconnect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "error_connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject data = (JSONObject) args[0];
                        Toast.makeText(getApplicationContext(),
                                data.toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("Error message", e.getMessage());
                        return;
                    }

                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskSuccess(Object object) {
        JSONObject object1 = (JSONObject) object;
        attemptSend(object1);
    }

    @Override
    public void onTaskFailure(String message) {
        Log.e("task failed", message);
    }
}

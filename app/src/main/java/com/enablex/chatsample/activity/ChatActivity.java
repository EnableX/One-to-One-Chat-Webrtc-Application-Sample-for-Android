package com.enablex.chatsample.activity;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.enablex.chatsample.R;
import com.enablex.chatsample.adapter.ChatAdapter;
import com.enablex.chatsample.model.MessageDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import enx_rtc_android.Controller.EnxReconnectObserver;
import enx_rtc_android.Controller.EnxRoom;
import enx_rtc_android.Controller.EnxRoomObserver;
import enx_rtc_android.Controller.EnxRtc;
import enx_rtc_android.Controller.EnxStream;
import enx_rtc_android.Controller.EnxStreamObserver;


public class ChatActivity extends AppCompatActivity
        implements EnxRoomObserver, EnxStreamObserver, View.OnClickListener, EnxReconnectObserver {
    EnxRtc enxRtc;
    String token;
    String name;
    EnxRoom enxRooms;
    EnxStream localStream;
    ImageButton btn_send;
    EditText txt_msgToSend;
    RecyclerView listChat;
    RelativeLayout txtSendView;
    public ArrayList<MessageDetails> arrayListChat;
    ChatAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getPreviousIntent();
        initialize();
    }

    public JSONObject getReconnectInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("allow_reconnect", true);
            jsonObject.put("number_of_attempts", 3);
            jsonObject.put("timeout_interval", 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void sendTextMessage(String txtMessage) {
        if (enxRooms != null) {
            try {
                enxRooms.sendMessage(txtMessage, true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initialize() {
        setUI();
        setClickListener();
        getSupportActionBar().setTitle("Chat App");
        arrayListChat = new ArrayList<>();
        adapter = new ChatAdapter(this, arrayListChat);
        listChat.setLayoutManager(new LinearLayoutManager(this));
        listChat.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        enxRtc = new EnxRtc(this, this, this);
        localStream = enxRtc.joinRoom(token, getLocalStreamJsonObject(), getReconnectInfo(), new JSONArray());
    }

    private void setClickListener() {
        btn_send.setOnClickListener(this);
    }

    private void setUI() {
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        txt_msgToSend = (EditText) findViewById(R.id.txt_msgTosend);
        listChat = (RecyclerView) findViewById(R.id.listChat);
        txtSendView = (RelativeLayout) findViewById(R.id.RL_txtSend);
        txtSendView.setVisibility(View.VISIBLE);
    }

    private JSONObject getLocalStreamJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("audio", false);
            jsonObject.put("video", false);
            jsonObject.put("data", true);
            jsonObject.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void getPreviousIntent() {
        if (getIntent() != null) {
            token = getIntent().getStringExtra("token");
            name = getIntent().getStringExtra("name");
        }
    }

    @Override
    public void onRoomConnected(EnxRoom enxRoom, JSONObject jsonObject) {
        //received when user connected with Enablex room
        enxRooms = enxRoom;
        enxRooms.setReconnectObserver(this);
    }

    @Override
    public void onRoomError(JSONObject jsonObject) {
        //received when any error occurred while connecting to the Enablex room
        Toast.makeText(ChatActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUserConnected(JSONObject jsonObject) {
        // received when a new remote participant joins the call
    }

    @Override
    public void onUserDisConnected(JSONObject jsonObject) {
        // received when a  remote participant left the call
        roomDisconnect();
    }

    private void roomDisconnect() {
        if (enxRooms != null) {
            enxRooms.disconnect();
        } else {
            this.finish();
        }
    }

    @Override
    public void onPublishedStream(EnxStream enxStream) {
        //received when audio video published successfully to the other remote users
    }

    @Override
    public void onUnPublishedStream(EnxStream enxStream) {
        //received when audio video unpublished successfully to the other remote users
    }

    @Override
    public void onStreamAdded(EnxStream enxStream) {
        //received when a new stream added
    }

    @Override
    public void onSubscribedStream(EnxStream enxStream) {
        //received when a remote stream subscribed successfully
    }

    @Override
    public void onUnSubscribedStream(EnxStream enxStream) {
        //received when a remote stream unsubscribed successfully
    }

    @Override
    public void onRoomDisConnected(JSONObject jsonObject) {
        //received when Enablex room successfully disconnected
        this.finish();
    }


    @Override
    public void onEventError(JSONObject jsonObject) {
        //received when any error occurred for any room event
    }

    @Override
    public void onEventInfo(JSONObject jsonObject) {
        // received for different events update
    }

    @Override
    public void onNotifyDeviceUpdate(String s) {
        // received when when new media device changed
    }

    @Override
    public void onAcknowledgedSendData(JSONObject jsonObject) {
        // received your chat data successfully sent to the other end
    }

    @Override
    public void onMessageReceived(JSONObject jsonObject) {
        // received when chat data received at room
        try {
            final String Id = jsonObject.getString("senderId");
            final String textMessage = jsonObject.getString("message");
            final String userName = jsonObject.getString("sender");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessageDetails messageDetails = new MessageDetails();
                    messageDetails.setMsgId(Id);
                    messageDetails.setReceiveTime(getCurrentTime());
                    messageDetails.setUserName(userName);
                    messageDetails.setBody(textMessage);
                    messageDetails.setFromId(Id);
                    messageDetails.setReceived(true);
                    if (arrayListChat == null) {
                        arrayListChat = new ArrayList<>();
                    }
                    arrayListChat.add(messageDetails);
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserDataReceived(JSONObject jsonObject) {

    }

    @Override
    public void onUserStartTyping(boolean b) {

    }


    @Override
    public void onSwitchedUserRole(JSONObject jsonObject) {
        // received when user switch their role (from moderator  to participant)
    }

    @Override
    public void onUserRoleChanged(JSONObject jsonObject) {
        // received when user role changed successfully
    }

    @Override
    public void onConferencessExtended(JSONObject jsonObject) {

    }

    @Override
    public void onConferenceRemainingDuration(JSONObject jsonObject) {

    }

    @Override
    public void onAckDropUser(JSONObject jsonObject) {

    }

    @Override
    public void onAckDestroy(JSONObject jsonObject) {

    }

    @Override
    public void onAudioEvent(JSONObject jsonObject) {
        //received when audio mute/unmute happens
    }

    @Override
    public void onVideoEvent(JSONObject jsonObject) {
        //received when video mute/unmute happens
    }

    @Override
    public void onReceivedData(JSONObject jsonObject) {
        //received when chat data received at room level
    }

    @Override
    public void onRemoteStreamAudioMute(JSONObject jsonObject) {
        //received when any remote stream mute audio
    }

    @Override
    public void onRemoteStreamAudioUnMute(JSONObject jsonObject) {
        //received when any remote stream unmute audio
    }

    @Override
    public void onRemoteStreamVideoMute(JSONObject jsonObject) {
        //received when any remote stream mute video
    }

    @Override
    public void onRemoteStreamVideoUnMute(JSONObject jsonObject) {
        //received when any remote stream unmute video
    }

    @Override
    public void onAckPinUsers(JSONObject jsonObject) {

    }

    @Override
    public void onAckUnpinUsers(JSONObject jsonObject) {

    }

    @Override
    public void onPinnedUsers(JSONObject jsonObject) {

    }

    @Override
    public void onRoomAwaited(EnxRoom enxRoom, JSONObject jsonObject) {

    }

    @Override
    public void onUserAwaited(JSONObject jsonObject) {

    }

    @Override
    public void onAckForApproveAwaitedUser(JSONObject jsonObject) {

    }

    @Override
    public void onAckForDenyAwaitedUser(JSONObject jsonObject) {

    }

    @Override
    public void onAckAddSpotlightUsers(JSONObject jsonObject) {

    }

    @Override
    public void onAckRemoveSpotlightUsers(JSONObject jsonObject) {

    }

    @Override
    public void onUpdateSpotlightUsers(JSONObject jsonObject) {

    }

    @Override
    public void onRoomBandwidthAlert(JSONObject jsonObject) {

    }

    @Override
    public void onStopAllSharingACK(JSONObject jsonObject) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:

                if (txt_msgToSend.getText().toString().length() != 0) {
                    MessageDetails messageDetails = getMessageOBJ(txt_msgToSend.getText().toString().trim());
                    sendTextMessage(txt_msgToSend.getText().toString().trim());
                    txt_msgToSend.setText("");
                    txt_msgToSend.clearFocus();
                    arrayListChat.add(messageDetails);
                    adapter.notifyDataSetChanged();
                }
                txt_msgToSend.performClick();
                txt_msgToSend.requestFocus();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        roomDisconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localStream != null) {
            localStream.detachRenderer();
        }
        if (enxRooms != null) {
            enxRooms = null;
        }
        if (enxRtc != null) {
            enxRtc = null;
        }
    }

    public MessageDetails getMessageOBJ(String text) {
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setBody(text);
        messageDetails.setFromId("");
        messageDetails.setUserName(name);
        messageDetails.setReceived(false);
        messageDetails.setMsgId("");
        messageDetails.setReceiveTime(getCurrentTime());
        return messageDetails;
    }

    public static String getCurrentTime() {
        Date date = new Date();
        String strDateFormat = "HH:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(date);
    }

    @Override
    public void onReconnect(String message) {
        // received when room tries to reconnect due to low bandwidth or any connection interruption
        try {
            if (message.equalsIgnoreCase("Reconnecting")) {
                progressDialog.setMessage("Wait, Reconnecting");
                progressDialog.show();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserReconnectSuccess(EnxRoom enxRoom, JSONObject jsonObject) {
        // received when reconnect successfully completed
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(this, "Reconnect Success", Toast.LENGTH_SHORT).show();
    }
}

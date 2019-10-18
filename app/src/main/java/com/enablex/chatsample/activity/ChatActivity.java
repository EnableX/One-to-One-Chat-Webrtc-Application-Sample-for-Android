package com.enablex.chatsample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.enablex.chatsample.R;
import com.enablex.chatsample.adapter.ChatAdapter;
import com.enablex.chatsample.model.MessageDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import enx_rtc_android.Controller.EnxRoom;
import enx_rtc_android.Controller.EnxRoomObserver;
import enx_rtc_android.Controller.EnxRtc;
import enx_rtc_android.Controller.EnxStream;
import enx_rtc_android.Controller.EnxStreamObserver;


public class ChatActivity extends AppCompatActivity
        implements EnxRoomObserver, EnxStreamObserver, View.OnClickListener {
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
        enxRtc = new EnxRtc(this, this, this);
        localStream = enxRtc.joinRoom(token, getLocalStreamJsonObject(), getReconnectInfo(), null);
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
            jsonObject.put("maxVideoBW", 400);
            jsonObject.put("minVideoBW", 300);
            JSONObject videoSize = new JSONObject();
            videoSize.put("minWidth", 720);
            videoSize.put("minHeight", 480);
            videoSize.put("maxWidth", 1280);
            videoSize.put("maxHeight", 720);
            jsonObject.put("videoSize", videoSize);
            jsonObject.put("audioMuted", false);
            jsonObject.put("videoMuted", false);
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
        enxRooms = enxRoom;
    }

    @Override
    public void onRoomError(JSONObject jsonObject) {
        Toast.makeText(ChatActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUserConnected(JSONObject jsonObject) {
    }

    @Override
    public void onUserDisConnected(JSONObject jsonObject) {
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
    }

    @Override
    public void onUnPublishedStream(EnxStream enxStream) {

    }

    @Override
    public void onStreamAdded(EnxStream enxStream) {
        if (enxStream != null) {
            enxRooms.subscribe(enxStream);
        }
    }

    @Override
    public void onSubscribedStream(EnxStream enxStream) {
    }

    @Override
    public void onUnSubscribedStream(EnxStream enxStream) {

    }

    @Override
    public void onRoomDisConnected(JSONObject jsonObject) {
        this.finish();
    }

    @Override
    public void onActiveTalkerList(JSONObject jsonObject) {
    }

    @Override
    public void onEventError(JSONObject jsonObject) {
    }

    @Override
    public void onEventInfo(JSONObject jsonObject) {

    }

    @Override
    public void onNotifyDeviceUpdate(String s) {

    }

    @Override
    public void onAcknowledgedSendData(JSONObject jsonObject) {

    }

    @Override
    public void onReceivedChatDataAtRoom(JSONObject jsonObject) {
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
    public void onSwitchedUserRole(JSONObject jsonObject) {

    }

    @Override
    public void onUserRoleChanged(JSONObject jsonObject) {

    }

    @Override
    public void onAudioEvent(JSONObject jsonObject) {
    }

    @Override
    public void onVideoEvent(JSONObject jsonObject) {
    }

    @Override
    public void onReceivedData(JSONObject jsonObject) {
    }

    @Override
    public void onRemoteStreamAudioMute(JSONObject jsonObject) {

    }

    @Override
    public void onRemoteStreamAudioUnMute(JSONObject jsonObject) {

    }

    @Override
    public void onRemoteStreamVideoMute(JSONObject jsonObject) {

    }

    @Override
    public void onRemoteStreamVideoUnMute(JSONObject jsonObject) {

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

}

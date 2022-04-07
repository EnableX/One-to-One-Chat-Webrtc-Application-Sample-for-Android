# 1-to-1 RTC: A Sample Chat App with EnableX Android Toolkit

This is a Sample Chat Application that demonstrates the use of EnableX platform Server APIs (https://www.enablex.io/developer/video-api/server-api) and Android Toolkit (https://www.enablex.io/developer/video-api/client-api/android-toolkit) to build 1-to-1 Chat Application.  It allows developers to ramp up on app development by hosting on their own devices.

This App creates a virtual Room on the fly  hosted on the Enablex platform using REST calls and uses the Room credentials (i.e. Room Id) to connect to the virtual Room as a mobile client.  The same Room credentials can be shared with others to join the same virtual Room to carry out an RTC session.

> EnableX Developer Center: https://developer.enablex.io/


## 1. How to get started

### 1.1 Pre-Requisites

#### 1.1.1 App Id and App Key 

* Register with EnableX [https://portal.enablex.io/cpaas/trial-sign-up/] 
* Login to the EnableX Portal
* Create your Application Key
* Get your App ID and App Key delivered to your Email


#### 1.1.2 Sample Android Client

* Clone or download this Repository [https://github.com/EnableX/One-to-One-Chat-Webrtc-Application-Sample-for-Android.git]


#### 1.1.3 Test Application Server

You need to set up an Application Server to provision Web Service API for your Android Application to communicate enabling Video Session. 

To help you to try our Android Application quickly, without having to set up Applciation Server, this Application is shipped pre-configured to work in a "try" mode with EnableX hosted Application Server i.e. https://demo.enablex.io. 

Our Application Server restricts a single Session Duations to 10 minutes, and allows 1 moderator and not more than 3 participants in a Session.

Once you tried EnableX Android Sample Application, you may need to setup your own  Application Server and verify your Application to work with your Application Server.  Refer to point 2 for more details on this.


#### 1.1.4 Configure Android Client

* Open the App
* Go to WebConstants and change the following:
```
 /* To try the App with Enablex Hosted Service you need to set the kTry = true When you setup your own Application Service, set kTry = false */
     
     public  static  final  boolean kTry = true;
     
 /* Your Web Service Host URL. Keet the defined host when kTry = true */
 
     String kBaseURL = "https://demo.enablex.io/"
     
 /* Your Application Credential required to try with EnableX Hosted Service
     When you setup your own Application Service, remove these */
     
     String kAppId = ""  
     String kAppkey = ""  
     
 ```

### 1.2 Test

#### 1.2.1 Open the App

* Open the App in your Device. You get a form to enter the credentials i.e. Name & Room Id.
* You need to create a Room by clicking the "Create Room" button.
* Once the Room Id is created, you can use it and share with others to connect to the Virtual Room to carry out an RTC Session either as a Moderator or a Participant (Choose applicable Role in the Form).

Note: Only one user with Moderator Role allowed to connect to a Virtual Room while trying with EnableX Hosted Service. Your Own Application Server may allow upto 5 Moderators.

Note:- In case of emulator/simulator your local stream will not create. It will create only on real device.

## 2 Setup Your Own Application Server

You may need to set up your own Application Server after you tried the Sample Application with EnableX hosted Server. We have differnt variants of Appliciation Server Sample Code. Pick one in your preferred language and follow instructions given in respective README.md file.

* NodeJS: [https://github.com/EnableX/Video-Conferencing-Open-Source-Web-Application-Sample.git]
* PHP: [https://github.com/EnableX/Group-Video-Call-Conferencing-Sample-Application-in-PHP]

Note the following:
* You need to use App ID and App Key to run this Service.
* Your Android Client End Point needs to connect to this Service to create Virtual Room and Create Token to join the session.
* Application Server is created using EnableX Server API, a Rest API Service helps in provisioning, session access and post-session reporting.  

To know more about Server API, go to:
https://developer.enablex.io/latest/server-api/


## 3. Android Toolkit

This Sample Applcation uses EnableX Android Toolkit to communicate with EnableX Servers to initiate and manage Real Time Communications. Please update your Application with latest version of EnableX Android Toolkit as and when a new release is avaialble.

* Documentation: https://www.enablex.io/developer/video-api/client-api/android-toolkit/
* Download Toolkit: https://www.enablex.io/developer/video-api/client-api/android-toolkit/


## 4. Application Walk-through

### 4.1 Create Token

We create a Token for a Room Id to get connected to EnableX Platform to connect to the Virtual Room to carry out an RTC Session.

To create Token, we make use of Server API. Refer following documentation:
https://www.enablex.io/developer/video-api/server-api/rooms-route/#create-token


### 4.2 Connect to a Room, Initiate & Publish the Stream

We use the Token to get connected to the Virtual Room. Once connected, we intiate local stream and publish into the room. Refer following documentation for this process:
https://www.enablex.io/developer/video-api/client-api/android-toolkit/publishing-local-stream/



### 4.3 Send Data

Use the below method to send the chat messages using EnxRoom Object.

```
enxRoom.sendMessage(message, true, null);

  ```
More on EnxRoom: https://www.enablex.io/developer/video-api/client-api/android-toolkit/enxroom/

### 4.4 Handle Server Events

EnableX Platform will emit back many events related to the ongoing RTC Session as and when they occur implicitly or explicitly as a result of user interaction. We use Call Back Methods to handle all such events.

```
/* Example of Call Back Methods */

/* Call Back Method: onRoomConnected
Handles successful connection to the Virtual Room */

void onRoomConnected(EnxRoom enxRoom, JSONObject roomMetaData){
    /* You may initiate and publish stream */
}


/* Call Back Method: onRoomError
 Error handler when room connection fails */

void onRoomError(JSONObject jsonObject){

}


/* Call Back Method: onReceivedChatDataAtRoom
 To handle received chat */

 @Override
    public void onReceivedChatDataAtRoom(JSONObject jsonObject) {
        /*handle data received*/
    }
```

## 5. Trial

EnableX provides hosted Demo Application Server of different use-case for you to try out.

1. Try a quick Video Call: https://demo.enablex.io/
2. Sign up for a free trial https://portal.enablex.io/cpaas/trial-sign-up/

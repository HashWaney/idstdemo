package com.alibaba.idst.demo;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.idst.demo.shopping.ShoppingCallBack;
import com.alibaba.idst.demo.shopping.ShoppingUtil;
import com.alibaba.idst.statistics.ProductInfo;
import com.alibaba.idst.statistics.StatisticsClient;
import com.alibaba.idst.statistics.StatisticsService;
import com.alibaba.idst.transport.Util;
import com.alibaba.nls.transport.sdk.client.SocketClient;
import com.alibaba.nls.transport.sdk.listener.IListener;
import com.alibaba.nls.transport.shoppingcart.ShoppingCartClient;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toast mToast;

    private String[] tts = new String[]{"宝宝在测试中，请勿打扰", "宝宝在测试", "测试中，不要打扰我", "离我远点，我在测试", "不要碰我，离我远点，测试中", "不要打扰宝宝测试", "不要打扰测试，否则失败了找你背锅"};

    private SocketClient socketClient = null;

    private static final Logger log = Logger.getLogger(MainActivity.class);

    TextView resultText;

    TextView ipText;

    StringBuffer stringBuffer;

    Button connectStartBtn;

    Button connectCloseBtn;

    Button dialogStartBtn;

    Button dialogStopBtn;

    Button autoBtn;

    CheckBox saveLogCheckBox;

    CheckBox isAuto;

    CheckBox isShowCost;

    TextView version;

    InetSocketAddress inetSocketAddress ;

    private final static String TAG = "MainActivity";

    private volatile long sumTime = 0;

    private volatile long currentSum = 0;

    private volatile long count = 0;

    private volatile FileOutputStream outputStream = null;

    private volatile FileOutputStream outputStreamLog = null;

    private Map<String, String> mapTime = new HashMap<>();

    private StatisticsClient statisticsClient = new StatisticsClient();

    private ShoppingCallBack shoppingCallBack = new ShoppingCallBack();

    private ShoppingCartClient shoppingCartClient = new ShoppingCartClient(shoppingCallBack);

    private boolean isFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        resultText = (TextView) findViewById(R.id.resultText);
        resultText.setMovementMethod(ScrollingMovementMethod.getInstance());
        connectStartBtn = findViewById(R.id.connectStart);
        connectCloseBtn = findViewById(R.id.connectClose);
        dialogStartBtn = findViewById(R.id.dialogStart);
        dialogStopBtn = findViewById(R.id.dialogStop);
        saveLogCheckBox = findViewById(R.id.checkBox);
        isAuto = findViewById(R.id.isAuto);
        isShowCost = findViewById(R.id.isShowCost);
        ipText = findViewById(R.id.IP);
        isShowCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShowCost.isChecked()){
                    ipText.setText("");
                }
            }
        });
        version = findViewById(R.id.version);
        connectCloseBtn.setEnabled(false);
        dialogStartBtn.setEnabled(false);
        dialogStopBtn.setEnabled(false);
        stringBuffer = new StringBuffer();
        resultText.setMovementMethod(new ScrollingMovementMethod());
        Log.d(TAG, "finish onCreate " + StatisticsService.getInstance().toString());
        createFile();
        readTts();
        version.setText("version: " + getVersionName());
    }

    public String getVersionName() {
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void readTts(){
        //获取文件在内存卡中files目录下的路径
        File file = getFilesDir();
        String filename = file.getAbsolutePath() + File.separator + "tts.txt";
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader br = new BufferedReader(fileReader);
            String str = "";
            ArrayList<String> list = new ArrayList<String>();
            while ((str = br.readLine()) != null){
                list.add(str);
            }
            Log.d(TAG, "list: " + JSONObject.toJSONString(list));
            if (list.size() > 0){
                tts = new String[list.size()];
                list.toArray(tts);
            }
            Log.d(TAG, "tts: " + JSONObject.toJSONString(tts));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static ArrayList execCmdsforResult(String cmd) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            process.getErrorStream();
            InputStream is = process.getInputStream();
            os.write((cmd + "\n").getBytes());
            os.write("exit\n".getBytes());
            os.flush();
            os.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String str = reader.readLine();
                if (str == null)
                    break;
                list.add(str);
            }
            reader.close();
            process.waitFor();
            process.destroy();
            return list;
        } catch (Exception localException) {
        }
        return list;
    }

    private void createFile(){
        //获取文件在内存卡中files目录下的路径
        File file = getFilesDir();
        execCmdsforResult("chmod -R 777 " + file.getAbsolutePath() );
        String filename = file.getAbsolutePath() + File.separator + "cost.log";
        String filenameLog = file.getAbsolutePath() + File.separator + "screen.log";
        log.info("logfile name: " + filename);
        //打开文件输出流
        try {
            outputStream = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            log.error(e.toString());
        }
        //打开文件输出流
        try {
            outputStreamLog = new FileOutputStream(filenameLog);
        } catch (FileNotFoundException e) {
            log.error(e.toString());
        }
    }

    private IListener iListener = new IListener() {
        @Override
        public void onConnected() {
            showTip("连接建立成功");
            if (isAuto.isChecked()) {
                onNuiInitalize();
                startDialog();
            }
        }

        @Override
        public void onDialogEvent(String s, String s1) {
            try {
                if (s1 == null) {
                    return;
                }
                String msg = (String.format("onDialogEvent: name:%s, payload: %s", s, s1));
                Log.d(TAG, msg);
                if (s.trim().equalsIgnoreCase("EVENT_BIO_DETECT_UPDATE")) {
                    String title = "";
                    JSONObject payload = JSONObject.parseObject(s1);
                    if (payload == null) {
                        return;
                    }
                    JSONArray sensorInfo = payload.getJSONArray("sensor_info");
                    if (sensorInfo == null || sensorInfo.size() <= 0) {
                        return;
                    } else {
                        int event = sensorInfo.getJSONObject(0).getInteger("event");
                        if (event == 3) {
                            title = "人员离开";
                        } else if (event == 1) {
                            title = "人员进入";
                        } else if (event == -1) {
                            title = "出错";
                        }
                        int value = sensorInfo.getJSONObject(0).getIntValue("value");
                        msg = String.format("%s: 距离: %d", title, value);
                    }
                } else if (s.trim().equalsIgnoreCase("EVENT_VAD_START")) {
                    mapTime.put("vad_start", String.valueOf(new Date().getTime() - currentSum));
                } else if (s.trim().equalsIgnoreCase("EVENT_ASR_PARTIAL_RESULT")) {
                    JSONObject payload = JSONObject.parseObject(s1);
                    if (payload == null) {
                        return;
                    }
                    String asr = payload.getString("result");
                    msg = String.format("asr_partial: %s", asr);
                    if (asr.length() > 0 && isFirst) {
                        isFirst = false;
                        mapTime.put("asr_start", String.valueOf(new Date().getTime() - currentSum - Long.valueOf(mapTime.get("vad_start"))));
                    }
                } else if (s.trim().equalsIgnoreCase("EVENT_ASR_RESULT")) {
                    JSONObject payload = JSONObject.parseObject(s1);
                    if (payload == null) {
                        return;
                    }
                    String asr = payload.getString("result");
                    msg = String.format("asr_finished: %s", asr);
                    mapTime.put("asr", String.valueOf(new Date().getTime() - currentSum - Long.valueOf(mapTime.get("vad"))));
                } else if (s.trim().equalsIgnoreCase("EVENT_DIALOG_RESULT")) {
                    JSONObject payload = JSONObject.parseObject(s1);
                    if (payload == null) {
                        return;
                    }
                    JSONObject result = payload.getJSONObject("result");
                    if (result == null) {
                        Log.e(TAG, "dialog result is not parsed");
                        return;
                    }
                    JSONObject actionPayload = result.getJSONObject("payload");
                    if (actionPayload == null) {
                        Log.e(TAG, "dialog action payload is not parsed");
                        return;
                    }
                    msg = "Dialog: " + actionPayload.getString("action_params");
                    mapTime.put("uds", String.valueOf(new Date().getTime() - currentSum - Long.valueOf(mapTime.get("asr")) - Long.valueOf(mapTime.get("vad"))));
                    socketClient.nuiTtsPlay("1", "1234567890", tts[Util.getRandomWithN(tts.length)]);
                } else if (s.trim().equalsIgnoreCase("EVENT_TTS_END") || s.trim().equalsIgnoreCase("EVENT_TTS_ERROR")) {
                    JSONObject payload = JSONObject.parseObject(s1);
                    mapTime.put("tts", String.valueOf(new Date().getTime() - currentSum - Long.valueOf(mapTime.get("uds")) - Long.valueOf(mapTime.get("asr")) - Long.valueOf(mapTime.get("vad"))));
                    dialogFinished();
                    startDialog();
                } else if (s.trim().equalsIgnoreCase("EVENT_VAD_END")) {
                    JSONObject payload = JSONObject.parseObject(s1);
                    mapTime.put("vad", String.valueOf(new Date().getTime() - currentSum));
                } else if (s.trim().equalsIgnoreCase("EVENT_VAD_TIMEOUT")) {
                    mapTime.put("asr", "xx");
                    mapTime.put("vad", "xx");
                    mapTime.put("uds", "xx");
                    mapTime.put("tts", "xx");
                    dialogFinished();
                    startDialog();
                } else if (s.trim().equalsIgnoreCase("EVENT_ASR_ERROR")) {
                    mapTime.put("asr", "xx");
                    mapTime.put("uds", "xx");
                    mapTime.put("tts", "xx");
                    dialogFinished();
                    startDialog();
                }
                showTip(msg);
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }

        private void startDialog() {
            if (socketClient != null && isAuto.isChecked()) {
                socketClient.nuiDialogStart(null);
            }
            count ++;
            isFirst = true;
            currentSum = new Date().getTime();
        }

        private void dialogFinished(){
            StringBuilder stringBuilder = new StringBuilder();
            for(Map.Entry<String, String> item: mapTime.entrySet()){
                stringBuilder.append(String.format("%s: %s    ", item.getKey(), item.getValue()));
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", String.valueOf(stringBuilder.toString()));
            data.putString("name", "time");
            msg.setData(data);
            handler.sendMessage(msg);
        }



        @Override
        public void onAuthEvent(String name, String payload) {
            showTip(String.format("onAuthEvent: name:%s, payload: %s", name, payload));
        }

        @Override
        public void onUnknownEvent(String name, String payload) {
            showTip(String.format("onUnknownEvent: name:%s, payload: %s", name, payload));
        }

        @Override
        public void onMessageError(String errorMsg) {
            showTip(String.format("onMessageError: %s", errorMsg));
        }

        @Override
        public void onConnectionClosed() {
            showTip("连接关闭");
        }

        @Override
        public void onBpEvent(String name, String payload) {
            showTip(String.format("onBpEvent: name:%s, payload: %s", name, payload));
        }
    };

    public void onStart(View view) {
        new Thread(networkTask).start();
        shoppingCartClient.syncAllProducts(ShoppingUtil.getAllProducts());
        shoppingCartClient.start();
    }

    private final static int MAX_LINE_COUNT = 25;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if (data.containsKey("name")){
                String val = data.getString("value");
                String text = "每轮对话平均耗时：" + val + "\r\n";
                if (isShowCost.isChecked()) {
                    ipText.setText(text);
                }
                if (outputStream != null && saveLogCheckBox.isChecked()){
                    try {
                        outputStream.write(text.getBytes("utf-8"));
                        outputStream.flush();
                    } catch (IOException e) {
                        log.error(e.toString());
                    }
                }
                return;
            }
            String val = data.getString("value");
            if (outputStreamLog != null && saveLogCheckBox.isChecked()){
                try {
                    outputStreamLog.write((val + "\n").getBytes("utf-8"));
                    outputStreamLog.flush();
                } catch (IOException e) {
                    log.error(e.toString());
                }
            }
            stringBuffer.append(val);
            stringBuffer.append("\n");
            resultText.setText(stringBuffer.toString());
//            resultText.setSelection(resultText.getText().length(), resultText.getText().length());
            int lineCount = resultText.getLineCount();
            if (lineCount > MAX_LINE_COUNT) {
                int offset = resultText.getLineCount() * resultText.getLineHeight();
                resultText.scrollTo(0, offset - resultText.getHeight() + resultText.getLineHeight());
            }
            if (stringBuffer.length() > 5000){
                stringBuffer.setLength(3000);
            }
        }
    };

    private void initSocketClient(){
        String host = "";
        host = ipText.getText().toString().trim();
        if (host == null || host.isEmpty()) {
        }
        log.info("host: " + host);
        int port = 10000;
        if (host == null || host.isEmpty()) {
            showTip("host is empty, will get ip and port automatically ");
            host = Util.getIp();
            port = 10000;
        } else {
            int index = host.indexOf(":");
            port = Integer.valueOf(host.substring(index + 1).trim());
            host = host.substring(0, index).trim();
        }
        inetSocketAddress = new InetSocketAddress(host, port);
        log.info("socketClient: " + socketClient == null);
        if (socketClient == null) {
            socketClient = SocketClient.getInstance();
        }
    }
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            initSocketClient();
            if (socketClient.start(inetSocketAddress, iListener, MainActivity.this,100000) < 0) {
                showTip("连接超时");
            } else {
                showTip("连接建立中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectStartBtn.setEnabled(false);
                                connectCloseBtn.setEnabled(true);
                                dialogStartBtn.setEnabled(true);
                            }
                        });
                    }
                }).start();
            }
        }
    };

    private void showTip(final String str) {
        Message msg = new Message();
        Bundle data = new Bundle();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String time = simpleDateFormat.format(new Date());
        data.putString("value",time + ":   " + str);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public void onClose(View view) {
        if (socketClient != null) {
            socketClient.close();
            connectStartBtn.setEnabled(true);
            connectCloseBtn.setEnabled(false);
            socketClient = null;
        }
    }

    // Dialog Control
    public void onDialogStart(View view) {
        if (socketClient != null) {
            socketClient.nuiDialogStart(null);
            dialogStopBtn.setEnabled(true);
            dialogStartBtn.setEnabled(false);
        }
    }

    public void onDialogStop(View view) {
        if (socketClient != null) {
            socketClient.nuiDialogStop();
//            dialogStopBtn.setEnabled(false);
//            dialogStartBtn.setEnabled(true);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("price", 2);
//            socketClient.nuiEventTrackerTrack(JSONObject.toJSONString(map));
        }
    }

    public void onNuiInitalize() {
        if (socketClient != null) {
            socketClient.nuiInitialize(null);
        }
    }

    public void onAutoRun(View view) {
        if (statisticsClient != null) {
            ProductInfo productInfo = new ProductInfo();
            productInfo.setProductName("统一冰红茶");
            productInfo.setNum(1);
            statisticsClient.addProduct(productInfo);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroy() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
        try {
            if (outputStreamLog != null) {
                outputStreamLog.close();
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

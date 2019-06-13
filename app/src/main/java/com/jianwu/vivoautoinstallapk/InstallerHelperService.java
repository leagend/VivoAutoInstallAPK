package com.jianwu.vivoautoinstallapk;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class InstallerHelperService extends AccessibilityService {
    private static final String TAG = "InstallerHelperService";

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onServiceConnected() {
        Log.i(TAG, "onServiceConnected");
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "onAccessibilityEvent: " + event.getPackageName());
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        if (event.getPackageName().equals("com.vivo.secime.service") || event.getPackageName().equals("com.android.packageinstaller")) {
            //vivo或oppo账号密码
            String password = (String) SharePreferencesUtils.getParam(getApplicationContext(),
                    AppConstants.KEY_PASSWORD, "");
//            Log.i(TAG, "password: " + password);
            if (!TextUtils.isEmpty(password)) {
                fillPassword(rootNode, password);
            }
        }
        findAndClickView(rootNode);
        rootNode.recycle();
    }

    /**
     * 自动填充密码
     */
    private void fillPassword(AccessibilityNodeInfo rootNode, String password) {
        AccessibilityNodeInfo editText = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (editText == null) {
            try {
                Thread.sleep(1000);
                Runtime.getRuntime().exec("input text "+password);
                Runtime.getRuntime().exec("input keyevent 61");
                Runtime.getRuntime().exec("input keyevent 61");
                Runtime.getRuntime().exec("input keyevent 66");
            } catch (Exception e) {
                return ;
            }
        }
        else {
            if ((editText.getPackageName().equals("com.bbk.account") || editText.getPackageName().equals("com.coloros.safecenter"))
                    && editText.getClassName().equals("android.widget.EditText")) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo
                        .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
                editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }
        }

        editText.recycle();
    }

    /**
     * 查找按钮并点击
     */
    private void findAndClickView(AccessibilityNodeInfo rootNode) {
        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("确定"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("继续安装"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("安装"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("打开"));

        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

        for (AccessibilityNodeInfo nodeInfo: nodeInfoList) {
            nodeInfo.recycle();
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt");
    }
}

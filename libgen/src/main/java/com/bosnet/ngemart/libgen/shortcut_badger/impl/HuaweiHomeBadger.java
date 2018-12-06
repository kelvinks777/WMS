package com.bosnet.ngemart.libgen.shortcut_badger.impl;

import com.bosnet.ngemart.libgen.shortcut_badger.Badger;
import com.bosnet.ngemart.libgen.shortcut_badger.ShortcutBadgeException;
import com.bosnet.ngemart.libgen.shortcut_badger.util.BroadcastHelper;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jason Ling
 */
public class HuaweiHomeBadger implements Badger {

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) {
        Bundle localBundle = new Bundle();
        localBundle.putString("package", context.getPackageName());
        localBundle.putString("class", componentName.getClassName());
        localBundle.putInt("badgenumber", badgeCount);
        context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.huawei.android.launcher"
        );
    }
}

package com.gin.wms.warehouse.component;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosnet.ngemart.libgen.Common;
import com.gin.ngemart.baseui.NgemartCommon;
import com.gin.wms.manager.db.data.UserData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;

import static com.bosnet.ngemart.libgen.Base64Util.ConvertStringToBitmap;

/**
 * Created by manbaul on 2/21/2018.
 */

public class LeftMenuModule {
    private final WarehouseActivity warehouseActivity;
    private final Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView ivProfilePic;
    private TextView tvUsername, tvAppVersion;

    private ILeftMenu listener;

    public LeftMenuModule(WarehouseActivity warehouseActivity, Toolbar toolbar) {
        this.warehouseActivity = warehouseActivity;
        this.toolbar = toolbar;

        initComponent();
    }

    private void initComponent() {
        drawerLayout = warehouseActivity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                warehouseActivity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = warehouseActivity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            try {
                if (listener == null)
                    return false;

                boolean result = listener.onMenuItemSelected(item.getItemId());
                drawerLayout.closeDrawer(GravityCompat.START);

                return result;
            } catch (Exception e) {
                warehouseActivity.showErrorDialog(e);
                return false;
            }
        });

        View view = navigationView.getHeaderView(0);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvAppVersion = view.findViewById(R.id.tvAppVersion);
    }

    public void setMenuResourse(int resourse) {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(resourse);
    }
    public void setAppVersion(Common.ApplicationVersion appVersion) {
        String strAppVersion;
        if (appVersion.isDebugAble)
            strAppVersion = "DEBUG: av:" + appVersion.versionName + " cv:" + Integer.toString(appVersion.versionCode);
        else
            strAppVersion = "av:" + appVersion.versionName + " cv:" + Integer.toString(appVersion.versionCode);
        tvAppVersion.setText(strAppVersion);
    }

    public void setProfileInfo(UserData userData) {
        if (userData != null) {
            tvUsername.setText(userData.fullName);
        }
    }

    public void setProfileImage(String profileImage) {
        ivProfilePic.setImageBitmap(NgemartCommon.getCircleBitmap(ConvertStringToBitmap(profileImage)));
    }

    public void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (listener != null)
                listener.onHandleBackPressed();
        }
    }

    public void setLeftMenuListener(ILeftMenu listener) {
        this.listener = listener;
    }

    public interface ILeftMenu {
        boolean onMenuItemSelected(int itemId) throws Exception;
        void onHandleBackPressed();
    }
}

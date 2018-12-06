package com.gin.wms.warehouse.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gin.wms.warehouse.base.WarehouseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/6/2018.
 */

public class HomePagerAdapter extends FragmentStatePagerAdapter {
    List<WarehouseFragment> fragmentList = new ArrayList<>();

    public HomePagerAdapter(FragmentManager fm, List<WarehouseFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public WarehouseFragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        WarehouseFragment shopFragment = (WarehouseFragment) object;
        if (fragmentList.contains(shopFragment))
            return POSITION_UNCHANGED;

        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void ReplaceFragment(int position, WarehouseFragment fragment) throws Exception {
        destroyItem(null, position, fragmentList.get(position));
        fragmentList.remove(position);
        fragmentList.add(position, fragment);
        notifyDataSetChanged();
    }

}

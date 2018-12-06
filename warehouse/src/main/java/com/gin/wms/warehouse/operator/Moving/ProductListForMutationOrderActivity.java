package com.gin.wms.warehouse.operator.Moving;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.gin.ngemart.baseui.adapter.NgemartRecyclerView;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter;
import com.gin.ngemart.baseui.component.NgemartCardView;
import com.gin.wms.manager.MovingTaskManager;
import com.gin.wms.manager.db.data.MovingTaskData;
import com.gin.wms.manager.db.data.MovingTaskDestItemData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.operator.TaskSwitcherActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductListForMutationOrderActivity extends WarehouseActivity implements NgemartCardView.CardListener<MovingTaskDestItemData> {
    private MovingTaskData movingTaskData;
    private MovingTaskManager movingTaskManager;
    private NgemartRecyclerView rv;
    private NgemartRecyclerViewAdapter<List<MovingTaskDestItemData>> adapter;
    private final List<MovingTaskDestItemData> destItemDataList = new ArrayList<>();

    public static final String MUTATION_DATA_CODE = "mutationData";
    public static final String DEST_ITEM_CODE = "destItemData";
    public static final String CARD_POSITION_CODE = "cardPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_product_list_for_mutation_order);

            getDataFromIntent();
            init();
            showDataToUI();

        } catch (Exception e){
            showErrorDialog(e);
        }
    }

    private void showDataToUI() {
        destItemDataList.clear();
        destItemDataList.addAll(movingTaskData.destItemList);
        adapter.notifyDataSetChanged();
    }

    private void init() throws Exception {
        initObjectManager();
        initToolBar();
        initComponent();
        initAdapter();
        initRecyclerAdapter();
    }

    private void initRecyclerAdapter() {
        int numColumn = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false);
        rv.setHasFixedSize(true);
        rv.SetDefaultDecoration();
        rv.setLayoutManager(gridLayoutManager);
    }

    private void initAdapter() {
        adapter = new NgemartRecyclerViewAdapter(this,R.layout.card_product_list_mutation_order, destItemDataList);
        adapter.setHasStableIds(true);
        adapter.setRecyclerListener(this);
        rv.setAdapter(adapter);
    }

    private void initComponent() throws Exception {
        rv = findViewById(R.id.rvListProduct);
        Button btnFinish = findViewById(R.id.btnFinishMoving);
        btnFinish.setOnClickListener(v -> showConfirmationToStartTask());
    }

    private void initObjectManager() throws Exception{
        movingTaskManager = new MovingTaskManager(getApplicationContext());
    }

    private void initToolBar() throws Exception{
        Toolbar toolbar = findViewById(R.id.tbProductlist);
        toolbar.setTitle(getResources().getString(R.string.title_moving_finish));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getDataFromIntent() throws Exception {
        Bundle bundle = getIntent().getExtras();
        if(movingTaskData == null){
            if (bundle != null) {
                movingTaskData = (MovingTaskData) bundle.getSerializable(MovingStartActivity.MUTATION_DATA_CODE);
            }
        }

    }

    private void showConfirmationToStartTask() {
        String title = getResources().getString(R.string.common_confirmation_title);
        String body = getResources().getString(R.string.ask_to_finish_replenish);
        showAskDialog(title, body,
                (dialog, which) -> finishMutationTaskThread(),
                (dialog, which) -> dialog.dismiss());
    }

    private void finishMutationTaskThread() {
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getResources().getString(R.string.progress_finish_replenishData), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                movingTaskManager.finishMovingTask(movingTaskData.movingId);
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                moveToTaskSwitcherActivity();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void moveToTaskSwitcherActivity() {
        Intent intent = new Intent(this, TaskSwitcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void switchToMovingFinishActivity(MovingTaskDestItemData data, MovingTaskData movingTaskData) {
        Intent intent = new Intent(this, MovingFinishActivity.class);
        Bundle movingTaskDat = new Bundle();
        Bundle destItemData = new Bundle();

        movingTaskDat.putSerializable(MUTATION_DATA_CODE, movingTaskData);
        destItemData.putSerializable(DEST_ITEM_CODE, data);

        intent.putExtras(movingTaskDat);
        intent.putExtras(destItemData);

        startActivity(intent);
    }

    @Override
    public void onCardClick(int position, View view, MovingTaskDestItemData data) {
        try {
            switchToMovingFinishActivity(data, movingTaskData);
        }
        catch (Exception e){
            showErrorDialog(e);
        }
    }
}


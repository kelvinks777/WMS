package com.gin.ngemart.baseui.image_preview;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bosnet.ngemart.libgen.GsonMapper;
import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.ngemart.baseui.R;

public class ImagePreviewActivity extends NgemartActivity implements View.OnClickListener {
    public static final String IMAGE_PREVIEW_PARAM = "image_preview_param";

    private ImageView imageView;
    private Button btnDelete, btnCustom, btnClose;
    private FrameLayout btnCustomContainer, btnDeleteContainer, btnCloseContainer;
    private GsonMapper jsonMapper;
    private ImagePreviewParam param;

    private static ImagePreviewBtnClickListener btnClickListener;

    public static void setImagePreviewbtnClickListener(ImagePreviewBtnClickListener listener) {
        btnClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initObject();
        initComponent();
        getDataFromIntent();
        initActionBar();
        SetUiByParam();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(param.activityTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void SetUiByParam() {

        if(param.btnClose) {
            btnCloseContainer.setVisibility(View.VISIBLE);
            if(param.btnCloseColor > -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnCloseContainer.setBackgroundColor(getResources().getColor(param.btnCloseColor, null));
                } else {
                    btnCloseContainer.setBackgroundColor(getResources().getColor(param.btnCloseColor));
                }
            }

            if(! param.btnCloseTitle.equals(""))
                btnClose.setText(param.btnCloseTitle);
        } else {
            btnCloseContainer.setVisibility(View.GONE);
        }

        if(param.btnDelete) {
            btnDeleteContainer.setVisibility(View.VISIBLE);
            if(param.btnDeleteColor > -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnDeleteContainer.setBackgroundColor(getResources().getColor(param.btnDeleteColor, null));
                } else {
                    btnDeleteContainer.setBackgroundColor(getResources().getColor(param.btnDeleteColor));
                }
            }

            if(! param.btnDeleteTitle.equals(""))
                btnDelete.setText(param.btnDeleteTitle);
        } else {
            btnDeleteContainer.setVisibility(View.GONE);
        }

        if(param.btnCustom) {
            btnCustomContainer.setVisibility(View.VISIBLE);
            if(param.btnCustomColor > -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnCustomContainer.setBackgroundColor(getResources().getColor(param.btnCustomColor, null));
                } else {
                    btnCustomContainer.setBackgroundColor(getResources().getColor(param.btnCustomColor));
                }
            }

            if(! param.btnCustomTitle.equals(""))
                btnCustom.setText(param.btnCustomTitle);
        } else {
            btnCustomContainer.setVisibility(View.GONE);
        }

        if(param.image != null && !param.image.equals("")) {
            Bitmap bitmap = getBitmapFromBase64(param.image);
            imageView.setImageBitmap(bitmap);
        }
    }

    public Bitmap getBitmapFromBase64(String strImage) {
        byte[] decodedString = Base64.decode(strImage, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void getDataFromIntent() {
        String strImagePreviewParam = getIntent().getStringExtra(IMAGE_PREVIEW_PARAM);
        param = jsonMapper.read(strImagePreviewParam, ImagePreviewParam.class);
    }

    private void initObject() {
        jsonMapper = new GsonMapper();
    }

    private void initComponent() {
        imageView = findViewById(R.id.imageView);
        btnDelete = findViewById(R.id.btnDelete);
        btnDeleteContainer = findViewById(R.id.btnDeleteContainer);
        btnCustom = findViewById(R.id.btnCustom);
        btnCustomContainer = findViewById(R.id.btnCustomContainer);
        btnClose = findViewById(R.id.btnClose);
        btnCloseContainer = findViewById(R.id.btnCloseContainer);

        btnClose.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnCustom.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btnClose) {
            btnClickListener.btnClose();
            onBackPressed();
        } else if (i == R.id.btnCustom) {
            btnClickListener.btnCustom();
            showAlertToast("btnCustom");
        } else if (i == R.id.btnDelete) {
            showAskDialog("Konfirmasi", "Yakin akan menghapus photo?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    btnClickListener.btnDeleteClick(param.position);
                    onBackPressed();
                }
            }, null);
        }
    }

    public static class ImagePreviewParam {
        public String activityTitle;
        public String image;
        public int position;
        public boolean btnDelete;
        public String btnDeleteTitle;
        public int btnDeleteColor;
        public boolean btnCustom;
        public String btnCustomTitle;
        public int btnCustomColor;
        public boolean btnClose;
        public String btnCloseTitle;
        public int btnCloseColor;

        public ImagePreviewParam() {
            btnDelete = false;
            btnCustom = false;
            btnClose = true;
            position = -1;
            btnDeleteColor = -1;
            btnCustomColor = -1;
            btnCloseColor = -1;
            btnCloseTitle = "";
            btnDeleteTitle = "";
            btnCustomTitle = "";
            activityTitle = "";
        }
    }

    public interface ImagePreviewBtnClickListener {
        void btnDeleteClick(int position);
        void btnClose();
        void btnCustom();
    }
}

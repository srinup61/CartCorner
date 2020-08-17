package com.intern.kartcorner.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.EditText;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.helper.FileUtil;
import com.intern.kartcorner.helper.ImagePickerActivity;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;

import static com.intern.kartcorner.app.Constants.BASE_URL;

public class UpdateProfileActivity extends AppCompatActivity {
    private PrefManager prefManager;
    private ProgressDailog progressDailog;
    private ShowToast showToast;
    private static final int REQUEST_IMAGE = 100;
    @BindView(R.id.input_updatefullname)
    EditText updatename;
    @BindView(R.id.input_updatemobile)
    EditText updatemobile;
    @BindView(R.id.input_updateemail)
    EditText updateemail;
    @BindView(R.id.profile_update_picture)
    CircleImageView profileImage;
    private String profileName,profilEmail,profileMobile,userid;
    private File profilepicfile,compressedfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);
        ActionBar actionBar;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        prefManager = new PrefManager(this);
        progressDailog = new ProgressDailog(this);
        showToast = new ShowToast(this);
        userid = prefManager.getUserId();
        initviews();
    }

    private void initviews() {
        Intent intent = getIntent();
        profileName = intent.getStringExtra("name");
        profileMobile = intent.getStringExtra("phone");
        profilEmail = intent.getStringExtra("email");
        updatename.setText(profileName);
        updatemobile.setText(profileMobile);
        updateemail.setText(profilEmail);
    }

    @OnClick(R.id.profile_cancel_update)
    void cancelupdate(){
        Intent intent = new Intent(UpdateProfileActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.profile_update_editprofile)
    void uploadImage(){
        showimagePicker();
    }
    @OnClick(R.id.profile_submit_update)
    void profileupdate(){
        progressDailog.showDailog();
        String name = updatename.getText().toString();
        String mobile = updatemobile.getText().toString();
        String email = updateemail.getText().toString();
        if(name.isEmpty()||mobile.isEmpty()||email.isEmpty()){
            showToast.showWarningToast("Enter Proper Data");
            progressDailog.dismissDailog();
            return;
        }

        if(compressedfile==null){
            update(name,mobile,email);
        }else if(compressedfile.length()==0){
            update(name,mobile,email);
        }else {
            update(name,mobile,email,compressedfile);
        }
    }

    private void update(String name, String mobile, String email, File profilepicfile) {
        Ion.with(this)
                .load("POST",BASE_URL+"profileupdatewithimage")
                .setHeader("encType","multipart/form-data")
                .setMultipartParameter("userid",userid)
                .setMultipartParameter("username",name)
                .setMultipartParameter("userphone",mobile)
                .setMultipartParameter("usermail",email)
                .setMultipartFile("userprofilepic",profilepicfile)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        showToast.showWarningToast("Something went wrong");
                        progressDailog.dismissDailog();
                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String error = jsonObject.getString("error");
                            String msg = jsonObject.getString("message");
                            if(error.equals("false")){
                                showToast.showSuccessToast(msg);
                                cancelupdate();
                            }else {
                                showToast.showInfoToast(msg);
                            }

                            progressDailog.dismissDailog();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void update(String name, String mobile, String email) {
        Ion.with(this)
                .load("POST",BASE_URL+"profileupdate")
                .setMultipartParameter("userid",userid)
                .setMultipartParameter("username",name)
                .setMultipartParameter("userphone",mobile)
                .setMultipartParameter("usermail",email)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){
                            showToast.showWarningToast("Something went wrong");
                            progressDailog.dismissDailog();
                        }else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String error = jsonObject.getString("error");
                                String msg = jsonObject.getString("message");
                                if(error.equals("false")){
                                    cancelupdate();
                                }else {

                                }
                                showToast.showSuccessToast(msg);
                                progressDailog.dismissDailog();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void showimagePicker() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }
    private void launchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }
    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri uri = Objects.requireNonNull(data).getParcelableExtra("path");
                try {
                    profilepicfile = (File) FileUtil.from(UpdateProfileActivity.this,uri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this).getContentResolver(), uri);
                    profileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Compressor(this)
                            .compressToFileAsFlowable(profilepicfile)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<File>() {
                                @Override
                                public void accept(File file) {
                                    compressedfile = file;
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
            }
        }
    }
    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(this).getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Intent intent = new Intent(UpdateProfileActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UpdateProfileActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}

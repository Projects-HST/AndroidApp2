package com.palprotech.ensyfi.activity.loginmodule;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.palprotech.ensyfi.R;
import com.palprotech.ensyfi.activity.studentmodule.FeeStatusActivity;
import com.palprotech.ensyfi.bean.student.support.SaveStudentData;
import com.palprotech.ensyfi.helper.AlertDialogHelper;
import com.palprotech.ensyfi.helper.ProgressDialogHelper;
import com.palprotech.ensyfi.interfaces.DialogClickListener;
import com.palprotech.ensyfi.servicehelpers.ServiceHelper;
import com.palprotech.ensyfi.serviceinterfaces.IServiceListener;
import com.palprotech.ensyfi.utils.AndroidMultiPartEntity;
import com.palprotech.ensyfi.utils.CommonUtils;
import com.palprotech.ensyfi.utils.EnsyfiConstants;
import com.palprotech.ensyfi.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Narendar on 05/04/17.
 */

public class ProfileActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getName();
    private ImageView mProfileImage = null, btnBack;
    private TextView txtUsrName, txtUserType, txtPassword;

    private TextView Name, Address, Mail, Occupation, Income, Mobile, OfficePhone, HomePhone;

    private TextView GName, GAddress, GMail, GOccupation, GIncome, GMobile, GOfficePhone, GHomePhone;
    private TextView teacherId, teacherName, teacherGender, teacherAge, teacherNationality, teacherReligion, teacherCaste,
            teacherCommunity, teacherAddress, teacherSubject, classTeacher, teacherMobile, teacherSecondaryMobile, teacherMail,
            teacherSecondaryMail, teacherSectionName, teacherClassName, teacherClassTaken;

    private TextView studentAdmissionId, studentAdmissionYear, studentAdmissionNumber, studentEmsiNumber, studentAdmissionDate,
            studentName, studentGender, studentDateOfBirth, studentAge, studentNationality, studentReligion, studentCaste,
            studentCommunity, studentParentOrGuardian, studentParentOrGuardianId, studentMotherTongue, studentLanguage,
            studentMobile, studentSecondaryMobile, studentMail, studentSecondaryMail, studentPreviousSchool,
            studentPreviousClass, studentPromotionStatus, studentTransferCertificate, studentRecordSheet, studentStatus,
            studentParentStatus, studentRegistered;

    private TextView ParentProfile, GuardianProfile, StudentProfile, FeeStatusView, TeacherProfile;

    private ServiceHelper serviceHelper;
    private SaveStudentData studentData;
    private EditText txtUsrID;
    private ImageView fatherInfo, motherInfo, guardianImg, studentImg, teacherImg;
    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;
    protected ProgressDialogHelper progressDialogHelper;
    RelativeLayout TeacherInfo, parentInfoPopup, guardianInfoPopup, studentInfoPopup, teacherInfoPopup;
    LinearLayout ParentInfo;
    Button btnCancel, GbtnCancel, tbtnCancel, SbtnCancel, btnSave;
    private String mActualFilePath = null;
    private Uri mSelectedImageUri = null;
    private Bitmap mCurrentUserImageBitmap = null;
    private ProgressDialog mProgressDialog = null;
    private String mUpdatedImageUrl = null;
    private UploadFileToServer mUploadTask = null;
    long totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SetUI();
    }

    private void SetUI() {

        findViewById();

        String url = PreferenceStorage.getUserPicture(this);

        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(mProfileImage);
        }

//        callFatherInfoPreferences();

        String userTypeString = PreferenceStorage.getUserType(getApplicationContext());
        int userType = Integer.parseInt(userTypeString);
        if (userType == 1) {
            TeacherInfo.setVisibility(View.GONE);
            ParentInfo.setVisibility(View.GONE);
            FeeStatusView.setVisibility(View.GONE);
        } else if (userType == 2) {
            TeacherInfo.setVisibility(View.VISIBLE);
            ParentInfo.setVisibility(View.GONE);
        } else if (userType == 3) {
            ParentInfo.setVisibility(View.VISIBLE);
        } else {
            ParentInfo.setVisibility(View.VISIBLE);
            FeeStatusView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnBack) {
            finish();
        }
        if (v == mProfileImage) {
            openImageIntent();
        }
        if (v == txtPassword) {
            AlertDialogHelper.showCompoundAlertDialog(ProfileActivity.this, "Change Password", "Password will be Reset. Do you still wish to continue...", "OK", "CANCEL", 1);
        }
        if (v == fatherInfo) {
//            callFatherInfoPreferences();
        }
        if (v == motherInfo) {
//            callMotherInfoPreferences();
        }
        if (v == FeeStatusView) {
            Intent intent = new Intent(getApplicationContext(), FeeStatusActivity.class);
            startActivity(intent);
        }
        if (v == ParentProfile) {
            Intent intent = new Intent(getApplicationContext(), ParentInfoActivity.class);
            startActivity(intent);
//            parentInfoPopup.setVisibility(View.VISIBLE);
//            btnCancel.setVisibility(View.VISIBLE);
        }
        if (v == btnCancel) {
//            parentInfoPopup.setVisibility(View.INVISIBLE);
//            btnCancel.setVisibility(View.INVISIBLE);
        }
        if (v == GuardianProfile) {
            Intent intent = new Intent(getApplicationContext(), GuardianInfoActivity.class);
            startActivity(intent);
//            callGuardianInfoPreferences();
        }
        if (v == GbtnCancel) {
//            guardianInfoPopup.setVisibility(View.INVISIBLE);
//            GbtnCancel.setVisibility(View.INVISIBLE);
        }
        if (v == StudentProfile) {
            Intent intent = new Intent(getApplicationContext(), StudentInfoActivity.class);
            startActivity(intent);
//            studentInfoPopup.setVisibility(View.VISIBLE);
//            SbtnCancel.setVisibility(View.VISIBLE);
//
            String userTypeString = PreferenceStorage.getUserType(getApplicationContext());
            int userType = Integer.parseInt(userTypeString);

            if (userType == 3) {
//                callStudentInfoPreferences();
            } else {
                callGetStudentInfoService();
//                callStudentInfoPreferences();
            }
        }
        if (v == SbtnCancel) {
            studentInfoPopup.setVisibility(View.INVISIBLE);
            SbtnCancel.setVisibility(View.INVISIBLE);
        }
        if (v == TeacherProfile) {
            Intent intent = new Intent(getApplicationContext(), TeacherInfoActivity.class);
            startActivity(intent);
//            callTeacherInfoPreferences();
        }
        if (v == tbtnCancel) {
            teacherInfoPopup.setVisibility(View.INVISIBLE);
            tbtnCancel.setVisibility(View.INVISIBLE);
        }
        if (v == btnSave) {
            saveUserProfile();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {

                JSONArray getData = response.getJSONArray("studentProfile");
                studentData.saveStudentProfile(getData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void saveUserProfile() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Updating Profile");
        mProgressDialog.show();
        if ((mActualFilePath != null)) {
            Log.d(TAG, "Update profile picture");
            saveUserImage();
        }
//        else {
//            saveProfileData();
//        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        private static final String TAG = "UploadFileToServer";
        private HttpClient httpclient;
        HttpPost httppost;
        public boolean isTaskAborted = false;

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
           /* progressBar.setVisibility(View.VISIBLE);
            txtPercentage.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);*/
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            /*progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");*/
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            //http://heylaapp.com/app/upload.php?user_id=529
            String responseString = null;

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(String.format(EnsyfiConstants.UPLOAD_PROFILE_IMAGE, Integer.parseInt(PreferenceStorage.getUserId(ProfileActivity.this))));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                //  publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                Log.d(TAG, "actual file path is" + mActualFilePath);
                if (mActualFilePath != null) {

                    File sourceFile = new File(mActualFilePath);

                    // Adding file data to http body
                    //fileToUpload
                    entity.addPart("fileToUpload", new FileBody(sourceFile));
                /*}else {
                    String fileNameVal = PreferenceStorage.getUserId(ProfileActivity.this)+".png";
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    mCurrentUserImageBitmap.compress(Bitmap.CompressFormat.PNG, 75, bos);
                    byte[] data = bos.toByteArray();
                    ByteArrayBody bab = new ByteArrayBody(data, fileNameVal);
                    entity.addPart("fileToUpload", bab);
                }*/
                    //entity.addPart("image", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(ProfileActivity.this)));
                /*entity.addPart("website",
                        new StringBody("www.ahmed.site40.net"));
                entity.addPart("email", new StringBody("ahmedchoteri@gmail.com"));*/

                    totalSize = entity.getContentLength();
                    httppost.setEntity(entity);

                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                        try {
                            JSONObject resp = new JSONObject(responseString);
                            String successVal = resp.getString("status");
                            mUpdatedImageUrl = resp.getString("image");
                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
                            if (successVal.equalsIgnoreCase("success")) {
                                Log.d(TAG, "Updated image succesfully");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            super.onPostExecute(result);
            if ((result == null) || (result.isEmpty()) || (result.contains("Error"))) {
                Toast.makeText(ProfileActivity.this, "Unable to save profile picture", Toast.LENGTH_SHORT).show();
            } else {
                if (mUpdatedImageUrl != null) {
                    PreferenceStorage.saveUserPicture(ProfileActivity.this, mUpdatedImageUrl);
                }
            }
//            saveProfileData();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        public void taskCancel() {
            if (httppost != null) {
                isTaskAborted = true;
                httppost.abort();
                httppost = null;
            }
            if (httpclient != null) {
                isTaskAborted = true;
                httpclient.getConnectionManager().shutdown();
            }
            httpclient = null;
        }
    }

    private void saveUserImage() {
        mUpdatedImageUrl = null;

        new UploadFileToServer().execute();

    }

    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyDir");

        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.d(TAG, "Failed to create directory for storing images");
                return;
            }
        }

        final String fname = PreferenceStorage.getUserId(this) + ".png";
        final File sdImageMainDirectory = new File(root.getPath() + File.separator + fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Log.d(TAG, "camera output Uri" + outputFileUri);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Profile Photo");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_IMAGE_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_GET) {
                Log.d(TAG, "ONActivity Result");
                final boolean isCamera;
                if (data == null) {
                    Log.d(TAG, "camera is true");
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    Log.d(TAG, "camera action is" + action);
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;

                if (isCamera) {
                    Log.d(TAG, "Add to gallery");
                    selectedImageUri = outputFileUri;
                    mActualFilePath = outputFileUri.getPath();
                    galleryAddPic(selectedImageUri);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    mActualFilePath = getRealPathFromURI(this, selectedImageUri);
                    Log.d(TAG, "path to image is" + mActualFilePath);

                    // dummyflag= true;

                }
                Log.d(TAG, "image Uri is" + selectedImageUri);
                if (selectedImageUri != null) {
                    Log.d(TAG, "image URI is" + selectedImageUri);
                    //if( ! dummyflag) {
                    setPic(selectedImageUri);
                       /* }else{
                            try {
                               Bitmap  bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                                mUserImage.setImageBitmap(bm);
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }*/

                    // mUserImage.setImageURI(selectedImageUri);
                    // mUserImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        }

    }

    private void galleryAddPic(Uri urirequest) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(urirequest.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String result = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);


            Cursor cursor = loader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
                cursor.close();
            } else {
                Log.d(TAG, "cursor is null");
            }
        } catch (Exception e) {
            result = null;
            Toast.makeText(this, "Was unable to save  image", Toast.LENGTH_SHORT).show();

        } finally {
            return result;
        }

    }

    private void setPic(Uri selectedImageUri) {
        // Get the dimensions of the View
        int targetW = mProfileImage.getWidth();
        int targetH = mProfileImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        mSelectedImageUri = selectedImageUri;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
            // Bitmap circlebitmap = createCircularBitmap(bitmap);
            mProfileImage.setImageBitmap(bitmap);
            //mUserImage.setScaleType(ImageView.ScaleType.FIT_XY);
            mCurrentUserImageBitmap = bitmap;
            //  new UploadFileToServer().execute();
            // new Upload(bitmap,"myuserimage").execute();
            //  ServiceLocatorUtils.getInstance().setmCurrentUserProfileImage(mCurrentUserImageBitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void callGetStudentInfoService() {

        if (CommonUtils.isNetworkAvailable(this)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(EnsyfiConstants.STUDENT_ADMISSION_ID, PreferenceStorage.getStudentAdmissionIdPreference(this));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = EnsyfiConstants.BASE_URL + PreferenceStorage.getInstituteCode(this) + EnsyfiConstants.GET_STUDENT_INFO_DETAILS_API;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        } else {

            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(EnsyfiConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    private void findViewById() {
        mProfileImage = (ImageView) findViewById(R.id.image_profile_pic);
        mProfileImage.setOnClickListener(this);

        btnBack = (ImageView) findViewById(R.id.back_res);
        btnBack.setOnClickListener(this);

        txtUsrID = (EditText) findViewById(R.id.userid);
        txtUsrID.setEnabled(false);

        txtPassword = (TextView) findViewById(R.id.password);
        txtPassword.setOnClickListener(this);

        txtUsrName = (TextView) findViewById(R.id.name);
        txtUserType = (TextView) findViewById(R.id.typename);
        progressDialogHelper = new ProgressDialogHelper(this);
        txtUsrID.setText(PreferenceStorage.getUserName(getApplicationContext()));
        txtUsrName.setText(PreferenceStorage.getName(getApplicationContext()));
        txtUserType.setText(PreferenceStorage.getUserTypeName(getApplicationContext()));
        btnSave = (Button) findViewById(R.id.btnSave);

//        fatherInfo = (ImageView) findViewById(R.id.img_father_profile);
//        fatherInfo.setOnClickListener(this);
//
//        motherInfo = (ImageView) findViewById(R.id.img_mother_profile);
//        motherInfo.setOnClickListener(this);
//
//        guardianImg = (ImageView) findViewById(R.id.img_guardian_profile);
//
//        teacherImg = (ImageView) findViewById(R.id.img_teacher_profile);
//
//        studentImg = (ImageView) findViewById(R.id.img_student_profile);

        ParentProfile = (TextView) findViewById(R.id.ic_parentprofile);
        ParentProfile.setOnClickListener(this);

        GuardianProfile = (TextView) findViewById(R.id.ic_guardianprofile);
        GuardianProfile.setOnClickListener(this);

        TeacherProfile = (TextView) findViewById(R.id.ic_teacherprofile);
        TeacherProfile.setOnClickListener(this);

        StudentProfile = (TextView) findViewById(R.id.ic_studentprofile);
        StudentProfile.setOnClickListener(this);

        FeeStatusView = (TextView) findViewById(R.id.ic_feestatus);
        FeeStatusView.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        studentData = new SaveStudentData(this);

        ParentInfo = (LinearLayout) findViewById(R.id.parentStudentView);
        TeacherInfo = (RelativeLayout) findViewById(R.id.teacherprofile);

//        parentInfoPopup = (RelativeLayout) findViewById(R.id.popup_parent);
//        guardianInfoPopup = (RelativeLayout) findViewById(R.id.popup_guardian);
//        studentInfoPopup = (RelativeLayout) findViewById(R.id.popup_student);
//        teacherInfoPopup = (RelativeLayout) findViewById(R.id.popup_teacher);

        //////For Parent ///////
//        Name = (TextView) findViewById(R.id.txtfathername);
//        Address = (TextView) findViewById(R.id.txtfatheraddress);
//        Mail = (TextView) findViewById(R.id.txtfathermail);
//        Occupation = (TextView) findViewById(R.id.txtfatheroccupation);
//        Income = (TextView) findViewById(R.id.txtincome);
//        Mobile = (TextView) findViewById(R.id.txtfathermobile);
//        OfficePhone = (TextView) findViewById(R.id.txtfatherofficephone);
//        HomePhone = (TextView) findViewById(R.id.txtfatherhomephone);
//
//        //////For Guardian///////
//        GName = (TextView) findViewById(R.id.txtmothername);
//        GAddress = (TextView) findViewById(R.id.txtmotheraddress);
//        GMail = (TextView) findViewById(R.id.txtmothermail);
//        GOccupation = (TextView) findViewById(R.id.txtmotheroccupation);
//        GIncome = (TextView) findViewById(R.id.txtmotherincome);
//        GMobile = (TextView) findViewById(R.id.txtmothermobile);
//        GOfficePhone = (TextView) findViewById(R.id.txtmotherofficephone);
//        GHomePhone = (TextView) findViewById(R.id.txtmotherhomephone);
//
//        // Teacher's Info view
//        teacherId = (TextView) findViewById(R.id.txtTeacherid);
//        teacherName = (TextView) findViewById(R.id.txtTeacherName);
//        teacherGender = (TextView) findViewById(R.id.txtTeacherGender);
//        teacherAge = (TextView) findViewById(R.id.txtTeacherAge);
//        teacherNationality = (TextView) findViewById(R.id.txtTeacherNationality);
//        teacherReligion = (TextView) findViewById(R.id.txtTeacherReligion);
//        teacherCaste = (TextView) findViewById(R.id.txtTeacherCaste);
//        teacherCommunity = (TextView) findViewById(R.id.txtTeacherCommunity);
//        teacherAddress = (TextView) findViewById(R.id.txtTeacherAddress);
//        teacherSubject = (TextView) findViewById(R.id.txtTeacherSubject);
//        classTeacher = (TextView) findViewById(R.id.txtClassTeacher);
//        teacherMobile = (TextView) findViewById(R.id.txtTeacherMobile);
//        teacherSecondaryMobile = (TextView) findViewById(R.id.txtTeacherSecondaryMobile);
//        teacherMail = (TextView) findViewById(R.id.txtTeacherMail);
//        teacherSecondaryMail = (TextView) findViewById(R.id.txtTeacherSecondaryMail);
//        teacherSectionName = (TextView) findViewById(R.id.txtTeacherSectionName);
//        teacherClassName = (TextView) findViewById(R.id.txtTeacherClassName);
//        teacherClassTaken = (TextView) findViewById(R.id.txtTeacherClassTaken);
//
//        ////// For Student ///////
//        studentAdmissionId = (TextView) findViewById(R.id.txtstudentadminid);
//        studentAdmissionYear = (TextView) findViewById(R.id.txtstudentadminyear);
//        studentAdmissionNumber = (TextView) findViewById(R.id.txtstudentadminnum);
//        studentEmsiNumber = (TextView) findViewById(R.id.txtstudentemsinum);
//        studentAdmissionDate = (TextView) findViewById(R.id.txtStudentAdmissionDate);
//        studentName = (TextView) findViewById(R.id.txtStudentName);
//        studentGender = (TextView) findViewById(R.id.txtStudentGender);
//        studentDateOfBirth = (TextView) findViewById(R.id.txtStudentDateOfBirth);
//        studentAge = (TextView) findViewById(R.id.txtStudentAge);
//        studentNationality = (TextView) findViewById(R.id.txtStudentNationality);
//        studentReligion = (TextView) findViewById(R.id.txtStudentReligion);
//        studentCaste = (TextView) findViewById(R.id.txtStudentCaste);
//        studentCommunity = (TextView) findViewById(R.id.txtStudentCommunity);
//        studentParentOrGuardian = (TextView) findViewById(R.id.txtStudentParentOrGuardian);
//        studentParentOrGuardianId = (TextView) findViewById(R.id.txtStudentParentOrGuardianId);
//        studentMotherTongue = (TextView) findViewById(R.id.txtStudentMotherTongue);
//        studentLanguage = (TextView) findViewById(R.id.txtStudentLanguage);
//        studentMobile = (TextView) findViewById(R.id.txtStudentMobile);
//        studentSecondaryMobile = (TextView) findViewById(R.id.txtStudentSecondaryMobile);
//        studentMail = (TextView) findViewById(R.id.txtStudentMail);
//        studentSecondaryMail = (TextView) findViewById(R.id.txtStudentSecondaryMail);
//        studentPreviousSchool = (TextView) findViewById(R.id.txtStudentPreviousSchool);
//        studentPreviousClass = (TextView) findViewById(R.id.txtStudentPreviousClass);
//        studentPromotionStatus = (TextView) findViewById(R.id.txtStudentPromotionStatus);
//        studentTransferCertificate = (TextView) findViewById(R.id.txtStudentTransferCertificate);
//        studentRecordSheet = (TextView) findViewById(R.id.txtStudentRecordSheet);
//        studentStatus = (TextView) findViewById(R.id.txtStudentStatus);
//        studentParentStatus = (TextView) findViewById(R.id.txtStudentParentStatus);
//        studentRegistered = (TextView) findViewById(R.id.txtStudentRegistered);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void callFatherInfoPreferences() {
        Name.setText(PreferenceStorage.getFatherName(getApplicationContext()));
        Address.setText(PreferenceStorage.getFatherAddress(getApplicationContext()));
        Mail.setText(PreferenceStorage.getFatherEmail(getApplicationContext()));
        Occupation.setText(PreferenceStorage.getFatherOccupation(getApplicationContext()));
        Income.setText(PreferenceStorage.getFatherIncome(getApplicationContext()));
        Mobile.setText(PreferenceStorage.getFatherMobile(getApplicationContext()));
        OfficePhone.setText(PreferenceStorage.getFatherOfficePhone(getApplicationContext()));
        HomePhone.setText(PreferenceStorage.getFatherHomePhone(getApplicationContext()));
        String url = PreferenceStorage.getFatherImg(this);

        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(fatherInfo);
        }
    }

    private void callMotherInfoPreferences() {
        Name.setText(PreferenceStorage.getMotherName(getApplicationContext()));
        Address.setText(PreferenceStorage.getMotherAddress(getApplicationContext()));
        Mail.setText(PreferenceStorage.getMotherEmail(getApplicationContext()));
        Occupation.setText(PreferenceStorage.getMotherOccupation(getApplicationContext()));
        Income.setText(PreferenceStorage.getMotherIncome(getApplicationContext()));
        Mobile.setText(PreferenceStorage.getMotherMobile(getApplicationContext()));
        OfficePhone.setText(PreferenceStorage.getMotherOfficePhone(getApplicationContext()));
        HomePhone.setText(PreferenceStorage.getMotherHomePhone(getApplicationContext()));
        String url = PreferenceStorage.getMotherImg(this);

        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(motherInfo);
        }
    }

    private void callGuardianInfoPreferences() {
        guardianInfoPopup.setVisibility(View.VISIBLE);
        GbtnCancel.setVisibility(View.VISIBLE);
        GName.setText(PreferenceStorage.getGuardianName(getApplicationContext()));
        GAddress.setText(PreferenceStorage.getGuardianAddress(getApplicationContext()));
        GMail.setText(PreferenceStorage.getGuardianEmail(getApplicationContext()));
        GOccupation.setText(PreferenceStorage.getGuardianOccupation(getApplicationContext()));
        GIncome.setText(PreferenceStorage.getGuardianIncome(getApplicationContext()));
        GMobile.setText(PreferenceStorage.getGuardianMobile(getApplicationContext()));
        GOfficePhone.setText(PreferenceStorage.getGuardianOfficePhone(getApplicationContext()));
        GHomePhone.setText(PreferenceStorage.getGuardianHomePhone(getApplicationContext()));
        String url = PreferenceStorage.getGuardianImg(this);

        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(guardianImg);
        }
    }

    private void callTeacherInfoPreferences() {
        teacherInfoPopup.setVisibility(View.VISIBLE);
        tbtnCancel.setVisibility(View.VISIBLE);
        teacherId.setText(PreferenceStorage.getTeacherId(getApplicationContext()));
        teacherName.setText(PreferenceStorage.getTeacherName(getApplicationContext()));
        teacherGender.setText(PreferenceStorage.getTeacherGender(getApplicationContext()));
        teacherAge.setText(PreferenceStorage.getTeacherAge(getApplicationContext()));
        teacherNationality.setText(PreferenceStorage.getTeacherNationality(getApplicationContext()));
        teacherReligion.setText(PreferenceStorage.getTeacherReligion(getApplicationContext()));
        teacherCaste.setText(PreferenceStorage.getTeacherCaste(getApplicationContext()));
        teacherCommunity.setText(PreferenceStorage.getTeacherCommunity(getApplicationContext()));
        teacherAddress.setText(PreferenceStorage.getTeacherAddress(getApplicationContext()));
        teacherSubject.setText(PreferenceStorage.getTeacherSubject(getApplicationContext()));
        classTeacher.setText(PreferenceStorage.getClassTeacher(getApplicationContext()));
        teacherMobile.setText(PreferenceStorage.getTeacherMobile(getApplicationContext()));
        teacherSecondaryMobile.setText(PreferenceStorage.getTeacherSecondaryMobile(getApplicationContext()));
        teacherMail.setText(PreferenceStorage.getTeacherMail(getApplicationContext()));
        teacherSecondaryMail.setText(PreferenceStorage.getTeacherSecondaryMail(getApplicationContext()));
        teacherSectionName.setText(PreferenceStorage.getTeacherSectionName(getApplicationContext()));
        teacherClassName.setText(PreferenceStorage.getTeacherClassName(getApplicationContext()));
        teacherClassTaken.setText(PreferenceStorage.getTeacherClassTaken(getApplicationContext()));
        String url = PreferenceStorage.getTeacherPic(this);

        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(teacherImg);
        }
    }

    private void callStudentInfoPreferences() {
        studentAdmissionId.setText(PreferenceStorage.getStudentAdmissionID(getApplicationContext()));
        studentAdmissionYear.setText(PreferenceStorage.getStudentAdmissionYear(getApplicationContext()));
        studentAdmissionNumber.setText(PreferenceStorage.getStudentAdmissionNumber(getApplicationContext()));
        studentEmsiNumber.setText(PreferenceStorage.getStudentEmsiNumber(getApplicationContext()));
        studentAdmissionDate.setText(PreferenceStorage.getStudentAdmissionDate(getApplicationContext()));
        studentName.setText(PreferenceStorage.getStudentName(getApplicationContext()));
        studentGender.setText(PreferenceStorage.getStudentGender(getApplicationContext()));
        studentDateOfBirth.setText(PreferenceStorage.getStudentDateOfBirth(getApplicationContext()));
        studentAge.setText(PreferenceStorage.getStudentAge(getApplicationContext()));
        studentNationality.setText(PreferenceStorage.getStudentNationality(getApplicationContext()));
        studentReligion.setText(PreferenceStorage.getStudentReligion(getApplicationContext()));
        studentCaste.setText(PreferenceStorage.getStudentCaste(getApplicationContext()));
        studentCommunity.setText(PreferenceStorage.getStudentCommunity(getApplicationContext()));
        studentParentOrGuardian.setText(PreferenceStorage.getStudentParentOrGuardian(getApplicationContext()));
        studentParentOrGuardianId.setText(PreferenceStorage.getStudentParentOrGuardianID(getApplicationContext()));
        studentMotherTongue.setText(PreferenceStorage.getStudentMotherTongue(getApplicationContext()));
        studentLanguage.setText(PreferenceStorage.getStudentLanguage(getApplicationContext()));
        studentMobile.setText(PreferenceStorage.getStudentMobile(getApplicationContext()));
        studentSecondaryMobile.setText(PreferenceStorage.getStudentSecondaryMobile(getApplicationContext()));
        studentMail.setText(PreferenceStorage.getStudentMail(getApplicationContext()));
        studentSecondaryMail.setText(PreferenceStorage.getStudentSecondaryMail(getApplicationContext()));
        studentPreviousSchool.setText(PreferenceStorage.getStudentPreviousSchool(getApplicationContext()));
        studentPreviousClass.setText(PreferenceStorage.getStudentPreviousClass(getApplicationContext()));
        studentPromotionStatus.setText(PreferenceStorage.getStudentPromotionStatus(getApplicationContext()));
        studentTransferCertificate.setText(PreferenceStorage.getStudentTransferCertificate(getApplicationContext()));
        studentRecordSheet.setText(PreferenceStorage.getStudentRecordSheet(getApplicationContext()));
        studentStatus.setText(PreferenceStorage.getStudentStatus(getApplicationContext()));
        studentParentStatus.setText(PreferenceStorage.getStudentParentStatus(getApplicationContext()));
        studentRegistered.setText(PreferenceStorage.getStudentRegistered(getApplicationContext()));
        String url = PreferenceStorage.getStudentImg(this);

        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).into(studentImg);
        }
    }

}


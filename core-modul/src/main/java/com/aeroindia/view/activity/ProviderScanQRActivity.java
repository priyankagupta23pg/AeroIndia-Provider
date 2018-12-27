package com.aeroindia.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aeroindia.R;
import com.aeroindia.custom.utility.AppConstant;
import com.aeroindia.custom.utility.AppUtilityFunction;
import com.aeroindia.pojos.request.QRScanProviderVolunteerRequest;
import com.aeroindia.pojos.request.UpdateReportProviderVolunteerRequest;
import com.aeroindia.pojos.response.GenericResponse;
import com.aeroindia.pojos.response.LoginResponse;
import com.aeroindia.pojos.response.QRCodeResponseModel;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.customComponent.CustomAlert;
import com.customComponent.Networking.CustomVolley;
import com.customComponent.Networking.VolleyTaskListener;
import com.customComponent.utility.BaseAppCompatActivity;
import com.customComponent.utility.ProjectPrefrence;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ProviderScanQRActivity extends BaseAppCompatActivity implements
        ZXingScannerView.ResultHandler {
private Context context;
private LoginResponse loginResponse;
private RelativeLayout backLayout;
    private ProviderScanQRActivity activity;
    private RelativeLayout lnr_scan;
    private String comment=null;
    private GenericResponse genericResponse;
   String zone = "";
    String pay_num = "";
private TextView headerTV,selectedExhibitorTv,serviceNameTv,statusTv,commentTv,qrcodeTv,zoneTv,claimListTv;
    ViewGroup contentFrame;
    private ZXingScannerView mScannerView;
    private LinearLayout ratingLL;
    private Button attend;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private TextView rescan,serviceNmTv;
    QRCodeResponseModel qrCodeResponseModel;
    private int claimId;
    private String type,claimList,serviceNm;
    private LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_scan_qr);
        context = this;
        activity = this;

        setupScreen();
    }
    private void setupScreen() {
        getSupportActionBar().hide();
        loginResponse = LoginResponse.create(ProjectPrefrence.getSharedPrefrenceData(AppConstant.PROJECT_PREF, AppConstant.LOGIN_DETAILS, context));
        backLayout = (RelativeLayout) findViewById(R.id.backLayout);
        linear = (LinearLayout) findViewById(R.id.linear);
        attend = (Button) findViewById(R.id.attend);
        headerTV = (TextView) findViewById(R.id.headerTV);
        serviceNmTv = (TextView) findViewById(R.id.serviceNm);
        lnr_scan = (RelativeLayout) findViewById(R.id.lnr_scan);

        rescan = (TextView) findViewById(R.id.rescan);
        rescan.setPaintFlags(rescan.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        selectedExhibitorTv = (TextView) findViewById(R.id.selectedExhibitorTv);
        headerTV.setText("Scan QR code");
if(getIntent()!=null)
{
    claimId=getIntent().getIntExtra("claimId",0);
    type=getIntent().getStringExtra("type");
    claimList=getIntent().getStringExtra("claimList");
    serviceNm=getIntent().getStringExtra("serviceNm");
}
        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        //headerTV.setText("Feedback");
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ComplaintsActivity.class);
                intent.putExtra("type",type);
                startActivity(intent);
                finish();
            }
        });
        statusTv = (TextView) findViewById(R.id.statusTv);
        commentTv = (TextView) findViewById(R.id.commentTv);
        serviceNameTv = (TextView) findViewById(R.id.serviceNameTv);
        claimListTv = (TextView) findViewById(R.id.claimListTv);
        qrcodeTv = (TextView) findViewById(R.id.qrcodeTv);
        zoneTv = (TextView) findViewById(R.id.zoneTv);
        ratingLL = (LinearLayout) findViewById(R.id.ratingLL);
        ratingLL.setVisibility(View.GONE);

        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareSubmitReportData("A");

            }
        });
        rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingLL.setVisibility(View.GONE);
                rescan.setVisibility(View.GONE);
                lnr_scan.setVisibility(View.VISIBLE);
            }
        });
        if(serviceNm!=null)
        {
            serviceNmTv.setText(serviceNm);
        }
        if(claimList!=null) {
            claimListTv.setVisibility(View.VISIBLE);
            String[] issuesList = claimList.split(",");
            for (String issue : issuesList) {
                TextView textView = new TextView(context);
                textView.setText("-> "+issue);
                linear.addView(textView);
            }
        }

        else
        {
            linear.removeAllViews();
        }
    }
    @Override
    public void onBackPressed()
    {
        backLayout.performClick();
    }
    private void prepareSubmitReportData(String status) {
        if (qrCodeResponseModel != null) {
            UpdateReportProviderVolunteerRequest updateReportProviderVolunteerRequest = new UpdateReportProviderVolunteerRequest();
            updateReportProviderVolunteerRequest.setRemarks(comment);
            updateReportProviderVolunteerRequest.setClaimId(claimId);
            if (loginResponse != null && loginResponse.getCompany() != null &&
                    loginResponse.getCompany().getCompanyId() != 0) {
                updateReportProviderVolunteerRequest.setUserId( loginResponse.getCompany().getCompanyId() );
            }


            if (qrCodeResponseModel.getId() != 0
                    ) {
                updateReportProviderVolunteerRequest.setId(qrCodeResponseModel.getId());
            }

            if (qrCodeResponseModel.getBarcodeId() != 0
                    ) {
                updateReportProviderVolunteerRequest.setBarcodeId(qrCodeResponseModel.getBarcodeId());
            }
            if (qrCodeResponseModel.getQrcodeNumber() != null) {
                updateReportProviderVolunteerRequest.setQrcodeNumber(qrCodeResponseModel.getQrcodeNumber());
            }
            updateReportProviderVolunteerRequest.setStatus(status);

            submitReportUsData(updateReportProviderVolunteerRequest);
        }
    }




    private void submitReportUsData(UpdateReportProviderVolunteerRequest request) {
        showHideProgressDialog(true);
        VolleyTaskListener taskListener = new VolleyTaskListener() {
            @Override
            public void postExecute(String response) {
                showHideProgressDialog(false);

                genericResponse = GenericResponse.create(response);
                if (genericResponse.isStatus()) {
                    Intent intent=new Intent(context,ComplaintsActivity.class);
                    intent.putExtra("type",type);

                    CustomAlert.alertWithOk(context, "Your report has been submitted Successfully.",intent);
                    // finish();
                } else {
                    Toast.makeText(context, genericResponse.getErrorMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }

            @Override
            public void onError(VolleyError error) {
                showHideProgressDialog(false);

                if (error != null) {
                    // String s = new String(error.networkResponse.data);
                    //  Log.d("ERROR MSG", s);
                    if (error instanceof TimeoutError) {
                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.timeout_issue));
                    } else if (AppUtilityFunction.isServerProblem(error)) {
                        // Toast.makeText(getApplicationContext(),R.string.LOGIN_FAILED,Toast.LENGTH_LONG).show();

                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.server_issue));
                    } else if (AppUtilityFunction.isNetworkProblem(error)) {
                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.IO_ERROR));
                    }
                }
            }
        };
        Log.d("Provider Request",request.serialize());
        CustomVolley volley = new CustomVolley(taskListener, null, AppConstant.UPDATEREPORT_PROVIDER, request.serialize(), null, null, context);
        volley.execute();
    }




    public void CameraPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            } else {
                CallCamera();
            }
        } else {
            CallCamera();
        }
    }

    private void CallCamera() {
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        pay_num = result.getText();

        //  Toast.makeText(context, pay_num, Toast.LENGTH_LONG).show();
      //  if (pay_num != null && !pay_num.equalsIgnoreCase("")) {
            String qrcodeNumber = "", serviceName = "", serviceTag = "";

//            if (qrcodeParam != null) {
//                if (qrcodeParam[0] != null && !qrcodeParam[0].equalsIgnoreCase("")) {
//                    qrcodeNumber = qrcodeParam[0];
//                }
//                if (qrcodeParam[1] != null && !qrcodeParam[1].equalsIgnoreCase("")) {
//                    String[] latLong = qrcodeParam[1].split(",");
//
//                }
//
//                prepareQRCodeRequest(qrcodeNumber);
//
//
//            }
            if (pay_num != null && !pay_num.equalsIgnoreCase("")) {
                String[] qrcodeParam = pay_num.split("-");
                if (qrcodeParam != null) {
                    if (qrcodeParam[0] != null && !qrcodeParam[0].equalsIgnoreCase("")) {
                        qrcodeNumber = qrcodeParam[0];

                    }

//                    if (qrcodeParam[1] != null && !qrcodeParam[1].equalsIgnoreCase("")) {
//                        serviceName = qrcodeParam[1];
//                        serviceNameTv.setText(serviceName);
//                    }

                    if (qrcodeParam[2] != null && !qrcodeParam[2].equalsIgnoreCase("")) {
                        zone = qrcodeParam[2];
                        //zoneTV.setText(zone);
                    }

                    if (qrcodeParam[3] != null && !qrcodeParam[3].equalsIgnoreCase("")) {
                        serviceTag = qrcodeParam[3];

                    }

                       prepareQRCodeRequest(qrcodeNumber);

                }
        }


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ProviderScanQRActivity.this);
            }
        }, 1000);
    }

    private void prepareQRCodeRequest(String qrcodeNumber) {
        if (loginResponse != null && loginResponse.getUser() != null &&
                loginResponse.getCompany().getCompanyId() != 0) {
            QRScanProviderVolunteerRequest requestModel = new QRScanProviderVolunteerRequest(qrcodeNumber,loginResponse.getCompany().getCompanyId(),loginResponse.getUser().getCategory());
            getQRCodeData(requestModel);
        }


    }

    private void getQRCodeData(QRScanProviderVolunteerRequest request) {
        VolleyTaskListener taskListener = new VolleyTaskListener() {
            @Override
            public void postExecute(String response) {
                qrCodeResponseModel = QRCodeResponseModel.create(response);
                if (qrCodeResponseModel.isStatus()) {
                    if (qrCodeResponseModel != null && qrCodeResponseModel.getServiceName() != null) {
                        ratingLL.setVisibility(View.VISIBLE);
                        rescan.setVisibility(View.VISIBLE);
                        lnr_scan.setVisibility(View.GONE);
                        setProfileData();
                    }

                    return;
                } else {

                    CustomAlert.alertOkWithFinish(context, qrCodeResponseModel.getErrorMessage());
                    return;
                }
            }

            @Override
            public void onError(VolleyError error) {
                if (error != null) {
                    // String s = new String(error.networkResponse.data);
                    //  Log.d("ERROR MSG", s);
                    if (error instanceof TimeoutError) {
                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.timeout_issue));
                    } else if (AppUtilityFunction.isServerProblem(error)) {
                        // Toast.makeText(getApplicationContext(),R.string.LOGIN_FAILED,Toast.LENGTH_LONG).show();

                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.server_issue));
                    } else if (AppUtilityFunction.isNetworkProblem(error)) {
                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.IO_ERROR));
                    }
                }
            }
        };
        CustomVolley volley = new CustomVolley(taskListener, null, AppConstant.GET_QR_CODE_PROVIDER_VOLUNTEER_API, request.serialize(), null, null, context);
        volley.execute();
    }

    private void setProfileData() {
        if (qrCodeResponseModel != null && qrCodeResponseModel.getServiceName() != null) {
            if(qrCodeResponseModel.getServiceName()!=null) {
                serviceNameTv.setText(qrCodeResponseModel.getServiceName());
            }
            if(qrCodeResponseModel.getServiceStatus().equalsIgnoreCase("P")) {
                statusTv.setText("Pending");
                statusTv.setTextColor(context.getResources().getColor(R.color.red));
            }
            else
            {
                statusTv.setText("Not Attended");
                statusTv.setTextColor(context.getResources().getColor(R.color.purple));
            }
            commentTv.setText(qrCodeResponseModel.getComment());
            qrcodeTv.setText(qrCodeResponseModel.getQrcodeNumber());
            zoneTv.setText(zone);
        }
    }

//    private void submitReportUsData(ReportUsRequestModel request) {
//        VolleyTaskListener taskListener = new VolleyTaskListener() {
//            @Override
//            public void postExecute(String response) {
//                reportUsResponseModel = QRCodeResponseModel.create(response);
//                if (reportUsResponseModel.isStatus()) {
//                    CustomAlert.alertOkWithFinish(context, "Your report has been submitted Successfully.");
//                    // finish();
//                } else {
//                    Toast.makeText(context, reportUsResponseModel.getErrorMessage(), Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                if (error != null) {
//                    // String s = new String(error.networkResponse.data);
//                    //  Log.d("ERROR MSG", s);
//                    if (error instanceof TimeoutError) {
//                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.timeout_issue));
//                    } else if (AppUtilityFunction.isServerProblem(error)) {
//                        // Toast.makeText(getApplicationContext(),R.string.LOGIN_FAILED,Toast.LENGTH_LONG).show();
//
//                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.server_issue));
//                    } else if (AppUtilityFunction.isNetworkProblem(error)) {
//                        CustomAlert.alertWithOk(context, context.getResources().getString(R.string.IO_ERROR));
//                    }
//                }
//            }
//        };
//        volley = new CustomVolley(taskListener, "Please wait..", AppConstant.SUBMIT_REPORT, request.serialize(), null, null, context);
//        volley.execute();
//    }



//    public String loadJSONFromAsset() {
//        String json = null;
//        try {
//            InputStream is = activity.getAssets().open("convertcsv.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//    }

    @Override
    public void onResume() {
        super.onResume();
        CameraPermission(activity);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}

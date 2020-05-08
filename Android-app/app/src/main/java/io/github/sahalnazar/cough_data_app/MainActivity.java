package io.github.sahalnazar.cough_data_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import tech.oom.idealrecorder.IdealRecorder;
import tech.oom.idealrecorder.StatusListener;


public class MainActivity extends AppCompatActivity {

    private Button mPlayBtn;
    private Button recordBtn;

    private WaveView waveView;
    private ScrollView scrollView;
    private LinearLayout mRecordLayout;

    private EditText mThermometerEditText;
    private EditText mDiabetesEditText;
    private EditText mBloodPressureEditText;

    private TextView mCovidTv;
    private TextView mCoughTv;

    private RadioGroup mCovidRg;
    private RadioGroup mCoughRg;

    private RadioButton mPositive;
    private RadioButton mNegative;
    private RadioButton mNotTested;
    private RadioButton mWaiting;
    private RadioButton mCoughNo;
    private RadioButton mCoughYes;

    private CheckBox mThermometerCb;
    private CheckBox mDiabetesCb;
    private CheckBox mBpCb;

    private Spinner mFeverSpinner;
    private Spinner mHeadacheSpinner;
    private Spinner mFatigueSpinner;
    private Spinner mRunnyNoseSpinner;
    private Spinner mDiarrheaSpinner;
    private Spinner mShortBreathingSpinner;
    private Spinner mContactCoronaSpinner;
    private Spinner mSmokingSpinner;
    private Spinner mHeartProblemSpinner;
    private Spinner mAsthmaSpinner;
    private Spinner mGoneOutsideSpinner;

    private String sCovid;
    private String sThermometerReading;
    private String sDiabetes;
    private String sBpReading;
    private String sFever;
    private String sHeadache;
    private String sFatigue;
    private String sRunnyNose;
    private String sDiarrhea;
    private String sShortBreathing;
    private String sContactCovid;
    private String sSmoking;
    private String sHeartDisease;
    private String sAsthma;
    private String sOutside;
    private String sCough;

    private DatabaseReference mDatabaseReference;
    private StorageReference mCoughStorageReference;

    ArrayAdapter<CharSequence> noneLowArrayAdapter;
    ArrayAdapter<CharSequence> noneNoArrayAdapter;

    private MediaPlayer mPlayer = null;

    private File wavFile;

    private ProgressDialog mProgressDialog;

    private IdealRecorder idealRecorder;
    private IdealRecorder.RecordConfig recordConfig;

    SharedPreferences prefs = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IdealRecorder.getInstance().init(this);
        idealRecorder = IdealRecorder.getInstance();

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        //initialize
        initialize();

        //setting up spinners
        setupSpinners();

        editTextListeners();

        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    record();
                    scrollView.requestDisallowInterceptTouchEvent(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    stopRecord();

                }
                return false;
            }
        });

        recordConfig = new IdealRecorder.RecordConfig(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("cough Research Data");
        mCoughStorageReference = FirebaseStorage.getInstance().getReference().child("cough");

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(wavFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e("Audio", "prepare() failed");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {

            new AlertDialog.Builder(this)
                    .setTitle("Cough Research")
                    .setMessage(getString(R.string.privacy_dialog))
                    .setPositiveButton("Lets Contribute!", null)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            prefs.edit().putBoolean("firstrun", true).apply();
                        }
                    })
                    .show();

            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

    private void RequestAudioAndStoragePermissions() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this, permissions, "Please provide Microphone and Storage permission to record cough.", null, new PermissionHandler() {
            @Override
            public void onGranted() {
                mRecordLayout.setVisibility(View.VISIBLE);
                waveView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Microphone and Storage permission are required to record cough.", Toast.LENGTH_SHORT).show();
                mRecordLayout.setVisibility(View.GONE);
                waveView.setVisibility(View.GONE);
            }

        });
    }

    private void stopRecord() {
        idealRecorder.stop();
    }

    private void editTextListeners() {
        mThermometerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mThermometerCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mDiabetesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mDiabetesCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mBloodPressureEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mBpCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setupSpinners() {
        noneLowArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.none_high_medium_low, android.R.layout.simple_spinner_item);
        noneLowArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        noneNoArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.none_yes_no, android.R.layout.simple_spinner_item);
        noneLowArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mFeverSpinner.setAdapter(noneLowArrayAdapter);
        mHeadacheSpinner.setAdapter(noneLowArrayAdapter);
        mFatigueSpinner.setAdapter(noneLowArrayAdapter);
        mRunnyNoseSpinner.setAdapter(noneLowArrayAdapter);
        mDiarrheaSpinner.setAdapter(noneLowArrayAdapter);
        mShortBreathingSpinner.setAdapter(noneLowArrayAdapter);
        mContactCoronaSpinner.setAdapter(noneNoArrayAdapter);
        mSmokingSpinner.setAdapter(noneNoArrayAdapter);
        mHeartProblemSpinner.setAdapter(noneNoArrayAdapter);
        mAsthmaSpinner.setAdapter(noneNoArrayAdapter);
        mGoneOutsideSpinner.setAdapter(noneLowArrayAdapter);
    }

    private void initialize() {

        mPlayBtn = findViewById(R.id.playBtn);
        mPositive = findViewById(R.id.positive);
        mNegative = findViewById(R.id.negative);
        mNotTested = findViewById(R.id.not_tested);
        mWaiting = findViewById(R.id.waiting_for_result);
        mThermometerEditText = findViewById(R.id.thermometerEt);
        mDiabetesEditText = findViewById(R.id.diabetesEt);
        mBloodPressureEditText = findViewById(R.id.bloodPressureEt);
        mRecordLayout = findViewById(R.id.recordLayout);
        mFeverSpinner = findViewById(R.id.feverSpinner);
        mHeadacheSpinner = findViewById(R.id.headacheSpinner);
        mFatigueSpinner = findViewById(R.id.fatigueSpinner);
        mRunnyNoseSpinner = findViewById(R.id.runnyNoseSpinner);
        mDiarrheaSpinner = findViewById(R.id.diarrheaSpinner);
        mShortBreathingSpinner = findViewById(R.id.shortBreathingSpinner);
        mContactCoronaSpinner = findViewById(R.id.contactCoronaSpinner);
        mSmokingSpinner = findViewById(R.id.smokingSpinner);
        mHeartProblemSpinner = findViewById(R.id.heartProblemSpinner);
        mAsthmaSpinner = findViewById(R.id.asthmaSpinner);
        mGoneOutsideSpinner = findViewById(R.id.goneOutsideSpinner);
        mCovidRg = findViewById(R.id.covidRG);
        mCoughRg = findViewById(R.id.coughRG);
        mCoughTv = findViewById(R.id.coughTV);
        mCovidTv = findViewById(R.id.covidTV);
        mCoughYes = findViewById(R.id.coughYes);
        mCoughNo = findViewById(R.id.coughNo);
        mThermometerCb = findViewById(R.id.thermometerCb);
        mDiabetesCb = findViewById(R.id.diabetesCb);
        mBpCb = findViewById(R.id.bloodPressureCb);
        mProgressDialog = new ProgressDialog(this);
        recordBtn = (Button) findViewById(R.id.register_record_btn);
        waveView = (WaveView) findViewById(R.id.wave_view);
        scrollView = findViewById(R.id.scrollView);

    }

    private void record() {
        idealRecorder.setRecordFilePath(getSaveFilePath());
        idealRecorder.setRecordConfig(recordConfig).setMaxRecordTime(20000).setVolumeInterval(200);
        idealRecorder.setStatusListener(statusListener);
        idealRecorder.start();
    }

    private String getSaveFilePath() {
        File file = new File(getExternalCacheDir(), "Audio");
        if (!file.exists()) {
            file.mkdirs();
        }
        wavFile = new File(file, "cough.wav");
        return wavFile.getAbsolutePath();
    }

    public void onCovidRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.positive:
                if (checked)
                    sCovid = mPositive.getText().toString();
                mCovidTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));

                break;
            case R.id.negative:
                if (checked)
                    sCovid = mNegative.getText().toString();
                mCovidTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                break;

            case R.id.not_tested:
                if (checked)
                    sCovid = mNotTested.getText().toString();
                mCovidTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                break;

            case R.id.waiting_for_result:
                if (checked)
                    sCovid = mWaiting.getText().toString();
                mCovidTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                break;

        }

    }

    public void onMachineCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.thermometerCb:
                if (checked) {
                    mThermometerEditText.setVisibility(View.VISIBLE);

                } else
                    mThermometerEditText.setVisibility(View.GONE);
                mThermometerCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                break;

            case R.id.diabetesCb:
                if (checked) {
                    mDiabetesEditText.setVisibility(View.VISIBLE);
                } else
                    mDiabetesEditText.setVisibility(View.GONE);
                mDiabetesCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                break;

            case R.id.bloodPressureCb:
                if (checked) {
                    mBloodPressureEditText.setVisibility(View.VISIBLE);
                } else
                    mBloodPressureEditText.setVisibility(View.GONE);
                mBpCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                break;

        }
    }

    public void onCoughRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.coughNo:
                if (checked)
                    sCough = mCoughNo.getText().toString();
                mRecordLayout.setVisibility(View.GONE);
                waveView.setVisibility(View.GONE);
                wavFile = null;
                mCoughTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));

                break;
            case R.id.coughYes:
                if (checked)
                    sCough = mCoughYes.getText().toString();
                RequestAudioAndStoragePermissions();
                mCoughTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTV));
                break;

        }
    }

    public void onSubmitBtnClicked(View view) {

        getData();

        if (mCovidRg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select an option.", Toast.LENGTH_SHORT).show();
            mCovidTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        } else if (mCoughRg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select an option.", Toast.LENGTH_SHORT).show();
            mCoughTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        } else if (mThermometerCb.isChecked() && sThermometerReading.matches("")) {
            Toast.makeText(this, "Please provide your temperature.", Toast.LENGTH_SHORT).show();
            mThermometerCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        } else if (mDiabetesCb.isChecked() && sDiabetes.matches("")) {
            Toast.makeText(this, "Please provide your diabetes.", Toast.LENGTH_SHORT).show();
            mDiabetesCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        } else if (mBpCb.isChecked() && sBpReading.matches("")) {
            Toast.makeText(this, "Please provide your blood pressure.", Toast.LENGTH_SHORT).show();
            mBpCb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));

        } else if (wavFile == null && mCoughYes.isChecked()) {
            Toast.makeText(this, "Please record Cough audio.", Toast.LENGTH_SHORT).show();
            mCoughTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));

        } else if (wavFile != null && mCoughYes.isChecked()) {
            uploadAudio();

        } else {

            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            CoughData coughData = new CoughData(sCovid, sThermometerReading,
                    sDiabetes, sBpReading, sFever, sHeadache, sFatigue, sRunnyNose, sDiarrhea,
                    sShortBreathing, sContactCovid, sSmoking, sHeartDisease, sAsthma, sOutside,
                    sCough, "NA");
            mDatabaseReference.push().setValue(coughData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Upload successful, Thank you!", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void uploadAudio() {

        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.show();

        Uri audioFileUri = Uri.fromFile(wavFile);
        StorageReference filepath = mCoughStorageReference.child(System.currentTimeMillis()
                + ".wav");


        filepath.putFile(audioFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> firebasePhotoUri = taskSnapshot.getStorage().getDownloadUrl();
                firebasePhotoUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        CoughData coughData = new CoughData(sCovid, sThermometerReading, sDiabetes, sBpReading, sFever, sHeadache, sFatigue, sRunnyNose, sDiarrhea, sShortBreathing, sContactCovid,
                                sSmoking, sHeartDisease, sAsthma, sOutside,
                                sCough, uri.toString());
                        mDatabaseReference.push().setValue(coughData);

                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Upload successful, Thank you!", Toast.LENGTH_LONG).show();

                    }

                });
            }
        });
    }

    private void getData() {
        sThermometerReading = mThermometerEditText.getText().toString();
        sDiabetes = mDiabetesEditText.getText().toString();
        sBpReading = mBloodPressureEditText.getText().toString();
        sFever = mFeverSpinner.getSelectedItem().toString();
        sHeadache = mHeadacheSpinner.getSelectedItem().toString();
        sFatigue = mFatigueSpinner.getSelectedItem().toString();
        sRunnyNose = mRunnyNoseSpinner.getSelectedItem().toString();
        sDiarrhea = mDiarrheaSpinner.getSelectedItem().toString();
        sShortBreathing = mShortBreathingSpinner.getSelectedItem().toString();
        sContactCovid = mContactCoronaSpinner.getSelectedItem().toString();
        sSmoking = mSmokingSpinner.getSelectedItem().toString();
        sHeartDisease = mHeartProblemSpinner.getSelectedItem().toString();
        sAsthma = mAsthmaSpinner.getSelectedItem().toString();
        sOutside = mGoneOutsideSpinner.getSelectedItem().toString();

    }

    private StatusListener statusListener = new StatusListener() {

        @Override
        public void onRecordData(short[] data, int length) {

            for (int i = 0; i < length; i += 60) {
                waveView.addData(data[i]);
            }
        }

        @Override
        public void onFileSaveFailed(String error) {
            Toast.makeText(MainActivity.this, "Something went wrong, Please try again.", Toast.LENGTH_SHORT).show();
        }

    };
}

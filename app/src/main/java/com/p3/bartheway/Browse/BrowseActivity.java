package com.p3.bartheway.Browse;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.p3.bartheway.Database.ApiClient;
import com.p3.bartheway.Database.ApiInterface;
import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseActivity extends AppCompatActivity implements ItemRecyclerAdapter.OnClickListener, BrowseView{

    private static final String TAG = "BluetoothActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;

    SwipeRefreshLayout swipeRefresh;
    ApiInterface apiInterface;
    BrowsePresenter presenter;

    List<Item> items;

    List<Student> student;

    Date date = new Date();

    private boolean mIsUserInitiatedDisconnect = false;

    // All controls here
    private TextView mTxtReceive;
    private TextView mTxtGame;
    private RecyclerView mRecyclerView;
    private ItemRecyclerAdapter mAdapter;
    private Button mBtnClearInput;
    private Button mBtnConfirm;
    private Bundle b;
    private boolean isAlertShowing = false;

    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        ActivityHelper.initialize(this);

        mTxtReceive = findViewById(R.id.txtReceive);
        mTxtGame = findViewById(R.id.txtGame);
        mRecyclerView = findViewById(R.id.item_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        swipeRefresh = findViewById(R.id.swipeRefresh);

        presenter = new BrowsePresenter(this);
        presenter.getData();

        swipeRefresh.setOnRefreshListener(
                () -> presenter.getData()
        );


        final Intent intent = getIntent();
        b = intent.getExtras();

        //checks if any information were given from the previous activity and either connects to the arduino or does nothing
        if(b != null) {
            if (b.get("Connect").equals("true")) {
                mDevice = b.getParcelable(BluetoothActivity.DEVICE_EXTRA);
                mDeviceUUID = UUID.fromString(b.getString(BluetoothActivity.DEVICE_UUID));
                mMaxChars = b.getInt(BluetoothActivity.BUFFER_SIZE);
            } else {
                mTxtReceive.setText("Not connected to Arduino");
            }
        }
        Log.d(TAG, "Ready");

        mBtnConfirm = findViewById(R.id.buttonConfirm);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (student != null && !isAlertShowing) {
                    if (student.get(0).getTitle() != null && !student.get(0).getTitle().equals("")) {
                        mBtnConfirm.setVisibility(View.INVISIBLE);
                        returnGame();
                    }
                }
                if (!mTxtGame.getText().toString().trim().equals("") && !mTxtReceive.getText().toString().trim().equals("")) {
                    mBtnConfirm.setVisibility(View.VISIBLE);
                } else {
                    mBtnConfirm.setVisibility(View.INVISIBLE);
                }

                handler.postDelayed(this, 1000);
            }
        });


        mBtnConfirm.setOnClickListener(v -> {
            String title = mTxtGame.getText().toString().trim();
            int card_uid = student.get(0).getCard_uid();
            Timestamp timestampBorrow = new Timestamp(date.getTime());
            byte returned = 0;
            Log.i("Card UID", "" + card_uid);
            Log.i("Title", title);
            Log.i("Timestamp", "" + timestampBorrow);
            Log.i("returned", "" + returned);
            saveLoan(card_uid, title, timestampBorrow, returned);
        });


        mBtnClearInput = findViewById(R.id.btnClearInput);

        //Clear inputs of text fields
        mBtnClearInput.setOnClickListener(arg0 -> {
            if(b.get("Connect").equals("true")) {
                mTxtReceive.setText("");
            }
        });
    }

    private void returnGame() {
        isAlertShowing = true;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setMessage("Is " + student.get(0).getStudentName() + " returning " + student.get(0).getTitle() + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    int card_uid = student.get(0).getCard_uid();
                    String title = student.get(0).getTitle();
                    Timestamp timestampReturn = new Timestamp(date.getTime());
                    byte returned = 1;
                    returnItem(card_uid, title, timestampReturn, returned);
                    student=null;
                    mTxtReceive.setText("");
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //reverses the UID when called similar to how IT-service does it with their scanner
    public static String reverse(String inputString){
        String[] words = inputString.split("\\s");
        String outputString = "";
        for (int i = words.length - 1; i >= 0; i--) {
            outputString = outputString + words[i];
        }
        return outputString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.bluetooth:
                startActivity(new Intent(getApplicationContext(), BluetoothActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        mTxtGame.setText(items.get(position).getTitle());
    }

    @Override
    public void showLoading() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefresh.setRefreshing(false);
    }


    // get data from database and updates the recyclerview
    @Override
    public void onGetResult(List<Item> items) {
        mAdapter = new ItemRecyclerAdapter(items, this);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
        this.items = items;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetStudent(List<Student> student) {
        this.student = student;
    }

    //The Thread class that runs and listens for data sent via Bluetooth
    private class ReadInput implements Runnable {
        private boolean bStop = false;
        private Thread t;
        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();

                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);
                        String hex = strInput.toUpperCase();
                        hex = reverse(hex);
                        Integer cardUID = Integer.parseInt(hex, 16);
                        Log.i("Card UID is ", "" + cardUID);

                        mTxtReceive.post(() -> {
                            presenter.getStudentData(cardUID);
                            if (student != null) {
                                mTxtReceive.setText(student.get(0).getStudentName());
                            } else{
                                Log.i("student", "is null");
                            }
                        });

                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    /**This calls disconnects the phone from the Arduino if the activity is set on pause to avoid crashing. AsyncTask is a form of Thread
     that runs in the background without disrupting anything
     */

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning()) ; // Wait until it stops
                mReadThread = null;
            }
            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    /**
     * Used to make easier Toasts
     * @param s
     */
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    /**
     * Calls the disconnect method
     */
    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    /**
     * Reestablishes the connection with the arduino
     */
    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            if(!getIntent().getStringExtra("Connect").equals("false")){
                new ConnectBT().execute();
            }
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    /**
     * Connects to that arduino and starts running the ReadInput Thread
     */
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
                progressDialog = ProgressDialog.show(BrowseActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            //mDevice and mDeviceUUID is the values we got from BluetoothActivity
            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                finish();
            } else {
                //successful connection so an instance of ReadInput is created and executed
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }
            progressDialog.dismiss();
        }
    }

    /**
     * Method that does everything in the database when a loan is made by calling the methods
     * saveLoan, updateStudentBorrow, and updateItemBorrow in ApiInterface
     * @param title
     * @param card_uid
     * @param timestampBorrow
     * @param returned
     */
    private void saveLoan(final int card_uid,
                          final String title,
                          final Timestamp timestampBorrow,
                          final byte returned) {


        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Loan> callLoan =  apiInterface.saveLoan(card_uid, title, timestampBorrow, returned);

        callLoan.enqueue(new Callback<Loan>() {
            @Override
            public void onResponse(@NonNull Call<Loan> call, @NonNull Response<Loan> response) {

                Log.i("onResponse", "try Loan");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().isSuccess();
                    if (success) {
                        Log.i("onResponse", "success Loan");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.i("onResponse", "loan" + response.body().getMessage());
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Loan> call, @NonNull Throwable t) {
                Log.i("onFailure", "loan" + t.getLocalizedMessage());
                Toast.makeText(BrowseActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
        Call<Student> callStudent =  apiInterface.updateStudent(title, card_uid);

        callStudent.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(@NonNull Call<Student> call, @NonNull Response<Student> response) {

                Log.i("onResponse", "try Student");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().getSuccess();
                    if (success) {
                        Log.i("onResponse", "success student");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.i("onResponse", "fail student");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Student> call, @NonNull Throwable t) {
                Log.i("onFailure", "failure student");
                Toast.makeText(BrowseActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
        Call<Item> callItem =  apiInterface.updateItem(title, card_uid);

        callItem.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {

                Log.i("onResponse", "try item");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().getSuccess();
                    if (success) {
                        Log.i("onResponse", "success item");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.i("onResponse", response.body().getMessage());
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                Log.i("onFailure", t.getLocalizedMessage());
                Toast.makeText(BrowseActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
     * Method that does everything in the database when an item is returned, by calling the methods
     * updateLoan, updateStudent, and updateItem in ApiInterface
     * @param title
     * @param card_uid
     * @param timestampReturn
     * @param returned
     */
    private void returnItem(final int card_uid,
                          final String title,
                          final Timestamp timestampReturn,
                          final byte returned) {


        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Loan> callReturnLoan =  apiInterface.returnLoan(card_uid, timestampReturn, returned);

        callReturnLoan.enqueue(new Callback<Loan>() {
            @Override
            public void onResponse(@NonNull Call<Loan> call, @NonNull Response<Loan> response) {

                Log.i("onResponse", "try return loan");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().isSuccess();
                    if (success) {
                        Log.i("onResponse", "success return loan");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.i("onResponse", "return loan " + response.body().getMessage());
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Loan> call, @NonNull Throwable t) {
                Log.i("onFailure", "return loan" + t.getLocalizedMessage());
                Toast.makeText(BrowseActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
        Call<Student> callStudent =  apiInterface.updateStudent(null, card_uid);

        callStudent.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(@NonNull Call<Student> call, @NonNull Response<Student> response) {

                Log.i("onResponse", "try update student");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().getSuccess();
                    if (success) {
                        Log.i("onResponse", "update student success");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.i("onResponse", "fail update student");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Student> call, @NonNull Throwable t) {
                Log.i("onFailure", "update student");
                Toast.makeText(BrowseActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
        Call<Item> callItem =  apiInterface.updateItem(title, -1);

        callItem.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {

                Log.i("onResponse", "try update item");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().getSuccess();
                    if (success) {
                        Log.i("onResponse", "update item success");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.i("onResponse", "update item " + response.body().getMessage());
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                Log.i("onFailure", "update item " + t.getLocalizedMessage());
                Toast.makeText(BrowseActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}

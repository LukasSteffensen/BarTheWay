package com.p3.bartheway.Browse;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.p3.bartheway.AddItemActivity;
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
    private Bundle b;

    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        ActivityHelper.initialize(this);

        mTxtReceive = (TextView) findViewById(R.id.txtReceive);
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

        if(b.get("Connect").equals("true")) {
            mDevice = b.getParcelable(BluetoothActivity.DEVICE_EXTRA);
            mDeviceUUID = UUID.fromString(b.getString(BluetoothActivity.DEVICE_UUID));
            mMaxChars = b.getInt(BluetoothActivity.BUFFER_SIZE);
        } else {
            mTxtReceive.setText("Not connected to Arduino");
        }
        Log.d(TAG, "Ready");

        mBtnClearInput = (Button) findViewById(R.id.btnClearInput);

        mBtnClearInput.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(b.get("Connect").equals("true")) {
                    mTxtReceive.setText("");
                }
//                This part right here was simply for testing at home without the Arduino parts.
//                It should be moved to the correct spot which is going to be in the "Confirm loan onClick"
//                byte returned = 0;
//                Timestamp timestamp = new Timestamp(date.getTime());
//                String title = mTxtGame.getText().toString().trim();
//                saveLoan(title, card_uid, timestamp, returned);
            }
        });
    }

    public static String reverseTheSentence (String inputString){
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

    @Override
    public void onGetResult(List<Item> items) {
        mAdapter = new ItemRecyclerAdapter(items, this);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
        this.items = items;
        Log.i("hallo", ""+items.get(1).getMaxPlayers());
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetStudent(List<Student> student) {
        this.student = student;
    }

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
                        hex = reverseTheSentence(hex);
                        Integer cardUID = Integer.parseInt(hex, 16);

                        mTxtReceive.post(new Runnable() {
                            @Override
                            public void run() {
                                presenter.getStudentData(cardUID);
                                if (student != null) {
                                    mTxtReceive.setText(student.get(0).getStudentName());
                                } else{
                                    Log.i("student", "is null");
                                }
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

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

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

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
                progressDialog = ProgressDialog.show(BrowseActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

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
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }
    }

    /**
     * Method that puts values into the database by calling the method "saveLoan" in ApiInterface
     * @param title
     * @param card_uid
     * @param timestamp
     * @param returned
     */
    private void saveLoan(final String title,
                          final int card_uid,
                          final Timestamp timestamp,
                          final byte returned) {


        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Loan> call =  apiInterface.saveLoan(title, card_uid, timestamp, returned);

        call.enqueue(new Callback<Loan>() {
            @Override
            public void onResponse(@NonNull Call<Loan> call, @NonNull Response<Loan> response) {

                Log.i("onResponse", "try");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().isSuccess();
                    if (success) {
                        Log.i("onResponse", "success");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.i("onResponse", "fail");
                        Toast.makeText(BrowseActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("onResponse", "is null or not successful");
                    Log.i("onResponse", response.toString());
                    Log.i("onResponse", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Loan> call, @NonNull Throwable t) {
                Log.i("onFailure", t.getLocalizedMessage());
                Toast.makeText(BrowseActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}

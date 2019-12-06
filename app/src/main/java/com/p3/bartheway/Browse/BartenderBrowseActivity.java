package com.p3.bartheway.Browse;

import java.io.IOException;
import java.io.InputStream;
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
import android.os.Handler;
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
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.p3.bartheway.AddItemActivity;
import com.p3.bartheway.Database.ApiClient;
import com.p3.bartheway.Database.ApiInterface;
import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.Login.LoginActivity;
import com.p3.bartheway.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BartenderBrowseActivity extends AppCompatActivity implements ItemRecyclerAdapter.OnClickListener, BrowseView{

    private static final String TAG = "BluetoothActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    SearchView mSearchView;

    SwipeRefreshLayout swipeRefresh;
    BrowsePresenter presenter;

    List<Item> items;

    List<Student> student;

    private boolean mIsUserInitiatedDisconnect = false;

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
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        mTxtReceive = findViewById(R.id.txtReceive);
        mTxtGame = findViewById(R.id.txtGame);
        mRecyclerView = findViewById(R.id.item_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        apiInterface = ApiClient
                .getApiClient()
                .create(ApiInterface.class);

        mSearchView = findViewById(R.id.search_view_browse);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        presenter = new BrowsePresenter(this);
        presenter.getItemData();

        swipeRefresh.setOnRefreshListener(
                () -> presenter.getItemData()
        );

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });


        final Intent intent = getIntent();
        b = intent.getExtras();

        //checks if any information were given from the previous activity and either connects to the arduino or does nothing
        if (b != null) {
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
                        mBtnConfirm.setAlpha(.3f);
                        mBtnConfirm.setClickable(false);
                        returnGame();
                    }
                }
                if (!mTxtGame.getText().toString().trim().equals("") && !mTxtReceive.getText().toString().trim().equals("") && !mTxtReceive.getText().toString().trim().equals("Not connected to Arduino")) {
                    mBtnConfirm.setAlpha(1);
                    mBtnConfirm.setClickable(true);
                } else {
                    mBtnConfirm.setAlpha(.3f);
                    mBtnConfirm.setClickable(false);
                }

                handler.postDelayed(this, 500);
            }
        });


        mBtnConfirm.setOnClickListener(v -> {
            Date date = new Date();
            String title = mTxtGame.getText().toString().trim();
            title = title.replaceAll("'", "''");
            long card_uid = student.get(0).getCard_uid();
            String timestampBorrow = new Timestamp(date.getTime()).toString();
            timestampBorrow = removeTimestampDecimals(timestampBorrow);
            byte returned = 0;
            presenter.saveLoan(this, card_uid, title, timestampBorrow, returned);
            mAdapter.notifyDataSetChanged();
            student = null;
            mTxtGame.setText("");
            mTxtReceive.setText("");
            mTxtReceive.setBackgroundResource(R.drawable.text_view_border1);
            mTxtGame.setBackgroundResource(R.drawable.text_view_border1);
            swipeRefresh.setRefreshing(true);
        });

        mBtnClearInput = findViewById(R.id.btnClearInput);

        //Clear inputs of text fields
        mBtnClearInput.setOnClickListener(arg0 -> {
            if (b.get("Connect").equals("true")) {
                mTxtReceive.setText("");
            }
            mTxtGame.setText("");
            mAdapter.selected_position = -1;
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onBackPressed() {
        String s = "back";
        logOutPressed(s);
    }

    private void returnGame() {
        isAlertShowing = true;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setMessage("Is " + student.get(0).getStudentName() + " returning " + student.get(0).getTitle() + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Date date = new Date();
                    mTxtGame.setBackgroundResource(R.drawable.text_view_border1);
                    mTxtReceive.setBackgroundResource(R.drawable.text_view_border1);

                    long card_uid = student.get(0).getCard_uid();
                    String title = student.get(0).getTitle();
                    title = title.replaceAll("'", "''");
                    String timestampReturn = new Timestamp(date.getTime()).toString();
                    timestampReturn = removeTimestampDecimals(timestampReturn);
                    byte returned = 1;
                    presenter.returnItem(this, card_uid, title, timestampReturn, returned);
                    student = null;
                    isAlertShowing = false;
                    mTxtReceive.setText("");
                }).setNegativeButton("No", ((dialog, which) -> {
            student = null;

            isAlertShowing = false;
            dialog.cancel();
        }));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //reverses the UID when called similar to how IT-service does it with their scanner
    public static String reverse(String inputString) {
        String[] words = inputString.split("\\s");
        String outputString = "";
        for (int i = words.length - 1; i >= 0; i--) {
            outputString = outputString + words[i];
        }
        return outputString;
    }

    void logOutPressed(String s) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        if (s.equals("back")) {
            builder.setMessage("Are you sure you want quit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // this will always exit the app
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }).setNegativeButton("No", ((dialog, which) -> {
                dialog.cancel();
            }));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            builder.setMessage("Are you sure you want to log out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }).setNegativeButton("No", ((dialog, which) -> {
                dialog.cancel();
            }));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth:
                startActivity(new Intent(getApplicationContext(), BluetoothActivity.class));
                return true;
            case R.id.current_borrowers:
                startActivity(new Intent(getApplicationContext(), CurrentBorrowersActivity.class));
                return true;
            case R.id.previous_borrowers:
                startActivity(new Intent(getApplicationContext(), PreviousBorrowersActivity.class));
                return true;
            case R.id.account_settings:
                return true;
            case R.id.delete_game:
                startActivity(new Intent(getApplicationContext(), DeleteItemActivity.class));
                return true;
            case R.id.add_game:
                startActivity(new Intent(getApplicationContext(), AddItemActivity.class));
                return true;
            case R.id.logout:
                String s = "logout";
                logOutPressed(s);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        if (items.get(position).getCardUid() > 0) {
            Toast.makeText(this, "This game is already borrowed", Toast.LENGTH_SHORT).show();
        } else {
            mTxtGame.setText(items.get(position).getTitle());
            mTxtGame.setBackgroundResource(R.drawable.text_view_border2);
        }

    }

    @Override
    public void showLoading() {
        swipeRefresh.setRefreshing(true);
        mTxtGame.setText("");
        mTxtReceive.setText("");
        mTxtReceive.setBackgroundResource(R.drawable.text_view_border1);
        mTxtGame.setBackgroundResource(R.drawable.text_view_border1);
    }

    @Override
    public void hideLoading() {
        swipeRefresh.setRefreshing(false);
    }


    // get data from database and updates the recyclerView
    @Override
    public void onGetResult(List<Item> items) {
        mAdapter = new ItemRecyclerAdapter(items, this, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        this.items = items;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetLoans(List<Loan> loans) {

    }

    @Override
    public void onGetStudent(List<Student> student) {
        this.student = student;
    }

    void getStudentData(long card_uid){

        showLoading();

        Call<List<Student>> call = apiInterface.getStudent(card_uid);
        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(@NonNull Call<List<Student>> call, @NonNull Response<List<Student>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    onGetStudent(response.body());
                    Log.i("getStudentDataSuccess", response.body().toString());
                    if (student != null) {
                        mTxtReceive.setText(student.get(0).getStudentName());
                        mTxtReceive.setBackgroundResource(R.drawable.text_view_border2);
                    } else {
                        Log.i("student", "is null");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Student>> call, @NonNull Throwable t) {
                hideLoading();
                onErrorLoading(t.getLocalizedMessage());
                Log.i("getStudentDataFail", t.toString());
            }
        });
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
                        if(hex.length() == 8) {
                            Long cardUID = Long.parseLong(hex, 16);
                            Log.i("Card UID is ", "" + cardUID);
                            mTxtReceive.post(() -> {
                                getStudentData(cardUID);
                            });
                        }
                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    /**
     * This calls disconnects the phone from the Arduino if the activity is set on pause to avoid crashing. AsyncTask is a form of Thread
     * that runs in the background without disrupting anything
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
     *
     * @param s
     */
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
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
            if (!getIntent().getStringExtra("Connect").equals("false")) {
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

    /**
     * Connects to that arduino and starts running the ReadInput Thread
     */
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(BartenderBrowseActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
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
                msg("Could not connect to device. Is the device powered and within range?");
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

    public static String removeTimestampDecimals(String s) {
        return (s == null || s.length() == 3)
                ? null
                : (s.substring(0, 19));
    }
}
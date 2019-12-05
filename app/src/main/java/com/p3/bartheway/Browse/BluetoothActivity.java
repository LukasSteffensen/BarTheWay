package com.p3.bartheway.Browse;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.p3.bartheway.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BluetoothActivity extends AppCompatActivity {
    private Button search;
    private Button connect;
    private ListView listView;
    private BluetoothAdapter mBTAdapter;
    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int mBufferSize = 50000; //Default
    public static final String DEVICE_EXTRA = "SOCKET";
    public static final String DEVICE_UUID = "uuid";
    private static final String DEVICE_LIST = "devicelist";
    private static final String DEVICE_LIST_SELECTED = "devicelistselected";
    public static final String BUFFER_SIZE = "buffersize";
    private static final String TAG = "BluetoothActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        search = (Button) findViewById(R.id.button_search);
        connect = (Button) findViewById(R.id.button_connect);

        listView = (ListView) findViewById(R.id.listView);

       // Sets up the listView with the adapter and tries to get any old list (if the screen was rotated for example)
        if (savedInstanceState != null) {
            ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(DEVICE_LIST);
            if (list != null) {
                initList(list);
                BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) listView.getAdapter();
                int selectedIndex = savedInstanceState.getInt(DEVICE_LIST_SELECTED);
                if (selectedIndex != -1) {
                    adapter.setSelectedIndex(selectedIndex);
                    connect.setEnabled(true);
                }
            } else {
                initList(new ArrayList<BluetoothDevice>());
            }

        } else {
            initList(new ArrayList<BluetoothDevice>());
        }


        /*
        Whenever the search button is clicked this will happen. First it checks if the phone has BT, then it checks if BT is enabled and asks
        for it to be enabled. Then finally it creates an instance of the thread SearchDevices (which is at the end of this class) and executes it.
         */
        search.setOnClickListener(arg0 -> {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBTAdapter == null) {
                Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
            } else if (!mBTAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, BT_ENABLE_REQUEST);
            } else {
                new SearchDevices().execute();
            }
        });

        //If a device has been selected, the BartenderBrowseActivity will open with a bunch of extras from this activity
        connect.setOnClickListener(arg0 -> {
            if (((BluetoothDeviceAdapter) (listView.getAdapter())).isSelected()) {
                BluetoothDevice device = ((BluetoothDeviceAdapter) (listView.getAdapter())).getSelectedItem();

                Intent intent = new Intent(getApplicationContext(), BartenderBrowseActivity.class);
                intent.putExtra(DEVICE_EXTRA, device);
                intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
                intent.putExtra(BUFFER_SIZE, mBufferSize);
                intent.putExtra("Connect", "true");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**Whenever the search  button is clicked and the user has to enable or disable Bluetooth, the OnActivityResult is called.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Gets the answer form the user (yes or no) and acts accordingly
        switch (requestCode) {
            case BT_ENABLE_REQUEST:
                if (resultCode == RESULT_OK) {
                    msg("Bluetooth Enabled successfully");
                    new SearchDevices().execute();
                } else {
                    msg("Bluetooth couldn't be enabled");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Quick way to call the Toast
     * @param str
     */
    private void msg(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize the List adapter
     * @param objects
     */
    private void initList(List<BluetoothDevice> objects) {
        final BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(getApplicationContext(), R.layout.list_item, R.id.lstContent, objects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                connect.setEnabled(true);
            }
        });
    }

    /**
     * Searches for paired devices. Doesn't do a scan! Only devices which are paired through Settings->Bluetooth
     * will show up with this. I didn't see any need to re-build the wheel over here
     * @author ryder
     *
     */
    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
            return new ArrayList<>(pairedDevices);
        }

        @Override
        protected void onPostExecute(List<BluetoothDevice> listDevices) {
            super.onPostExecute(listDevices);
            if (listDevices.size() > 0) {
                BluetoothDeviceAdapter adapter = (BluetoothDeviceAdapter) listView.getAdapter();
                adapter.replaceItems(listDevices);
            } else {
                msg("No paired devices found, please pair your serial BT device and try again");
            }
        }

    }
}

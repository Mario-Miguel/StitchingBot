package es.uniovi.eii.stitchingbot

import android.os.Bundle
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        drawerLayout= findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_logo_list, R.id.nav_create_logo, R.id.nav_load_file, R.id.nav_sewing_machines, R.id.nav_arduino_configuration), drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }




//    /* Broadcast receiver to listen for discovery results. */
//    public val bluetoothDiscoveryResult = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.i("BluetoothStitching", "Un dispositivo mas")
//            if (intent?.action == BluetoothDevice.ACTION_FOUND) {
//                Log.i("BluetoothStitching", "Un dispositivo mas")
//                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
//                deviceListAdapter.addDevice(device)
//            }
//        }
//    }
//
//    /* Broadcast receiver to listen for discovery updates. */
//    public val bluetoothDiscoveryMonitor = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.i("BluetoothStitching", "Un dispositivo mas")
//            when (intent?.action) {
//                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
//                    //progress_bar.visible()
//                    toast("Scan started...")
//                }
//                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
//                    //progress_bar.invisible()
//                    toast("Scan complete. Found ${deviceListAdapter.itemCount} devices.")
//                }
//            }
//        }
//    }


}
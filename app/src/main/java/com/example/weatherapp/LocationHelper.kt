import android.content.Context
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // Function to get current location
    fun getCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            val locationTask: Task<Location> = fusedLocationClient.lastLocation
            locationTask.addOnSuccessListener { location ->
                // Called when location is received
                onLocationReceived(location)
            }
        } else {
            // Handle location permission denied
            onLocationReceived(null)
        }
    }
}

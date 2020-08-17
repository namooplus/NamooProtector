package nm.security.namooprotector.bundle

import android.graphics.drawable.Drawable

data class AppBundle(val icon: Drawable, val label: String, val packageName: String, var state: Boolean)
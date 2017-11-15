package nz.zhang.aucklandtransportwear.wakaapi

enum class Cities (val cityName: String, val shortCode: String, val defaultLat: Double, val defaultLong: Double){
    AUCKLAND("Auckland", "nz-akl", -36.844, 174.766),
    WELLINGTON("Wellington", "nz-wlg", -41.279, 174.779);

    override fun toString(): String {
        return cityName
    }

}
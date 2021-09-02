package krypton.absenmobile.api.model;

import java.lang.reflect.Array;

public class UserDetails {
    private String name;
    private String username;
    private String longitude;
    private String latitude;
    private boolean is_dudi;
    private boolean is_guru;
    private boolean is_superuser;
    private String[] absen;

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public boolean isDudi() {
        return is_dudi;
    }

    public boolean isGuru() {
        return is_guru;
    }

    public boolean isSuperuser() {
        return is_superuser;
    }

    public String[] getAbsen() {
        return absen;
    }
}

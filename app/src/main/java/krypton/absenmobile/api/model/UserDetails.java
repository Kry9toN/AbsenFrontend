package krypton.absenmobile.api.model;

import java.util.List;

public class UserDetails {

    private String password;
    private String lastLogin;
    private String username;
    private String name;
    private Boolean is_dudi;
    private Boolean is_guru;
    private Boolean is_active;
    private Boolean is_staff;
    private Boolean is_admin;
    private Boolean is_superuser;
    private Integer longitude;
    private Integer latitude;
    private List<Object> absen = null;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsDudi() {
        return is_dudi;
    }

    public void setIsDudi(Boolean isDudi) {
        this.is_dudi = isDudi;
    }

    public Boolean getIsGuru() {
        return is_guru;
    }

    public void setIsGuru(Boolean isGuru) {
        this.is_guru = isGuru;
    }

    public Boolean getIsActive() {
        return is_active;
    }

    public void setIsActive(Boolean isActive) {
        this.is_active = isActive;
    }

    public Boolean getIsStaff() {
        return is_staff;
    }

    public void setIsStaff(Boolean isStaff) {
        this.is_staff = isStaff;
    }

    public Boolean getIsAdmin() {
        return is_admin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.is_admin = isAdmin;
    }

    public Boolean getIsSuperuser() {
        return is_superuser;
    }

    public void setIsSuperuser(Boolean isSuperuser) {
        this.is_superuser = isSuperuser;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public Integer getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public List<Object> getAbsen() {
        return absen;
    }

    public void setAbsen(List<Object> absen) {
        this.absen = absen;
    }

}

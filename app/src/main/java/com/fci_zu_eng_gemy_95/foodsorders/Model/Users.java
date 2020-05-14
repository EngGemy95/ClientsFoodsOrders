package com.fci_zu_eng_gemy_95.foodsorders.Model;

public class Users {
    String Name , Password , phone , SecureCode;
    String IsStaff ;

    public Users() {
    }

    public Users(String name, String password , String secureCode) {
        Name = name;
        Password = password;
        IsStaff = "false";
        SecureCode = secureCode ;
    }

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}

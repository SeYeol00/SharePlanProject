package com.example.shareplan;

public class UserInfo
{
    String Email;
    String Id; //Uid
    String Password;
    String Name;
    String PhoneNumber;
    String Address;
    Boolean Authority; //권한

    public UserInfo() { }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Boolean getAuthority() {
        return Authority;
    }

    public void setAuthority(Boolean authority) {
        Authority = authority;
    }
}

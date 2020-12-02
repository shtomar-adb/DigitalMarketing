package com.app.cloud.request;

public class User {

    private String name;
    private String age;
    private String email;
    private String gender;
    private String dob;
    private String phone;

    public User(String name,
                String email, String phone,
                String dob , String age, String gender){

        this.name = name;
        this.age  = age;
        this.email = email;
        this.gender = gender;
        this.phone =phone;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

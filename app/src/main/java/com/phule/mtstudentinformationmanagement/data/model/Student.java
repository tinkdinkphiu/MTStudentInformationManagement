package com.phule.mtstudentinformationmanagement.data.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Comparator;

public class Student implements Serializable {
    @PropertyName("Code")
    private String code;
    @PropertyName("Name")
    private String name;
    @PropertyName("Birthday")
    private String birthday;
    @PropertyName("Address")
    private String address;
    @PropertyName("Gender")
    private String gender;
    @PropertyName("Phone")
    private String phone;
    @PropertyName("EnrollmentDate")
    private String enrollmentDate;
    @PropertyName("Major")
    private String major;

    public Student() {

    }
    public Student(String code, String name, String birthday, String address, String gender, String phone, String enrollmentDate, String major) {
        this.code = code;
        this.name = name;
        this.birthday = birthday;
        this.address = address;
        this.gender = gender;
        this.phone = phone;
        this.enrollmentDate = enrollmentDate;
        this.major = major;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    // Code sort
    public static Comparator<Student> studentCodeASCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.getCode().compareTo(s2.getCode());
        }
    };

    public static Comparator<Student> studentCodeDESCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s2.getCode().compareTo(s1.getCode());
        }
    };

    // Name sort
    public static Comparator<Student> studentNameASCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.getName().compareTo(s2.getName());
        }
    };

    public static Comparator<Student> studentNameDESCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s2.getName().compareTo(s1.getName());
        }
    };

    // Birthday sort
    public static Comparator<Student> studentBirthdayASCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.getBirthday().compareTo(s2.getBirthday());
        }
    };

    public static Comparator<Student> studentBirthdayDESCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s2.getBirthday().compareTo(s1.getBirthday());
        }
    };

    // Address sort
    public static Comparator<Student> studentAddressASCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.getAddress().compareTo(s2.getAddress());
        }
    };

    public static Comparator<Student> studentAddressDESCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s2.getAddress().compareTo(s1.getAddress());
        }
    };

    // Gender sort
    public static Comparator<Student> studentGenderASCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.getGender().compareTo(s2.getGender());
        }
    };

    public static Comparator<Student> studentGenderDESCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s2.getGender().compareTo(s1.getGender());
        }
    };

    // Phone sort
    public static Comparator<Student> studentPhoneASCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.getPhone().compareTo(s2.getPhone());
        }
    };

    public static Comparator<Student> studentPhoneDESCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s2.getPhone().compareTo(s1.getPhone());
        }
    };

    // Enrollment date sort
    public static Comparator<Student> studentEnrollmentDateASCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.getEnrollmentDate().compareTo(s2.getEnrollmentDate());
        }
    };

    public static Comparator<Student> studentEnrollmentDateDESCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s2.getEnrollmentDate().compareTo(s1.getEnrollmentDate());
        }
    };

    // Major sort
    public static Comparator<Student> studentMajorASCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.getMajor().compareTo(s2.getMajor());
        }
    };

    public static Comparator<Student> studentMajorDESCComparator = new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s2.getMajor().compareTo(s1.getMajor());
        }
    };

}

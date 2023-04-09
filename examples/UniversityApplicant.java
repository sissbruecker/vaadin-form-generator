public class UniversityApplicant {
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String phoneNumber;
    private String address;
    private String highSchoolName;
    private int highSchoolGraduationYear;
    private double gpa;

    public UniversityApplicant(String firstName, String lastName, int age, String email, String phoneNumber, String address, String highSchoolName, int highSchoolGraduationYear, double gpa) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.highSchoolName = highSchoolName;
        this.highSchoolGraduationYear = highSchoolGraduationYear;
        this.gpa = gpa;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHighSchoolName() {
        return highSchoolName;
    }

    public void setHighSchoolName(String highSchoolName) {
        this.highSchoolName = highSchoolName;
    }

    public int getHighSchoolGraduationYear() {
        return highSchoolGraduationYear;
    }

    public void setHighSchoolGraduationYear(int highSchoolGraduationYear) {
        this.highSchoolGraduationYear = highSchoolGraduationYear;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }
}

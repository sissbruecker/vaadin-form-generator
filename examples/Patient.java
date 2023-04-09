public class Patient {
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;
    private String bloodGroup;
    private String allergies;
    private String medicalHistory;

    public Patient(String name, int age, String gender, String address, String phoneNumber, String bloodGroup, String allergies, String medicalHistory) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.medicalHistory = medicalHistory;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public String toString() {
        return "Patient{" +
               "name='" + name + '\'' +
               ", age=" + age +
               ", gender='" + gender + '\'' +
               ", address='" + address + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", bloodGroup='" + bloodGroup + '\'' +
               ", allergies='" + allergies + '\'' +
               ", medicalHistory='" + medicalHistory + '\'' +
               '}';
    }
}

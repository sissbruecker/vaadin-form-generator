import java.util.Date;

public class PresidentialApplicant {

    // fields to store applicant information
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String address;
    private String phone;
    private String email;
    private String partyAffiliation;
    private String education;
    private String occupation;
    private String experience;
    private String runningMate;
    private Date birthDate;
    private Date applicationDate;

    // constructor to initialize applicant information
    public PresidentialApplicant(String firstName, String lastName, int age, String gender, String address, String phone,
                                 String email, String partyAffiliation, String education, String occupation, String experience,
                                 String runningMate, Date birthDate, Date applicationDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.partyAffiliation = partyAffiliation;
        this.education = education;
        this.occupation = occupation;
        this.experience = experience;
        this.runningMate = runningMate;
        this.birthDate = birthDate;
        this.applicationDate = applicationDate;
    }

    // getters and setters for all fields
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPartyAffiliation() {
        return partyAffiliation;
    }

    public void setPartyAffiliation(String partyAffiliation) {
        this.partyAffiliation = partyAffiliation;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getRunningMate() {
        return runningMate;
    }

    public void setRunningMate(String runningMate) {
        this.runningMate = runningMate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }
}

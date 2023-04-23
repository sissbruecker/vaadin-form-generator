public class Pet {
    private String name;
    private String species;
    private String breed;
    private int age;
    private String gender;
    private String color;
    private boolean isNeutered;
    private boolean isVaccinated;
    private String microchipId;
    private String ownerName;
    private String ownerPhoneNumber;
    private String vetName;
    private String vetPhoneNumber;

    // Constructor
    public Pet(String name, String species, String breed, int age, String gender, String color,
               boolean isNeutered, boolean isVaccinated, String microchipId, String ownerName,
               String ownerPhoneNumber, String vetName, String vetPhoneNumber) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.color = color;
        this.isNeutered = isNeutered;
        this.isVaccinated = isVaccinated;
        this.microchipId = microchipId;
        this.ownerName = ownerName;
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.vetName = vetName;
        this.vetPhoneNumber = vetPhoneNumber;
    }

    // Getters and setters for all fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isNeutered() {
        return isNeutered;
    }

    public void setNeutered(boolean neutered) {
        isNeutered = neutered;
    }

    public boolean isVaccinated() {
        return isVaccinated;
    }

    public void setVaccinated(boolean vaccinated) {
        isVaccinated = vaccinated;
    }

    public String getMicrochipId() {
        return microchipId;
    }

    public void setMicrochipId(String microchipId) {
        this.microchipId = microchipId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }

    public String getVetPhoneNumber() {
        return vetPhoneNumber;
    }

    public void setVetPhoneNumber(String vetPhoneNumber) {
        this.vetPhoneNumber = vetPhoneNumber;
    }
}

package models;

public class Brand {
    private int brandId;
    private String name;
    private String description;

    // Constructeur par défaut
    public Brand() {
    }

    // Constructeur avec paramètres
    public Brand(int brandId, String name, String description) {
        this.brandId = brandId;
        this.name = name;
        this.description = description;
    }

    // Getters et setters
    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "brandId=" + brandId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}


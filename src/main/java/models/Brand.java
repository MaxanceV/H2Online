package models;

/**
 * Represents a brand entity with an ID, name, and description.
 */
public class Brand {
    private int brandId;
    private String name;
    private String description;

    /**
     * Default constructor for Brand.
     */
    public Brand() {
    }

    /**
     * Constructs a Brand with the specified ID, name, and description.
     *
     * @param brandId The unique identifier of the brand.
     * @param name The name of the brand.
     * @param description The description of the brand.
     */
    public Brand(int brandId, String name, String description) {
        this.brandId = brandId;
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the brand ID.
     *
     * @return The brand ID.
     */
    public int getBrandId() {
        return brandId;
    }

    /**
     * Sets the brand ID.
     *
     * @param brandId The brand ID to set.
     */
    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    /**
     * Gets the name of the brand.
     *
     * @return The brand name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the brand.
     *
     * @param name The brand name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the brand.
     *
     * @return The brand description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the brand.
     *
     * @param description The brand description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a string representation of the brand object.
     *
     * @return A string representation of the brand.
     */
    @Override
    public String toString() {
        return "Brand{" +
                "brandId=" + brandId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

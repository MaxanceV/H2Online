// Category class
package models;

/**
 * Represents a category entity with an ID, name, and description.
 */
public class Category {
    private int categoryId;
    private String name;
    private String description;

    /**
     * Default constructor for Category.
     */
    public Category() {
    }

    /**
     * Constructs a Category with the specified ID, name, and description.
     *
     * @param categoryId The unique identifier of the category.
     * @param name The name of the category.
     * @param description The description of the category.
     */
    public Category(int categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the category ID.
     *
     * @return The category ID.
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the category ID.
     *
     * @param categoryId The category ID to set.
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the name of the category.
     *
     * @return The category name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the category.
     *
     * @param name The category name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the category.
     *
     * @return The category description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the category.
     *
     * @param description The category description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a string representation of the category object.
     *
     * @return A string representation of the category.
     */
    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
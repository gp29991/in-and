package models;

public class Category {
    private int categoryId;
    private String categoryName;
    private String username;

    public Category(int categoryId, String categoryName, String username) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.username = username;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

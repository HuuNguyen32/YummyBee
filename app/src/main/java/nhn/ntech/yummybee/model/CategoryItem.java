package nhn.ntech.yummybee.model;

public class CategoryItem {

    private String id;
    private String name;
    private int order;
    private String imageUrl;

    public CategoryItem(){

    }

    public CategoryItem(String id, String name, int order, String imageUrl) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

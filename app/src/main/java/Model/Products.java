package Model;

public class Products {

    private String Name,Image,Price,Product_Id,Category,Date,Time;

    public Products(){

    }

    public Products(String name, String image, String price, String product_Id, String category, String date, String time) {
        Name = name;
        Image = image;
        Price = price;
        Product_Id = product_Id;
        Category = category;
        Date = date;
        Time = time;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getProduct_Id() {
        return Product_Id;
    }

    public void setProduct_Id(String product_Id) {
        Product_Id = product_Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String pname) {
        Name = pname;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}

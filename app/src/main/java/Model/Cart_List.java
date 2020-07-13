package Model;

public class Cart_List {

    private String Date,Name,Price,Product_Id,Time,quantity;

    public Cart_List(String date, String name, String price, String product_Id, String time, String quantity) {
        Date = date;
        Name = name;
        Price = price;
        Product_Id = product_Id;
        Time = time;
        this.quantity = quantity;
    }

    public Cart_List(){

    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getProduct_Id() {
        return Product_Id;
    }

    public void setProduct_Id(String product_Id) {
        Product_Id = product_Id;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

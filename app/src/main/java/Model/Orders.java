package Model;

public class Orders {

    String Name, Time,Date,Phone,Address,Price,State,Payment,TimeSlot;

    public Orders(){

    }

    public Orders(String userName, String time, String date, String phone, String address, String price, String state, String payment, String timeSlot) {
        Name = userName;
        Time = time;
        Date = date;
        Phone = phone;
        Address = address;
        Price = price;
        State = state;
        Payment = payment;
        TimeSlot = timeSlot;
    }

    public String getTimeSlot() {
        return TimeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        TimeSlot = timeSlot;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String total_Price) {
        Price = total_Price;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}

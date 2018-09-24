package aesc.dev.androiddrinkshop.Model;

public class Order {

    //OrderId Ascendente 1
    //OrderStatus
    //OrderPrice
    //OrderDetall
    //OrderComment
    //OrderAddress
    //UserPhone
    private long OrderId;
    private int OrderStatus;
    private float OrderPrice;
    private String OrderDetall;
    private String OrderComment;
    private String OrderAddress;
    private String UserPhone;

    public Order() {
    }

    public long getOrderId() {
        return OrderId;
    }

    public void setOrderId(long orderId) {
        OrderId = orderId;
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        OrderStatus = orderStatus;
    }

    public float getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(float orderPrice) {
        OrderPrice = orderPrice;
    }

    public String getOrderDetall() {
        return OrderDetall;
    }

    public void setOrderDetall(String orderDetall) {
        OrderDetall = orderDetall;
    }

    public String getOrderComment() {
        return OrderComment;
    }

    public void setOrderComment(String orderComment) {
        OrderComment = orderComment;
    }

    public String getOrderAddress() {
        return OrderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        OrderAddress = orderAddress;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}

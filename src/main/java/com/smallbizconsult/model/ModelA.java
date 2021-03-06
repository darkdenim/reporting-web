package com.smallbizconsult.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModelA {

    private long rowId;
    private long orderId;
    private Date orderDate;
    private Priority priority;
    private int quantity;
    private double sales;
    private double discount;
    private ShipMode shipMode;
    private double profit;
    private double unitPrice;
    private double shipping;
    private String customer;
    private String province;
    private String region;
    private Segment segment;
    private String productCategory;
    private String productSubCategory;
    private String productName;
    private String productContainer;
    private double baseMargin;
    private Date shipDate;

    public ModelA(List<Object> row) throws ParseException {
        System.out.println("Row: " + row.toString());
        this.rowId = ((Double) row.get(Headings.RowID.ordinal())).longValue();
        this.orderId = ((Double) row.get(Headings.OrderID.ordinal())).longValue();
        this.orderDate = toDate(row.get(Headings.OrderDate.ordinal()));
        this.priority = Priority.getPriority(row.get(Headings.OrderPriority.ordinal()).toString());
        this.quantity = ((Double) row.get(Headings.OrderQuantity.ordinal())).intValue();
        this.sales = toDouble(row.get(Headings.Sales.ordinal()));
        this.discount = toDouble(row.get(Headings.SalesDiscount.ordinal()));
        this.shipMode = ShipMode.getShipMode(row.get(Headings.ShipMode.ordinal()).toString());
        this.profit = toDouble(row.get(Headings.Profit.ordinal()));
        this.unitPrice = toDouble(row.get(Headings.UnitPrice.ordinal()));
        this.shipping = toDouble(row.get(Headings.ShippingCost.ordinal()));
        this.customer = row.get(Headings.CustomerName.ordinal()).toString();
        this.province = row.get(Headings.Province.ordinal()).toString();
        this.region = row.get(Headings.Region.ordinal()).toString();
        this.segment = Segment.getSegment(row.get(Headings.CustomerSegment.ordinal()).toString());
        this.productCategory = row.get(Headings.ProductCategory.ordinal()).toString();
        this.productSubCategory = row.get(Headings.ProductSubCategory.ordinal()).toString();
        this.productName = row.get(Headings.ProductName.ordinal()).toString();
        this.productContainer = row.get(Headings.ProductContainer.ordinal()).toString();
        this.baseMargin = toDouble(row.get(Headings.ProductBaseMargin.ordinal()));

//        String formula = row.get(Headings.ShipDate.ordinal()).toString();
//        int x = Integer.parseInt(formula.substring(2));
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(this.orderDate);
//        if (formula.startsWith("+")) {
//            calendar.add(Calendar.DAY_OF_MONTH, x);
//        } else {
//            calendar.add(Calendar.DAY_OF_MONTH, x*-1);
//        }
//        this.shipDate = calendar.getTime();
        this.shipDate = new Date();
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(List<String> row) {
        this.rowId = Long.parseLong(row.get(Headings.RowID.ordinal()));
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(List<String> row) {
        this.orderId = Long.parseLong(row.get(Headings.OrderID.ordinal()));
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public ShipMode getShipMode() {
        return shipMode;
    }

    public void setShipMode(ShipMode shipMode) {
        this.shipMode = shipMode;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductSubCategory() {
        return productSubCategory;
    }

    public void setProductSubCategory(String productSubCategory) {
        this.productSubCategory = productSubCategory;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductContainer() {
        return productContainer;
    }

    public void setProductContainer(String productContainer) {
        this.productContainer = productContainer;
    }

    public double getBaseMargin() {
        return baseMargin;
    }

    public void setBaseMargin(double baseMargin) {
        this.baseMargin = baseMargin;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    private Double toDouble(Object obj) {
        if (obj == null || obj.toString().isEmpty()) {
            return 0d;
        }
        return Double.parseDouble(obj.toString());
    }

    private Date toDate(Object obj) throws ParseException {
        String cellValue = obj.toString();
        cellValue = cellValue.replace("DATE(", "").replace(")", ",");
        String[] tokens = cellValue.split(",");
        if (tokens.length < 4) {
            return new Date();
        }
        String date = tokens[0] + "/" + tokens[1] + "/" + tokens[2];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dt = sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);

        String days = tokens[3];
        if (days.startsWith("-")) {
            calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(days.substring(1)) * -1);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(days.substring(1)));
        }

        return calendar.getTime();
    }

    @Override
    public String toString() {
        return "ModelA{" +
                "rowId=" + rowId +
                ", orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", priority=" + priority +
                ", quantity=" + quantity +
                ", sales=" + sales +
                ", discount=" + discount +
                ", shipMode=" + shipMode +
                ", profit=" + profit +
                ", unitPrice=" + unitPrice +
                ", shipping=" + shipping +
                ", customer='" + customer + '\'' +
                ", province='" + province + '\'' +
                ", region='" + region + '\'' +
                ", segment=" + segment +
                ", productCategory='" + productCategory + '\'' +
                ", productSubCategory='" + productSubCategory + '\'' +
                ", productName='" + productName + '\'' +
                ", productContainer='" + productContainer + '\'' +
                ", baseMargin=" + baseMargin +
                ", shipDate=" + shipDate +
                '}';
    }

    public enum Priority {
        Low, Medium, High, Critical;

        public static Priority getPriority(String caption) {
            for (Priority priority : Priority.values()) {
                if (priority.equals(caption)) {
                    return priority;
                }
            }
            return null;
        }
    }

    public enum Headings {
        RowID("Row ID"), OrderID("Order ID"), OrderDate("Order Date"), OrderPriority("Order Priority"),
        OrderQuantity("Order Quantity"), Sales("Sales"), SalesDiscount("Sales	Discount"), ShipMode("Ship Mode"),
        Profit("Profit"), UnitPrice("Unit Price"), ShippingCost("Shipping Cost"), CustomerName("Customer Name"),
        Province("Province"), Region("Region"), CustomerSegment("Customer Segment"), ProductCategory("Product Category"),
        ProductSubCategory("Product Sub-Category"), ProductName("Product Name"),
        ProductContainer("Product Container"), ProductBaseMargin("Product Base Margin"), ShipDate("Ship Date");

        public String caption;

        Headings(String caption) {
            this.caption = caption;
        }

        public static Headings getHeading(String caption) {
            for (Headings heading : Headings.values()) {
                if (heading.equals(caption)) {
                    return heading;
                }
            }
            return null;
        }
    }

    public enum ShipMode {
        DeliveryTruck("Delivery Truck"), RegularAir("Regular Air"), ExpressAir("Express Air");

        String caption;

        ShipMode(String caption) {
            this.caption = caption;
        }

        public static ShipMode getShipMode(String caption) {
            for (ShipMode mode : ShipMode.values()) {
                if (mode.caption.equals(caption)) {
                    return mode;
                }
            }
            return null;
        }
    }

    public enum Segment {
        Consumer("Consumer"), Corporate("Corporate"), SmallBusiness("Small Business"), HomeOffice("Home Office"),;

        String caption;

        Segment(String caption) {
            this.caption = caption;
        }

        public static Segment getSegment(String caption) {
            for (Segment segment : Segment.values()) {
                if (segment.caption.equals(caption)) {
                    return segment;
                }
            }
            return null;
        }
    }
}
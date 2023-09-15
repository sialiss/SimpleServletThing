package exchange;

public class Exchange {
    private String name;
    private Integer quantity;
    private Double cost;

    Exchange() {
        this.name = "";
        this.quantity = 0;
        this.cost = 0.0;
    }

    Exchange(String name, Integer quantity, Double cost) {
        this.name = name;
        this.quantity = quantity;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}

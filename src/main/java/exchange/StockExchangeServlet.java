package exchange;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Stock Exchange", urlPatterns = {"/stock_exchange"})
public class StockExchangeServlet extends HttpServlet {

    private String head = """
            <html>
            <head>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/purecss@3.0.0/build/pure-min.css" integrity="sha384-X38yfunGUhNzHpBaEBsWLO+A0HDYOQi8ufWDkZ0k9e0eXz/tH3II7uKZ9msv++Ls" crossorigin="anonymous">
            </head>
            <body>
            """;

    private String tail = """

            </body>
            </html>""";

    @Override
    public void init() {
        List<Exchange> stock_exchange = new ArrayList<Exchange>();

        stock_exchange.add(new Exchange("Yahoo", 1, 19.48));
        stock_exchange.add(new Exchange("Yandex", 5, 25.6));
        stock_exchange.add(new Exchange("Google", 10, 138.1));

        getServletContext().setAttribute("stock_exchange", stock_exchange);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        process(request, response);
    }

    @SuppressWarnings("unchecked")
    private List<Exchange> getExchangeList() {
        return (List<Exchange>) getServletContext().getAttribute("stock_exchange");
    }

    private void listExchange(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();

        writer.write(head);

        String responseStart = """
                <div class="header text-center">
                <h1>Stock exchange</h1>
                </div>
                <div>
                <table class="table table-primary table-hover text-center">
                <thead><th>Id</th><th>Name</th><th>Quantity</th><th>Cost/each</th><th>Sum</th><th></th></thead>
                <tbody>""";
        writer.write(responseStart);

        String template = """
                <td>
                <a class="btn btn-light" href="stock_exchange?command=delete&id=%d">Delete</a>
                <a class="btn btn-light" href="stock_exchange?command=edit&id=%d">Edit</a>
                </td>""";

        List<Exchange> stock_exchange = getExchangeList();
        for (int i = 0; i < stock_exchange.size(); i++) {
            Exchange exchange = stock_exchange.get(i);

            writer.write("\n<tr>");
            writer.write("<td>" + i + "</td>");
            writer.write("<td>" + exchange.getName() + "</td>");
            writer.write("<td>" + exchange.getQuantity() + "</td>");
            writer.write("<td>" + exchange.getCost() + "</td>");
            writer.write("<td>" + exchange.getCost()*exchange.getQuantity() + "</td>");
            writer.write(String.format(template, i, i));
            writer.write("</tr>");
        }

        String responseEnd = """

                </tbody>
                </table>
                </div>
                <br>
                <div class="text-center">
                <a class="btn btn-secondary" href="stock_exchange?command=add">Add</a>
                </div>""";
        writer.write(responseEnd);

        writer.write(tail);
    }

    private void changeExchange(HttpServletResponse response, int id) throws IOException {
        PrintWriter writer = response.getWriter();

        writer.write(head);

        String responseStart = """
                <div class="header">
                <h1>Add / edit exchange</h1>
                </div>
                <form method="post" action="?command=save" class="pure-form pure-form-aligned">
                """;
        writer.write(responseStart);

        Exchange exchange;
        if (id != -1) {
            exchange = getExchangeList().get(id);
            writer.write("<input type='hidden' name='id' value='" + id + "'>\n");
        } else {
            exchange = new Exchange();
        }

        String fieldTemplate = """
                <div class="pure-control-group">
                <label>%s</label>
                <input name="%s" value="%s" />
                </div>
                """;

        writer.write(String.format(fieldTemplate, "Name", "name", exchange.getName().toString()));
        writer.write(String.format(fieldTemplate, "Quantity", "quantity", exchange.getQuantity().toString()));
        writer.write(String.format(fieldTemplate, "Cost", "cost", exchange.getCost().toString()));

        String responseEnd = """
                <div class="pure-controls">
                <button type="submit" class="btn btn-light">Save</button>
                </div>
                </form>
                </body>
                </html>""";
        writer.write(responseEnd);

        writer.write(tail);
    }

    private void deleteExchange(HttpServletResponse response, int id) throws IOException {
        List<Exchange> stock_exchange = getExchangeList();
        stock_exchange.remove(id);

        response.sendRedirect("stock_exchange");
    }

    private void saveExchange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        int quantity = Integer.valueOf(request.getParameter("quantity"));
        double cost = Double.valueOf(request.getParameter("cost"));

        List<Exchange> stock_exchange = getExchangeList();

        String id = request.getParameter("id");
        if (id != null) {
            Exchange exchange = stock_exchange.get(Integer.valueOf(id));
            exchange.setName(name);
            exchange.setQuantity(quantity);
            exchange.setCost(cost);
        } else {
            stock_exchange.add(new Exchange(name, quantity, cost));
        }

        response.sendRedirect("stock_exchange");
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(200);

        String command = request.getParameter("command");
        System.out.println(command);
        if (command != null) {
            switch (command) {
                case "add":
                    changeExchange(response, -1);
                    break;
                case "edit":
                    changeExchange(response, Integer.valueOf(request.getParameter("id")));
                    break;
                case "delete":
                    deleteExchange(response, Integer.valueOf(request.getParameter("id")));
                    break;
                case "save":
                    saveExchange(request, response);
                    break;
            }
        } else {
            listExchange(response);
        }
    }
}

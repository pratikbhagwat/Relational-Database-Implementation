/**
 * Commenting the printing part for testing purposes
 * throwing exceptions manually to indicate the failure to execute the query
 */

package RelationalDatabasseImplementation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class RelationalDatabase implements DBFunctionalities{
    private Connection connection = null;

    public RelationalDatabase(Connection connection){
        this.connection = connection;
    }

    @Override
    public void createAccount(String username, String password, String firstName, String lastName) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("insert into Users (username, password , firstname, lastname) values(?,?,?,?)");
        preparedStatement.setString(1,username);
        preparedStatement.setString(2,password);
        preparedStatement.setString(3,firstName);
        preparedStatement.setString(4,lastName);
        preparedStatement.execute();
        preparedStatement.close();
    }

    @Override
    public void submitOrder(String date, String username, String password, Map<Integer, Integer> listOfProductsAndQuantities) throws SQLException {
        if (authenticateUser(username,password)){

            updateStockAndInsertOrdersAtOnce(date,username,listOfProductsAndQuantities);

        }else{
//            System.out.println("User not authenticated");
            throw  new SQLException();
        }
    }


    private void updateStockAndInsertOrdersAtOnce(String date, String username, Map<Integer, Integer> listOfProductsAndQuantities) throws SQLException {
        // this function uses the batch of two queries
        // the initial query updates the products of the shopping list into the product table
        // the later one inserts the order details into the order table.

        /** start of the update query **/
        StringBuilder updatesToBeExecuted = new StringBuilder();
        updatesToBeExecuted.append("UPDATE products\n" +
                "   SET stock = CASE productid ");
        ArrayList<String> keyString = new ArrayList<>();

        for (int key:listOfProductsAndQuantities.keySet()){
            updatesToBeExecuted.append(" when "+ key + " then " + "stock - " + listOfProductsAndQuantities.get(key));
            keyString.add(""+key);
        }

        updatesToBeExecuted.append( " else stock end where productid in " + "(" + String.join(",",keyString) + ")" );
        PreparedStatement preparedStatement = this.connection.prepareStatement(updatesToBeExecuted.toString());
        preparedStatement.addBatch();

        /** end of the update query **/

        BigDecimal maxSoFar = getMaxOrderIdSoFar();
        BigDecimal newOrderId;
        if (maxSoFar == null){
            newOrderId = new BigDecimal(String.valueOf(1));
        }else {
            newOrderId = maxSoFar.add( new BigDecimal(String.valueOf(1)));
        }

        /** start of the update query **/
        StringBuilder insertsToBeExecuted = new StringBuilder();
        insertsToBeExecuted.append("INSERT INTO orders(orderid,username, productid, quantity, date) values ");

        ArrayList<String> listOfValues = new ArrayList<>();
        for (int key:listOfProductsAndQuantities.keySet()){
            listOfValues.add(" ( "+ newOrderId.toString() + " , " + "\"" +username+"\"" + " , " + key + " , "+ listOfProductsAndQuantities.get(key) + " , " + "\""+date+"\"" + " )");
        }


        insertsToBeExecuted.append(String.join(",",listOfValues));
        preparedStatement.addBatch(insertsToBeExecuted.toString());
        /** end of the update query **/

        preparedStatement.executeBatch();
        preparedStatement.close();

    }

    /**
     *
     * @return the max order id so far in the orders table
     * @throws SQLException gets the max order id so far in the orders table
     */
    private BigDecimal getMaxOrderIdSoFar() throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("select max(orderid) from orders" );
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            return resultSet.getBigDecimal(1);
        }
        return new BigDecimal(String.valueOf(0));
    }


    @Override
    public void postReview(String username, String password, int productID, DBFunctionalities.Rating rating, String reviewText) throws SQLException {
        if (authenticateUser(username,password)){
            if (authorizeUser(username,productID)) {
                PreparedStatement preparedStatement = this.connection.prepareStatement("insert into Reviews (username, productid, reviewtext, date, rating) values (?,?,?,?,?)");
                Date date = new Date();//util.date object
                java.sql.Date todaysDate = new java.sql.Date(date.getTime());
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, productID);
                preparedStatement.setString(3, reviewText);
                preparedStatement.setDate(4, todaysDate);
                preparedStatement.setString(5, "" + (rating.ordinal() + 1));
                preparedStatement.execute();
                preparedStatement.close();
            }else {
//                System.out.println("User not authorized");
                throw  new SQLException();
            }
        }else {
//            System.out.println("User not authenticated");
            throw  new SQLException();
        }
    }

    /**
     *
     * @param username : username of the user
     * @param productID : id of the product
     * @return: whether the user is authorized to post the review for the following product.
     * @throws SQLException
     */
    private boolean authorizeUser(String username, int productID) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("select * from orders where username = "+ "?" + " and " + "productid = " + "?" + " limit 1" );
        preparedStatement.setString(1,username);
        preparedStatement.setInt(2,productID);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            return resultSet.getObject(1) != null;
        }
        return false;

    }

    /**
     *
     * @param username :  username of the user
     * @param password : password of the user
     * @return authentification of the user. i.e. username and password matches or not.
     * @throws SQLException
     */
    private boolean authenticateUser(String username, String password) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("select password from Users where username = " + "\""+username+"\"");
        boolean authentication ;
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            authentication = resultSet.getString("password").equals(password);
        }else{
            authentication = false;
        }
        preparedStatement.close();
        return authentication;
    }

    @Override
    public void addProduct(String name, String description, double price, int initialStock) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("insert into Products ( name , description , price, stock) values(?,?,?,?)");
        preparedStatement.setString(1,name);
        preparedStatement.setString(2,description);
        preparedStatement.setDouble(3,price);
        preparedStatement.setInt(4,initialStock);
        preparedStatement.execute();
        preparedStatement.close();
    }

    @Override
    public void updateStockLevel(int productID, int itemCountToAdd) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("update Products set stock = stock + "+ "?" + " where productid = " + "?");
        preparedStatement.setInt(1,itemCountToAdd);
        preparedStatement.setInt(2,productID);
        preparedStatement.execute();
        preparedStatement.close();
    }

    @Override
    public void getProductAndReviews(int productID) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("select a.productid,a.name,a.description,b.username,b.reviewtext,b.rating from Products as a inner join Reviews as b on a.productid = b.productid where a.productid = ?");
        preparedStatement.setInt(1,productID);

        ResultSet resultSet = preparedStatement.executeQuery();
        int numberOfColumns =  resultSet.getMetaData().getColumnCount();

        /** printing logic **/
        ArrayList<String> columnNames = new ArrayList<>();
        for (int i=1;i<=numberOfColumns;i++){
            columnNames.add(resultSet.getMetaData().getColumnLabel(i));
        }
//        System.out.println(columnNames);


        while (resultSet.next()){
            ArrayList<String> rowData = new ArrayList<>();
            for (int i = 1;i<=numberOfColumns;i++){
                rowData.add(resultSet.getString(i));
            }
//            System.out.println(rowData);
        }

        preparedStatement.close();
    }

    @Override
    public void getAverageUserRating(String username) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT avg(rating) FROM Reviews where username = " + "?");
        preparedStatement.setString(1,username);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
//            System.out.println(resultSet.getDouble(1));
        }
    }
}

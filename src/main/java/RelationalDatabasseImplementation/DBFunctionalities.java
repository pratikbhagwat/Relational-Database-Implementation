package RelationalDatabasseImplementation;

import java.sql.SQLException;
import java.util.Map;

public interface DBFunctionalities {
    enum Rating{
        BAD,SATISFACTORY,GOOD,VERYGOOD,BEST
    }

    /**
     *
     * @param username username of the user
     * @param password password of the user
     * @param firstName firstname of the user
     * @param lastName lastname of the user
     * description:This method will create a new account, the username has to be unique for the account to be created.
     *
     * @throws SQLException
     */
    public void createAccount(String username,String password,String firstName,String lastName) throws SQLException;

    /**
     *
     * @param date : date of the purchase
     * @param username : username of user who placed the order
     * @param password : password of user who placed the order
     * @param listOfProductsAndQuantities : list of product ids and their respective quantities
     * description : places the order by updating the product table and order table(atomically).
     * @throws SQLException
     */
    public void submitOrder(String date, String username, String password, Map<Integer,Integer> listOfProductsAndQuantities) throws SQLException;

    /**
     *
     * @param username: username of user who placed the order
     * @param password: password of user who placed the order
     * @param productID: id of the product
     * @param rating: rating given by the user for the product
     * @param reviewText : text of the review
     * description : posts the review for the product by an authorized user
     * @throws SQLException
     */
    public void postReview(String username,String password,int productID,Rating rating,String reviewText) throws SQLException;

    /**
     *
     * @param name : name of the product
     * @param description: description of the product
     * @param price : price of the product
     * @param initialStock : initial stock of the product
     * description : adds the new product to the product table.
     * @throws SQLException
     */
    public void addProduct(String name,String description,double price,int initialStock) throws SQLException;

    /**
     *
     * @param productID : id of the product
     * @param itemCountToAdd : number of items to be added
     * description : updates the stock for the given product id by adding itemCountToAdd to existing number
     * @throws SQLException
     */
    public void updateStockLevel(int productID,int itemCountToAdd) throws SQLException;

    /**
     *
     * @param productID : id of the product
     * @throws SQLException
     */
    public void getProductAndReviews(int productID) throws SQLException;

    /**
     *
     * @param username : username of the user
     * description : gets the average rating given by a particular user.
     * @throws SQLException
     */
    public void getAverageUserRating(String username) throws SQLException;

}

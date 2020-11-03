package RelationalDatabasseImplementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QueryExecuterThread implements Runnable {
    public Connection connection = null;
    public int myId = -1;
    public static ArrayList<Integer> resultArrayList = null; // set this before creating new objects of this thread class.
    private boolean stop = false;


    public QueryExecuterThread(Connection connection,int myId){
        this.connection = connection;
        this.myId = myId;
    }
    @Override
    public void run() {
        RelationalDatabase relationalDatabase = new RelationalDatabase(this.connection);

        while(!stop){
            try {
                performRandomOperation(relationalDatabase);
                resultArrayList.set(myId,resultArrayList.get(myId)+1);// storing the number of operations performed by this thread
            }catch (SQLException e){

                System.out.println(e.toString());
//                e.printStackTrace();
            }

        }
    }

    /**
     *
     * @param relationalDatabase relational database class object
     * description: performs a random operations based on the probabilities mentioned in the assignment
     * @throws SQLException
     */
    private void performRandomOperation(RelationalDatabase relationalDatabase) throws SQLException {
        // if the random integer falls in these vallues then the correcponding function is called
//        0-1 : addproduct
//        2-4 : createaccount
//        5-9 : getaverageuserrating
//        10-14 : postReview
//        15-24 : updateStockLevel
//        25-34 : submitorder
//        35-99 : productandreviews

        int choice = getRandomNumberBetween(0,99);
        if (choice < 2){
            int productNumber = getRandomNumberBetween(10000+1,Integer.MAX_VALUE);
            relationalDatabase.addProduct("product"+productNumber,"desc"+productNumber,productNumber,10000);
        }else if (choice < 5){
            int userNumber = getRandomNumberBetween(1000+1,Integer.MAX_VALUE);
            relationalDatabase.createAccount("user"+userNumber,"pass"+userNumber,"userFName"+userNumber,"userLName"+userNumber);
        }else if (choice < 10){
            int userNumber = getRandomNumberBetween(1,1000);
            relationalDatabase.getAverageUserRating("user"+userNumber);
        }else if (choice < 15){
            int userNumber = getRandomNumberBetween(1,1000);
            int productNumber = getRandomNumberBetween(1,10000);
            relationalDatabase.postReview("user"+userNumber,"pass"+userNumber,productNumber, DBFunctionalities.Rating.values()[ new Random().nextInt(DBFunctionalities.Rating.values().length)],"Some review text by user "+userNumber +" for product "+productNumber);
        }else if (choice < 25){
            int productNumber = getRandomNumberBetween(1,10000);
            int itemsToAdd = getRandomNumberBetween(1,100);
            relationalDatabase.updateStockLevel(productNumber,itemsToAdd);
        }else if (choice < 35){
            int userNumber = getRandomNumberBetween(1,1000);
            relationalDatabase.submitOrder("2020-08-01","user"+userNumber,"pass"+userNumber,getRandomOrder());
        }else if (choice < 100){
            int productNumber = getRandomNumberBetween(1,10000);
            relationalDatabase.getProductAndReviews(productNumber);
        }else {
            System.out.println("something went wrong");
            throw new SQLException();
        }

    }

    /**
     * description: gets the random number between start and end including start and end
     * @param start: start of the range
     * @param end: end of the range
     * @return
     */
    private int getRandomNumberBetween(int start, int end) {
        return (int)(Math.random() * (end-start) + start);
    }

    /**
     * description: gets the random order list
     * @return
     */
    private  Map<Integer, Integer> getRandomOrder() {
        Map<Integer,Integer> randomOrderMap = new HashMap<>();
        int numberOfDistinctProducts = getRandomNumberBetween(1,25);
        for (int i=0;i<numberOfDistinctProducts;i++){
            int productNumber = getRandomNumberBetween(1,10000);
            int quantityNumber = getRandomNumberBetween(1,100);
            randomOrderMap.putIfAbsent(productNumber,quantityNumber);
        }
        return randomOrderMap;
    }
    public void stopTheThread(){
        this.stop = true;
    }
}

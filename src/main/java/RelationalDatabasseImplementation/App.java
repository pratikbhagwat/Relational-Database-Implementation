/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package RelationalDatabasseImplementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class App {

    public static void main(String[] args) throws SQLException, InterruptedException {
//        uncomment this to reinitialize the db
//    initializeDB(DriverManager.getConnection(args[0],args[1],args[2]));



        int numberOfThreads = Integer.parseInt(args[3]);

        ArrayList<Thread> listOfThreads = new ArrayList<>();
        ArrayList<QueryExecuterThread> listOfQueryExecuters = new ArrayList<>();
        QueryExecuterThread.resultArrayList = new ArrayList<>();

        for (int i = 0;i<numberOfThreads;i++){
            QueryExecuterThread.resultArrayList.add(0);
        }
        for (int i =0;i<numberOfThreads;i++){
            listOfQueryExecuters.add(new QueryExecuterThread(DriverManager.getConnection(args[0],args[1],args[2]),i));
        }
        for (int i=0;i<numberOfThreads;i++){
            listOfThreads.add(new Thread(listOfQueryExecuters.get(i)));
        }
        for (Thread t: listOfThreads){
            t.start();
        }
        Thread.sleep(300000);
        for (QueryExecuterThread queryExecuter : listOfQueryExecuters){
            queryExecuter.stopTheThread();
        }
        Thread.sleep(300);
        System.out.println(QueryExecuterThread.resultArrayList);
        System.out.println(QueryExecuterThread.resultArrayList.stream().mapToInt(number->number).sum());

    }

    /**
     * description: initializes the database
     * @param connection
     */
    public static void initializeDB(Connection connection){
        RelationalDatabase relationalDatabase = new RelationalDatabase(connection);
        //uncomment these lines to reinitialize the db
        createAccounts(relationalDatabase);
        createProducts(relationalDatabase);
        createOrders(relationalDatabase);
        createReviews(relationalDatabase);
    }

    /**
     *
     * @param relationalDatabase
     * description: initializes the orders
     */
    private static void createOrders(RelationalDatabase relationalDatabase) {
        for(int i =1;i<=10000;i++){
            int userNumber = (int)(Math.random() * (1000-1) + 1);
            try {
                relationalDatabase.submitOrder("2020-09-13","user"+userNumber,"pass"+userNumber,getRandomOrder());
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * description: generates random order
     * @return
     */
    private static Map<Integer, Integer> getRandomOrder() {
        Map<Integer,Integer> randomOrderMap = new HashMap<>();
        for (int i=0;i<5;i++){
            int productNumber = (int)(Math.random() * (10000-1) + 1);
            int quantityNumber = (int)(Math.random() * (100-1) + 1);
            randomOrderMap.putIfAbsent(productNumber,quantityNumber);
        }
        return randomOrderMap;
    }

    /**
     * description: initializes accounts
     * @param relationalDatabase
     */
    public static void createAccounts(RelationalDatabase relationalDatabase){
        for ( int i = 1;i<=1000;i++){
            try{
                relationalDatabase.createAccount("user"+i,"pass"+i,"userFName"+i,"userLName"+i);
            }catch (SQLException e){
                e.printStackTrace();
            }

        }
    }

    /**
     * description: initializes products
     * @param relationalDatabase
     */
    public static void createProducts(RelationalDatabase relationalDatabase)  {
        for ( int i = 1;i<=10000;i++){
            try {
                relationalDatabase.addProduct("product"+i,"desc"+i,i,10000);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * description: initializes reviews
     * @param relationalDatabase
     */
    public static void createReviews(RelationalDatabase relationalDatabase) {
        for ( int i = 1;i<=100000;i++){
            int userNumber = (int)(Math.random() * (1000-1) + 1);
            int productNumber = (int)(Math.random() * (10000-1) + 1);
            try {
                relationalDatabase.postReview("user"+userNumber,"pass"+userNumber,productNumber, DBFunctionalities.Rating.values()[ new Random().nextInt(DBFunctionalities.Rating.values().length)],"Some review text by user "+userNumber +" for product "+productNumber);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

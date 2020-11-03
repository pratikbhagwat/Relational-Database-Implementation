The app.java contains the main method.
It takes four arguments as follows:
1: DB URL (rewriteBatchedStatements=true&serverTimezone=UTC) please set the following parameters in url.
One is for enabling batch execution which the program needs for atomic operation of submit order.
Second is the serverTimezone, it is needed by the jdbc driver that I am using.

2: The username
3: The password
4: Number of threads to be executed concurrently.


We need to keep the copy of our db after each execution we must reinitialize our db.
Using this we can repeatedly test the db performance based on number of threads acting on it.

The number of stocks will never go below 0 as stock field is an unsigned integer.
Also the submit order operation is atomic (according to my best knowledge).



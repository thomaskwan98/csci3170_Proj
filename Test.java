import java.sql.*;
import java.util.Scanner;

public class Test {
    static final String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db28";
    static final String dbUsername = "Group28";
    static final String dbPassword = "115528";


    public static void main(String[] args){
        Scanner stdin = new Scanner(System.in); 

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
            boolean notTerminated=true;
            while(notTerminated){
                System.out.println("-----Main menu-----");
                System.out.println("What kinds of operations would you like to perform?");
                System.out.println("1. Operations for Adminstrator");
                System.out.println("2. Operations for User");
                System.out.println("3. Operations for Manager");
                System.out.println("4. Exit this program");
                System.out.print("Enter Your Choice: ");
                int choice = Integer.parseInt(stdin.nextLine());

                switch(choice){
                    case 1:
                        throw new java.lang.UnsupportedOperationException("Not supported yet.");
                        //break;
                    case 2:
                        UserQuery.action(conn,stdin);
                        break;
                    case 3:
                        throw new java.lang.UnsupportedOperationException("Not supported yet.");
                        //break;
                    case 4:            
                        notTerminated=false;
                        break;
                    default:
                        System.out.println("Unknown input, please try again!");
                }

            }
        } catch (ClassNotFoundException e) {
            System.out.println("[Error]: java MySQL DB Driver not found!!");
            System.exit(0);
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
}

class UserQuery{
    static void action(Connection conn,Scanner stdin){
        boolean notTerminated=true;
        while(notTerminated){
            System.out.println("-----Operations for user menu-----");
            System.out.println("What kinds of operations would you like to perform?");
            System.out.println("1. Search for Cars");
            System.out.println("2. Show loan record of a user");
            System.out.println("3. Return to the main menu");
            System.out.print("Enter Your Choice: ");
            int choice = Integer.parseInt(stdin.nextLine());
            switch(choice){
                case 1:
                    int mode;
                    String keyword;
                    while(true){
                        System.out.println("Choose the search critieria: ");
                        System.out.println("1. call number");
                        System.out.println("2. name");
                        System.out.println("3. company");
                        try{
                            mode=Integer.parseInt(stdin.nextLine());
                        }catch(Exception e){
                            System.out.println(e);
                            continue;
                        }
                        if(!(mode>=1 && mode<=3)){
                            System.out.println("Please choose from 1-3 again");
                            continue;
                        }

                        System.out.print("Type in the Search keyword: ");
                        keyword=stdin.nextLine();
                        if(keyword.length()==0){
                            System.out.println("ERROR: please input something");
                            continue;
                        }
                        if(keyword.length()!=8 && mode==1){
                            System.out.println("ERROR: callnum's length != 8");
                            continue;
                        }
                        if(keyword.length()>10 && mode==2){
                            System.out.println("ERROR: name's length > 10");
                            continue;
                        }        
                        if(keyword.length()>25 && mode==3){
                            System.out.println("ERROR: company's length > 25");
                            continue;
                        }
                        break;
                    }
                    UserQuery.searchCar(mode,keyword,conn);
                    break;
                case 2:
                    String uid;
                    while(true){
                        System.out.print("Enter the CUSER id: ");
                        uid = stdin.nextLine();
                        if(uid.length()>12)
                            System.out.println("ERROR: CUSER id >length-12");
                        else if(uid.isEmpty())
                            System.out.println("ERROR: Empty CUSER id");
                        else
                            break;
                    }
                    UserQuery.showLoan(uid,conn);
                    break;
                case 3:
                    notTerminated=false;
                    break;
                default:
                    System.out.println("Unknown input, please try again!");
            }
        }
    }

    static void showLoan(String uid,Connection conn){
         //show loan records of a user
        String loanQuery=String.join("\n",
                                    "SELECT temp.callnum,temp.copynum,name,cname,checkout,returndate",
                                    "FROM (SELECT *",
                                    "        FROM rent2",
                                    "        WHERE uid = ? ) AS temp",
                                    "JOIN car2 ON temp.callnum=car2.callnum",
                                    "JOIN produce2 ON temp.callnum=produce2.callnum",
                                    "",
                                    "ORDER BY",
                                    "    temp.checkout",
                                    ";"
        );
        //"SELECT temp.callnum,temp.copynum,name,cname,checkout,returndate FROM (SELECT * FROM rent2 WHERE uid = ? ) AS temp JOIN car2 ON temp.callnum=car2.callnum JOIN produce2 ON temp.callnum=produce2.callnum ORDER BY temp.checkout;";
  
        try{
            //prepare and run the sql statement
            java.sql.PreparedStatement  stmt = conn.prepareStatement(loanQuery);
            stmt.setString(1, uid);
            java.sql.ResultSet resultSet = stmt.executeQuery();

            //process the return            
            if (!resultSet.isBeforeFirst())
                System.out.println("No records found.");
            else{
                System.out.println("|Call Num\t|CopyNum\t|Name\t|Company\t|Check-out\t|Returned?\t|");
                while (resultSet.next()) {     
                    System.out.printf("|%s\t|%s\t\t|%s\t|%s\t|%s\t|%s\t\t|\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getDate(5), (resultSet.getString(6)==null?"No":"Yes"));
                }
                System.out.println("End of Query");
            }
        }catch (SQLException e) {
            System.out.println(e);
        }
        return;
    }
    static void searchCar(int mode,String keyword, Connection conn){
        String query="";
        switch(mode){
            case 1:
                //search by exact callnum
                query=String.join(  "\n",
                                    "SELECT temp.callnum,name,ccname,cname,count(*)",
                                    "FROM (SELECT * FROM car2 WHERE car2.callnum = ? ) AS temp",
                                    "JOIN copy2 ON temp.callnum=copy2.callnum",
                                    "JOIN car_category2 ON temp.ccid=car_category2.ccid",
                                    "JOIN produce2 ON temp.callnum=produce2.callnum",
                                    "",
                                    "WHERE",
                                    "    copy2.copynum",
                                    "    NOT IN (",
                                    "    SELECT copynum FROM rent2",
                                    "    WHERE",
                                    "        callnum = temp.callnum AND returndate IS NULL",
                                    "    )",
                                    "",
                                    "ORDER BY",
                                    "    temp.callnum",
                                    ";"
                );
                break;
            case 2:
                //search by carname(partial matching)
                query=String.join("\n",
                                    "SELECT temp.callnum,name,ccname,cname,count(*)",
                                     "FROM (SELECT * FROM car2 WHERE car2.name LIKE '%"+keyword+"%' ) AS temp",
                                     "JOIN copy2 ON temp.callnum=copy2.callnum",
                                     "JOIN car_category2 ON temp.ccid=car_category2.ccid",
                                     "JOIN produce2 ON temp.callnum=produce2.callnum",
                                     "",
                                     "WHERE",
                                     "    (temp.callnum,copy2.copynum)",
                                     "    NOT IN (",
                                     "    SELECT callnum,copynum FROM rent2",
                                     "    WHERE",
                                     "        callnum = temp.callnum AND returndate IS NULL",
                                     "    )",
                                     "",
                                     "GROUP BY",
                                     "    temp.callnum",
                                     "ORDER BY",
                                     "    temp.callnum",
                                     ";"
                );
                break;
            case 3:
                //search by company
                query=String.join("\n",
                                    "SELECT temp.callnum,name,ccname,cname,count(*)",
                                     "FROM (SELECT car2.callnum,name,ccid,cname",
                                     "    FROM car2",
                                     "    INNER JOIN produce2 ON car2.callnum=produce2.callnum",
                                     "    WHERE produce2.cname LIKE '%"+keyword+"%' ) AS temp",
                                     "JOIN copy2 ON temp.callnum=copy2.callnum",
                                     "JOIN car_category2 ON temp.ccid=car_category2.ccid",
                                     "",
                                     "WHERE",
                                     "    (temp.callnum,copy2.copynum)",
                                     "    NOT IN (",
                                     "    SELECT callnum,copynum FROM rent2",
                                     "    WHERE",
                                     "        callnum = temp.callnum AND returndate IS NULL",
                                     "    )",
                                     "",
                                     "GROUP BY",
                                     "    temp.callnum",
                                     "ORDER BY",
                                     "    temp.callnum",
                                     ";"
                );
                break;                                     
        }

        try{
            java.sql.PreparedStatement  stmt = conn.prepareStatement(query);
            if(mode==1)
                stmt.setString(1, keyword);
            java.sql.ResultSet resultSet = stmt.executeQuery();

            //process the return            
            if (!resultSet.isBeforeFirst())
                System.out.println("No records found.");
            else{
                System.out.println("|Call Num\t|Name\t|Car Category\t|Company\t|Available No. of Copy\t|");
                while (resultSet.next()) {     
                    System.out.printf("|%s\t|%s\t|%s\t|%s\t|%d\t|\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4),resultSet.getInt(5));                                                       
                }
                System.out.println("End of Query");
            }
        }catch (SQLException e) {
            System.out.println(e);
        }
        return
        ;
    }    
}

import dao.*;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.URL;
//import org.junit.Test;
//import static org.junit.Assert.assertEquals;



public class TestStudentDAO{

    public static void main(String[] args){


        Connection connection = null;

        //Load in properties file
        FileReader reader;
        Properties p = new Properties();
        String studySlugDir= System.getenv("STUDYSLUGDIR");

        try{
        	reader= new FileReader(studySlugDir+"/app/cfg/db.properties");
        	p.load(reader);
        }
        catch(IOException e){
        	e.printStackTrace();
        }
        /*
        System.out.println("IP: "+p.getProperty("ip"));
        System.out.println("Port: "+p.getProperty("port"));
        System.out.println("User: "+p.getProperty("user"));
		*/

        //Prepares url and user
        String url="jdbc:postgresql://"+p.getProperty("ip")
            +":"+p.getProperty("port")+"/studyslug";
        String user= p.getProperty("user");

        try{
            Class.forName("org.postgresql.Driver");    
            connection = 
                DriverManager.getConnection(url,user,p.getProperty("password"));
            
            if ( connection != null){
                System.out.println("Connection to database succesful");
            }
        
        }
        catch(Exception e){
            System.out.println("Error while connection to database: " +e);
            e.printStackTrace();
        }
        
        StudentDAO student = new StudentDAO(connection);

        //Fake people to add to test database
        String studentArray[][] = {
            {"Tom", "Smith", "tsmith@ucsc.edu"},
            {"John", "Doe",  "jdoe@gmail.com"},
            {"Hangry","Potatos", "hpotatos@gmail.com"},
            {"Arnold","Palmer","apalmer@gmail.com"},
            {"Michael","Runsalot","mrunsalot@gmail.com"},
            {"Han", "Solo", "hsolo@gmail.com"},
            {"Bruce", "Wayne", "bwayne@batman.net"},
            {"Indiana","Jones","ijones@gmail.com"},
            {"Chevy", "Chae", "cchase@gmail.com"},
            {"Darrell", "Long", "dlong@gmail.com"}

        };        
        
        //Array to track assigned student ID's. Used to 
        int[] studentIDs = new int[studentArray.length];

        //Array creating known students and their IDs. Used for verifying database
        //correctness. 
        Student[] studentList = new Student[studentArray.length];

        //Go through the fake student data and put them into the database. 
        for(int i= 0; i< studentArray.length; i++){
            student.reset();
            student.setFirstName(studentArray[i][0]);
            student.setLastName(studentArray[i][1]);
            student.setEmailAddress(studentArray[i][2]);
            student.save();


            //Creates a list of new student objects. Makes verification a little 
            //cleaner. 
            studentList[i]= new Student();
            studentList[i].setStudentID(student.getStudentID());
            studentList[i].setFirstName(studentArray[i][0]);
            studentList[i].setLastName(studentArray[i][1]);
            studentList[i].setEmailAddress(studentArray[i][2]);
        }

        System.out.println("Students Added to Database.\n\n");
        System.out.println("One by one query of each individual student based on ID");

        //Iterate through known student IDs and check query database for them.

        int passedTests= 0;
        for(int i= 0; i<studentIDs.length;i++){
            student.find(studentList[i].getStudentID());
            Student result= student.getStudent();
            if(result.equals(studentList[i])){
                //Query returns correct result.
                System.out.println("Student "+i+" added and verified");
                passedTests++;
            }
            else{
                //Query returns incorrect result.
                System.out.println("\nSTUDENT "+i+" NOT VERIFIED");
                System.out.println("Student From Database");
                System.out.println(result);
                System.out.println("Proper Result");
                System.out.println(studentList[i]);
                System.out.println();
            }

        }
        if(studentList.length==passedTests){
            System.out.println("All tests passed");
        }
        else{
            System.out.println("ERRORS! "+passedTests+ " out of " +studentList.length+ 
                                " passed.");
        }


        System.out.println("Single student query via student ID checked against " 
            +"correct data finished");
        

        /**
         *Testing update ability of a single person.
         *
        **/
        String testName= "TestName";
        System.out.println("\nTesting Update Function");
        System.out.println("Update student "+studentList[0].getStudentID());
        student.find(studentList[0].getStudentID());
        student.setFirstName(testName);
        student.save();

        //Now refind that student based on student ID.
        student.find(studentList[0].getStudentID());


        if(student.getFirstName().equals(testName)){
            System.out.println("Update test passes!");
        }
        else{
            System.out.println("Update test fails");
            System.out.println("Queried first name: "+student.getFirstName());
            System.out.println("Intended first name: "+ testName);
        }


        /**
         *Testing deletion. We'll delete everyone for that matter.
        **/
        System.out.println("\nTesting deletion");
        //
        passedTests= 0;
        for(int i= 0; i<studentIDs.length;i++){
            student.find(studentList[i].getStudentID());
            //Deletion Failed
            if(!student.remove()){
                System.out.println("Deletion Failed");
            }

            if(!student.find(studentList[i].getStudentID())){
                //Query returns correct result.
                System.out.println("Student "+i+" is deleted");
                passedTests++;
            }
            else{
                //Query returns incorrect result.
                System.out.println("\nSTUDENT "+i+" NOT DELETED");
                System.out.println("Student From Database");
                System.out.println(student.getStudent());
            }

        }
        if(studentList.length==passedTests){
            System.out.println("All tests passed");
        }
        else{
            System.out.println("ERRORS! "+passedTests+ " out of " +studentList.length+ 
                                " passed.");
        }
    }

}

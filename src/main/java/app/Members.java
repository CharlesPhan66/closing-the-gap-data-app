package app;

/**
 * Class represeting a LGA from the Studio Project database
 * In the template, this only uses the code and name for 2016
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 */
public class Members {
   // Student ID
   private String code;

   // Student Name
   private String name;

   // Create an empty list
   public Members() {}

   /**
    * Create a Member and set the fields
    */
   public Members(String code, String name){
      this.code = code;
      this.name = name;
   }

   public String getCode() {
      return code;
   }

   public String getName() {
      return name;
   }
}

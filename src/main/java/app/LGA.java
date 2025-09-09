package app;

/**
 * Class represeting a LGA from the Studio Project database
 * In the template, this only uses the code and name for 2016
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 */
public class LGA {
   // No-argument constructor for frameworks and template engines
   public LGA() {}

   /**
    * Create an LGA and set the fields
    */
   public LGA(String code, String name, int year) {
      this.code = code;
      this.name = name;
      this.year = year;
   }

   // LGA Code
   private String code;

   // LGA Name
   private String name;

   // LGA Year
   private int year;


   public String getCode() {
      return code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getYear() {
      return year;
   }

   public void setYear(int year) {
      this.year = year;
   }
}

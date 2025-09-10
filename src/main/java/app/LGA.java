package app;

/**
 * Class represeting a LGA from the Studio Project database
 * In the template, this only uses the code and name for 2016
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 */
public class LGA {
   // LGA Code
   private String code;

   // LGA Name
   private String name;

   // LGA Year
   private int year;

   // LGA Total Population
   private int population;

   // LGA State ID
   private int stateID;

   /**
    * Create an LGA and set the fields
    */
   public LGA(String code, String name, int year, int population, int stateID){
      this.code = code;
      this.name = name;
      this.year = year;
      this.population = population;
      this.stateID = stateID;
   }

   public String getCode() {
      return code;
   }

   public String getName() {
      return name;
   }

   public int getYear() {
      return year;
   }

   public int getPopulation() {
      return population;
   }

   public int getStateID() {
      return stateID;
   }
}

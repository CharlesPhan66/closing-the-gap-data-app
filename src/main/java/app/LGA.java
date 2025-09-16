package app;

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
   // No-argument constructor for frameworks and template engines
   public LGA() {}

   public LGA(String code, String name, int year, int population, int stateID){
      this.code = code;
      this.name = name;
      this.year = year;
      this.population = population;
      this.stateID = stateID;
   }

   public LGA(String code, String name, int year) {
      this.code = code;
      this.name = name;
      this.year = year;
   }
   /**
    * Create an LGA and set the fields
    */

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

   public void setPopulation(int population) {
      this.population = population;
   }

   public int getPopulation() {
      return population;
   }

   public void setStateID(int stateID) {
      this.stateID = stateID;
   }
   public int getStateID() {
      return stateID;
   }
}

package app;

public class Persona {
   // Persona Code
   private String code;

   // Persona Name
   private String quote;

   // Persona Background
   private String background;
   
   // Persona Needs & Goals
   private String needs_goals;

   // Persona Pain Points
   private String pain_points;

   // Persona Skills & Experience
   private String skills_experience;

   public Persona() {}

   /**
    * Create a Member and set the fields
    */
   public Persona(String code, String quote, String background, String needs_goals, String pain_points, String skills_experience){
      this.code = code;
      this.quote = quote;
      this.background = background;
      this.needs_goals = needs_goals;
      this.pain_points = pain_points;
      this.skills_experience = skills_experience;
   }

   public String getCode() {
      return code;
   }

   public String getQuote() {
      return quote;
   }
   
   public String getBackground() {
      return background;
   }

   public String getNeeds_Goals() {
      return needs_goals;
   }

   public String getPain_Points() {
      return pain_points;
   }

   public String getSkills_Experience() {
      return skills_experience;
   }
}

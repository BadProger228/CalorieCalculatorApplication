using System.Text.Json.Serialization;

namespace CaloriesCalculator
{
    public class FoodItemDto
    {
        public string FoodName { get; set; } = null!;

        public double Calories { get; set; }   
        public double Protein { get; set; }
        public double Fat { get; set; }
        public double Carbs { get; set; }

        [JsonPropertyName("weight_g")]
        public double WeightGrams { get; set; }
    }

    public class CreateSavedRationRequest
    {
        public int UserId { get; set; }
        public string Name { get; set; } = null!;

        [JsonPropertyName("food")]
        public List<FoodItemDto> Items { get; set; } = new();
    }

    public class SavedRationResponse
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public string Name { get; set; } = null!;
        public DateTime CreatedAt { get; set; }
        public int TotalCalories { get; set; }
        public List<FoodItemDto> Items { get; set; } = new();
    }
    public class LoginResponse
    {
        public int UserId { get; set; }
    }


}

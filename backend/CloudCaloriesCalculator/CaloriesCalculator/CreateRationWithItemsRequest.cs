namespace CaloriesCalculator
{
    using System.Text.Json.Serialization;
    public class CreateRationWithItemsRequest
    {
        public int UserId { get; set; }
        public DateOnly Date { get; set; }
        public List<FoodItemDto> Items { get; set; } = new();
    }
}

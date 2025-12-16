namespace CaloriesCalculator
{
    public class DailyRation
    {
        public int Id { get; set; }
        public int UserId { get; set; }
        public DateOnly Date { get; set; }
        public int TotalCalories { get; set; }
    }
}
